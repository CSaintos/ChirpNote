package com.example.chirpnote;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class WaveformView extends View {
    public WaveformView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    private Paint paint;


    @Override
    public void draw(Canvas canvas) {
        paint.setColor(Color.GREEN);
        super.draw(canvas);
        canvas.drawRoundRect(new RectF(20f,30f,20+30f,30f+60f),6f,6f,paint);
    }
}