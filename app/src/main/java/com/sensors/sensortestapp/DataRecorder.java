package com.sensors.sensortestapp;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by js1 on 09/10/2016.
 */

public class DataRecorder {

    private static final String TAG = "DataRecorder";

    final private File mTargetFile;

    private FileWriter mFileWriter = null;
    private BufferedWriter mBufferedWriter = null;

    public DataRecorder() throws RuntimeException, IOException
    {
        if (!isExternalStorageWritable())
            throw new RuntimeException("Could not write to external storage");

        File storageDir = getStorageDir("sensors");

        if (storageDir == null)
            throw new RuntimeException("Could not create write directory");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = "sensor_data_" + formatter.format(Calendar.getInstance().getTime());

        mTargetFile = new File(storageDir, filename);

        if (mTargetFile == null)
            throw new RuntimeException("Could not create file");

        resumeWriting();

    }

    public void stopWriting()
    {
        try {
            mBufferedWriter.close();
            mFileWriter.close();

            mBufferedWriter = null;
            mFileWriter = null;
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not close: ");
            e.printStackTrace();
        }
    }

    public void resumeWriting()
    {
        if (mBufferedWriter != null || mFileWriter != null)
        {
            Log.d(TAG, "Cannot resume writing: Buffered writer or filewriter are not null");
            return;
        }
        try {
            mFileWriter = new FileWriter(mTargetFile.getAbsoluteFile(), true);
            mBufferedWriter = new BufferedWriter(mFileWriter);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not resume ");
            e.printStackTrace();
        }
    }

    public void writeString(String string)
    {
        if (mBufferedWriter == null)
            return;

        try {
            mBufferedWriter.write(string);
        } catch (IOException e) {
            Log.e(TAG, "Failed to write: " );
            e.printStackTrace();
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getStorageDir(String dirName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), dirName);
        if (!file.mkdirs() || file.isDirectory()) {
            Log.e(TAG, "Directory " + file.getAbsolutePath() + " not created");
            file = null;
        }
        return file;
    }





}
