package com.zjl.test.filehelper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.zjl.test.R;

public class FlipperReaderActivity extends Activity implements OnTouchListener, OnGestureListener{

    private static final String TAG = FlipperReaderActivity.class.getName();
    private ViewFlipper flipper;
    private GestureDetector gesDetecor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flipper_layout);
        gesDetecor = new GestureDetector(this);
        flipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
        flipper.addView(addTextView("step 1"));
        flipper.addView(addTextView("step 2"));
        flipper.addView(addTextView("step 3"));
        flipper.addView(addTextView("step 4"));
        
    }


    private View addTextView(String content) {
        TextView tv = new TextView(this);
        tv.setText(content);
        tv.setTextSize(40);
        tv.setGravity(1);
        return tv;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch");
        return gesDetecor.onTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouchEvent");
        return gesDetecor.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling");
        if (e1.getX() - e2.getX() > 120) {
            //from right to left
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.left_out));
            this.flipper.showNext();
        } else if (e1.getX() - e2.getX() < -120) {
          //from left to right
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.right_out));
            this.flipper.showPrevious();
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        Log.d(TAG, "onScroll");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    
}
