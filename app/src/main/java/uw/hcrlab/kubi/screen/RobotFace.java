package uw.hcrlab.kubi.screen;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RobotFace extends SurfaceView implements SurfaceHolder.Callback {
	private static String TAG = OldRobotFace.class.getSimpleName();
	private RobotEye leftEye;
	private RobotEye rightEye;

	// The width & height of the view (set when the surface is initially created)
	private int screenWidth;
	private int screenHeight;

	public RobotFace(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Tell the SurfaceHolder to receive SurfaceHolder callback
		getHolder().addCallback(this);	
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		PointF leftEyeCoordinate = new PointF(screenWidth * ScreenConstants.LEFT_EYE_HORIZONTAL_FACTOR,
				screenHeight * ScreenConstants.EYE_VETICAL_FACTOR);
		PointF rightEyeCoordinate = new PointF(screenWidth * ScreenConstants.RIGHT_EYE_HORIZONTAL_FACTOR,
				screenHeight * ScreenConstants.EYE_VETICAL_FACTOR);
		// Initialize the eyes (this assumes the screen has been measured)
		leftEye = new RobotEye(leftEyeCoordinate, ScreenConstants.DEFAULT_EYE_RADIUS, EyeSide.LEFT);
		rightEye = new RobotEye(rightEyeCoordinate, ScreenConstants.DEFAULT_EYE_RADIUS, EyeSide.RIGHT);

		RobotFaceUtils.drawFace(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
		RobotFaceUtils.showAction(this, FacialAction.BLINK);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

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
