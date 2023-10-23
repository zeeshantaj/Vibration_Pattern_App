package com.example.vibration_patter_maker_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private static final int PERMISSION_REQUEST_CODE = 123; // You can choose any value
    private Toolbar toolbar;
    long[] vibrationPatternArray;
    private EditText fileNameEdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton vibrationBtn = findViewById(R.id.vibrationBtn);

        // vibrationBtn = findViewById(R.id.vibrationBtn);
        playBtn = findViewById(R.id.playBtn);
        stopBtn = findViewById(R.id.stopBtn);
        toolbar = findViewById(R.id.toolbar);
        seekBar = findViewById(R.id.customSeek);
        fileNameEdt = findViewById(R.id.fileNameEdt);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrationPattern = new ArrayList<>();

        setSupportActionBar(toolbar);

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

        vibrationPatternArray = new long[waveForVibrate.size()];
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.saveFile){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, so request it
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                // Permission is already granted; perform file operations here

                if (vibrationPatternArray != null){
                    Toast.makeText(this, "file save", Toast.LENGTH_SHORT).show();

                    saveVibrationPattern(vibrationPatternArray );
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        if (Environment.isExternalStorageManager()) {
//                            // You have the necessary permissions to manage external storage
//
//                            saveVibrationPattern(vibrationPatternArray );
//                        } else {
//                            // Request permission from the user
//                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                            intent.addCategory("android.intent.category.DEFAULT");
//                            intent.setData(Uri.parse("package:" + getPackageName()));
//                            startActivityForResult(intent, 0);
//                        }


                }
                else {
                    Toast.makeText(this, "Please Create Vibration Pattern First then Click to Save", Toast.LENGTH_SHORT).show();
                }




            }
        }
        return false;
    }
    private void saveVibrationPattern(long[] pattern) {
        // Save the vibration pattern to the public directory
        File publicDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "VibrationPatterns");

        if (publicDirectory.exists() || publicDirectory.mkdirs()) {


            if (fileNameEdt.getText().toString().isEmpty()){
                Toast.makeText(this, "Write Name your file to save", Toast.LENGTH_SHORT).show();
                return;
            }
            // Create or open a file in the public directory
            File file = new File(publicDirectory, fileNameEdt.getText().toString()+".txt");

            try {
                // Convert the pattern to a comma-separated string
                StringBuilder patternString = new StringBuilder();
                for (long duration : pattern) {
                    patternString.append(duration).append(",");
                }

                // Write the pattern to the file
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(patternString.toString());
                fileWriter.close();

                // Successfully saved the file
                Log.i("Success", "Pattern saved successfully"+file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                // Handle the exception appropriately
            }
        } else {
            Toast.makeText(this, "Failed to create the public directory", Toast.LENGTH_SHORT).show();
            Log.e("Error", "Failed to create the public directory");
        }
    }

//    private void saveVibrationPattern(long[] pattern) {
//
//        // Define the directory and file name
//        //String directoryName = "nameFileHere";
//        if (fileNameEdt.getText().toString().isEmpty()){
//            Toast.makeText(this, "Name your File First", Toast.LENGTH_SHORT).show();
//            return;
//        }
////        String directoryName = "vibration_Pattern";
//        //String directoryName = fileNameEdt.getText().toString();
//      //  String fileName = fileNameEdt.getText().toString()+".txt";
//
//
//        // Convert the pattern to a comma-separated string
//        try {
//            // Create or open a file in the app's scoped storage directory
//            File file = new File(getExternalFilesDir(null), fileNameEdt.getText().toString()+".txt");
//
//            FileWriter fileWriter = new FileWriter(file);
//            StringBuilder patternString = new StringBuilder();
//
//            // Convert the pattern to a comma-separated string
//            for (long duration : pattern) {
//                patternString.append(duration).append(",");
//            }
//
//            // Write the pattern to the file
//            fileWriter.write(patternString.toString());
//            fileWriter.close();
//
//            // Successfully saved the file
//            Log.i("Success", "Pattern saved successfully"+file.getPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            // Handle the exception appropriately
//        }
//
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted; you can now perform file operations
            } else {
                // Permission denied; inform the user or handle it accordingly
                Toast.makeText(this, "Permission denied. App cannot save files to external storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}