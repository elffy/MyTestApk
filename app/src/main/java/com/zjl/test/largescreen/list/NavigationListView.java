package com.zjl.test.largescreen.list;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;


import com.zjl.test.R;

import java.util.HashSet;
import java.util.List;

/**
 * this listView mainly used for navigation in landscape mode.
 */
public class NavigationListView extends ListView /*implements MultiDeleteAnimation.MultiDeleteAnimationOperator*/ {

    /**
     * the checkBox's id, which in list item view.
     */
    private int mCheckboxId;
    private Listener mListener;
    private boolean mIsChecked = false;
    /**
     * Store the checkBox's position
     */
    private int[] mTempLoc = new int[2];

    private final int MISS = -1;
    private final int SELECT = 1;
    private final int SPECIAL = -2;
    private int mState = MISS;

    private HashSet<String> mDeletedHeaderSections;

    private final int DEFAULT_PRE_POSITION = -2;
    /**
     * Store the previous list item position, avoiding execute same operation
     * many times.
     */
    private int mPrePosition = DEFAULT_PRE_POSITION;

    private int mStartX;
    private int mEndX;

    /**
     * used to determine whether the slide select enable or not.
     */
    private boolean mSlideEnable;

    private TouchMonitorListener mTouchMonitorListener;
//    private ListViewCollapseRunner mCollapseRunner;
    private DrawListener mDrawListener;
    private Drawable mTopBoarder;

    public NavigationListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public NavigationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NavigationListView(Context context) {
        super(context);
    }

    private void init(Context context,AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SliderListView, 0, 0);
//        if (a != null) {
//            mCheckboxId = a.getResourceId(R.styleable.SliderListView_slider_checkbox_id, 0);
//            mSlideEnable = a.getBoolean(R.styleable.SliderListView_slider_enabled, false);
//            a.recycle();
//        }
        mScroller = new SelectScroller();
    }

    public interface Listener {
        void setChecked(int position, boolean isChecked);

        boolean isChecked(int position);
    }

    public interface DrawListener {
        void drawHeader(Canvas canvas);
    }

    public interface TouchMonitorListener {
        void onTouchActionUp();
    }

    public void setTouchMonitorListener(TouchMonitorListener listener) {
        mTouchMonitorListener = listener;
    }

    public void setDrawListener(DrawListener drawListener) {
        mDrawListener = drawListener;
    }

    public void setSlideListener(Listener listener) {
        mListener = listener;
    }

    public void setSlideEnable(boolean enable) {
        if (!enable) {
            recovery();
        }
        mSlideEnable = enable;
    }

    public boolean isSlideEnable() {
        return mSlideEnable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mSlideEnable) {
            return super.onInterceptTouchEvent(ev);
        }

        int position = startCheckPosition(ev);
        int action = ev.getActionMasked();

        // if the list item has checkBox and the touch spot is located in it,
        // it will intercept {@link MotionEvent#ACTION_DOWN}.
        if (position > MISS && action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        // if the touch spot can't be located in checkBox, it will invoke
        // {@link super#onInterceptTouchEvent(ev)}.
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mSlideEnable) {
            return super.onTouchEvent(ev);
        }
        int position = startCheckPosition(ev);

        int x = (int) ev.getX();
        int y = (int) ev.getY();
        mCurrPoint.set(x, y);

        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mDownPoint.set(x, y);
                // if the down point located in header view or other place, but checkbox area.
                // we will do noting, just invoke the super method.
                if (position == SPECIAL || (position == MISS && mState == MISS)) {
                    break;
                }

                onDown(position);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                boolean flag = position == SPECIAL || (position == MISS && mState == MISS);
                if (mState != SELECT && !flag) {
                    // if the down point don't located in checkbox area, then we move to the
                    // checkbox area, we should set the init-state for selection.
                    onDown(position);
                    return true;
                }

                if (mState == SELECT && position == SPECIAL) {
                    return true;
                } else if (flag) {
                    break;
                }

                continueSelect(x, y, position);
                return true;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                recovery();
                if (mTouchMonitorListener != null) {
                    mTouchMonitorListener.onTouchActionUp();
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    private int startCheckPosition(MotionEvent ev) {
        return viewIdHitPosition(ev, mCheckboxId);
    }

    private void onDown(int position) {
        // Avoid touch on Scrolling.
        if (mListener == null  || position <= MISS || mPrePosition == position) {
            return;
        }
        mState = SELECT;
        mIsChecked = mListener.isChecked(position);

        //click the checkBox quickly, it can't invoke {@link #onMovint(int position)}.
        //But we want the checkBox to change it's checkable. So if the condition is met,
        //the checkBox will change it's checkable when touch down.
        //See bug: 0008936
        onMoving(position);
    }

    private void onMoving(int position) {
        if (mListener == null || position <= MISS || mPrePosition == position) {
            return;
        }
        mPrePosition = position;
        mListener.setChecked(position, !mIsChecked);
    }

    private void recovery() {
        if (mState == MISS) {
            return;
        }

        if (mScroller.isScrolling()) {
            mScroller.stopScroll();
        }
        mIsChecked = false;
        mState = MISS;
        mPrePosition = DEFAULT_PRE_POSITION;
        mStartX = 0;
        mEndX = 0;
    }

    public boolean isSelecting() {
        return mState == SELECT;
    }

    /**
     * check the touch spot whether be located in checkBox or not.
     *
     * @param ev
     * @param id
     *            the resource id of checkBox.
     * @return if the touch spot is located in checkBox, it will return the
     *         occurred list item's position; Otherwise, it will return
     *         {@link MISS}
     */
    private int viewIdHitPosition(MotionEvent ev, int id) {

        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        // includes headers/footers
        int touchPos = pointToPosition(x, y);

        final int numHeaders = getHeaderViewsCount();
        final int numFooters = getFooterViewsCount();
        final int count = getCount();

        // We're only interested if the touch was on an item that's not a header
        // or footer.
        if (touchPos != AdapterView.INVALID_POSITION && touchPos >= numHeaders
          && touchPos < (count - numFooters)) {
            final int rawX = (int) ev.getRawX();


            final int rawY = (int) ev.getRawY();
            final View item = getChildAt(touchPos - getFirstVisiblePosition());

            View checkBox = id == 0 ? null : item.findViewById(id);
            if (checkBox != null && (checkBox.getVisibility() == View.VISIBLE)) {
                // if in select state, we only need consider the distance of X
                // direction.
                if (mState == SELECT && rawX > mStartX && rawX < mEndX) {
                    return touchPos;
                }

                checkBox.getLocationOnScreen(mTempLoc);

                if (rawX > mTempLoc[0] && rawY > mTempLoc[1]
                  && rawX < mTempLoc[0] + checkBox.getWidth()
                  && rawY < mTempLoc[1] + checkBox.getHeight()) {

                    mStartX = mTempLoc[0];
                    mEndX = mTempLoc[0] + checkBox.getWidth();

                    return touchPos;
                }
            } else if (checkBox == null && mState == SELECT && rawX > mStartX && rawX < mEndX) {
                //if the occurred item is headers/footers. it will not scroll and not return it's position.
                return SPECIAL;
            }
        }

        return MISS;
    }
    ///////////////////////////////////////////////////////////////////////////////////////

    private SelectScroller mScroller;

    /**
     * Determines the start of the upward select-scroll region
     * at the top of the ListView. Specified by a fraction
     * of the ListView height, thus screen resolution agnostic.
     */
    private float mSelectUpScrollStartFrac = 1.0f / 5.0f;

    /**
     * Determines the start of the downward select-scroll region
     * at the bottom of the ListView. Specified by a fraction
     * of the ListView height, thus screen resolution agnostic.
     */
    private float mSelectDownScrollStartFrac = 1.0f / 5.0f;

    /**
     * the speed cardinal number, determine the speed's value.
     */
    private final float SPEED_CARDINAL = 0.01f;


    private Point mDownPoint = new Point();
    private Point mCurrPoint = new Point();

    /**
     * the start of the downward select-scroll region.
     */
    private float mDownScrollStartY;

    /**
     * the start of the upward select-scroll region.
     */
    private float mUpScrollStartY;

    private void initScrollData() {
        final int padTop = getPaddingTop();
        final int listHeight = getHeight() - padTop - getPaddingBottom();
        float heightF = (float) listHeight;

        mUpScrollStartY = padTop + mSelectUpScrollStartFrac * heightF;
        mDownScrollStartY = padTop + (1.0f - mSelectDownScrollStartFrac) * heightF;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initScrollData();
    }

    private void continueSelect(int x, int y, int position) {
        int deltaY = y - mDownPoint.y;
        if (y > mDownScrollStartY && deltaY > 0) {
            mScroller.startScroll(SelectScroller.DOWN);
            return;
        } else if (y < mUpScrollStartY && deltaY < 0) {
            mScroller.startScroll(SelectScroller.UP);
            return;
        } else if (y > mUpScrollStartY && y < mDownScrollStartY){
            mScroller.stopScroll();
        }

        onMoving(position);
    }


    private final class SelectScroller implements Runnable {

        private boolean mAbort;
        private int mScrollDir;
        private boolean mScrolling = false;
        private float mScrollSpeed;

        private long mPrevTime;
        private long mCurrTime;

        public static final int UP = 1;
        public static final int DOWN = 2;

        public void startScroll(int dir) {
            if (!mScrolling) {
                mScrolling = true;
                mAbort = false;
                mScrollDir = dir;
                mPrevTime = SystemClock.uptimeMillis();
                post(this);
            }
        }

        public void stopScroll() {
            NavigationListView.this.removeCallbacks(this);
            mScrolling = false;
        }

        public boolean isScrolling() {
            return mScrolling;
        }

        @Override
        public void run() {
            if (mAbort) {
                return;
            }

            int first = getFirstVisiblePosition();
            int last = getLastVisiblePosition();
            int count = getCount();
            int padTop = getPaddingTop();
            final int listHeight = getHeight() - padTop - getPaddingBottom();

            performSelectAction();

            if (mScrollDir == UP) {
                View v = getChildAt(0);
                if (v == null || (first == 0 && v.getTop() == padTop)) {
                    mScrolling = false;
                    return;
                }

                int yDelta = mCurrPoint.y < padTop ? padTop : mCurrPoint.y;
                mScrollSpeed = (yDelta - mUpScrollStartY) * SPEED_CARDINAL;
            } else {
                View v = getChildAt(last - first);
                if (v == null || (last == count - 1 && v.getBottom() <= listHeight + padTop)) {
                    mScrolling = false;
                    return;
                }

                int y = getHeight() - getPaddingBottom();
                int yDelta = mCurrPoint.y > y ? y : mCurrPoint.y;
                mScrollSpeed = (yDelta - mDownScrollStartY) * SPEED_CARDINAL;
            }

            mCurrTime = SystemClock.uptimeMillis();
            float dt = mCurrTime - mPrevTime;
            int dy = Math.round(mScrollSpeed * dt);

            smoothScrollBy(dy, (int) dt);
            invalidate();

            mPrevTime = mCurrTime;
            post(this);
        }

        /**
         * select or unSelect the item when scrolling.
         */
        private void performSelectAction() {
            if (mCurrPoint.y < getPaddingTop()) {
                onMoving(getFirstVisiblePosition());
                return;
            } else if (mCurrPoint.y > (getHeight() - getPaddingBottom())) {
                onMoving(getLastVisiblePosition());
                return;
            }
            int position = pointToPosition(mCurrPoint.x, mCurrPoint.y);
            View child = getChildAt(position - getFirstVisiblePosition());
            if (child != null) {
                View cb = child.findViewById(mCheckboxId);
                if (cb != null) {
                    onMoving(position);
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
//        if (mCollapseRunner != null) {
//            boolean ended = mCollapseRunner.compute();
//            mCollapseRunner.draw(canvas);
//            invalidate();
//            if (ended) {
//                mCollapseRunner = null;
//            }
//            if(mDrawListener!=null)
//                mDrawListener.drawHeader(canvas);
//        } else {
            super.draw(canvas);

            if (getScrollY() < 0) {
                // on over scroll down, draw boarder on top
                if (mTopBoarder == null) {
                    mTopBoarder = getResources().getDrawable(R.drawable.dividing_line);
                }
                mTopBoarder.setBounds(0, -mTopBoarder.getIntrinsicHeight(), getWidth(), 0);
                mTopBoarder.draw(canvas);
            }
//        }
    }

    public void setDeletedHeaderSection(HashSet<String> headerSection) {
        mDeletedHeaderSections = headerSection;
    }

    private HashSet<Integer> mDeleteItemWithHeaders;
    private HashSet<Integer> mHeadersToDelete;
    public void setHeadersInfoForDelete(HashSet<Integer> deleteItemWithHeaders, HashSet<Integer> headersToDelete) {
        mDeleteItemWithHeaders = deleteItemWithHeaders;
        mHeadersToDelete = headersToDelete;
    }

    public void startDeleteAnimations(List<Integer> deletedPositions, Animation.AnimationListener animationListener) {
//        mCollapseRunner = new ListViewCollapseRunner(this, deletedPositions, animationListener);
//        mCollapseRunner.setInterpolator(CubicInterpolator.OUT);
//        mCollapseRunner.setDeletedHeaderSection(mDeletedHeaderSections);
//        mCollapseRunner.setHeadersInfoForDelete(mDeleteItemWithHeaders, mHeadersToDelete);
//        mCollapseRunner.start();
        invalidate();
    }
}
