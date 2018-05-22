package cl.proyecto.kemosahbe.testapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    Button play, pause, stop;
    SeekBar seek;
    TextView duration, time;
    Messenger musicService = null;
    Boolean mBound = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = (Button)findViewById(R.id.btnplay);
        pause = (Button)findViewById(R.id.btnpause);
        stop = (Button)findViewById(R.id.btnstop);
        seek = (SeekBar) findViewById(R.id.seekbar);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);

        duration = (TextView)findViewById(R.id.duracion);
        time = (TextView)findViewById(R.id.tiempo);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Message mMessage = Message.obtain();
                Bundle mBundle = new Bundle();
                mBundle.putInt("cmd",MusicService.CMD_SEEK);
                //Toast.makeText(MainActivity.this, ""+progress, Toast.LENGTH_SHORT).show();
                mBundle.putInt("progress",progress);
                mMessage.setData(mBundle);
                try{
                    if(fromUser) musicService.send(mMessage);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        //Intent intent = new Intent(this, msgService.class);
        Intent intent = new Intent(this, MusicService.class);
        //Log.i("DEBUG","Current Thread: "+Thread.currentThread().getName());
        //Log.i("DEBUG", "Thread ID: "+Thread.currentThread().getId());
        Messenger messenger = new Messenger(handler);
        try{
            intent.putExtra("MESSENGER",messenger);
            bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    @Override
    protected void onDestroy(){
        if(mBound){
            unbindService(mConnection);
            mBound = false;
        }
        super.onDestroy();
    }
    public void onClick(View v){
        Message mMessage = Message.obtain();
        Bundle mBundle = new Bundle();
        switch (v.getId()){
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
                seek.setProgress(0);
                time.setText("0:00");
                break;
            default:
                break;
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
    public String toTime(int mili){
        double segTotal = mili / 1000.0;
        int minInt = (int)(segTotal / 60);
        double segPorcen = ((segTotal / 60.0 - minInt) * 100.0);
        double segInt = (60.0 / 100.0) * segPorcen;
        if(Math.round(segInt)<10){
            return minInt + ":0" + Math.round(segInt);
        }else {
            return minInt + ":" + Math.round(segInt);
        }
        //return minInt + ":" + (int)segInt;
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            int[] mArray = {};
            if(data != null){
                mArray = data.getIntArray("timearray");
                duration.setText(toTime(mArray[0]));
                time.setText(toTime(mArray[1]));
                seek.setProgress(mArray[1]/1000);
            }
        }
    };
}
