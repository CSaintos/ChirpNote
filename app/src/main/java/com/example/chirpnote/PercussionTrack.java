package com.example.chirpnote;

import com.example.chirpnote.activities.PercussionActivity;
import com.example.midiFileLib.src.MidiFile;
import com.example.midiFileLib.src.MidiTrack;
import com.example.midiFileLib.src.event.MidiEvent;
import com.example.midiFileLib.src.event.NoteOff;
import com.example.midiFileLib.src.event.NoteOn;
import com.example.midiFileLib.src.event.meta.Tempo;
import com.example.midiFileLib.src.event.meta.TimeSignature;
import com.example.midiFileLib.src.util.MidiProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class PercussionTrack implements Track {
    // States
    private boolean mRecording;

    // For writing to MIDI file
    public final int RESOLUTION = 960;
    private MidiTrack mTempoTrack;
    protected MidiTrack mNoteTrack;
    private String mFilePath;

    // For playback
    private MidiProcessor mMidiProcessor;
    private MidiEventHandler mMidiEventHandler;

    private ChirpNoteSession mSession;
    public final static int CHANNEL = 9;

    public PercussionTrack(ChirpNoteSession session){
        mRecording = false;
        mSession = session;
        mFilePath = session.getMidiPath();
        mMidiEventHandler = new MidiEventHandler("PercussionPlayback");
    }

    /**
     * Gets whether or not this percussion track is currently being played back
     * @return True if the percussion track is being played
     */
    @Override
    public boolean isPlaying(){
        return mMidiProcessor != null && mMidiProcessor.isRunning();
    }

    /**
     * Gets whether or not this percussion track is currently being recorded
     * @return True if the percussion track is being recorded
     */
    @Override
    public boolean isRecording(){
        return mRecording;
    }

    /**
     * Gets whether or not this chord track has been recorded
     * @return True if the chord track has been recorded
     */
    public boolean isRecorded(){
        return mSession.isMidiPrepared();
    }

    /**
     * Starts the recording process for this percussion track
     * Note: Do not call this method if this percussion track was obtained from a Mixer instance
     * @exception IllegalStateException if the recording process cannot be started
     */
    @Override
    public void startRecording() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot start the recording process when the percussion track is already being recorded");
        }
        if(isPlaying()){
            throw new IllegalStateException("Cannot start the recording process when the percussion track is being played back");
        }
        if(isRecorded()) {
            // Do not start the recording process more than once, as this will overwrite the entire percussion track
            // We only want this behavior for a RealTimeMelody
            return;
        }
        mRecording = true;

        // Setup MIDI tracks
        mTempoTrack = new MidiTrack();
        mNoteTrack = new MidiTrack();

        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
        mTempoTrack.insertEvent(ts);

        // Tempo setup
        Tempo tempo = new Tempo();
        tempo.setBpm(mSession.getTempo());
        mTempoTrack.insertEvent(tempo);

        // Stop the recording to write the MIDI file instantly (to add percussion patterns, we will edit the MIDI file)
        stopRecording();
    }

    /**
     * Stops the recording process for this percussion track
     * @exception IllegalStateException if the recording process cannot be stopped
     */
    @Override
    public void stopRecording() throws IllegalStateException {
        if(!isRecording()){
            throw new IllegalStateException("Cannot stop the recording process if there is no active recording process (start recording first)");
        }
        ArrayList<MidiTrack> tracks = new ArrayList<>();
        tracks.add(mTempoTrack);
        tracks.add(mNoteTrack);

        // Write tracks to MIDI file
        MidiFile midiFile = new MidiFile(RESOLUTION, tracks);
        try {
            midiFile.writeToFile(new File(mFilePath));
        } catch(IOException e) {
            System.err.println(e);
        }
        mRecording = false;
        mSession.setMidiPrepared();
    }

    /**
     * Adds a pattern to this track at the given position
     * @param pattern The percussion pattern to add
     * @param position The position in the track to add the pattern (replaces the existing pattern at this position)
     * @throws IllegalStateException if the pattern cannot be added to the track at this time
     */
    public void addPattern(PercussionPattern pattern, int position) throws IllegalStateException {
        // Recording process is stopped right after it is started for a ChordTrack,
        // so we check if the chord track has been recorded, and not if the recording process is active
        if(!isRecorded()){
            throw new IllegalStateException("Cannot add a pattern to the track if the recording process has not been started");
        }
        if(pattern == null){
            if(position < mSession.mPercussionPatterns.size()){
                mSession.mPercussionPatterns.set(position, encodePattern(pattern));
            } else {
                mSession.mPercussionPatterns.add(encodePattern(pattern));
            }
            return;
        }
        // Read existing MIDI file
        MidiFile midiFile = null;
        File output = new File(mFilePath);
        try {
            midiFile = new MidiFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MidiTrack track = midiFile.getTracks().get(1);

        // Add pattern
        if(position < mSession.mPercussionPatterns.size()){
            // Add to the middle of the track
            int startTick = RESOLUTION * 4 * position;
            int endTick = RESOLUTION * 4 * (position + 1);
            // Remove the old pattern
            Iterator<MidiEvent> it = track.getEvents().iterator();
            MidiEvent curr;
            while(it.hasNext()){
                curr = it.next();
                if(curr.getTick() >= startTick){
                    if(curr instanceof NoteOn){
                        if(curr.getTick() >= endTick){
                            break;
                        }
                        NoteOn noteEvent = (NoteOn) curr;
                        if(noteEvent.getChannel() == CHANNEL){
                            it.remove();
                        }
                    } else if(curr instanceof NoteOff) {
                        NoteOff noteEvent = (NoteOff) curr;
                        if(noteEvent.getChannel() == CHANNEL){
                            it.remove();
                        }
                    }
                }
            }
            // Add the new pattern
            it = pattern.getMidiFile().getTracks().get(1).getEvents().iterator();
            while(it.hasNext()){
                curr = it.next();
                if(curr instanceof NoteOn){
                    NoteOn temp = ((NoteOn) curr);
                    track.insertEvent(new NoteOn(startTick + (temp.getTick() * 2), CHANNEL, temp.getNoteValue(), temp.getVelocity()));
                } else if(curr instanceof NoteOff){
                    NoteOff temp = ((NoteOff) curr);
                    track.insertEvent(new NoteOn(startTick + (temp.getTick() * 2), CHANNEL, temp.getNoteValue(), 0));
                }
            }
            mSession.mPercussionPatterns.set(position, encodePattern(pattern));
        } else {
            // Add to the end of the track
            int startTick = RESOLUTION * 4 * mSession.mPercussionPatterns.size();
            Iterator<MidiEvent> it = pattern.getMidiFile().getTracks().get(1).getEvents().iterator();
            MidiEvent next;
            while(it.hasNext()){
                next = it.next();
                if(next instanceof NoteOn){
                    NoteOn temp = ((NoteOn) next);
                    track.insertEvent(new NoteOn(startTick + (temp.getTick() * 2), CHANNEL, temp.getNoteValue(), temp.getVelocity()));
                } else if(next instanceof NoteOff){
                    NoteOff temp = ((NoteOff) next);
                    track.insertEvent(new NoteOn(startTick + (temp.getTick() * 2), CHANNEL, temp.getNoteValue(), 0));
                }
            }
            mSession.mPercussionPatterns.add(encodePattern(pattern));
        }
        // Write changes to MIDI file
        try {
            midiFile.writeToFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    The string encoding for percussion patterns is defined as follows:
	char 0: index of percussion style in assets directory, range (0-9, a if null)
	char 1-2: index of percussion pattern in assets directory, range (00-99, aa if null)
	*/

    /**
     * Encodes the given percussion pattern as a String
     * @param pattern The pattern to encode
     * @return The pattern encoding
     */
    private String encodePattern(PercussionPattern pattern) {
        if (pattern == null) return "aaa";
        PercussionPattern.PatternAsset patternAsset = pattern.getPatternAsset();
        return patternAsset.styIndex + padNumber(patternAsset.patIndex);
    }

    /**
     * Decodes the given percussion pattern encoding
     * @param percussionElement The encoded pattern
     * @return The decoded percussion pattern
     */
    public PercussionPattern decodePattern(String percussionElement) {
        String encodedStyle = percussionElement.substring(0, 1);
        String encodedPattern = percussionElement.substring(1);
        int styIndex = 0;
        int patIndex = 0;
        try {
            styIndex = Integer.parseInt(encodedStyle);
            patIndex = Integer.parseInt(encodedPattern);
        } catch (Exception e) {
            return null;
        }
        String style = PercussionActivity.stylePatternMap.keyAt(styIndex);
        return PercussionActivity.stylePatternMap.get(style).get(patIndex);
    }

    /**
     * Plays back this percussion track
     * @exception IllegalStateException if the percussion track cannot be played
     */
    @Override
    public void play() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot play the percussion track when there is an active recording process (stop recording first)");
        }
        if(!isRecorded()){
            throw new IllegalStateException("Cannot play the percussion track if it has not been recorded yet (record it first)");
        }
        if(isPlaying()){
            throw new IllegalStateException("Cannot play the percussion track if it is already being played (stop playback first)");
        }
        MidiFile midiFile = null;
        try {
            midiFile = new MidiFile(new File(mFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMidiProcessor = new MidiProcessor(midiFile);
        mMidiProcessor.registerEventListener(mMidiEventHandler, NoteOn.class);
        mMidiProcessor.start();
    }

    /**
     * Stops playback of this percussion track
     * @exception IllegalStateException if the percussion track cannot be stopped
     */
    @Override
    public void stop() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot stop the percussion track when there is an active recording process (stop recording first)");
        }
        if(!isPlaying()){
            throw new IllegalStateException("Cannot stop the percussion track if it is not being played (start playback first)");
        }
        mMidiProcessor.reset();
    }

    /**
     * Adds leading zeroes to the given number
     * @param num The number to pad
     * @return The padded number as a String
     */
    private String padNumber(int num){
        if(num < 10) return "0" + num;
        return "" + num;
    }
}
