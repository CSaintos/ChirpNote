package com.example.chirpnote;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class WaveformView extends View {
    public WaveformView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.GREEN);
        screenWidth = (float) context.getResources().getDisplayMetrics().widthPixels;
        maxSpike = (int) (screenWidth / (width + distance));
    }
    private Paint paint;
    private ArrayList<Float> amplitudeList = new ArrayList<>();
    private ArrayList<RectF> amplitudeSpikes = new ArrayList<>();
    private float radius = 6;
    private float width = 9;
    private float screenWidth;
    private float screenHeight = 400;
    private float distance = 6;
    private int maxSpike;

    public void insertAmplitude(float amplitude){
        amplitudeList.add(amplitude);

        amplitudeSpikes.clear();
        ArrayList<Float> amps = (ArrayList<Float>) amplitudeList.subList(maxSpike,amplitudeList.size()+1);

        for (float f:amps){
            int i = amplitudeList.indexOf(f);
            float left = screenWidth - i* (width + distance);
            float top = screenHeight/2 - amps.get(i)/2;
            float right = left + width;
            float bottom = top + amps.get(i);
            amplitudeSpikes.add(new RectF(left,top,right,bottom));

        }


    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (RectF a: amplitudeSpikes){
            canvas.drawRoundRect(a,radius,radius,paint);
        }
    }
}