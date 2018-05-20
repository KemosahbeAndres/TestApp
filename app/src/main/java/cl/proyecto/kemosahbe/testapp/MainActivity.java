package cl.proyecto.kemosahbe.testapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    Button btn1, btn2, btn3, btn4;
    TextView text;
    private static final String tag = "App:";
    Messenger musicService = null;
    Boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        btn2 = (Button)findViewById(R.id.btnplay);
        btn2.setOnClickListener(this);
        btn3 = (Button)findViewById(R.id.btnstop);
        btn3.setOnClickListener(this);
        btn4 = (Button)findViewById(R.id.btnpause);
        btn4.setOnClickListener(this);
        Log.e(tag, "Hola Mundo.");
        text = (TextView)findViewById(R.id.txt);
        text.setText("Hola Mundo");


    }
    @Override
    protected void onStart(){
        super.onStart();
        //Intent intent = new Intent(this, msgService.class);
        Intent intent = new Intent(this, MusicService.class);
        //Messenger messenger = new Messenger(handler);
        try{
            //intent.putExtra("MESSENGER",messenger);
            //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
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
        Message mMessage = Message.obtain();
        Bundle mBundle = new Bundle();
        switch (v.getId()){
            case R.id.btn1:
                //sendcmd("Boton1");
                break;
            case R.id.btnplay:
                mBundle.putInt("cmd",MusicService.CMD_PLAY);
                mMessage.setData(mBundle);
                try{
                    musicService.send(mMessage);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.btnpause:
                mBundle.putInt("cmd",MusicService.CMD_PAUSE);
                mMessage.setData(mBundle);
                try{
                    musicService.send(mMessage);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.btnstop:
                mBundle.putInt("cmd",MusicService.CMD_STOP);
                mMessage.setData(mBundle);
                try{
                    musicService.send(mMessage);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
    public void sendcmd(String s) {
        if (!mBound) return;
        Message msg = Message.obtain();
        Bundle bund = new Bundle();
        bund.putString("id", s);
        msg.setData(bund);
        try {
            musicService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void sendcmd(Bundle bundle){
        if (!mBound) return;
        Message msg = Message.obtain();
        msg.setData(bundle);
        try {
            musicService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            mBound = false;
        }
    };
    //private Handler handler = new Handler(){
         // Bundle data = msg.getData();
          //if(data!=null) text.setText(data.getString("respuesta"));
      //}
    //};
}
