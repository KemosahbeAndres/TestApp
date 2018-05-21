package cl.proyecto.kemosahbe.testapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener{
    MediaPlayer mp;
    Messenger sMessenger, aMessenger;
    Boolean stoped = false;
    Task mTask;
    //Constantes
    static final int CMD_STOP = 0;
    static final int CMD_PLAY = 1;
    static final int CMD_PAUSE = 2;
    static final int CMD_SEEK = 3;


    @Override
    public void onCreate(){
        //Log.i("DEBUG","onCreate Service Thread: "+Thread.currentThread().getName());
        //Log.i("DEBUG","ID: "+Thread.currentThread().getId());
        HandlerThread hThread = new HandlerThread("MusicServiceThread");
        hThread.start();
        //Toast.makeText(this, "Servicio Iniciado", Toast.LENGTH_SHORT).show();
        mp = MediaPlayer.create(this, R.raw.thefatrat_fly_away_feat_anjulie);
        mp.setOnCompletionListener(this);
        sMessenger = new Messenger(new mHandler(hThread.getLooper()));
        //mHandler hService = new mHandler(hThread.getLooper());
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Log.i("DEBUG","Binded to Thread: "+Thread.currentThread().getName());
        //Log.i("DEBUG","ID: "+Thread.currentThread().getId());
        Bundle extras = intent.getExtras();
        if(extras != null) aMessenger = (Messenger) extras.get("MESSENGER");
        Bundle mbundle = new Bundle();
        Message msg = Message.obtain();
        int[] mInfo = {mp.getDuration(),mp.getCurrentPosition()};
        mbundle.putIntArray("timearray",mInfo);
        msg.setData(mbundle);
        try {
            aMessenger.send(msg);
        }catch(Exception e){
            e.printStackTrace();
        }
        return sMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mp.stop();
        mp.release();
        //Log.i("DEBUG","Servicio Unbinded");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        //Log.i("DEBUG","Servicio Destruido");
        super.onDestroy();
    }

    public void timeloop(Boolean wait){
            Bundle mbundle = new Bundle();
            Message msg = Message.obtain();
            int[] mInfo = {mp.getDuration(),mp.getCurrentPosition()};
            mbundle.putIntArray("timearray",mInfo);
            msg.setData(mbundle);
            try {
                aMessenger.send(msg);
            }catch(Exception e){
                e.printStackTrace();
            }
        try {
            if(wait) Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.seekTo(0);
        mediaPlayer.stop();
        stoped = true;
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        timeloop(false);
    }

    class mHandler extends Handler{
        public mHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            //Log.i("DEBUG","Message running on: "+Thread.currentThread().getName());
            //Log.i("DEBUG","ID: "+Thread.currentThread().getId());
            Bundle mBundle = msg.getData();
            int cmd = mBundle.getInt("cmd");
            switch (cmd){
                case CMD_PLAY:
                    mTask = new Task();
                    mTask.execute();
                    mp.start();
                    stoped = false;
                    break;
                case CMD_PAUSE:
                    if(!stoped) mp.pause();
                    break;
                case CMD_STOP:
                    mp.seekTo(0);
                    mp.stop();
                    stoped = true;
                    try {
                        mp.prepareAsync();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case CMD_SEEK:
                    mp.seekTo(mBundle.getInt("progress")*1000);
                    break;
                default:
                    break;
            }
        }
    }
    public class Task extends AsyncTask<Void,Integer,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            while(mp.isPlaying()) timeloop(true);
            return true;
        }
    }
}
