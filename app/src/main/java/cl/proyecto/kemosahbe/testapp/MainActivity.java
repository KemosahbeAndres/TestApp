package cl.proyecto.kemosahbe.testapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn1, btn2, btn3;
    TextView text;
    private static final String tag = "App:";
    Messenger mService = null;
    Boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        btn2 = (Button)findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        btn3 = (Button)findViewById(R.id.btn3);
        btn3.setOnClickListener(this);
        Log.d(tag, "Hola Mundo.");
        text = (TextView)findViewById(R.id.txt);
        text.setText("Hola Mundo");
    }
    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this, msgService.class);
        Messenger messenger = new Messenger(handler);
        try{
            intent.putExtra("MESSENGER",messenger);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    @Override
    protected void onStop(){
        super.onStop();
        if(mBound){
            unbindService(mConnection);
            mBound = false;
        }
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn1:
                saludo("Boton1");
                break;
            case R.id.btn2:
                saludo("Boton2");
                break;
            case R.id.btn3:
                saludo("Boton 0003");
                break;
            default:
                break;
        }
    }

    public void saludo(String s) {
        if (!mBound) return;
        Message msg = Message.obtain();
        Bundle bund = new Bundle();
        bund.putString("id", s);
        msg.setData(bund);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void saludo(Bundle bundle){
        if (!mBound) return;
        Message msg = Message.obtain();
        msg.setData(bundle);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };
    private Handler handler = new Handler(){
      public void handleMessage(Message msg){
          Bundle data = msg.getData();
          if(data!=null) text.setText(data.getString("respuesta"));
      }
    };
}
