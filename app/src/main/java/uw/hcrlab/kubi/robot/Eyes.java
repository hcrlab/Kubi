package uw.hcrlab.kubi.robot;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import uw.hcrlab.kubi.R;

/**
 * Created by Alexander on 5/3/2016.
 */
public class Eyes extends FrameLayout {
    public static String TAG = Eyes.class.getSimpleName();

    public enum Look {
        BLINK,
        BLINK2,
        LOOK_LEFT,
        LOOK_RIGHT,
        LOOK_DOWN,
        LOOK_DOWN_LEFT,
        LOOK_DOWN_RIGHT,
        LOOK_UP,
        LOOK_UP_RIGHT,
        LOOK_UP_LEFT,
        HAPPY,
        SAD,
        SHOCKED
    }

    private boolean surfaceExists = false;
    private boolean isPlaying = false;

    private SurfaceView surface;
    private HashMap<Integer, MediaPlayer> media;
    private MediaPlayer current;

    private Random random = new Random();
    private Runnable blink = new Runnable() {
        @Override
        public void run() {
            look(Look.BLINK);
        }
    };

    private long getNextBlinkTime() {
        // 2 to 4 seconds in the future
        return 2000 + random.nextInt(10) * 200;
    }

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
                look(Look.BLINK);
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

        Log.d(TAG, "Start called...");

        if(mp != null) {
            removeCallbacks(blink);

            if(current == mp) {
                current.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        current.start();
                        current.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                isPlaying = false;
                                postDelayed(blink, getNextBlinkTime());
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
                        postDelayed(blink, getNextBlinkTime());
                    }
                });
                isPlaying = true;
            }
        }
    }

    public void look(Look type) {
        switch(type) {
            case BLINK:
                start(R.raw.blink);
                break;

            case BLINK2:
                start(R.raw.blink_twice);
                break;

            case HAPPY:
                start(R.raw.happy);
                break;

            case SAD:
                start(R.raw.sad);
                break;

            case SHOCKED:
                start(R.raw.shocked);
                break;

            case LOOK_LEFT:
                start(R.raw.look_left);
                break;

            case LOOK_UP_LEFT:
                start(R.raw.look_up_left);
                break;

            case LOOK_UP:
                start(R.raw.look_up);
                break;

            case LOOK_UP_RIGHT:
                start(R.raw.look_up_right);
                break;

            case LOOK_RIGHT:
                start(R.raw.look_right);
                break;

            case LOOK_DOWN_RIGHT:
                start(R.raw.look_down_right);
                break;

            case LOOK_DOWN:
                start(R.raw.look_down);
                break;

            case LOOK_DOWN_LEFT:
                start(R.raw.look_down_left);
                break;
        }
    }
}
