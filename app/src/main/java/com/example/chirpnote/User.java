package com.example.chirpnote;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import org.bson.types.ObjectId;

public class User extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    private String email;

    private String name;

    private String password;

    private String username;
	
	public User(){}

    public ObjectId get_id(){
		return _id;
	}
	
    public void set_id(ObjectId _id){
		this._id = _id;
	}

    public String getEmail(){
		return email;
	}
	
    public void setEmail(String email){
		this.email = email;
	}

    public String getName(){
		return name;
	}
	
    public void setName(String name){
		this.name = name;
	}

    public String getPassword(){
		return password;
	}
	
    public void setPassword(String password){
		this.password = password;
	}

    public String getUsername(){
		return username;
	}
	
    public void setUsername(String username){
		this.username = username;
	}
}
