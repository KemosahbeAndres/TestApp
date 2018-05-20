//Comunicacion entre Actividad y Servicio.
//Uso de la clase Messenger.
package cl.proyecto.kemosahbe.testapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

public class msgService extends Service {
    static final int CMD_PLAY = 1;
    static final int CMD_STOP = 2;
    static final String MSG_MEDIA = "MEDIAPLAYER";
    MediaPlayer mp = MediaPlayer.create(this, R.raw.TheFatRat_Fly_Away_feat_Anjulie);
    //Messenger que envia el cliente.
    private Messenger mMessenger;
    //Messenger que envia el servicio.
    private Messenger outMessenger;
    public Looper mServiceLooper;

    @Override
    public void onCreate(){
        HandlerThread thread = new HandlerThread("Message Service");
        thread.start();
        mServiceLooper = thread.getLooper();
        mMessenger = new Messenger(new mHandler(mServiceLooper));
    }

    //Clase Handler maneja el mensaje enviado por el cliente.
    class mHandler extends Handler{
        mHandler(Looper looper){
            super(looper);
        }
        //HandleMessage maneja el mensaje del cliente.
        //y si es necesario, envia una respuesta.
        //Se produce una comunicacion bidireccional.
        @Override
        public void handleMessage(Message msg){
            Bundle bund = msg.getData();
            if(bund!=null){
                //Se prepara una respuesta en la variable 'response'.
                Message response = Message.obtain();
                Bundle paquete = new Bundle();
                if(bund.getInt(MSG_MEDIA) != -1){
                    playmedia(bund.getInt(MSG_MEDIA));
                }else {
                    paquete.putString("respuesta", bund.getString("id"));
                    response.setData(paquete);
                    try {
                        //Aqui se envia el mensaje de respuesta hacia el cliente.
                        outMessenger.send(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                notificacion();
            }
        }
    }
    void playmedia(int cmd){
        switch(cmd){
            case CMD_PLAY:
                mp.start();
                break;
            case CMD_STOP:
                mp.stop();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras != null){
            outMessenger = (Messenger) extras.get("MESSENGER");
        }
        return mMessenger.getBinder();
    }
    @Override
    public void onDestroy(){
        if(mp != null) mp.release();
    }
    public void notificacion(){
        //Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("dato","Jajajajaja funciono xD");
        //PendingIntent mIntent = PendingIntent.getActivity(this,0,intent,0);
        Notification.Builder not = new Notification.Builder(this);
        not.setContentTitle("Mi notificacion")
                .setContentText("Texto de la notificacion.")
                .setSmallIcon(R.drawable.bell)
                //.setLargeIcon(R.drawable.bell)
                .setAutoCancel(true);
        NotificationManager ntManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        try {
            ntManager.notify(0,not.build());
            Thread.sleep(3000);
            ntManager.cancel(0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
