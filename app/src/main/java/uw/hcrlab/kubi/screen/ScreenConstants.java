package uw.hcrlab.kubi.screen;

import android.graphics.Color;
import android.graphics.Paint.Style;

public class ScreenConstants {

	/* RobotEye constants */
	public static final float ANGRY_VERTICAL_RADIUS_FACTOR = 1f/4;
	public static final float HALF = 1f/2;
	public static final float SAD_VERTICAL_RADIUS_FACTOR = ANGRY_VERTICAL_RADIUS_FACTOR;
	public static final float HAPPY_COORDINATE_SHIFT_FACTOR = 1f/3;
	public final static Style FILL_STYLE = Style.FILL;
	public final static Style STROKE_STYLE = Style.STROKE;
	public final static int EYE_COLOR = Color.WHITE;
	public final static int IRIS_COLOR = Color.BLACK;
	public final static int BACKGROUND_COLOR = Color.BLACK;
	/* the angle in which we rotate the eye shape for some states */
	public final static int ALPHA = 20;

	/* RobotFace constants */
	public final static float DEFAULT_EYE_RADIUS = 200;
	public final static float LOOK_LIMIT_FACTOR = 2f/3;
}
