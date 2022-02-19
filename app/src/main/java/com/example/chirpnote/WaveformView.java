package com.example.chirpnote;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class WaveformView extends View {

    public WaveformView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //paint.setColor(Color.GREEN);
        screenWidth = (float) context.getResources().getDisplayMetrics().widthPixels;
        maxSpike = (int) (screenWidth / (width + distance));
        this.paint = new Paint();
        paint.setColor(Color.GREEN);
    }

    public WaveformView(Context context){
        super(context);
        this.paint = new Paint();
        paint.setColor(Color.GREEN);

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
        float normalization = (float) Math.min((int) amplitude/7,400);
        amplitudeList.add(normalization);
        amplitudeSpikes.clear();
        ArrayList<Float> amps = new ArrayList<Float>(amplitudeList.subList(amplitudeList.size() - Math.min(amplitudeList.size(),maxSpike), amplitudeList.size()));

        for (float f:amps){
            int i = amps.indexOf(f);
            float left = screenWidth - i* (width + distance);
            float top = screenHeight/2 - amps.get(i)/2;
            float right = left + width;
            float bottom = top + amps.get(i);
            amplitudeSpikes.add(new RectF(left,top,right,bottom));

        }
        amps.clear();
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (RectF a: amplitudeSpikes){
            canvas.drawRoundRect(a,radius,radius,paint);
        }
    }
}