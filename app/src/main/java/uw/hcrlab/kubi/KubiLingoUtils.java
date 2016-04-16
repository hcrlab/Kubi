package uw.hcrlab.kubi;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

public class KubiLingoUtils {

    /*
    Equivalent to R.layout.foo, where foo is the id of some layout element and
    "foo" is available as a string at runtime.
     */
    public static View getViewByIdString(String name, View view, Fragment fragment) {
        int cardResourceId = fragment.getResources().getIdentifier(
                name, "id", fragment.getActivity().getPackageName());
        View target = view.findViewById(cardResourceId);
        if (target == null) {
            Log.i(fragment.getTag(), String.format("getViewByStringID can't find '%s'", name));
        }

        return target;
    }

    public static Drawable getDrawableByString(String name, Fragment fragment) {
        int drawableId = fragment.getResources()
                .getIdentifier(name, "drawable", fragment.getActivity().getPackageName());
        Drawable drawable = fragment.getResources().getDrawable(drawableId);
        if (drawable == null) {
            Log.i(fragment.getTag(), "getDrawableByString can't find " + name);
        }
        return drawable;
    }

}
