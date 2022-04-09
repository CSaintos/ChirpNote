package com.example.chirpnote;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

public class ExportHelper {
    public ExportHelper(){}
    /**
     * Zips a session and its contents using Zip4j
     * @param directoryToZip The directory to zip
     */
    public void zipSession(File directoryToZip){
        try {
            //TODO change static session.zip to the session + session tracking indicator.zip so we maintain consistency
            new ZipFile(Environment.getDataDirectory() + "/session.zip").addFolder(directoryToZip);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    /**
     * Share a file using android's built in share intent
     * @param context the activity context
     * @param sharedFile the file to be shared
     */
    public void shareFile(Context context,File sharedFile){
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri audioURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+ ".provider",sharedFile);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM,audioURI);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "Share File:"));
    }
}
