package uw.hcrlab.kubi.screen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.SurfaceHolder;

public class RobotFaceUtils {

	private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	/*
	 *  Redraws the whole face on the canvas.
	 *  Required when new settings for the eye size and locations are called.
	 */
	public static void drawFace(RobotFace robotFace) {
		Canvas canvas = null;
		SurfaceHolder holder = robotFace.getHolder();
		
		try {
			canvas = holder.lockCanvas();
			synchronized (holder) {
				// clear the screen
				canvas.drawColor(Constants.BACKGROUND_COLOR);
				setPaint(Constants.EYE_COLOR, Constants.FILL_STYLE);
				RobotEyeUtils.drawEye(canvas, paint, robotFace.getLeftEye(), State.NORMAL);
				RobotEyeUtils.drawEye(canvas, paint, robotFace.getRightEye(), State.NORMAL);
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
			case NBLINK:		showNormalBlink(face);	break;
			default:			break;
		}
	}

	private static void setPaint(int color, Style style) {
		paint.setColor(color);
		paint.setStyle(style);
	}

	private static void showLookRight(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();

		int limit = (int)(Constants.DEFAULT_EYE_RADIUS * Constants.LOOK_LIMIT_FACTOR);

		// move the pupil to the right
		for (int i = 0; i <= limit; i += 10 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), i, 0);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), i, 0);
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
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), i, 0);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), i, 0);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	private static void showLookLeft(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();

		int limit = (int)(Constants.DEFAULT_EYE_RADIUS * Constants.LOOK_LIMIT_FACTOR);

		// move the pupil to the left
		for (int i = 0; i <= limit; i += 10 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), -i, 0);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), -i, 0);
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
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), -i, 0);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), -i, 0);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	private static void showGuilty(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		int limit = (int)(Constants.DEFAULT_EYE_RADIUS * Constants.LOOK_LIMIT_FACTOR);

		// move the pupil down
		for (int j = 0; j > -limit; j-= 5){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), 0, -j);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), 0, -j);
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
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), i, j);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), i, j);
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
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), i, j);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), i, j);
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
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), 0, -j);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), 0, -j);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
		RobotEyeUtils.drawEye(canvas, paint, face.getRightEye(), State.NORMAL);
		// delay
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		showNormalBlink(face);
	}

	private static void showThink(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();

		int limit = (int)(Constants.DEFAULT_EYE_RADIUS * Constants.LOOK_LIMIT_FACTOR);

		// move the pupil up
		for (int j = 0; j < limit; j+= 5){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), 0, -j);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), 0, -j);
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
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), i, -j);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), i, -j);
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
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), i, -j);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), i, -j);
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
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveIris(canvas, paint, face.getLeftEye(), 0, -j);
					RobotEyeUtils.moveIris(canvas, paint, face.getRightEye(), 0, -j);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
		RobotEyeUtils.drawEye(canvas, paint, face.getRightEye(), State.NORMAL);
		
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
		for (int i = 0; i < (Constants.DEFAULT_EYE_RADIUS / 8); i += 10 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.expandEye(canvas, paint, face.getLeftEye(), i);
					RobotEyeUtils.expandEye(canvas, paint, face.getRightEye(), i);
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
				canvas.drawColor(Constants.BACKGROUND_COLOR);
				RobotEyeUtils.expandEye(canvas, paint, face.getLeftEye(), Constants.DEFAULT_EYE_RADIUS / 8);
				RobotEyeUtils.expandEye(canvas, paint, face.getRightEye(), Constants.DEFAULT_EYE_RADIUS / 8); 
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
		for (int i = (int) (Constants.DEFAULT_EYE_RADIUS / 8); i > 0; i -= 2) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.expandEye(canvas, paint, face.getLeftEye(), i);
					RobotEyeUtils.expandEye(canvas, paint, face.getRightEye(), i);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	// only used while sleeping (Emotion is SLEEP)
	private static void showWake(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		// goes back to normal
		for (int i = (int) (Constants.DEFAULT_EYE_RADIUS * 2); i > 0; i -= 20) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.moveUpperLids(canvas, paint, face.getLeftEye(), i);
					RobotEyeUtils.moveUpperLids(canvas, paint, face.getRightEye(), i); 
				}
			} catch (NullPointerException e) {
				// TODO:
				//Log.i(TAG, "Canvas is unavailable.");
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
		// TODO: draw normal
		// TODO
		//showNormalBlink();
	}

	// the functions below are only used while normal (Emotion is NORMAL)
	
	private static void showSleep(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		// closing eyes
		for (int i = 0; i < Constants.DEFAULT_EYE_RADIUS * 2; i += 5 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					//TODO
					//left_eye.moveUpperLidsAndPupil(canvas, i, 0, i/3);
					//right_eye.moveUpperLidsAndPupil(canvas, i, 0, i/3);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}				   	
		}
		//TODO
		//this.setEmotion(Emotion.SLEEP);
	}

	private static void showGaze(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		// for the oval, need to move the eye up by alpha first
		//TODO
/*		if (this.eye_shape == EyeShape.OVAL) {
			for (int i = 0; i <= 20 ; i++) {
				try {
					canvas = holder.lockCanvas();
					synchronized (holder) {
						canvas.drawColor(Constants.BACKGROUND_COLOR);
						left_eye.changeAngle(canvas, i);
						right_eye.changeAngle(canvas, -i);
					}
				} finally {
					if (canvas != null) {
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
			
			// delay
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
			
		// gazing
		for (int i = 0; i < Constants.DEFAULT_EYE_RADIUS * 3/4; i += 4 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					//TODO
					//left_eye.moveBothLids(canvas, i);
					//right_eye.moveBothLids(canvas, i); 
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
		for (int i = (int) (Constants.DEFAULT_EYE_RADIUS * 3/4); i > 0; i -= 5) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					//TODO
					//left_eye.moveBothLids(canvas, i);
					//right_eye.moveBothLids(canvas, i); 
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
		
		// for the oval eye, need to go back to normal from alpha
		//TODO
		/*if (this.eye_shape == EyeShape.OVAL) {
			for (int i = 20; i > 0 ; i--) {
				try {
					canvas = holder.lockCanvas();
					synchronized (holder) {
						canvas.drawColor(Constants.BACKGROUND_COLOR);
						left_eye.changeAngle(canvas, i);
						right_eye.changeAngle(canvas, -i);
					}
				} finally {
					if (canvas != null) {
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}*/

		RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
		RobotEyeUtils.drawEye(canvas, paint, face.getRightEye(), State.NORMAL);	
	}

	private static void showGiggle(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		// giggling
		for (int i = 0; i < 3; i++){
			//TODO
			//this.setEmotion(Emotion.HAPPY);
			// delay
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {	
					//TODO
					//left_eye.MoveEyeVertical(canvas, -50);
					//right_eye.MoveEyeVertical(canvas, -50);
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
		
				RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
		RobotEyeUtils.drawEye(canvas, paint, face.getRightEye(), State.NORMAL);	
	}

	private static void showNormalBlink(RobotFace face) {
		// TODO: uncomment the next 2 lines
		//showBlink();
		//showBlink();
		// delay
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// TODO: uncomment this
		//showBlink();
	}

	private static void showBlink(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		// closing eye
		for (int i = 0; i < Constants.DEFAULT_EYE_RADIUS * 2; i += 150 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					//TODO
					//left_eye.moveUpperLids(canvas, i);
					//right_eye.moveUpperLids(canvas, i); 
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}				   	
		}
		//TODO
		//this.setEmotion(Emotion.SLEEP);
		// delay
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// goes back to normal
		for (int i = (int) (Constants.DEFAULT_EYE_RADIUS * 2); i > 0; i -= 150) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					//TODO
					//left_eye.moveUpperLids(canvas, i);
					//right_eye.moveUpperLids(canvas, i); 
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}			
				RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
		RobotEyeUtils.drawEye(canvas, paint, face.getRightEye(), State.NORMAL);
	}

	private static void showWink(RobotFace face) {
		Canvas canvas = null;
		SurfaceHolder holder = face.getHolder();
		
		// closing the right eye
		for (int i = 0; i < Constants.DEFAULT_EYE_RADIUS * 2; i += 100 ){
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
					RobotEyeUtils.moveUpperLids(canvas, paint, face.getRightEye(), i); 
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
		//TODO
		//this.setEmotion(Emotion.WINK);
		// delay
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// goes back to normal
		for (int i = (int) (Constants.DEFAULT_EYE_RADIUS * 2); i > 0; i -= 100) {
			try {
				canvas = holder.lockCanvas();
				synchronized (holder) {
					// clear the screen
					canvas.drawColor(Constants.BACKGROUND_COLOR);
					RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
					RobotEyeUtils.moveUpperLids(canvas, paint, face.getRightEye(), i); 
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
		RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
		RobotEyeUtils.drawEye(canvas, paint, face.getRightEye(), State.NORMAL);
	}

	private static void showSmile(RobotFace face) {
		//TODO
		//this.setEmotion(Emotion.HAPPY);
	
		// delay
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//TODO
		//RobotEyeUtils.drawEye(canvas, paint, face.getLeftEye(), State.NORMAL);
		//RobotEyeUtils.drawEye(canvas, paint, face.getRightEye(), State.NORMAL);
	}

}
