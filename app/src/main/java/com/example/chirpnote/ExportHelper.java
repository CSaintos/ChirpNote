package com.example.chirpnote;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.List;

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
    public void exportToDrive(Context context, File sharedFile) {
        boolean found = false;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("*/*");
        Uri aURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+ ".provider",sharedFile);
        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(share, 0);
        for (ResolveInfo res:resInfo)
        System.out.println(res.activityInfo.packageName);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains("com.google.android.apps.docs") ||
                        info.activityInfo.name.toLowerCase().contains("com.google.android.apps.docs")) {
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.putExtra(Intent.EXTRA_STREAM,aURI);
                    share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return;

            context.startActivity(Intent.createChooser(share, "Export to Drive"));
        }
    }
}
