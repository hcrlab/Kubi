package uw.hcrlab.kubi.screen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * This class contains methods that draw the robot eye.
 * 
 * @author kimyen
 *
 */
public class RobotEyeUtils {
	/**
	 * Draws a single eye-ball on the screen given the robot eye
	 * @param canvas
	 * @param paint
	 * @param robotEye - the robot eye to draw
	 */
	public static void drawEye(Canvas canvas, Paint paint, RobotEye robotEye, FacialState facialState) {
		paint.setStyle(ScreenConstants.FILL_STYLE);
		paint.setColor(ScreenConstants.EYE_COLOR);
		// Draw the outer shape of the eye
		drawEyeShape(canvas, paint, facialState, robotEye);
		
		// Draw the iris
		paint.setColor(ScreenConstants.IRIS_COLOR);
		canvas.drawCircle(robotEye.getIrisCoordinate().x, robotEye.getIrisCoordinate().y, 
				robotEye.getIrisRadius(), paint);
	}

	/** 
	 * Move the iris to look at a point at p.
	 * This method is only used with normal eye state.
	 */
	public static void lookAt(Canvas canvas, Paint paint, RobotEye robotEye, PointF p){
		
		drawEyeShape(canvas, paint, FacialState.NORMAL, robotEye);
					
		// Handle case where point is inside the eye
		// Calculate where the inner circle center should be 
		if(distanceBetweenPoints(robotEye.getIrisCoordinate(), p) <= robotEye.getRadius()){
			robotEye.setIrisCoordinate(p);
		} else {
			PointF irisLocation = calculateIrisLocation(robotEye, p);
			robotEye.setIrisCoordinate(irisLocation);
		}

		// Draw the iris
		paint.setColor(ScreenConstants.IRIS_COLOR);
		canvas.drawCircle(robotEye.getIrisCoordinate().x, robotEye.getIrisCoordinate().y, 
				robotEye.getIrisRadius(), paint);
	}

	// the functions below are only used while normal (Emotion is NORMAL)
	
	public static void moveLowerLids(Canvas canvas, Paint paint, RobotEye robotEye, int i) {
		paint.setColor(ScreenConstants.EYE_COLOR);
		// draw the top bound
		RectF rect = generateRectangle(robotEye);
		canvas.drawArc(rect, 180, 180, true, paint);
		// draw the bottom bound
		rect = generateRectangle(robotEye.getCoordinate().x, robotEye.getCoordinate().y,
				robotEye.getRadius(), Math.abs(robotEye.getRadius() - i));
		if (robotEye.getRadius() > i) {
			canvas.drawArc(rect, 0, 180, true, paint);
		} else {
			paint.setColor(ScreenConstants.BACKGROUND_COLOR);
			canvas.drawArc(rect, 180, 180, true, paint);
		}
		// draw iris
		drawIris(canvas, paint, robotEye);
	}

	public static void moveUpperLids(Canvas canvas, Paint paint, RobotEye robotEye, int i) {
		paint.setColor(ScreenConstants.EYE_COLOR);
		// draw the bottom bound
		RectF rect = generateRectangle(robotEye);
		canvas.drawArc(rect, 0, 180, true, paint);
		// draw the top bound
		rect = generateRectangle(robotEye.getCoordinate().x, robotEye.getCoordinate().y,
				robotEye.getRadius(), Math.abs(robotEye.getRadius() - i));
		if (robotEye.getRadius() > i) {
			canvas.drawArc(rect, 180, 180, true, paint);
		} else {
			paint.setColor(ScreenConstants.BACKGROUND_COLOR);
			canvas.drawArc(rect, 0, 180, true, paint);
		}
		// draw iris
		drawIris(canvas, paint, robotEye);
	}

	public static void moveBothLids(Canvas canvas, Paint paint, RobotEye robotEye, int i) {
		canvas.save();
		//canvas.rotate(ALPHA, robotEye.getCoordinate().x, robotEye.getCoordinate().y);
		paint.setColor(ScreenConstants.EYE_COLOR);
		RectF rect = generateRectangle(robotEye.getCoordinate().x, robotEye.getCoordinate().y,
				robotEye.getRadius(), Math.abs(robotEye.getRadius() - i));
		canvas.drawOval(rect, paint);
		// draw iris
		drawIris(canvas, paint, robotEye);
		//canvas.restore();
	}

	public static void drawIris(Canvas canvas, Paint paint, RobotEye robotEye) {
		paint.setColor(ScreenConstants.IRIS_COLOR);
		canvas.drawCircle(robotEye.getCoordinate().x, robotEye.getCoordinate().y, robotEye.getIrisRadius(), paint);
	}

	public static void drawEnlargePupil(Canvas canvas, Paint paint, RobotEye robotEye, float a, float b) {
		paint.setColor(ScreenConstants.IRIS_COLOR);
		canvas.drawCircle(robotEye.getCoordinate().x + a, robotEye.getCoordinate().y + b, robotEye.getIrisRadius(), paint);
	}

	public static void expandEye(Canvas canvas, Paint paint, RobotEye robotEye, float i) {
		RobotEye newEye = robotEye;
		newEye.setIrisRadius(robotEye.getIrisRadius() + i);
		newEye.setRadius(robotEye.getRadius() + i/2);
		drawEye(canvas, paint, newEye, FacialState.NORMAL);
	}

	public static void MoveEyeVertical(Canvas canvas, Paint paint, RobotEye robotEye, float i) {
		RobotEye newEye = robotEye;
		newEye.getCoordinate().set(robotEye.getCoordinate().x, robotEye.getCoordinate().y + i);
		drawEye(canvas, paint, newEye, FacialState.NORMAL);
	}

	public static void changeAngle(Canvas canvas, Paint paint, RobotEye robotEye, float alpha) {
		canvas.save();
		canvas.rotate(alpha, robotEye.getCoordinate().x, robotEye.getCoordinate().y);
		drawEye(canvas, paint, robotEye, FacialState.NORMAL);
		canvas.restore();
	}

	/* move the iris i to the right and j down */
	public static void moveIris(Canvas canvas, Paint paint, RobotEye robotEye, float i, float j) {
		RobotEye newEye = robotEye;
		newEye.getIrisCoordinate().set(robotEye.getIrisCoordinate().x + i, robotEye.getIrisCoordinate().y + j);
		drawEye(canvas, paint, newEye, FacialState.NORMAL);
	}

	/* move the iris x to the right and y down */
	public static void moveUpperLidsAndIris(Canvas canvas, Paint paint, RobotEye robotEye, float i, float x, float y) {
		paint.setColor(ScreenConstants.EYE_COLOR);
		// draw the bottom bound
		RectF rect = generateRectangle(robotEye);
		canvas.drawArc(rect, 0, 180, true, paint);
		// draw the top bound
		rect = generateRectangle(robotEye.getCoordinate().x, robotEye.getCoordinate().y,
				robotEye.getRadius(), Math.abs(robotEye.getRadius() - i));
		if (robotEye.getRadius() > i) {
			canvas.drawArc(rect, 180, 180, true, paint);
		} else {
			paint.setColor(ScreenConstants.BACKGROUND_COLOR);
			canvas.drawArc(rect, 0, 180, true, paint);
		}
		// draw iris
		drawEnlargePupil(canvas, paint, robotEye, x, y);
	}

	/*
	 * calculates the location the iris would be to perform looking at p
	 * @param x the location of the target x coordinate
	 * @param y the location of the target y coordinate
	 * @param p Center of the circle (x coordinate)
	 * @param q Center of the circle (y coordinate)
	 */
	private static PointF calculateIrisLocation(RobotEye robotEye, PointF p) {

		float a = robotEye.getCoordinate().x;
		float b = robotEye.getCoordinate().y;
		// Calculate the line 
		float m = ( b - p.y ) / ( a - p.x);
		float c =  p.y - m * p.x;

		// Calculate two solutions
		float r = robotEye.getRadius() - robotEye.getIrisRadius();

		// Math formulas to get intersection between a circle and a line
		float x1 = (float) (- 1 * m * c + m * b + a + Math.sqrt(Math.pow((m * c - m * b - a), 2) 
				- (m * m + 1) * ( b * b - r * r + a * a - 2 * c * b + c * c)));
		x1 /= (m * m + 1);

		float y1 = m * x1 + c;

		float x2 = (float) (- 1 * m * c + m * b + a - Math.sqrt(Math.pow((m * c - m * b - a), 2)
				- (m * m + 1) * ( b * b - r * r + a * a - 2 * c * b + c * c)));
		x2 /= (m * m + 1);

		float y2 = m * x2 + c;

		// Find the smallest distance from the center point (p, q)
		float dist1 = distanceBetweenPoints(x1, y1, p.x, p.y);
		float dist2 = distanceBetweenPoints(x2, y2, p.x, p.y);

		return dist1 < dist2 ? new PointF((int)x1, (int)y1) : new PointF((int)x2, (int)y2); 
	}

	/* calculate distance between 2 points p1, p2 */
	private static float distanceBetweenPoints(PointF p1, PointF p2) {
		return distanceBetweenPoints(p1.x, p1.y, p2.x, p2.y);
	}

	/* calculate distance between 2 points (x1, y1) and (x2, y2) */
	private static float distanceBetweenPoints(float x1, float y1, float x2, float y2 ){
		return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/* helper function that draw the shape of the eye with respect to the emotion of the robot eye */
	private static void drawEyeShape(Canvas canvas, Paint paint, FacialState facialState, RobotEye robotEye) {
		switch (facialState) {
			case NORMAL: 	drawNormalShape(canvas, paint, robotEye);		break;
			case HAPPY:		drawHappyShape(canvas, paint, robotEye);		break;
			case SAD:		drawSadShape(canvas, paint, robotEye);			break;
			case SLEEP:		drawSleepShape(canvas, paint, robotEye);		break;
			case ANGRY:		drawAngryShape(canvas, paint, robotEye);		break;
			default:		break;
		}
	}

	/*
	 * rotates the canvas ALPHA degree to the robotEye's side
	 * then draw an oval shape with small vertical radius
	 */
	private static void drawAngryShape(Canvas canvas, Paint paint, RobotEye robotEye) {
		int side = 1;
		if (robotEye.getSide() == EyeSide.LEFT) {
			side = -1;
		}
		canvas.save();
		canvas.rotate(side * ScreenConstants.ALPHA, robotEye.getCoordinate().x, robotEye.getCoordinate().y);
		drawOvalShape(canvas, paint, robotEye.getCoordinate(), robotEye.getRadius(), 
				ScreenConstants.ANGRY_VERTICAL_RADIUS_FACTOR * robotEye.getRadius());
		canvas.restore();
	}

	/*
	 * This method draws bottom half circle with stroke style
	 * TODO: test this method
	 */
	private static void drawSleepShape(Canvas canvas, Paint paint, RobotEye robotEye) {
		paint.setStyle(ScreenConstants.STROKE_STYLE);
		canvas.drawArc(generateRectangle(robotEye), 0, 180, false, paint);
	}

	/*
	 * This method draws complicated pieces of a sad eye shape:
	 * + an half oval bottom
	 * + an ~ kind of shape on the top
	 *  TODO: test this method
	 */
	private static void drawSadShape(Canvas canvas, Paint paint, RobotEye robotEye) {

		drawBottomHalfOval(canvas, paint, robotEye);

		int side = 1;
		if (robotEye.getSide() == EyeSide.LEFT) {
			side = -1;
		}

		float a = robotEye.getRadius();
		float b = ScreenConstants.SAD_VERTICAL_RADIUS_FACTOR * robotEye.getRadius();

		// draws an oval with half vertical radius
		drawOvalShape(canvas, paint, robotEye.getCoordinate(), a, b);

		// erase top side
		paint.setColor(ScreenConstants.BACKGROUND_COLOR);
		PointF newCoordinate = new PointF(robotEye.getCoordinate().x + ScreenConstants.HALF * a * side,
				robotEye.getCoordinate().y + b);
		drawRectangleShape(canvas, paint, newCoordinate, ScreenConstants.HALF * a, b);

		// draw top side
		paint.setColor(ScreenConstants.EYE_COLOR);
		RectF rect = generateRectangle(robotEye.getCoordinate().x, robotEye.getCoordinate().y + 2 * b, a, b);
		canvas.drawArc(rect, 225 + (side * 45), 90, true, paint);

		// draw bottom side
		rect = generateRectangle(robotEye.getCoordinate().x, robotEye.getCoordinate().y + 2 * b, a, 2 * b);
		canvas.drawArc(rect, 45 + ((-side) * 45), 90, true, paint);
	}

	/* draws the bottom half of an oval */
	private static void drawBottomHalfOval(Canvas canvas, Paint paint,
			RobotEye robotEye) {
		/* draw an arc starts at 0 degree (3 o'clock) in the clockwise direction,
		 * and fill in the area from the coordinate to the arc.
		 * The arc ends after sweeping through 180 degree (at 9 o'clock).
		 */
		canvas.drawArc(generateRectangle(robotEye), 0, 180, true, paint);
	}

	/*
	 * draws 2 circles:
	 * + a normal eye shape at robotEye.coordinate coordinate, and
	 * + another normal eye shape with the background color overlapping with the first shape, and
	 * with coordinate shifted down a factor or the vertical radius.
	 */
	private static void drawHappyShape(Canvas canvas, Paint paint, RobotEye robotEye) {
		drawOvalShape(canvas, paint, robotEye.getCoordinate(), robotEye.getRadius(), robotEye.getRadius());
		paint.setColor(ScreenConstants.BACKGROUND_COLOR);
		drawOvalShape(canvas, paint, shift(robotEye.getCoordinate(), 0, ScreenConstants.HAPPY_COORDINATE_SHIFT_FACTOR*robotEye.getRadius()),
				robotEye.getRadius(), robotEye.getRadius());
	}

	/* 
	 * draws a circle with eye color background color at 
	 * robotEye.coordinate coordinate and robotEye.radius radius.
	 */
	private static void drawNormalShape(Canvas canvas, Paint paint, RobotEye robotEye) {
		drawOvalShape(canvas, paint, robotEye.getCoordinate(), robotEye.getRadius(), robotEye.getRadius());
	}

	/*
	 *  draws an oval shape based on 
	 *  + the coordinate from PointF p, and
	 *  + horizontal radius a and vertical radius b
	 */
	private static void drawOvalShape(Canvas canvas, Paint paint, PointF p, float a, float b) {
		canvas.drawOval(generateRectangle(p.x, p.y, a, b), paint);
	}

	/* draws a rectangle based on
	 * + the coordinate from PointF p, and
	 * + horizontal radius a and vertical radius b
	 */
	private static void drawRectangleShape(Canvas canvas, Paint paint, PointF p, float a, float b) {
		canvas.drawRect(generateRectangle(p.x, p.y, a, b), paint);
	}

	/*
	 * shift x by a and y by b
	 */
	private static PointF shift(PointF coordinate, float a, float b) {
		return new PointF(coordinate.x + a, coordinate.y + b);
	}

	/* generates a rectangle that cover the robot eye */
	private static RectF generateRectangle(RobotEye robotEye) {
		return generateRectangle(robotEye.getCoordinate().x, robotEye.getCoordinate().y, 
				robotEye.getRadius(), robotEye.getRadius());
	}

	/* generates a rectangle using coordinate (x, y) and horizontal radius a and vertical radius b. */
	private static RectF generateRectangle(float x, float y, float a, float b) {
		float left 		= x - a;
		float right 	= x + a;
		float top 		= y - b;
		float bottom 	= y + b;
		return new RectF(left, top, right, bottom);
	}

}
