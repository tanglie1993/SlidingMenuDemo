package tanglie.myapplication.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class TestContentViewGroup extends ViewGroup {

    private View content;
    private Scroller scroller;
    private Interpolator decelerateInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t + 1.0f;
        }
    };


    public TestContentViewGroup(Context context) {
        this(context, null);
        init(context);
    }

    public TestContentViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        scroller = new Scroller(context, decelerateInterpolator);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        content.layout(l, t, r, b);
    }

    public void setContent(View v) {
        if (content != null)
            this.removeView(content);
        content = v;
        addView(content, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, LayoutParams.MATCH_PARENT);
        final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, LayoutParams.MATCH_PARENT);
        content.measure(contentWidth, contentHeight);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
            postInvalidate();
        }
    }

    private VelocityTracker mVelocityTracker;

    private int mPointerId;

    private static final int PIXELS_PER_SECOND = 1000;
    private static final int MAX_VELOCITY = 100000;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("onTouchEvent");
        addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("onTouchEvent ACTION_DOWN");
                //求第一个触点的id， 此时可能有多个触点，但至少一个
                mPointerId = event.getPointerId(0);
                return true;
            case MotionEvent.ACTION_MOVE:
                System.out.println("onTouchEvent ACTION_MOVE");
                scrollTo((int) -event.getX(), 0);
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("onTouchEvent ACTION_UP");
                mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND, MAX_VELOCITY);
                final float velocityX = mVelocityTracker.getXVelocity(mPointerId);
                smoothScroll(-velocityX, -event.getX());
                releaseVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                releaseVelocityTracker();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void addMovement(final MotionEvent event) {
        if(null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if(null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void smoothScroll(final float velocityX, final float currentX) {
        scroller.startScroll((int) currentX, 0, -(int) currentX, 0);
        System.out.println("smoothScroll: velocityX " + velocityX + "currentX " + currentX);

    }

}
