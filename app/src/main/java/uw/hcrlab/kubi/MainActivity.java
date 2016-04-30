package uw.hcrlab.kubi;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.IOException;
import java.util.List;

import uw.hcrlab.kubi.lesson.Prompt;
import uw.hcrlab.kubi.lesson.PromptData;
import uw.hcrlab.kubi.lesson.PromptTypes;
import uw.hcrlab.kubi.lesson.prompts.SelectPrompt;
import uw.hcrlab.kubi.robot.Robot;
import uw.hcrlab.kubi.screen.RobotFace;


public class MainActivity extends FragmentActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    /* Activity's Properties */
    private Robot robot;

    private SurfaceView eyesView;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating Main Activity ...");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Notice: this is how each activity will get the robot and connect it to the robot face
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        eyesView = (SurfaceView) findViewById(R.id.eyesView);

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.look_left_then_right);
        mp = new MediaPlayer();
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                            @Override
                            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                                Log.d(TAG, "Video size: " + width + ", " + height);
                            }
                        });

                        mediaPlayer.setDisplay(eyesView.getHolder());
                    }
                });

                mediaPlayer.seekTo(0);
            }
        });

        try {
            mp.setDataSource(this, video);
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize robot with UI components
        robot = Robot.Factory.create(this, R.id.face, R.id.prompt_container, R.id.thought_bubble, R.id.leftCard, R.id.rightCard);
    }

    VideoView eyes;

    @Override
    protected void onResume() {
        super.onResume();

//        eyes = (VideoView) findViewById(R.id.eyes);
//
//        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.look_left_then_right);
//        eyes.setVideoURI(video);
//        eyes.setZOrderOnTop(true);
//        eyes.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.seekTo(0);
//            }
//        });
//        eyes.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Log.d(TAG, "Eyes animation done!");
//            }
//        });

//        eyes.start();

        robot.startup();
        App.FbConnect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        App.FbDisconnect();
        robot.shutdown();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "Destroying Main Activity ...");
        super.onDestroy();

        robot.cleanup();
    }

    /* Touch events */
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "Screen touched ");
                // TODO: log to Firebase?

                Log.d(TAG, "Starting eye animation");
                //eyes.start();

                mp.start();

                if(robot.isHintOpen()) {
                    robot.hideHint();
                } else {
                    robot.showHint("Some hint text");
                }

                break;
            default:
                break;
        }
        return true;
    }
}
