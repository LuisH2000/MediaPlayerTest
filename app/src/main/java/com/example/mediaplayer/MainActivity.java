package com.example.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    MusicService mService;
    Button btnSad, btnNeutral, btnMotivational;
    EditText etVol;
    AudioManager audioManager;

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService.
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
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

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}