package com.example.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    MusicService mService;
    Button btnSad, btnNeutral, btnMotivational;
    EditText etVol;
    AudioManager audioManager;
    SeekBar volumeBar;
    private SensorManager mSensorManager;
    float lastProximitySensor;
    boolean firstReading = true;

    boolean mBound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnSad = findViewById(R.id.btn_sad);
        btnNeutral = findViewById(R.id.btn_neutral);
        btnMotivational = findViewById(R.id.btn_motivational);
        etVol = findViewById(R.id.et_vol);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeBar = findViewById(R.id.volume_bar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeBar.setProgress(volumeLevel);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService.
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
    }

        /** Defines callbacks for service binding, passed to bindService(). */
    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    public void startNeutralMusic(View view) {
        if (mBound)
        {
            mService.setMusic("Neutral");
        }
    }

    public void startMotivationalMusic(View view) {
        if (mBound)
        {
            mService.setMusic("Motivational");
        }
    }

    public void startSadMusic(View view) {
        if (mBound)
        {
            mService.setMusic("Sad");
        }
    }

    public void setVolume(View view) {
        String vol = etVol.getText().toString();
        int volume = Integer.parseInt(vol);

        showToast("Vol Max: " + audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0 );
        volumeBar.setProgress(volume);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP: {
                super.onKeyDown(keyCode, event);
                int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                volumeBar.setProgress(volumeLevel);
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    @Override
    protected void onDestroy()
    {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_PROXIMITY :
                    if(firstReading){
                        lastProximitySensor = event.values[0];
                        firstReading = false;
                    }

                    if(lastProximitySensor != event.values[0])
                        if(event.values[0] <= 0)
                            mService.playStopMusic();

                    lastProximitySensor = event.values[0];
                    break;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}