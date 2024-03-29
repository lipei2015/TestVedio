package com.example.testvedio.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * 重新设置Surface的尺寸，防止预览变形
 */
public class ResizeAbleSurfaceView extends SurfaceView {
    private int mWidth = -1;
    private int mHeight = -1;

    public ResizeAbleSurfaceView(Context context) {
        super(context);
    }

    public ResizeAbleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeAbleSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (-1 == mWidth || -1 == mHeight) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(mWidth, mHeight);
        }
    }

    public void resize(int width, int height) {
        mWidth = width;
        mHeight = height;
        getHolder().setFixedSize(width, height);
        requestLayout();
        invalidate();
    }
}
