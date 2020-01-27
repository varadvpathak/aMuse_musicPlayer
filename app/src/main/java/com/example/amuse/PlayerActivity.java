package com.example.amuse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button btn_next,btn_prev,btn_pause;
    TextView songName;
    SeekBar songSeek;

    static MediaPlayer myMediaPlayer;
    int position;
    String sName;

    ArrayList<File> mySongs;
    Thread updateSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btn_next=(Button)findViewById(R.id.next);
        btn_pause=(Button)findViewById(R.id.pause);
        btn_prev=(Button)findViewById(R.id.prev);
        songName=(TextView)findViewById(R.id.songname);
        songSeek=(SeekBar)findViewById(R.id.seekBar);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateSeekbar = new Thread() {

            @Override
            public void run()
            {
                int totalDuration = myMediaPlayer.getDuration();
                int currentPosition = 0;
                 while(currentPosition<totalDuration)
                 {
                     try
                     {
                        sleep(500);
                        currentPosition = myMediaPlayer.getCurrentPosition();
                        songSeek.setProgress(currentPosition);
                     }
                     catch (InterruptedException e)
                     {
                         e.printStackTrace();
                     }
                 }
            }
        };

        if(myMediaPlayer!=null)
        {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs=(ArrayList)bundle.getParcelableArrayList("songs");
        sName = mySongs.get(position).getName().toString();

        String songNam = i.getStringExtra("songname");

        songName.setText(songNam);
        songName.setSelected(true);

        position= bundle.getInt("pos");

        Uri uri = Uri.parse(mySongs.get(position).toString());
        myMediaPlayer = MediaPlayer.create(getApplicationContext(),uri);

        myMediaPlayer.start();
        songSeek.setMax(myMediaPlayer.getDuration());

        updateSeekbar.start();

        songSeek.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songSeek.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);


        songSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeek.setMax(myMediaPlayer.getDuration());

                if(myMediaPlayer.isPlaying())
                {
                    btn_pause.setBackgroundResource(R.drawable.iconplay);
                    myMediaPlayer.pause();
                }
                else
                {
                    btn_pause.setBackgroundResource(R.drawable.pause);
                    myMediaPlayer.start();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();
                myMediaPlayer.release();
                position = (position + 1) % mySongs.size();

                Uri u = Uri.parse(mySongs.get(position).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                sName = mySongs.get(position).getName().toString();
                songName.setText(sName);
                myMediaPlayer.start();
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();
                myMediaPlayer.release();

                position = ((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                myMediaPlayer = MediaPlayer.create(getApplicationContext(),u);

                sName = mySongs.get(position).getName().toString();
                songName.setText(sName);
                myMediaPlayer.start();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
