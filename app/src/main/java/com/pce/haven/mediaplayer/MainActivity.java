package com.pce.haven.mediaplayer;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private TextView leftTime;
    private TextView rightTime;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;
    private Thread thread;
    private SimpleDateFormat dateFormat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUI();
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                leftTime.setText(dateFormat.format(new Date(currentPosition)));
                rightTime.setText(dateFormat.format(new Date(duration-currentPosition)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Thread thread = new Thread();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.playButton:{
                if(mediaPlayer.isPlaying()){
                    pauseMusic();
                }
                else {
                    playMusic();
                }
            }
                break;
            case R.id.prevButton:{
                rewind();
            }
            break;
            case R.id.nextButton:{
                next();
            }
        }

    }

    public void setUpUI(){
        dateFormat = new SimpleDateFormat("mm:ss");
        seekBar  = findViewById(R.id.seekBar);
        leftTime = findViewById(R.id.leftTime);
        rightTime = findViewById(R.id.rightTime);
        prevButton = findViewById(R.id.prevButton);
        playButton = findViewById(R.id.playButton);
        nextButton = findViewById(R.id.nextButton);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.music);
        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

    }

    public void playMusic(){
        if(mediaPlayer != null){
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
            updateThread();
            mediaPlayer.start();
        }
    }

    public void pauseMusic(){
        if(mediaPlayer != null){
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
            mediaPlayer.pause();
        }
    }

    public void rewind(){
        if(mediaPlayer != null)
           mediaPlayer.seekTo(0);
    }

    public void next(){
        if(mediaPlayer != null){
            mediaPlayer.seekTo(mediaPlayer.getDuration()-1000);
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void updateThread(){
        thread = new Thread(){
            @Override
            public void run() {
                try{
                    while(mediaPlayer!=null && mediaPlayer.isPlaying()){
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);
                                leftTime.setText(dateFormat.format(new Date(mediaPlayer.getCurrentPosition())));
                                rightTime.setText(dateFormat.format(new Date(mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition())));
                            }
                        });
                    }

                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        thread.interrupt();
        mediaPlayer.release();
        super.onDestroy();
    }
}
