package com.sensors.sensortestapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DataRecorder recorder = null;
    private static final int askForStoragePermission = 11;

    private SensorDataCollector sensorDataCollector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTest1 = (EditText) findViewById(R.id.editText);
                EditText editText2 = (EditText) findViewById(R.id.editText2);

                Log.d("BUTTON","Name: \""+editTest1.getText()+"\", Weight: "+editText2.getText()+"Kg");
                editTest1.setText("");
                editText2.setText("");
            }
        });

        sensorDataCollector = new SensorDataCollector((SensorManager) getSystemService(Context.SENSOR_SERVICE));

    }

    private void startRecording()
    {
        try {
            recorder = new DataRecorder();
            recorder.start();
            sensorDataCollector.setRecorder(recorder);
        }
        catch (Exception e)
        {
            Log.e("MAIN", "Could not create data recorder obj");
            e.printStackTrace();
            recorder = null;
        }
    }

    private void requestWritePermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "No write permissions yet.. requesting");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    askForStoragePermission);
        }
        else
        {
            startRecording();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case askForStoragePermission: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "Permission granted, starting recording..");
                    startRecording();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    requestWritePermission();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        sensorDataCollector.registerListeners();
        requestWritePermission();

    }

    protected void onPause() {
        super.onPause();

        sensorDataCollector.unRegisterListeners();
    }


}
