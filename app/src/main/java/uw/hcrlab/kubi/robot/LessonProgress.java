package uw.hcrlab.kubi.robot;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import uw.hcrlab.kubi.App;
import uw.hcrlab.kubi.R;

/**
 * Created by Alexander on 4/25/2016.
 */
public class LessonProgress {
    private static String TAG = LessonProgress.class.getSimpleName();

    private ProgressBar pb;
    private TextSwitcher ts;

    private Integer taskCount = 1;

    private Firebase pref;
    private Firebase tref;

    public LessonProgress(final Activity activity, final int pbResId, int switcherResId) {
        this.pb = (ProgressBar) activity.findViewById(pbResId);
        this.ts = (TextSwitcher) activity.findViewById(switcherResId);

        this.ts.setInAnimation(AnimationUtils.loadAnimation(activity, R.anim.progress_text_in));
        this.ts.setOutAnimation(AnimationUtils.loadAnimation(activity, R.anim.progress_text_out));
        this.ts.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(activity);

                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(activity.getResources().getColor(R.color.white, null));

                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                lp.gravity = Gravity.CENTER;
                tv.setLayoutParams(lp);

                return tv;
            }
        });

        this.ts.setText(taskCount.toString());

        this.pref = App.getFirebase().child("progress");
        this.pref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                if(snap.exists()) {
                    setProgress(snap.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "The following Firebase error occurred when getting the device's progress:");
                Log.e(TAG, firebaseError.toString());
            }
        });

        this.tref = App.getFirebase().child("tasks");
        this.tref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                if(snap.exists()) {
                    ts.setText(snap.getValue(Integer.class).toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "The following Firebase error occurred when getting the device's current task count:");
                Log.e(TAG, firebaseError.toString());
            }
        });
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

        taskCount = 1;
        ts.setText(taskCount.toString());
    }

    public int getMaxProgress() {
        return pb.getMax();
    }

    public int getProgress() {
        return pb.getProgress();
    }
}
