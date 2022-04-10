package com.example.chirpnote;

import android.os.Environment;
import android.widget.Button;

import com.example.midiFileLib.src.MidiFile;
import com.example.midiFileLib.src.MidiTrack;
import com.example.midiFileLib.src.event.MidiEvent;
import com.example.midiFileLib.src.event.NoteOn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class RealTimeMelody extends Melody {
    private final int CHANNEL = 3;
    private long mRecordingStartTime;
    private MidiFile tempMidiFile;
    private File tempOutput;

    /**
     * A MIDI melody which is recorded in real time on the UI keyboard
     * @param tempo The tempo of the melody
     * @param filePath The path to store the file (of the melody recording) at
     * @param playButton The button used to start playback of the melody track
     */
    public RealTimeMelody(int tempo, String filePath, Button playButton){
        super(tempo, filePath, playButton);
    }

    /**
     * A MIDI melody which is recorded in real time on the UI keyboard
     * @param session The session this melody is a part of
     */
    public RealTimeMelody(Session session){
        super(session, session.getMidiPath());
    }

    @Override
    public void startRecording() throws IllegalStateException {
        if(Mixer.mixerExists(mSession)){
            tempMidiFile = null;
            tempOutput = new File(mFilePath);
            try {
                tempMidiFile = new MidiFile(tempOutput);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mNoteTrack = tempMidiFile.getTracks().get(1);
            mNoteTrack.removeChannel(CHANNEL);
            mRecording = true;
        } else {
            super.startRecording();
        }
        mRecordingStartTime = System.currentTimeMillis();
    }

    @Override
    public void stopRecording() throws IllegalStateException {
        if(Mixer.mixerExists(mSession)){
            mRecording = false;
            try {
                tempMidiFile.writeToFile(tempOutput);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSession.setRealTimeMelodyRecorded();
        } else {
            super.stopRecording();
        }
        if(mSession != null) {
            mSession.setRealTimeMelodyRecorded();
        }
    }

    @Override
    public boolean isRecorded(){
        return mSession != null ? mSession.isRealTimeMelodyRecorded() : super.isRecorded();
    }

    /**
     * Writes a note on event for the given note to this melody. Should be called when a note is stopped.
     * (Note: Do not call, unless you manually want to add a noteOn event.
     * This happens automatically during an active recording process, if the note has been constructed with a melody)
     * @param note The note to write a note on event for
     * @return False if melody is not being recorded yet (cannot add a note if there is no active recording process)
     */
    public boolean writeNoteOn(MusicNote note){
        if(!isRecording() || note == null){
            return false;
        }
        mNoteTrack.insertEvent(new NoteOn(getCurrentTick(), CHANNEL, note.getNoteNumber(), note.VELOCITY));
        return true;
    }

    /**
     * Writes a note off event for the given note to this melody. Should be called when a note is stopped.
     * (Note: Do not call, unless you manually want to add a noteOff event.
     * This happens automatically during an active recording process, if the note has been constructed with a melody)
     * @param note The note to write a note off event for
     * @return False if melody is not being recorded, or if the given note is null
     */
    public boolean writeNoteOff(MusicNote note){
        if(!isRecording() || note == null){
            return false;
        }
        // Using a NoteOn event with a velocity of 0, instead of a NoteOff event (essentially the same thing)
        mNoteTrack.insertEvent(new NoteOn(getCurrentTick(), CHANNEL, note.getNoteNumber(), 0));
        return true;
    }

    private long getCurrentTick(){
        return (System.currentTimeMillis() - mRecordingStartTime) * getTempo()  * RESOLUTION / 60000;
    }
    
    /**
    * Quantize each note in this melody to the nearest note based on user selection. 4/8/16
    * @param notetick the notetick value selected to quantize
    * @exception IllegalStateException if the melody cannot be quantized at this time
    */
    public void quantize(Long notetick) throws IllegalStateException {
        // Only quantize if melody is constructed using a session
        if(mSession == null){
            return;
        }
        if(!isRecorded()){
            throw new IllegalStateException("Cannot quantize the melody if it has not been recorded yet (record it first)");
        }
        // Read existing MIDI file
        MidiFile midiFile = null;
        File output = new File(mFilePath);
        try {
            midiFile = new MidiFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Quantize melody
        /*
        NOTE: A NoteOn event with a velocity of 0 is used to turn off notes, instead of a NoteOff event
        -Initialize HashMap to store how much a NoteOn event has shifted by, to be able to update the event that turns off this note later
        -Iterate through events in the midiFile
            -Only look for NoteOn events on this same CHANNEL
            -If we hit a NoteOn event with velocity 0 (used to turn off a note)
                -Get the delta for the earlier NoteOn event from the HashMap (noteMap.get(noteNumber))
                -Update the current event by that delta (by updating tick and delta fields)
                -Remove the event from the HashMap
            -If we hit a NoteOn event with any other velocity
                -Check if the event needs to be re-positioned (quantized), using some quantization technique/algorithm
                    -If it does need to be re-positioned, do so by updating the tick and delta fields
                    -Add to HashMap (map.put(noteNumber, amountChangedInTicks))
        */
        HashMap<Integer, Integer> noteMap = new HashMap<>(); // {note MIDI number : amount shifted, in ticks (negative if shifted backwards)}
        MidiTrack track = midiFile.getTracks().get(1);

        /*
        Logic for getting the values that are multiples of eight notes
         */
        ArrayList<Long> valuesForNoteType = new ArrayList<>();
        Long valueToAdd = notetick;
        valuesForNoteType.add(valueToAdd);
        while (valueToAdd < track.getLengthInTicks()){
            valueToAdd = valueToAdd + notetick;
            valuesForNoteType.add(valueToAdd);
        }
        System.out.println(valuesForNoteType);

        Iterator<MidiEvent> it = track.getEvents().iterator();
        MidiEvent prev = null, next = it.hasNext() ? it.next() : null, curr;
        while(next != null){
            curr = next;
            next = it.hasNext() ? it.next() : null;
            if(curr instanceof NoteOn) {
                NoteOn noteEvent = (NoteOn) curr;
                if(noteEvent.getChannel() == CHANNEL) {
                    if(noteEvent.getVelocity() == 0 && noteMap.get(noteEvent.getNoteValue()) != null) {
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
                        // TODO: Compute how much the event needs to be quantized (how many ticks to move it up or down)
                        long value = 0;
                        long differenceValue = 0;
                        value = curr.getTick();
                        for (int i = 0;i < valuesForNoteType.size();i++){
                            for (int j = 1;j < valuesForNoteType.size();j++){
                                if (valuesForNoteType.get(j) != null){
                                    if (value > valuesForNoteType.get(i) && value < valuesForNoteType.get(j)){
                                        Long originalValue = value;
//                                        System.out.println(curr.getTick());
//                                        curr.setTick(valuesForNoteType.get(i));
//                                        System.out.println("new:" + curr.getTick());
                                        differenceValue = valuesForNoteType.get(i) - value;
                                    }
                                }
                                else{
                                    break;
                                }
                            }
                        }

                        /*
                        noteEvent is the current event you want to check
                        To get the note MIDI number, noteEvent.getNoteValue()
                        To get the tick the event happens at, noteEvent.getTick()
                         */
                        int tickDelta = (int) differenceValue;
                        // ^set tickDelta to how much to move the current event by (negative int if it needs to be moved back)
                        // Currently just moving everything forward by one measure (RESOLUTION is how many ticks per beat, 4 beats in one measure)
                        if(tickDelta != 0) {
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
                }
            }
            prev = curr;
        }
        // Write changes to MIDI file
        try {
            midiFile.writeToFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
