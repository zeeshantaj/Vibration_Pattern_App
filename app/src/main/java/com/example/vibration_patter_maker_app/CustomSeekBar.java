package com.example.vibration_patter_maker_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CustomSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    long[] values;
    public CustomSeekBar(@NonNull Context context) {
        super(context);
    }

    public CustomSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setArray(long[] values) {
        this.values = values;
        setMax(values.length - 1);
        invalidate();
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (values != null) {
            int width = getWidth();
            int height = getHeight();
            int thumbX = (int) (width * getProgress() / getMax());

            // Paint paint = new Paint();

//            for (int i = 0; i < values.length; i++) {
//
//                int darkColor = getResources().getColor(R.color.blue);
//                int lightColor = getResources().getColor(R.color.lightBlue);
//
//                int color = (i % 2 == 0) ? darkColor : lightColor;
//                paint.setColor(color);
//                float x = i * width / (values.length - 1);
//                canvas.drawRect(x, 0, x + width / (values.length - 1), height, paint);
//            }
            Paint paint = new Paint();

            for (int i = 0; i < values.length; i++) {
                int darkColor = ContextCompat.getColor(this.getContext(), R.color.blue);
                int lightColor = ContextCompat.getColor(this.getContext(), R.color.lightBlue);
                int color = (i % 2 == 0) ? darkColor : lightColor;
                paint.setColor(color);
                float x = i * width / getMax(); // Adjust based on max value
                canvas.drawRect(x, 0, x + width / getMax(), height, paint);
            }
            super.onDraw(canvas);
        }
    }
    public void setProgressForValue(int index, int progress) {
        if (index < values.length && index >= 0) {
            setProgress(progress);
        }
    }
}
