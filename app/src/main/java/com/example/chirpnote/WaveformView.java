package com.example.chirpnote;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * This class represents the custom View used in the XML for the Record Audio Activity
 */
public class WaveformView extends View {

    /**
     * Initializer constructor
     * @param context this
     * @param attrs attributes
     */
    public WaveformView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        screenWidth = (float) context.getResources().getDisplayMetrics().widthPixels;
        maxSpike = (int) (screenWidth / (width + distance));
        this.paint = new Paint();
        paint.setColor(Color.GREEN);
    }

    /**
     * You need to make two different constructors to be compatible for the view
     * @param context this
     */
    public WaveformView(Context context){
        super(context);
        this.paint = new Paint();
        paint.setColor(Color.GREEN);

    }


    //various items related to calculations with screen
    private Paint paint;
    private ArrayList<Float> amplitudeList = new ArrayList<>();
    private ArrayList<RectF> amplitudeSpikes = new ArrayList<>();
    private float radius = 6;
    private float width = 9;
    private float screenWidth;
    private float screenHeight = 400;
    private float distance = 6;
    private int maxSpike;
    ArrayList<Float> amps = new ArrayList<>();

    /**
     * This method does all the work related to amplification sizes per listen and storage for draw()
     * @param amplitude the amplitude being returned by the recording
     */
    public void insertAmplitude(float amplitude){
        amps.clear();
        float normalization = (float) Math.min((int) amplitude/7,400);
        amplitudeList.add(normalization);
        amplitudeSpikes.clear();
        amps = new ArrayList<Float>(amplitudeList.subList(amplitudeList.size() - Math.min(amplitudeList.size(),maxSpike), amplitudeList.size()));

        for (float f:amps){
            int i = amps.indexOf(f);
            float left = screenWidth - i* (width + distance);
            float top = screenHeight/2 - amps.get(i)/2;
            float right = left + width;
            float bottom = top + amps.get(i);
            amplitudeSpikes.add(new RectF(left,top,right,bottom));

        }
        //invalidate needed for changing drawing elements
        invalidate();
    }

    /**
     * Draw Method
     * @param canvas the canvas of the view
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < amplitudeSpikes.size()-1;i++){
            canvas.drawRoundRect(amplitudeSpikes.get(i),radius,radius,paint);

        }
    }
}