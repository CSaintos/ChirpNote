package com.example.chirpnote;

import android.util.Base64;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Session extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    private String midiFile;

    private String audioFile;

    @Required
    private RealmList<String> chords;

    private String key;

    @Required
    private RealmList<String> melodyElements;

    private String name;

    private Integer nextMelodyTick;

    @Required
    private RealmList<String> percussionPatterns;

    private Integer tempo;

    @Required
    private RealmList<Integer> trackVolumes;

    private String username;

    public Session(){}

    /**
     * A Session that can be stored in a Realm object
     * @param session The session whose data to use to create the Realm Session
     */
    public Session(ChirpNoteSession session){
        // No need to set any of the track lists or files, as they will be empty when a session is first created
        set_id(session.getId());
        setName(session.getName());
        setKey(Key.encode(session.getKey()));
        setTempo(session.getTempo());
        setTrackVolumes(listToRealmList(session.mTrackVolumes));
        setUsername(session.getUsername());
    }

    public ObjectId get_id(){
        return _id;
    }

    public void set_id(ObjectId _id){
        this._id = _id;
    }

    public String getMidiFile(){
        return midiFile;
    }

    public void setMidiFile(String midiFile) {
        this.midiFile = midiFile;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public RealmList<String> getChords(){
        return chords;
    }

    public void setChords(RealmList<String> chords){
        this.chords = chords;
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key){
        this.key = key;
    }

    public RealmList<String> getMelodyElements(){
        return melodyElements;
    }

    public void setMelodyElements(RealmList<String> melodyElements){
        this.melodyElements = melodyElements;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Integer getNextMelodyTick(){
        return nextMelodyTick;
    }

    public void setNextMelodyTick(Integer nextMelodyTick){
        this.nextMelodyTick = nextMelodyTick;
    }

    public RealmList<String> getPercussionPatterns(){
        return percussionPatterns;
    }

    public void setPercussionPatterns(RealmList<String> percussionPatterns){
        this.percussionPatterns = percussionPatterns;
    }

    public Integer getTempo(){
        return tempo;
    }

    public void setTempo(Integer tempo){
        this.tempo = tempo;
    }

    public RealmList<Integer> getTrackVolumes(){
        return trackVolumes;
    }

    public void setTrackVolumes(RealmList<Integer> trackVolumes){
        this.trackVolumes = trackVolumes;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Converts the given list to a Realm list
     * @param list The list to convert
     * @return The realm list containing the elements from the given list
     */
    public <T> RealmList<T> listToRealmList(List<T> list){
        RealmList<T> realmList = new RealmList<>();
        for(T item : list){
            realmList.add(item);
        }
        return realmList;
    }

    /**
     * Encodes the file at the given path using the Base64 String encoding
     * @param filePath The path of the file to encode
     * @return The encoded Base64 string
     * @exception IOException if the file could not be read
     */
    public String encodeFile(String filePath) {
        File file = new File(filePath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * Writes the given encoded file to the given file path
     * @param encodedFile The Base64 encoding of the file to write
     * @param filePath The path to write the file to
     */
    public void writeEncodedFile(String encodedFile, String filePath){
        byte[] bytes = Base64.decode(encodedFile, Base64.NO_WRAP);
        File file = new File(filePath);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(bytes);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
