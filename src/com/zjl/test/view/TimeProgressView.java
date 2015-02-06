package com.zjl.test.view;

import com.smartisan.zjl.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class TimeProgressView extends View {

    private static final int LINE_WIDTH = 8;
    private static final int OVAL_RADIUS = 20;
    private int mHeight;
    private int mWidth;
    private String mStart;
    private String mEnd;
    private String mCurrent;
    private String sale_begin = "产品起售";
    private String sale_end = "销售截止";
    private String product_end = "产品到期";
    
    private float mTextSize;
    private Paint mPaint;
    RectF mOval;
    public TimeProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOval = new RectF();
        mTextSize = context.getResources().getDimension(R.dimen.time_progress_view_textsize);
        mStart = "08-12";
        mEnd = "08-26";
        mCurrent = "12-31";
    }

    public void setTime(String start, String end, String current) {
        mStart = start;
        mEnd = end;
        mCurrent = current;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mHeight = h;
        mWidth = w;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(Color.BLACK);
        float length = mPaint.measureText(sale_begin);
        float time_length = mPaint.measureText(mStart);
        float offsetY = mHeight * 0.4f;
        canvas.drawText(sale_begin, 0, offsetY, mPaint);
        canvas.drawText(sale_end, mWidth / 2 - length / 2, offsetY, mPaint);
        canvas.drawText(product_end, mWidth - length -2, offsetY, mPaint);

        offsetY = mHeight * 0.6f;
        float offsetX = (length - time_length) / 2;
        canvas.drawText(mStart, offsetX, offsetY, mPaint);
        canvas.drawText(mEnd, offsetX + mWidth / 2 - length / 2, offsetY, mPaint);
        canvas.drawText(mCurrent, offsetX + mWidth - length -2, offsetY, mPaint);
        
        drawProgressView(canvas, length);
    }

    private void drawProgressView(Canvas canvas, float length) {
        float newWidth = mWidth - length + OVAL_RADIUS * 2;
        canvas.translate(length / 2 - OVAL_RADIUS, mHeight * 0.15f);
        mPaint.setStrokeWidth(LINE_WIDTH);
        mPaint.setColor(Color.GRAY);
        canvas.drawLine(OVAL_RADIUS, 0, newWidth - OVAL_RADIUS, 0, mPaint);

        mPaint.setColor(Color.BLUE);
        canvas.drawLine(OVAL_RADIUS, 0, (newWidth - OVAL_RADIUS) * 0.8f, 0, mPaint);

        mOval.set(0, -OVAL_RADIUS, OVAL_RADIUS * 2, OVAL_RADIUS);
        canvas.drawOval(mOval, mPaint);
        mOval.offset(newWidth / 2 - OVAL_RADIUS, 0);
        canvas.drawOval(mOval, mPaint);
        mOval.offset(newWidth / 2 - OVAL_RADIUS, 0);
        canvas.drawOval(mOval, mPaint);
    }
    
}
