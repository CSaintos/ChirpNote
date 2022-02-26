package com.example.chirpnote;

public interface Track {
    /**
     * Starts recording this track
     */
    void startRecording();

    /**
     * Stops recording this track
     */
    void stopRecording();

    /**
     * Gets whether or not this track is currently being recorded
     * @return True if the track is being recorded
     */
    boolean isRecording();

    /**
     * Plays this track
     */
    void play();

    /**
     * Stops playback of this track
     */
    void stop();

    /**
     * Gets whether or not this track is currently being played back
     * @return True if the track is being played
     */
    boolean isPlaying();
}
