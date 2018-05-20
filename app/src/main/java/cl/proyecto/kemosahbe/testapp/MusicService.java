package cl.proyecto.kemosahbe.testapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class MusicService extends Service {
    MediaPlayer mp;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Servicio Iniciado", Toast.LENGTH_SHORT).show();
        mp = MediaPlayer.create(this, R.raw.thefatrat_aly_away_feat_anjulie);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mp.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        Toast.makeText(this, "Deteniendo Servicio", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
