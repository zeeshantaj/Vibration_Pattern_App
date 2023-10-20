package com.example.vibration_patter_maker_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class MainActivity extends AppCompatActivity {

    private Button vibrationBtn;
    private Vibrator vibrator;
    private List<Long> vibrationPattern;
    private boolean isRecording = false;

    private Handler handler = new Handler();
    private static final int VIBRATION_INTERVAL = 100;
    private Button playBtn,stopBtn;
    private long touchStartTime;

    private List<Long> wave = new ArrayList<>();
    private List<Long> waveForVibrate = new ArrayList<>();
    private List<Long> interval=new ArrayList<>();

    long touchEndTime;

    long releaseTime = -1l;
    long pressTime=-1l;
    long duration1 = 1l;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton vibrationBtn = findViewById(R.id.vibrationBtn);

        // vibrationBtn = findViewById(R.id.vibrationBtn);
        playBtn = findViewById(R.id.playBtn);
        stopBtn = findViewById(R.id.stopBtn);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrationPattern = new ArrayList<>();

        // handle compose vibration button
//        vibrationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                long[] vibrationWaveFormDurationPattern = {0, 10, 200, 500, 700, 1000, 300, 200, 50, 10};
//
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//
//                    VibrationEffect vibrationEffect = VibrationEffect.createWaveform(vibrationWaveFormDurationPattern, -1);
//
//                    vibrator.cancel();
//
//                    vibrator.vibrate(vibrationEffect);
//                }
//            }
//        });
        vibrationBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startRecording();
                        touchStartTime = System.currentTimeMillis(); // Record the start time

                        //wave.add(dura);


                        pressTime = System.currentTimeMillis();
                        if(releaseTime != -1l)
                            duration1 = pressTime - releaseTime;
                        Log.e("MyApp","duration"+duration1);
                        return true;
                    case MotionEvent.ACTION_UP:
                        touchEndTime = System.currentTimeMillis();
                        long duration = touchEndTime - touchStartTime; // Calculate the touch duration

                        interval.add(duration);
                        wave.add(duration);
                        vibrationPattern.add(duration * 1000); // Convert to microseconds and add to the list
                        stopRecordingAndSave();
                        return true;
                }
                return true;
            }
        });
        //pulsator.stop();

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
                createWave();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });
//        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_anim);
//
//        // Set the animation listener to handle any post-animation actions
//        pulseAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                // Animation started
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                // Animation ended
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//                // Animation repeated
//            }
//        });
//
//        // Start the pulse animation
//        vibrationBtn.startAnimation(pulseAnimation);
    }


    private void createWave() {
        for (int i = 0; i < wave.size(); i++) {
            if (i % 2 == 0) {
                // Add the timestamp at even positions (0, 2, 4, ...)
                waveForVibrate.add(wave.get(i));
            } else {
                // Add the duration at odd positions (1, 3, 5, ...)
                waveForVibrate.add(interval.get(i));
            }

            Log.e("MyApp","waveForVibrateElements"+waveForVibrate.get(i));
        }
        Log.e("MyApp","waveForVibrate"+waveForVibrate.size());
    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            vibrationPattern.clear(); // Clear any previous patterns
            vibrate(); // Start the first vibration
        }
    }

    private void stopRecordingAndSave() {
        if (isRecording) {
            isRecording = false;
            vibrator.cancel(); // Stop the vibration
            // Save the recorded vibrationPattern to a file or use it as needed
        }
    }

    private void vibrate() {
        if (isRecording) {

            vibrator.vibrate(VIBRATION_INTERVAL); // Vibrate at a regular interval
            vibrationPattern.add((long) VIBRATION_INTERVAL); // Record the duration
            // Schedule the next vibration and pattern recording
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vibrate();
                }
            }, VIBRATION_INTERVAL);
        }
    }
    private void play(){

        createWave();
        Log.e("MyApp","waveForPlay"+waveForVibrate.size());
        long[] vibrationPatternArray = new long[waveForVibrate.size()];

        for (int i = 0; i < waveForVibrate.size(); i++) {
            vibrationPatternArray[i] = waveForVibrate.get(i);
            Log.e("MyApp","wave index "+vibrationPatternArray.length);
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationPatternArray, -1);
        }
    }

    private void stop() {
        vibrator.cancel();
        wave.clear();
        interval.clear();
        waveForVibrate.clear();
        ///vibrationPattern.clear();
    }
    private void startPulseAnimation(View view) {
        Animation pulseAnimation = new ScaleAnimation(1f, 1.1f, 1f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        pulseAnimation.setDuration(1000); // Adjust the duration as needed
        pulseAnimation.setRepeatMode(Animation.REVERSE);
        pulseAnimation.setRepeatCount(1);
        view.startAnimation(pulseAnimation);
    }
}