package com.example.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MusicService extends Service {
    private final IBinder binder = new LocalBinder();
    MediaPlayer reproductor;


    @Override
    //On destroy se invoca cuando se ejcuta stopService
    public void onDestroy()
    {
        Toast.makeText(this,"Servicio detenido",Toast.LENGTH_SHORT).show();
        reproductor.stop();
    }


    public class LocalBinder extends Binder {
        MusicService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setMusic(String music)
    {
        if(reproductor != null){
            reproductor.release();
        }

        if(music.equals("Sad")){
            reproductor = MediaPlayer.create(this, R.raw.sad_music);
            reproductor.setLooping(true);
            reproductor.start();
        }
        if(music.equals("Neutral")){
            reproductor = MediaPlayer.create(this, R.raw.neutral_music);
            reproductor.setLooping(true);
            reproductor.start();
        }
        if(music.equals("Motivational")){
            reproductor = MediaPlayer.create(this, R.raw.motivational_music);
            reproductor.setLooping(true);
            reproductor.start();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
