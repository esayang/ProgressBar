package com.easyang.smartprogressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import com.easyang.progresslib.SmartProgressBar;

public class MainActivity extends AppCompatActivity {

    private SmartProgressBar mSpbProgressBar1;
    private SmartProgressBar mSpbProgressBar2;
    private SmartProgressBar mSpbProgressBar3;
    private SmartProgressBar mSpbProgressBar4;
    private SmartProgressBar mSpbProgressBar5;
    private SeekBar mSbBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mSpbProgressBar1 = (SmartProgressBar) findViewById(R.id.spb_progress_bar_1);
        mSpbProgressBar2 = (SmartProgressBar) findViewById(R.id.spb_progress_bar_2);
        mSpbProgressBar3 = (SmartProgressBar) findViewById(R.id.spb_progress_bar_3);
        mSpbProgressBar4 = (SmartProgressBar) findViewById(R.id.spb_progress_bar_4);
        mSpbProgressBar5 = (SmartProgressBar) findViewById(R.id.spb_progress_bar_5);
        mSbBar = (SeekBar) findViewById(R.id.sb_bar);


        mSbBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSpbProgressBar1.setProgress(progress);
                mSpbProgressBar2.setProgress(progress);
                mSpbProgressBar3.setProgress(progress);
                mSpbProgressBar4.setProgress(progress);
                mSpbProgressBar5.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
