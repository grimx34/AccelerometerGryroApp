package com.sensors.sensortestapp;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
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

    private final File mStorageDir;

    private HandlerThread mWriterThread = null;
    private WorkerThreadHandler mThreadHandler = null;


    private final static int STOP_WRITING   = 1;
    private final static int WRITE_STRING   = 2;


    public DataRecorder() throws RuntimeException, IOException
    {
        if (!isExternalStorageWritable())
            throw new RuntimeException("Could not write to external storage");

        mStorageDir = getStorageDir("sensors");

        if (mStorageDir == null)
            throw new RuntimeException("Could not create write directory");

    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private File getStorageDir(String dirName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), dirName);
        if (!file.mkdirs() && !file.isDirectory()) {
            Log.e(TAG, "Directory " + file.getAbsolutePath() + " not created");
            file = null;
        }
        return file;
    }

    public void writeString(String string)
    {
        if (mThreadHandler == null || !mWriterThread.isAlive()) this.start();
        if (mThreadHandler == null) return;
        mThreadHandler.obtainMessage(WRITE_STRING, string).sendToTarget();
    }

    public void stop()
    {
        if (mWriterThread == null || mThreadHandler == null) return;
        mThreadHandler.sendEmptyMessage(STOP_WRITING);
        mWriterThread.quitSafely();
    }

    public void start()
    {
        if (mWriterThread != null && mWriterThread.isAlive())
        {
            Log.d(TAG, "Writer thread already running, won't restart");
            return;
        }

        mWriterThread = new HandlerThread("DataRecorderWorkerThread");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = "sensor_data_" + formatter.format(Calendar.getInstance().getTime()) + ".csv";

        File targetFile = new File(mStorageDir, filename);

        if (targetFile == null)
            throw new RuntimeException("Could not create file");

        mWriterThread.start();
        mThreadHandler = new WorkerThreadHandler(targetFile, mWriterThread.getLooper());
    }

    static class WorkerThreadHandler extends Handler
    {
        final private File mTargetFile;

        private FileWriter mFileWriter = null;
        private BufferedWriter mBufferedWriter = null;

        WorkerThreadHandler(File targetFile, Looper looper)
        {
            super(looper);
            mTargetFile = targetFile;

            startWriting();
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case STOP_WRITING:
                    stopWriting();
                    break;
                case WRITE_STRING:
                    String line = (String)msg.obj;
                    writeString(line);
                    break;
            }
        }

        private void stopWriting()
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

        private void startWriting()
        {
            if (mBufferedWriter != null || mFileWriter != null)
            {
                Log.d(TAG, "Cannot resume writing: Buffered writer or filewriter are not null");
                return;
            }
            try {
                mFileWriter = new FileWriter(mTargetFile.getAbsoluteFile(), true);
                mBufferedWriter = new BufferedWriter(mFileWriter);

                Log.d(TAG, "Succesfully opened file " + mTargetFile.getAbsolutePath() + " for writing.");
            }
            catch (IOException e)
            {
                Log.e(TAG, "Could not resume ");
                e.printStackTrace();
            }
        }

        private void writeString(String string)
        {
            if (mBufferedWriter == null)
                return;

            try {
                mBufferedWriter.write(string + "\n");
            } catch (IOException e) {
                Log.e(TAG, "Failed to write: " );
                e.printStackTrace();
            }
        }
    }
}