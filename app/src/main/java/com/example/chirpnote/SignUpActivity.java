package com.example.chirpnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Document;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class SignUpActivity extends AppCompatActivity {

    String appID = "chirpnote-jwrci";
    App app;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection; // not sure we need this
    User user;
    EditText email, password;
    String emailStr, passwordStr;
    TextView AlreadyHaveAccountText;
    Button SignUpButton, LogInButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        app = new App(new AppConfiguration.Builder(appID).build());

        email = (EditText) findViewById(R.id.SignUpEmailText);
        password = (EditText) findViewById(R.id.SignUpPasswordText);
        SignUpButton = (Button) findViewById(R.id.SignUpButton);
        LogInButton = (Button) findViewById(R.id.LogInButton);
        AlreadyHaveAccountText = (TextView) findViewById(R.id.AlreadyHaveAccountText);

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //mongoClient = user.getMongoClient("mongodb-atlas"); // 13:36 in video, unable to access getMongoClient() because its using the User class we created in our project
            }
        });


    }
}