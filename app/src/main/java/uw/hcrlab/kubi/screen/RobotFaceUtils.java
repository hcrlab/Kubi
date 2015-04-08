package uw.hcrlab.kubi.screen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * This class contains methods that animate the robot face action.
 * The animation happens in a constant speed, which is bad.
 * To improve, re-implement this class to show animation in a ease-in ease-out animation pattern.
 */
public class RobotFaceUtils {
    private static final String TAG = RobotFaceUtils.class.getSimpleName();
	private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	/*
	 *  Redraws the whole face on the canvas.
	 *  Required when new settings for the eye size and locations are called.
	 */
	public static void drawFace(RobotFace robotFace, State state) {
		Canvas canvas = null;
		SurfaceHolder holder = robotFace.getHolder();
		
		try {
			canvas = holder.lockCanvas();
			synchronized (holder) {
				// clear the screen
				canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
				setPaint(ScreenConstants.EYE_COLOR, ScreenConstants.FILL_STYLE);
                drawEyes(robotFace, canvas, state);
			}
		} finally {
			if (canvas != null) {
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

    public static void showAction(RobotFace face, Action action) {
		switch (action) {
			case SMILE: 		showSmile(face);		break;
			case WINK:			showWink(face);			break;
			case BLINK: 		showBlink(face);		break;
			case GIGGLE: 		showGiggle(face);		break;
			case GLARE:			showGaze(face);			break;
			case SLEEP:			showSleep(face);		break;
			case WAKE:			showWake(face);			break;
			case SURPRISED: 	showSurprised(face);	break;
			case THINK:			showThink(face);		break;
			case GUILTY:		showGuilty(face);		break;
			case LOOK_LEFT:		showLookLeft(face);		break;
			case LOOK_RIGHT:	showLookRight(face);	break;
			case NBLINK:		showNBlink(face);	break;
			default:			break;
		}
	}

    /* private methods */

    private static void drawEyes(RobotFace robotFace, Canvas canvas, State state) {
        RobotEyeUtils.drawEye(canvas, paint, robotFace.getLeftEye(), state);
        RobotEyeUtils.drawEye(canvas, paint, robotFace.getRightEye(), state);
    }

    private static void moveBothIris(RobotFace face, Canvas canvas, int i, int j) {
        RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), i, j);
        RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), i, j);
    }

    private static void expandBothEyes(RobotFace face, Canvas canvas, float i) {
        RobotEyeUtils.expandEye(canvas, paint, face.getLeftEye(), i);
        RobotEyeUtils.expandEye(canvas, paint, face.getRightEye(), i);
    }

    private static void moveBothUpperLids(RobotFace face, Canvas canvas, int i) {
        RobotEyeUtils.moveUpperLid(canvas, paint, face.getLeftEye(), i);
        RobotEyeUtils.moveUpperLid(canvas, paint, face.getRightEye(), i);
    }

	private static void setPaint(int color, Style style) {
		paint.setColor(color);
		paint.setStyle(style);
	}

	private static void showLookRight(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();

		int limit = (int)(ScreenConstants.DEFAULT_EYE_RADIUS * ScreenConstants.LOOK_LIMIT_FACTOR);

		// move the pupil to the right
		for (int i = 0; i <= limit; i += 10 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, i, 0);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		// delay
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// goes back to normal
		for (int i = limit; i >= 0; i -= 10) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, i, 0);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        drawFace(face, State.NORMAL);
	}

	private static void showLookLeft(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();

		int limit = (int)(ScreenConstants.DEFAULT_EYE_RADIUS * ScreenConstants.LOOK_LIMIT_FACTOR);

		// move the pupil to the left
		for (int i = 0; i <= limit; i += 10 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, -i, 0);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}				   	
		}
		
		// delay
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		// goes back to normal
		for (int i = limit; i >= 0; i -= 10) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, -i, 0);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        drawFace(face, State.NORMAL);
	}

	private static void showGuilty(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		int limit = (int)(ScreenConstants.DEFAULT_EYE_RADIUS * ScreenConstants.LOOK_LIMIT_FACTOR);

		// move the pupil down
		for (int j = 0; j > -limit; j-= 5){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, 0, -j);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		// move the pupil from the middle to the left
		for (int i = 0; i > -limit/3; i -= 1 ){
			int j = (int) Math.sqrt(limit * limit - Math.abs(i) * Math.abs(i));
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, i, j);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		// delay
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// move the pupil from the left to the middle
		for (int i = - limit/3; i < 0; i += 1 ){
			int j = (int) Math.sqrt(limit * limit - Math.abs(i) * Math.abs(i));
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, i, j);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
		
		// move the pupil up
		for (int j = -limit; j < 0; j+= 5){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, 0, -j);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        drawFace(face, State.NORMAL);

		// delay
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		showNBlink(face);
	}

    private static void showThink(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();

		int limit = (int)(ScreenConstants.DEFAULT_EYE_RADIUS * ScreenConstants.LOOK_LIMIT_FACTOR);

		// move the pupil up
		for (int j = 0; j < limit; j+= 5){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, 0, -j);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		// move the pupil from the middle to the right
		for (int i = 0; i < limit/3; i += 1 ){
			int j = (int) Math.sqrt(limit * limit - Math.abs(i) * Math.abs(i));
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, i, -j);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		// delay
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// move the pupil from the right back to the middle
		for (int i = limit/3; i < 0; i += 1 ){
			int j = (int) Math.sqrt(limit * limit - Math.abs(i) * Math.abs(i));
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, i, -j);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		// move the pupil down
		for (int j = limit; j > 0; j-= 5){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothIris(face, canvas, 0, -j);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        drawFace(face, State.NORMAL);
		
		// delay
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		showBlink(face);
	}

	private static void showSurprised(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();

		// expand the iris
		for (int i = 0; i < (ScreenConstants.DEFAULT_EYE_RADIUS / 8); i += 10 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    expandBothEyes(face, canvas, i);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		try {
			canvas = holder.lockCanvas();
			synchronized (holder) {
				// clear the screen
				canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
				expandBothEyes(face, canvas, ScreenConstants.DEFAULT_EYE_RADIUS / 8);
			}
		} finally {
			if (canvas != null) {
				holder.unlockCanvasAndPost(canvas);
			}
		}

		// delay
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// goes back to normal
		for (int i = (int) (ScreenConstants.DEFAULT_EYE_RADIUS / 8); i > 0; i -= 2) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    expandBothEyes(face, canvas, i);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        drawFace(face, State.NORMAL);
	}

    // only used while sleeping
	private static void showWake(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		// goes back to normal
		for (int i = (int) (ScreenConstants.DEFAULT_EYE_RADIUS * 2); i > 0; i -= 20) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothUpperLids(face, canvas, i);
				}
			} catch (NullPointerException e) {
				Log.i(TAG, "Canvas is unavailable.");
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        drawFace(face, State.NORMAL);
        showNBlink(face);
	}

    // the functions below are only used while normal
	
	private static void showSleep(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();

		// closing eyes
		for (int i = 0; i < ScreenConstants.DEFAULT_EYE_RADIUS * 2; i += 5 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    RobotEyeUtils.moveUpperLidsAndIri(canvas, paint, face.getLeftEye(), i, 0, i/3);
                    RobotEyeUtils.moveUpperLidsAndIri(canvas, paint, face.getRightEye(), i, 0, i/3);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        drawFace(face, State.SLEEP);
	}

	private static void showGaze(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
			
		// gazing
		for (int i = 0; i < ScreenConstants.DEFAULT_EYE_RADIUS * 3/4; i += 4 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
					RobotEyeUtils.moveBothLids(canvas, paint, face.getLeftEye(), i);
                    RobotEyeUtils.moveBothLids(canvas, paint, face.getRightEye(), i);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
		// delay
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// goes back to normal
		for (int i = (int) (ScreenConstants.DEFAULT_EYE_RADIUS * 3/4); i > 0; i -= 5) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    RobotEyeUtils.moveBothLids(canvas, paint, face.getLeftEye(), i);
                    RobotEyeUtils.moveBothLids(canvas, paint, face.getRightEye(), i);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        drawFace(face, State.NORMAL);
	}

	private static void showGiggle(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		// giggling
		for (int i = 0; i < 3; i++){

            drawFace(face, State.HAPPY);

			// delay
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
                    RobotEyeUtils.MoveEyeVertical(canvas, paint, face.getLeftEye(), -50);
                    RobotEyeUtils.MoveEyeVertical(canvas, paint, face.getRightEye(), -50);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
			
			// delay
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

        drawFace(face, State.NORMAL);
	}

	private static void showNBlink(RobotFace face) {
		showBlink(face);
		showBlink(face);

		// delay
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		showBlink(face);
	}

	private static void showBlink(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		// closing eye
		for (int i = 0; i < ScreenConstants.DEFAULT_EYE_RADIUS * 2; i += 150 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothUpperLids(face, canvas, i);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}				   	
		}

        drawFace(face, State.SLEEP);

		// delay
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// goes back to normal
		for (int i = (int) (ScreenConstants.DEFAULT_EYE_RADIUS * 2); i > 0; i -= 150) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
                    moveBothUpperLids(face, canvas, i);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        drawFace(face, State.NORMAL);
	}

	private static void showWink(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		// closing the right eye
		for (int i = 0; i < ScreenConstants.DEFAULT_EYE_RADIUS * 2; i += 100 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
					RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
					RobotEyeUtils.moveUpperLid(canvas, paint, face.getRightEye(), i);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
        RobotEyeUtils.drawEye(canvas, paint, face.getRightEye(), State.SLEEP);

		// delay
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// goes back to normal
		for (int i = (int) (ScreenConstants.DEFAULT_EYE_RADIUS * 2); i > 0; i -= 100) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(ScreenConstants.BACKGROUND_COLOR);
					RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
					RobotEyeUtils.moveUpperLid(canvas, paint, face.getRightEye(), i);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

        drawFace(face, State.NORMAL);
	}

	private static void showSmile(RobotFace face) {
        drawFace(face, State.HAPPY);
	
		// delay
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

        drawFace(face, State.NORMAL);
	}

}
