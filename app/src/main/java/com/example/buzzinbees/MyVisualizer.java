package com.example.buzzinbees;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.core.math.MathUtils;

import java.util.LinkedList;


public class MyVisualizer extends View{

    //set up the drawing variables
    private Paint mForePaint = new Paint();
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();

    float HEX1SCALE = 0.7087f;
    float HEX2SCALE = 0.5748f;
    float HEX3SCALE = 0.5354f;
    float HEX4SCALE = 0.3386f;

    float HEX1MIN = 0.3544f;
    float HEX2MIN = 0.2874f;
    float HEX3MIN = 0.2667f;
    float HEX4MIN = 0.1693f;

    float HEX1MAX = 0.9087f;
    float HEX2MAX = 0.7748f;
    float HEX3MAX = 0.7354f;
    float HEX4MAX = 0.5386f;


    //constructors, do not delete!
    public MyVisualizer(Context context) {
        super(context);
        initialize();
    }
    public MyVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }
    public MyVisualizer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    //initialize drawing variables
    void initialize(){
        mBytes=null;
        mForePaint.setStrokeWidth(10f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(224, 181, 86));


    }

    //update the visualizer function
    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }


    public void scaleHexagons(ImageView hex,float value){
        hex.setScaleX(value);
        hex.setScaleY(value);
    }

    public void lerpScaleHexagons(ImageView hex,int id,float value){
        float sX = hex.getScaleX();
        float sY = hex.getScaleY();



        float vX = lerp(sX, sX*value, value);
        float vY = lerp(sY, sY*value, value);

        switch(id){
            case(1):
                vX = normalize(vX,HEX1MIN,HEX1MAX);
                vY = normalize(vY,HEX1MIN,HEX1MAX);
                break;
            case(2):
                vX = normalize(vX,HEX2MIN,HEX2MAX);
                vY = normalize(vY,HEX2MIN,HEX2MAX);
                break;
            case(3):
                vX = normalize(vX,HEX3MIN,HEX3MAX);
                vY = normalize(vY,HEX3MIN,HEX3MAX);
                break;
            case(4):
                vX = normalize(vX,HEX4MIN,HEX4MAX);
                vY = normalize(vY,HEX4MIN,HEX4MAX);
                break;
        }

        Log.d("FFT","["+Float.toString(vX)+","+Float.toString(vY)+"]");
        hex.setScaleX(vX);
        hex.setScaleY(vY);
    }

    float lerp(float a, float b, float f)
    {
        return ((a * (1.0f - f)) + (b * f));
    }

    float normalize(float f, float min, float max){
        float a = (max-min)/(1f- 0f);

        float b = 1f - (a * 1f);

        float mappedValue = (a * f) + b;

        return mappedValue;
    }

    //draw the waveform
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        mRect.set(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / 2
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
        }

        canvas.drawLines(mPoints, mForePaint);
    }

}