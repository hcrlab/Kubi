package uw.hcrlab.kubi.screen;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;

/**
 * TODO: document your custom view class.
 */
public class EyesView extends View {

    private int mScreenHeight;
    private int mScreenWidth;

    private int mRadius;

    private float mLX;
    private float mRX;
    private float mY;

    private ShapeDrawable mPupilDrawable;

    public EyesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPupilDrawable = new ShapeDrawable(new OvalShape());
        mPupilDrawable.getPaint().setColor(0xff74AC23);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mScreenHeight = h;
        mScreenWidth = w;

        mRadius = mScreenHeight/6;

        mLX = mScreenWidth/2 - 1.25f * mRadius;
        mRX = mScreenWidth/2 + 1.25f * mRadius;
        mY = mRadius + 0.25f * mRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPupilDrawable.draw(canvas);
    }

    public void go() {
        mPupilDrawable.setBounds((int)mLX - mRadius, (int)mY - mRadius, (int)mLX + 2 * mRadius, (int)mY + 2 * mRadius);

        ValueAnimator anim = ValueAnimator.ofInt(0, 20);
        anim.setInterpolator(new AnticipateOvershootInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();

                mPupilDrawable.setBounds((int)mLX - mRadius, (int)mY - mRadius, (int)mLX + 2 * mRadius, (int)mY + 2 * mRadius);
            }
        });
        anim.setDuration(500);
        anim.start();
    }
}
