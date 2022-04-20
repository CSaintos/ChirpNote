package com.example.chirpnote;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

public class Session extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

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

    public ObjectId get_id(){
        return _id;
    }

    public void set_id(ObjectId _id){
        this._id = _id;
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
}
