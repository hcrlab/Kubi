package uw.hcrlab.kubi.robot;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.HashMap;

import uw.hcrlab.kubi.R;
import uw.hcrlab.kubi.screen.RobotFace;

/**
 * Created by Alexander on 5/3/2016.
 */
public class Eyes extends FrameLayout {
    public static String TAG = Eyes.class.getSimpleName();

    private boolean surfaceExists = false;
    private boolean isPlaying = false;

    private SurfaceView surface;

    private HashMap<Integer, MediaPlayer> media;

    private MediaPlayer current;

    public Eyes(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.eyes_layout, this);

        if(this.isInEditMode()) {
            return;
        }

        surface = (SurfaceView) findViewById(R.id.eyes_layout_surface);

        media = new HashMap<>();
        media.put(R.raw.look_right_left, loadMedia(R.raw.look_right_left));
        media.put(R.raw.look_left, loadMedia(R.raw.look_left));
        media.put(R.raw.look_up_left, loadMedia(R.raw.look_up_left));
        media.put(R.raw.look_up, loadMedia(R.raw.look_up));
        media.put(R.raw.look_up_right, loadMedia(R.raw.look_up_right));
        media.put(R.raw.look_right, loadMedia(R.raw.look_right));
        media.put(R.raw.look_down_right, loadMedia(R.raw.look_down_right));
        media.put(R.raw.look_down, loadMedia(R.raw.look_down));
        media.put(R.raw.look_down_left, loadMedia(R.raw.look_down_left));
        media.put(R.raw.sad, loadMedia(R.raw.sad));
        media.put(R.raw.shocked, loadMedia(R.raw.shocked));
        media.put(R.raw.happy, loadMedia(R.raw.happy));
        media.put(R.raw.blink, loadMedia(R.raw.blink));
        media.put(R.raw.blink_twice, loadMedia(R.raw.blink_twice));

        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                surfaceExists = true;
                loadDefault(R.raw.blink);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.d(TAG, "Surface changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                surfaceExists = false;
            }
        });
    }

    public void loadDefault(int res) {
//        final MediaPlayer current = media.get(R.raw.blink);
//
//        if(current != null) {
//            current.seekTo(0);
//            current.setDisplay(surface.getHolder());
//        }
    }

    private MediaPlayer loadMedia(int res) {
        final Uri video = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + res);

        MediaPlayer mp = new MediaPlayer();

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "Prepared video: " + video.toString());
                mediaPlayer.seekTo(0);
                mediaPlayer.setOnPreparedListener(null);
            }
        });

        try {
            mp.setDataSource(getContext(), video);
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mp;
    }

    public void start(int res) {
        if(!surfaceExists || isPlaying) {
            return;
        }

        MediaPlayer mp = media.get(res);

        if(mp != null) {
            if(current == mp) {
                current.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        current.start();
                        current.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                isPlaying = false;
                            }
                        });
                        isPlaying = true;
                        current.setOnSeekCompleteListener(null);
                    }
                });
                current.pause();
                current.seekTo(0);
            } else {
                if(current != null) {
                    current.pause();
                    current.setDisplay(null);
                    current.seekTo(0);
                }

                current = mp;
                current.setDisplay(surface.getHolder());
                current.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                        current.start();
                        current.setOnVideoSizeChangedListener(null);
                    }
                });
                //current.start();
                current.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        isPlaying = false;
                    }
                });
                isPlaying = true;
            }
        }
    }

    public void showAction(FaceAction action) {
        if(action == FaceAction.BLINK) {
            start(R.raw.blink);
        }
    }
}
