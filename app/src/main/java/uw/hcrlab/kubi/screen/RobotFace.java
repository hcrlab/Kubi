package uw.hcrlab.kubi.screen;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import uw.hcrlab.kubi.robot.FaceAction;
import uw.hcrlab.kubi.robot.State;

public class RobotFace extends SurfaceView implements SurfaceHolder.Callback {
    public static String TAG = RobotFace.class.getSimpleName();

	private RobotEye leftEye;
	private RobotEye rightEye;

	// The width & height of the view (set when the surface is initially created)
	private int screenWidth;
	private int screenHeight;
	private float radius;

	public RobotFace(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Tell the SurfaceHolder to receive SurfaceHolder callback
		getHolder().addCallback(this);	
	}

    /* SurfaceView methods */

    /* Obtain the size (width and height) of the view to correctly position the eyes */
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        this.screenHeight = h;
        this.screenWidth = w;
    }

    /* SurfaceHolder.Callback methods */

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        radius = this.screenHeight/6;
        float leftEyeXPosition = this.screenWidth/2 - 1.25f * radius;
        float rightEyeXPosition = this.screenWidth/2 + 1.25f * radius;
        float eyeYPosition = radius + 0.25f * radius;

        PointF leftEyeCoordinate = new PointF(leftEyeXPosition, eyeYPosition);
        PointF rightEyeCoordinate = new PointF(rightEyeXPosition, eyeYPosition);
        leftEye = new RobotEye(leftEyeCoordinate, radius, EyeSide.LEFT);
        rightEye = new RobotEye(rightEyeCoordinate, radius, EyeSide.RIGHT);

		RobotFaceUtils.drawFace(this, State.NORMAL);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
        //TODO: Should this occur here? All calls to showAction should happen from RobotThread
		RobotFaceUtils.showAction(this, FaceAction.BLINK);
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

	public float getEyeRadius() { return this.radius; }
}
