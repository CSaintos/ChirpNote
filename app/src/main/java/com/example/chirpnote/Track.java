package com.example.chirpnote;

public interface Track {
    /**
     * Starts recording this track
     * @return False if the recording process could not be started
     */
    boolean startRecording();

    /**
     * Stops recording this track
     * @return False if the track's recording process could not be stopped
     */
    boolean stopRecording();

    /**
     * Gets whether or not this track is currently being recorded
     * @return True if the track is being recorded
     */
    boolean isRecording();

    /**
     * Gets whether or not this track is has been recorded
     * @return True if the track has been recorded
     */
    boolean isRecorded();

    /**
     * Plays this track
     * @return False if the track could not be played back
     */
    boolean play();

    /**
     * Stops playback of this track
     * @return False if the track's playback could not be stopped
     */
    boolean stop();

    /**
     * Gets whether or not this track is currently being played back
     * @return True if the track is being played
     */
    boolean isPlaying();
}
