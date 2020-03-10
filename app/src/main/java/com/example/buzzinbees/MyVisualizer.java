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

    float HEX1MAX = 0.95f;
    float HEX2MAX = 0.80f;
    float HEX3MAX = 0.75f;
    float HEX4MAX = 0.55f;


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
        mForePaint.setStrokeCap(Paint.Cap.ROUND);
        mForePaint.setStrokeJoin(Paint.Join.BEVEL);
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


        float mapVal = value;

        switch(id){
            case(1):
                mapVal = map(value,-130f,130f,Constant.HEX1MIN,Constant.HEX1MAX,true);
                break;
            case(2):
                mapVal = map(value,-130f,130f,Constant.HEX2MIN,Constant.HEX2MAX,true);
                break;
            case(3):
                mapVal = map(value,-130f,130f,Constant.HEX3MIN,Constant.HEX3MAX,true);
                break;
            case(4):
                mapVal = map(value,-130f,130f,Constant.HEX4MIN,Constant.HEX4MAX,true);
                break;
        }
//        Log.d("FFT","Map Val:"+Float.toString(mapVal));

        float vX = lerp(sX,mapVal , 0.3f);
        float vY = lerp(sY,mapVal , 0.3f);

//        Log.d("FFT","["+Float.toString(vX)+","+Float.toString(vY)+"]");
        hex.setScaleX(vX);
        hex.setScaleY(vY);
    }

    float lerp(float start, float end, float percent)
    {
        return (start + percent * (end - start));
    }

    float map(float value , float inputMin, float inputMax , float outputMin, float outputMax, Boolean clamp ){
        //the bin value, the min and max to be mapped between the sent range
        float outVal = ((value - inputMin) / (inputMax - inputMin) * (outputMax - outputMin) + outputMin);

        if( clamp ){
            if(outputMax < outputMin){
                if( outVal < outputMax )outVal = outputMax;
                else if( outVal > outputMin )outVal = outputMin;
            }else{
                if( outVal > outputMax )outVal = outputMax;
                else if( outVal < outputMin )outVal = outputMin;
            }
        }
        return outVal;
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
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 4) / 128;
            //positioning line
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 4) / 128;
            //limiting line values
        }

        canvas.drawLines(mPoints, mForePaint);
    }

}