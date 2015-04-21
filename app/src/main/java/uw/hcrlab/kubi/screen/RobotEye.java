package uw.hcrlab.kubi.screen;

import android.graphics.PointF;

/** This class represents a Robot Eye.
 *  A Robot Eye is defined with these properties:
 *  + the coordinate of the robot eye,
 *  + the coordinate of the iris,
 *  + the radius of the eye: radius,
 *  + the radius of the iris: irisRadius, and
 *  + the side of the eye: side (left/right).
 *  
 *  @author kimyen
 */
public class RobotEye {
	/* Coordinates of where the eye should be on the screen */
	private PointF coordinate;
	/* Coordinates of the inner eye ball currently */
	private PointF irisCoordinate;
	
	private float radius;
	private float irisRadius;

	/* either left or right eye */
	private EyeSide side;
	
	/* the ratio between the eye radius and the iris radius */
	private float ratio = 2.5f /6;
	
	public RobotEye(float radius, EyeSide side) {
		this(new PointF(0, 0), radius, side);
	}
	
	public RobotEye(PointF coordinate, float radius, EyeSide side){
		this.coordinate = coordinate;
		this.irisCoordinate = coordinate;
		this.radius = radius;
		this.irisRadius = radius * ratio ;
		this.side = side;
	}

    public RobotEye copy() {
        return this.copy();
    }

	public PointF getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(PointF coordinate) {
		this.coordinate = coordinate;
	}

	public PointF getIrisCoordinate() {
		return irisCoordinate;
	}

	public void setIrisCoordinate(PointF irisCoordinate) {
		this.irisCoordinate = irisCoordinate;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getIrisRadius() {
		return irisRadius;
	}

	public void setIrisRadius(float irisRadius) {
		this.irisRadius = irisRadius;
	}

	public EyeSide getSide() {
		return side;
	}

	public void setSide(EyeSide side) {
		this.side = side;
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
}
