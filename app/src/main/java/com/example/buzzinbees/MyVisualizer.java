package com.example.buzzinbees;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.util.LinkedList;


public class MyVisualizer extends View{

    //set up the drawing variables
    private Paint mForePaint = new Paint();
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();



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

    public void scaleHexagons( ImageView hex1,ImageView hex2,ImageView hex3,ImageView hex4){
        hex1.setScaleX(0.7087f);
        hex1.setScaleY(0.7087f);

        hex2.setScaleX(0.5748f);
        hex2.setScaleY(0.5748f);

        hex3.setScaleX(0.5354f);
        hex3.setScaleY(0.5354f);

        hex4.setScaleX(0.3386f);
        hex4.setScaleY(0.3386f);
    }
    public void scaleHexagons1(ImageView hex,float value){
        hex.setScaleX(value);
        hex.setScaleY(value);
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