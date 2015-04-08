package uw.hcrlab.kubi.screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Observer;

public class RobotFace extends SurfaceView implements SurfaceHolder.Callback {
	private RobotEye leftEye;
	private RobotEye rightEye;

	// The width & height of the view (set when the surface is initially created)
	private int screenWidth;
	private int screenHeight;

    /* observers that listen to onTouchEvent */
    private static ArrayList<Observer> observers = new ArrayList<Observer>();

	public RobotFace(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Tell the SurfaceHolder to receive SurfaceHolder callback
		getHolder().addCallback(this);	
	}

    /* allow others to listen to this class */
    public void addObserver(Observer ob) {
        observers.add(ob);
    }

    /* SurfaceView methods */

    /* Handles the view's behavior when something is touched on screen */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (Observer ob : observers) {
            ob.update(null, event);
        }
        return true;
    }

    /* Obtain the size (width and height) of the view to correctly position the eyes */
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        this.screenHeight = h;
        this.screenWidth = w;
    }

    /* SurfaceHolder.Callback methods */

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		PointF leftEyeCoordinate = new PointF(screenWidth * ScreenConstants.LEFT_EYE_HORIZONTAL_FACTOR,
				screenHeight * ScreenConstants.EYE_VETICAL_FACTOR);
		PointF rightEyeCoordinate = new PointF(screenWidth * ScreenConstants.RIGHT_EYE_HORIZONTAL_FACTOR,
				screenHeight * ScreenConstants.EYE_VETICAL_FACTOR);
		// Initialize the eyes (this assumes the screen has been measured)
		leftEye = new RobotEye(leftEyeCoordinate, ScreenConstants.DEFAULT_EYE_RADIUS, EyeSide.LEFT);
		rightEye = new RobotEye(rightEyeCoordinate, ScreenConstants.DEFAULT_EYE_RADIUS, EyeSide.RIGHT);

		RobotFaceUtils.drawFace(this, State.NORMAL);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
		RobotFaceUtils.showAction(this, Action.BLINK);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

    /* setters and getters for leftEye and rightEye */

	public RobotEye getLeftEye() {
		return leftEye;
	}

	public void setLeftEye(RobotEye leftEye) {
		this.leftEye = leftEye;
	}

	public RobotEye getRightEye() {
		return rightEye;
	}

	public void setRightEye(RobotEye rightEye) {
		this.rightEye = rightEye;
	}
}
