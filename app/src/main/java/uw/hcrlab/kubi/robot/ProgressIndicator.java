package uw.hcrlab.kubi.robot;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;

/**
 * Created by Alexander on 4/25/2016.
 */
public class ProgressIndicator implements ValueEventListener {
    private static String TAG = ProgressIndicator.class.getSimpleName();

    private ProgressBar pb;
    private TextSwitcher ts;

    private Integer taskCount = 1;

    private Firebase ref;

    private static ProgressIndicator instance;

    public static ProgressIndicator getInstance(final Activity activity, final int pbResId, int switcherResId) {
        if(instance == null) {
            instance = new ProgressIndicator(activity, pbResId, switcherResId);
        }

        return instance;
    }

    private ProgressIndicator(final Activity activity, final int pbResId, int switcherResId) {
        this.pb = (ProgressBar) activity.findViewById(pbResId);
        this.ts = (TextSwitcher) activity.findViewById(switcherResId);

        this.ts.setInAnimation(AnimationUtils.loadAnimation(activity, R.anim.progress_text_in));
        this.ts.setOutAnimation(AnimationUtils.loadAnimation(activity, R.anim.progress_text_out));
        this.ts.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(activity);

                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(activity.getResources().getColor(R.color.black, null));
                tv.setTextSize(activity.getResources().getDimensionPixelSize(R.dimen.progress_text_size));

                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                lp.gravity = Gravity.CENTER;
                tv.setLayoutParams(lp);

                return tv;
            }
        });

        this.ts.setText(taskCount.toString());

        this.ref = App.getFirebase().child("state");
        this.ref.addValueEventListener(this);
    }

    public void cleanup() {
        this.ref.removeEventListener(this);
    }

    private void setProgress(int val) {
        int progress = pb.getProgress();

        if(progress == val) {
            // No update is necessary
            return;
        }

        if(progress == 100) {
            // TODO: Do something special since we have reached the end of the lesson...
            pb.setProgress(0);
            progress = 0;
        }

        ObjectAnimator animation = ObjectAnimator.ofInt (pb, "progress", progress, val);
        animation.setDuration (500);
        animation.setInterpolator (new AccelerateDecelerateInterpolator());
        animation.start ();
    }

    public void reset() {
        pb.setProgress(0);

        taskCount = 0;
        ts.setText(taskCount.toString());
    }

    public int getMaxProgress() {
        return pb.getMax();
    }

    public int getProgress() {
        return pb.getProgress();
    }

    @Override
    public void onDataChange(DataSnapshot snap) {
        if(snap.exists()) {
            int p = snap.child("percent").getValue(Integer.class);

            if(p != pb.getProgress()) {
                setProgress(p);
            }

            Integer tasks = snap.child("tasks").getValue(Integer.class);

            if(!tasks.equals(taskCount)) {
                taskCount = tasks;
                ts.setText(tasks.toString());
            }
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Log.e(TAG, "The following Firebase error occurred when getting the device's current progress state:");
        Log.e(TAG, firebaseError.toString());
    }
}
