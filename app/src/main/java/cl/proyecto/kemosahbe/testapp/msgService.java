//Comunicacion entre Actividad y Servicio.
//Uso de la clase Messenger.
package cl.proyecto.kemosahbe.testapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.graphics.drawable.IconCompat;
import android.view.View;
import android.widget.Toast;

public class msgService extends Service {
    static final int MSG_HELLO = 1;
    //Messenger que envia el cliente.
    private Messenger mMessenger;
    //Messenger que envia el servicio.
    //ke pasas.
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
                paquete.putString("respuesta",bund.getString("id"));
                response.setData(paquete);
                try{
                    //Aqui se envia el mensaje de respuesta hacia el cliente.
                    outMessenger.send(response);
                }catch(Exception e){
                    e.printStackTrace();
                }
                notificacion();
            }
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
