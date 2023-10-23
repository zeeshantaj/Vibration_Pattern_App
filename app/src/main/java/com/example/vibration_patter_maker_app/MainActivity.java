package com.example.vibration_patter_maker_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.SeekBar;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Vibrator vibrator;
    private List<Long> vibrationPattern;
    private boolean isRecording = false;
    private Handler handler = new Handler();
    private static final int VIBRATION_INTERVAL = 100;
    private Button playBtn,stopBtn;
    private long touchStartTime;

    private List<Long> touchTime = new ArrayList<>();
    private List<Long> waveForVibrate = new ArrayList<>();
    private List<Long> touchReleaseTime =new ArrayList<>();

    long touchEndTime;
    private long lastTouchEndTime = 0;
    private CustomSeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton vibrationBtn = findViewById(R.id.vibrationBtn);

        // vibrationBtn = findViewById(R.id.vibrationBtn);
        playBtn = findViewById(R.id.playBtn);
        stopBtn = findViewById(R.id.stopBtn);
        seekBar = findViewById(R.id.customSeek);
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
                            Log.e("touch", "touch");
                        if (touchEndTime == 0) {
                            touchEndTime = touchStartTime;
                        }
                            long dura = touchStartTime - touchEndTime;
                            Log.e("MyApp", "dura" + dura);
                            touchReleaseTime.add(dura);
                            return true;

                    case MotionEvent.ACTION_UP:
                        touchEndTime = System.currentTimeMillis();
                        long duration = touchEndTime - touchStartTime; // Calculate the touch duration
                        Log.e("touch", "release" + duration);
                        touchTime.add(duration);
                        //
                        vibrationPattern.add(duration * 1000); // Convert to microseconds and add to the list

                        stopRecordingAndSave();
                        return true;
                }
                return true;

            }
        });


        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
                //createWave();
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

        int maxSize = Math.max(touchReleaseTime.size(), touchTime.size());

        for (int i = 0; i < maxSize; i++) {
            Log.e("MyApp","touchTime"+touchTime.get(i));
            Log.e("MyApp","touchReleaseTime"+touchReleaseTime.get(i));
//            if (i % 2 == 0) {
//                // Add the timestamp at even positions (0, 2, 4, ...)
//                waveForVibrate.add(touchReleaseTime.get(i));
//            } else {
//                // Add the duration at odd positions (1, 3, 5, ...)
//                waveForVibrate.add(touchTime.get(i));
//            }

            if (i < touchTime.size()){
                waveForVibrate.add(touchReleaseTime.get(i));
            }
            if (i < touchReleaseTime.size()) {
                waveForVibrate.add(touchTime.get(i));
            }
         }

        Log.e("MyApp","waveSize"+waveForVibrate.size());

        long[] vibrationPatternArray = new long[waveForVibrate.size()];
        Log.e("MyApp","vibrationArraySize"+vibrationPatternArray.length);
        for (int i = 0; i < waveForVibrate.size(); i++) {
            vibrationPatternArray[i] = waveForVibrate.get(i);
            Log.e("MyApp","vibrationArraySizeElement"+vibrationPatternArray[i]);

        }

        seekBar.setArray(vibrationPatternArray);



        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationPatternArray, -1);
        }
    }

    private void stop() {
        vibrator.cancel();
        touchTime.clear();
        touchReleaseTime.clear();
        waveForVibrate.clear();
        touchEndTime = 0;
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