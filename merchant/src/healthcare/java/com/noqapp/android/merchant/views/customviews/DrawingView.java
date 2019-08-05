package com.noqapp.android.merchant.views.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class DrawingView extends AppCompatImageView implements View.OnTouchListener {
    private float downx = 0;
    private float downy = 0;
    private Canvas canvas;
    private Paint paint;

    public DrawingView(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    public void setNewImage(Bitmap alteredBitmap, Bitmap bmp, int pointerColor) {
        canvas = new Canvas(alteredBitmap);
        paint = new Paint();
        paint.setColor(pointerColor);
        paint.setStrokeWidth(5);
        canvas.drawBitmap(bmp, new Matrix(), paint);
        setImageBitmap(alteredBitmap);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        float upx = 0;
        float upy = 0;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = getPointerCoordinates(event)[0];
                downy = getPointerCoordinates(event)[1];
                break;
            case MotionEvent.ACTION_MOVE:
                upx = getPointerCoordinates(event)[0];
                upy = getPointerCoordinates(event)[1];
                canvas.drawLine(downx, downy, upx, upy, paint);
                invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = getPointerCoordinates(event)[0];
                upy = getPointerCoordinates(event)[1];
                canvas.drawLine(downx, downy, upx, upy, paint);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    final float[] getPointerCoordinates(MotionEvent e) {
        final int index = e.getActionIndex();
        final float[] coords = new float[]{e.getX(index), e.getY(index)};
        Matrix matrix = new Matrix();
        getImageMatrix().invert(matrix);
        matrix.postTranslate(getScrollX(), getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }
}