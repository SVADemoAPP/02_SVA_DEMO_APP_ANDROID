package com.hck.imagemap;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlayerAcitivity extends Activity implements OnTouchListener,
        OnPreparedListener
{

    // private
    private MediaController mController;
    private VideoView viv;
    private String url;
   // private String msg;
    private Button back;
    private RelativeLayout video_title;
    private ProgressBar play_progressbar;
    private TextView play_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        url = extras.getString("url");
       // msg = extras.getString("msg");
        Log.i("info", "landscape"); // 横屏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏全屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
        setContentView(R.layout.player_land);
        video_title = (RelativeLayout) findViewById(R.id.video_title);
        viv = (VideoView) findViewById(R.id.videoView);
        back = (Button) findViewById(R.id.back_movie);
        play_progressbar = (ProgressBar) findViewById(R.id.play_progressbar);
        play_tv = (TextView) findViewById(R.id.play_tv);
        mController = new MediaController(VideoPlayerAcitivity.this);
        viv.setMediaController(mController);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // handler.sendEmptyMessage(0x111);
                try
                {
                    System.out.println("urlurlurlurlurlurlurlurl  "+url);
                    viv.setVideoURI(Uri.parse(url));
                    // 让VideiView获取焦点
                    viv.requestFocus();
                    // viv.seekTo(100);
                } catch (Exception e)
                {
                    Log.e("VideoPlayerAcitivity", e.toString());
                }
            }
        }).start();

        viv.setOnInfoListener(new OnInfoListener()
        {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra)
            {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
                {
                    play_progressbar.setVisibility(View.VISIBLE);
                    play_tv.setVisibility(View.VISIBLE);
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
                {
                    // 此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                    if (mp.isPlaying())
                    {
                        play_progressbar.setVisibility(View.GONE);
                        play_tv.setVisibility(View.GONE);
                    }
                }
                return true;
            }
        });
        viv.setOnPreparedListener(new OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                // 缓冲完成就隐藏
                play_progressbar.setVisibility(View.GONE);
                play_tv.setVisibility(View.GONE);
                viv.start();
                Hint_video_title();
                video_title.setVisibility(View.GONE);
            }
        });
        back.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        video_title.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

    }

    /**
     * title自动消失
     */
    private void Hint_video_title()
    { // 延迟1s运行
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (!mController.isShowing())
                {
                    video_title.setVisibility(View.GONE);
                } else
                {
                    video_title.setVisibility(View.VISIBLE);
                }
                Hint_video_title();
            }
        }, 200);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // progress = viv.getCurrentPosition();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        return false;

    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        play_progressbar.setVisibility(View.GONE);
        play_tv.setVisibility(View.GONE);
    }

}
