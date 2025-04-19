package in.amankumar110.chatapp.ui.customviews;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import androidx.appcompat.widget.AppCompatEditText;

public class ScrollableEditText extends AppCompatEditText {
    private VelocityTracker velocityTracker = null;
    private float lastY;
    private boolean isScrolling = false;
    private static final int MIN_FLING_VELOCITY = 500; // Minimum velocity for fling (pixels per second)

    public ScrollableEditText(Context context) {
        super(context);
        init();
    }

    public ScrollableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMaxLines(3);
        setVerticalScrollBarEnabled(true);
        setMovementMethod(ScrollingMovementMethod.getInstance());
        setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getLineCount() <= getMaxLines()) {
            // If content fits within 3 lines, let EditText handle it normally
            return super.onTouchEvent(event);
        }

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getY();
                isScrolling = false;
                return super.onTouchEvent(event);

            case MotionEvent.ACTION_MOVE:
                float deltaY = lastY - event.getY();
                lastY = event.getY();

                // Check if we should start scrolling
                if (Math.abs(deltaY) > 5) { // Small threshold to detect intentional scroll
                    isScrolling = true;
                    scrollBy(0, (int) deltaY);
                    return true; // Consume the event
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isScrolling) {
                    velocityTracker.computeCurrentVelocity(1000); // Pixels per second
                    float yVelocity = velocityTracker.getYVelocity();

                    if (Math.abs(yVelocity) > MIN_FLING_VELOCITY) {
                        fling((int) -yVelocity);
                    }
                    velocityTracker.recycle();
                    velocityTracker = null;
                    return true;
                }
                velocityTracker.recycle();
                velocityTracker = null;
                break;
        }
        return super.onTouchEvent(event);
    }

    private void fling(int velocityY) {
        // Simulate fling behavior
        scrollBy(0, velocityY / 10); // Adjust divisor for smoother/stronger fling
        post(() -> {
            if (Math.abs(velocityY) > MIN_FLING_VELOCITY) {
                fling(velocityY / 2); // Decelerate
            }
        });
    }
}