package cl.proyecto.kemosahbe.testapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class MusicService extends Service {
    MediaPlayer mp;
    Messenger sMessenger;
    Boolean stoped = false;
    //Constantes
    static final int CMD_STOP = 0;
    static final int CMD_PLAY = 1;
    static final int CMD_PAUSE = 2;


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Servicio Iniciado", Toast.LENGTH_SHORT).show();
        mp = MediaPlayer.create(this, R.raw.thefatrat_aly_away_feat_anjulie);
        sMessenger = new Messenger(new mHandler());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mp.stop();
        mp.release();
        Toast.makeText(this, "Deteniendo Servicio", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    class mHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Bundle mBundle = msg.getData();
            int cmd = mBundle.getInt("cmd");
            switch (cmd){
                case CMD_PLAY:
                    mp.start();
                    stoped = false;
                    break;
                case CMD_PAUSE:
                    if(!stoped) mp.pause();
                    break;
                case CMD_STOP:
                    mp.stop();
                    stoped = true;
                    try {
                        mp.prepareAsync();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
