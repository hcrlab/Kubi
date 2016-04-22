package uw.hcrlab.kubi.lesson;

import java.util.HashMap;
import java.util.Map;

import uw.hcrlab.kubi.R;

/**
 * Created by lrperlmu on 4/21/16.
 */
public class DrawableHelper {

    // TODO: Remove this when we get rid of the use of drawables for image content (i.e. when not debugging any more)
    public static int getIdFromString(String name) {
        Map<String, Integer> drawables = new HashMap<String, Integer>();
        drawables.put("APPLE", R.drawable.apple);
        drawables.put("BANANA", R.drawable.banana);
        drawables.put("GIRL", R.drawable.girl);
        drawables.put("BOY", R.drawable.boy);
        drawables.put("FRANCE", R.drawable.france);
        return drawables.get(name.toUpperCase());
    }

}
