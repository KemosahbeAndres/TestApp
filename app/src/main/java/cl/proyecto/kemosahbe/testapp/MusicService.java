package cl.proyecto.kemosahbe.testapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class MusicService extends Service{
    MediaPlayer mp;
    Messenger sMessenger, aMessenger;
    Boolean stoped = false;
    Task mTask;
    //Constantes
    static final int CMD_STOP = 0;
    static final int CMD_PLAY = 1;
    static final int CMD_PAUSE = 2;

    @Override
    public void onCreate(){
        HandlerThread thread = new HandlerThread("Music Service");
        thread.start();
        Toast.makeText(this, "Servicio Iniciado", Toast.LENGTH_SHORT).show();
        mp = MediaPlayer.create(this, R.raw.thefatrat_fly_away_feat_anjulie);
        sMessenger = new Messenger(new mHandler());

    }

    @Override
    public IBinder onBind(Intent intent) {
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
        Toast.makeText(this, "Deteniendo Servicio", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }
    public void timeloop(){
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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class mHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
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
                case 55:
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
            while(mp.isPlaying()) timeloop();
            return true;
        }
    }
}
