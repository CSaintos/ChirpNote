package com.example.chirpnote;

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
import java.util.HashMap;
import java.util.Iterator;

public class ChordTrack implements Track {
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
    public final static int CHANNEL = 1;

    /**
     * A Chord track
     * @param session The session this chord track is a part of
     */
    public ChordTrack(ChirpNoteSession session){
        mRecording = false;
        mSession = session;
        mFilePath = session.getMidiPath();
        mMidiEventHandler = new MidiEventHandler("ChordPlayback");
    }

    /**
     * Gets whether or not this chord track is currently being played back
     * @return True if the chord track is being played
     */
    @Override
    public boolean isPlaying(){
        return mMidiProcessor != null && mMidiProcessor.isRunning();
    }

    /**
     * Gets whether or not this chord track is currently being recorded
     * @return True if the chord track is being recorded
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
     * Starts the recording process for this chord track
     * Note: Do not call this method if this chord track was obtained from a Mixer instance
     * @exception IllegalStateException if the recording process cannot be started
     */
    @Override
    public void startRecording() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot start the recording process when the chord track is already being recorded");
        }
        if(isPlaying()){
            throw new IllegalStateException("Cannot start the recording process when the chord track is being played back");
        }
        if(isRecorded()) {
            // Do not start the recording process more than once, as this will overwrite the entire chord track
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

        // Stop the recording to write the MIDI file instantly (to add chords, we will edit the MIDI file)
        stopRecording();
    }

    /**
     * Stops the recording process for this chord track
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
     * Adds a chord to this track at the given position
     * @param chord The chord to add
     * @param position The position in the track to add the chord (replaces the existing chord at this position)
     * @exception NullPointerException if the given chord is null
     * @exception IllegalStateException if the chord cannot be added to the track at this time
     */
    public void addChord(Chord chord, int position) throws NullPointerException, IllegalStateException {
        if(chord == null){
            throw new NullPointerException("Cannot add a null Chord to the chord track");
        }
        // Recording process is stopped right after it is started for a ChordTrack,
        // so we check if the chord track has been recorded, and not if the recording process is active
        if(!isRecorded()){
            throw new IllegalStateException("Cannot add a chord to the track if the recording process has not been started");
        }

        // Read existing MIDI file
        MidiFile midiFile = null;
        File output = new File(mFilePath);
        try {
            midiFile = new MidiFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add chord
        if(position < mSession.mChords.size()){
            // Add to the middle of the track
            int startTick = RESOLUTION * 4 * position;
            // Remove the old chord
            Chord oldChord = decodeChord(mSession.mChords.get(position));
            int[][] noteOnEvents = oldChord.getNoteEvents();
            for(int i = 0; i < noteOnEvents.length; i++){
                NoteOn event = new NoteOn(startTick + noteOnEvents[i][1], CHANNEL, noteOnEvents[i][0], noteOnEvents[i][2]);
                midiFile.getTracks().get(1).removeNoteOnEvent(event);
            }
            // Add the new chord
            noteOnEvents = chord.getNoteEvents();
            for(int i = 0; i < noteOnEvents.length; i++){
                NoteOn event = new NoteOn(startTick + noteOnEvents[i][1], CHANNEL, noteOnEvents[i][0], noteOnEvents[i][2]);
                midiFile.getTracks().get(1).insertEvent(event);
            }
            mSession.mChords.set(position, encodeChord(chord));
        } else {
            // Add to the end of the track
            int startTick = RESOLUTION * 4 * mSession.mChords.size();
            int[][] noteOnEvents = chord.getNoteEvents();
            for (int i = 0; i < noteOnEvents.length; i++) {
                NoteOn event = new NoteOn(startTick + noteOnEvents[i][1], CHANNEL, noteOnEvents[i][0], noteOnEvents[i][2]);
                midiFile.getTracks().get(1).insertEvent(event);
            }
            mSession.mChords.add(encodeChord(chord));
        }

        // Write changes to MIDI file
        try {
            midiFile.writeToFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes four measures (a row) from the session (affects all MIDI tracks)
     * All other MIDI events are moved back to fill the empty space (rather than inserting rests)
     * @param position The position to remove at
     */
    public void removeFourMeasures(int position){
        // Remove the four measures from MIDI file
        MidiFile midiFile = null;
        File output = new File(mFilePath);
        try {
            midiFile = new MidiFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MidiTrack track = midiFile.getTracks().get(1);
        int startTick = RESOLUTION * 4 * position;
        int endTick = RESOLUTION * 4 * (position + 4);
        ArrayList<MidiEvent> eventsToRemove = new ArrayList<>();
        Iterator<MidiEvent> it = track.getEvents().iterator();
        MidiEvent curr;
        while(it.hasNext()){
            curr = it.next();
            if(curr.getTick() >= endTick){
                break;
            } else if(curr.getTick() >= startTick){
                if(curr instanceof NoteOn || curr instanceof NoteOff){
                    eventsToRemove.add(curr);
                }
            }
        }
        for(MidiEvent event : eventsToRemove){
            track.removeEvent(event);
        }
        // Remove the four measures from session lists
        for(int i = 0; i < 4; i++){
            mSession.mChords.remove(position);
            mSession.mPercussionPatterns.remove(position);
        }
        // Shift all MIDI events (back) that came after the fourth removed measure
        MidiEvent next, prev = null;
        if(position < mSession.mChords.size()){
            HashMap<Integer, Integer> noteMap = new HashMap<>(); // {note MIDI number : amount shifted, in ticks}
            it = track.getEvents().iterator();
            next = it.hasNext() ? it.next() : null;
            while(next != null){
                curr = next;
                next = it.hasNext() ? it.next() : null;
                if(curr.getTick() >= startTick && curr instanceof NoteOn) {
                    NoteOn noteEvent = (NoteOn) curr;
                    if(noteEvent.getVelocity() == 0 && noteMap.get(noteEvent.getNoteValue()) != null){
                        curr.setTick(curr.getTick() + noteMap.get(noteEvent.getNoteValue()));
                        if(prev != null) {
                            curr.setDelta(curr.getTick() - prev.getTick());
                        } else {
                            curr.setDelta(curr.getTick());
                        }
                        if(next != null){
                            next.setDelta(next.getTick() - curr.getTick());
                        }
                        noteMap.remove(noteEvent.getNoteValue());
                    } else {
                        int tickDelta = -(RESOLUTION * 16); // Shift back by 16 beats (4 measures)
                        curr.setTick(curr.getTick() + tickDelta);
                        if(prev != null) {
                            curr.setDelta(curr.getTick() - prev.getTick());
                        } else {
                            curr.setDelta(curr.getTick());
                        }
                        if(next != null){
                            next.setDelta(next.getTick() - curr.getTick());
                        }
                        noteMap.put(noteEvent.getNoteValue(), tickDelta);
                    }
                }
                prev = curr;
            }
        }
        try {
            midiFile.writeToFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    The string encoding for chords is defined as follows:
	char 0-1: index of the root note of the chord in the Chord.RootNote enum (01 = C_SHARP)
	char 2: index of the chord type in the Chord.Type enum (0 = MAJOR)
	char 3: index of the chord inversion in the Chord.Inversion enum (0 = ROOT)
	char 4: chord octave number
	char 5: chord alteration number
	char 6: chord roman numeral
	*/

    /**
     * Encodes the given chord as a String
     * @param chord The chord to encode
     * @return The chord encoding
     */
    private String encodeChord(Chord chord){
        return padNumber(chord.getRootNote().ordinal()) + chord.getType().ordinal()
                + chord.getInversion().ordinal() + chord.getOctave() + chord.getAlteration() + chord.getRoman();
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

    /**
     * Decodes the given chord encoding
     * @param encodedChord The String encoding of a chord to decode
     * @return The decoded chord
     */
    public Chord decodeChord(String encodedChord){
        int rootIdx = removeLeadingZeroes(encodedChord.substring(0, 2));
        int typeIdx = Character.getNumericValue(encodedChord.charAt(2));
        int invIdx = Character.getNumericValue(encodedChord.charAt(3));
        int octave = Character.getNumericValue(encodedChord.charAt(4));
        int alteration = Character.getNumericValue(encodedChord.charAt(5));
        int roman = Character.getNumericValue(encodedChord.charAt(6));
        Chord chord = new Chord(Chord.RootNote.values()[rootIdx], Chord.Type.values()[typeIdx], mSession.getTempo(), roman);
        chord.setInversion(Chord.Inversion.values()[invIdx]);
        while(chord.getOctave() < octave){
            chord.octaveUp();
        }
        while(chord.getOctave() > octave){
            chord.octaveDown();
        }
        chord.setAlteration(alteration);
        return chord;
    }

    /**
     * Removes any leading zeroes from the given String
     * @param str The String to remove leading zeroes from
     * @return The trimmed String as an Integer
     */
    private int removeLeadingZeroes(String str){
        return Integer.parseInt(str.replaceFirst("^0+(?!$)", ""));
    }

    /**
     * Plays back this chord track
     * @exception IllegalStateException if the chord track cannot be played
     */
    @Override
    public void play() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot play the chord track when there is an active recording process (stop recording first)");
        }
        if(!isRecorded()){
            throw new IllegalStateException("Cannot play the chord track if it has not been recorded yet (record it first)");
        }
        if(isPlaying()){
            throw new IllegalStateException("Cannot play the chord track if it is already being played (stop playback first)");
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
     * Stops playback of this chord track
     * @exception IllegalStateException if the chord track cannot be stopped
     */
    @Override
    public void stop() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot stop the chord track when there is an active recording process (stop recording first)");
        }
        if(!isPlaying()){
            throw new IllegalStateException("Cannot stop the chord track if it is not being played (start playback first)");
        }
        mMidiProcessor.reset();
    }
}
