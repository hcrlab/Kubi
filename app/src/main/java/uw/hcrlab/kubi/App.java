package uw.hcrlab.kubi;

import android.app.Application;
import android.content.Context;

import com.firebase.client.Firebase;

/**
 * Created by Alexander on 4/9/2015.
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        Firebase.setAndroidContext(this);
    }

    public static Context getContext(){
        return mContext;
    }
}
