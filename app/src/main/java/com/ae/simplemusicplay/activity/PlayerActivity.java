package com.ae.simplemusicplay.activity;

import android.app.Activity;
import android.os.Bundle;

import com.ae.simplemusicplay.R;
import com.ae.simplemusicplay.widgets.CircleImageView;
import com.ae.simplemusicplay.widgets.CircularSeekBar;

public class PlayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        CircleImageView image = (CircleImageView)findViewById(R.id.album_art);
        CircularSeekBar seekBar = (CircularSeekBar)findViewById(R.id.song_progress_circular);
        image.setImageResource(R.mipmap.test_icon);
        seekBar.setMax(100);
        seekBar.setProgress(25);
    }
}
