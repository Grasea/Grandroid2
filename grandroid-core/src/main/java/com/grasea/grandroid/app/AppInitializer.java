/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.app;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Rovers
 */
public class AppInitializer {

    protected Context context;
    protected File appStorageDir;
    protected File appCacheDir;
    protected File databaseDir;

    public AppInitializer(Context context) {
        this.context = context;
    }

    public File getAppStorageDir() {
        if (appStorageDir == null) {
            appStorageDir = context.getExternalFilesDir(null);
            Log.d("grandroid", "appStorageDir=" + appStorageDir.getAbsolutePath());
        }
        return appStorageDir;
    }

    public File getAppCacheDir() {
        if (appCacheDir == null) {
            appCacheDir = context.getExternalCacheDir();
            Log.d("grandroid", "appCacheDir=" + appCacheDir.getAbsolutePath());
        }
        return appCacheDir;
    }

    public File getDatabaseDir() {
        if (databaseDir == null) {
            databaseDir = new File("/data/data/" + context.getPackageName() + "/databases/");
        }
        return databaseDir;
    }

    /**
     *
     * @param assetName
     * @return stored path of the asset file
     */
    public String syncAsset(String assetName, File outputDir) throws IOException {
        File outputFile = new File(outputDir, assetName);

        if (!outputFile.exists()) {

            String outFileName = outputFile.getAbsolutePath();

            // Open your local db as the input stream
            InputStream myInput = context.getAssets().open(assetName);
            // Path to the just created empty db
            outputDir.mkdirs();
            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[myInput.available()];
            int read;
            while ((read = myInput.read(buffer)) != -1) {
                myOutput.write(buffer, 0, read);
            }

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        return outputFile.getAbsolutePath();
    }

    public void copyAssets(File outputDir) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("grandroid", "Failed to get asset file list.", e);
        }
        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(outputDir, filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("grandroid", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void downloadTo(String url, File outputDir, String fileName) throws MalformedURLException, FileNotFoundException, IOException {
        URL u = new URL(url);
        HttpURLConnection ucon = (HttpURLConnection) u.openConnection();
        ucon.setConnectTimeout(10000);
        ucon.setReadTimeout(60000);

        ucon.connect();
        InputStream myInput = ucon.getInputStream();

        File outputFile = new File(outputDir, fileName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outputFile);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
}
