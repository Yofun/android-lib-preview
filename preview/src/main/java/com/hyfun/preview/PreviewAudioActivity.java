package com.hyfun.preview;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;

public class PreviewAudioActivity extends BaseActivity {

    private static final String TAG = PreviewAudioActivity.class.getSimpleName();

    private StandardGSYVideoPlayer viewAudio;
    private ImageView viewBack; // 返回键
    private ImageView viewImageRecord;
    private TextView viewTextTitle;
    private TextView viewTextCurrentTime;
    private SeekBar viewSeekBar;
    private TextView viewTextTotalTime;
    private ImageView viewPlayPause;
    private ProgressBar viewPbLoading;


    private String title = "";
    private String audioPath;

    // 旋转动画
    private ObjectAnimator objectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setFullScreen(this);
        setContentView(R.layout.activity_preview_audio);
        Intent intent = getIntent();
        title = intent.getStringExtra(Const.AUDIO_TITLE);
        audioPath = intent.getStringExtra(Const.AUDIO_PATH);
        if (TextUtils.isEmpty(title)) title = "";
        // 初始化view
        {
            viewAudio = findViewById(R.id.preview_view_play_audio);
            viewBack = findViewById(R.id.preview_view_preview_audio_iv_back);
            viewImageRecord = findViewById(R.id.preview_view_preview_audio_iv_record);
            viewTextTitle = findViewById(R.id.preview_view_preview_audio_tv_title);
            viewTextCurrentTime = findViewById(R.id.preview_view_preview_audio_tv_current_time);
            viewSeekBar = findViewById(R.id.preview_view_preview_audio_sb_progress);
            viewTextTotalTime = findViewById(R.id.preview_view_preview_audio_tv_total_time);
            viewPlayPause = findViewById(R.id.preview_view_preview_audio_iv_play_pause);
            viewPbLoading = findViewById(R.id.preview_view_preview_audio_pb_loading);
        }

        // 初始化一些动作
        {
            viewSeekBar.setEnabled(false);
            viewTextTitle.setText(title);
            objectAnimator = createRotateAnimator();
        }


        viewAudio.setUp(audioPath, true, title);
        //是否可以滑动调整
        viewAudio.setIsTouchWiget(false);
        // 设置是否循环播放
        viewAudio.setLooping(true);
        viewAudio.startPlayLogic();


        // 监听

        viewAudio.setGSYVideoProgressListener(new GSYVideoProgressListener() {
            @Override
            public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
                viewSeekBar.setProgress(currentPosition);
                viewTextCurrentTime.setText(formatTime(currentPosition));
            }
        });
        viewAudio.setVideoAllCallBack(new VideoAllCallBack() {
            @Override
            public void onStartPrepared(String url, Object... objects) {

            }

            @Override
            public void onPrepared(String url, Object... objects) {
                // 表示已经准备好了
                viewPbLoading.setVisibility(View.GONE);
                viewPlayPause.setVisibility(View.VISIBLE);
                objectAnimator.start();
                play();
                viewSeekBar.setEnabled(true);
                viewSeekBar.setMax(viewAudio.getDuration());
                viewTextTotalTime.setText(formatTime(viewAudio.getDuration()));
            }

            @Override
            public void onClickStartIcon(String url, Object... objects) {

            }

            @Override
            public void onClickStartError(String url, Object... objects) {

            }

            @Override
            public void onClickStop(String url, Object... objects) {

            }

            @Override
            public void onClickStopFullscreen(String url, Object... objects) {

            }

            @Override
            public void onClickResume(String url, Object... objects) {

            }

            @Override
            public void onClickResumeFullscreen(String url, Object... objects) {

            }

            @Override
            public void onClickSeekbar(String url, Object... objects) {

            }

            @Override
            public void onClickSeekbarFullscreen(String url, Object... objects) {

            }

            @Override
            public void onAutoComplete(String url, Object... objects) {

            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {

            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {

            }

            @Override
            public void onQuitSmallWidget(String url, Object... objects) {

            }

            @Override
            public void onEnterSmallWidget(String url, Object... objects) {

            }

            @Override
            public void onTouchScreenSeekVolume(String url, Object... objects) {

            }

            @Override
            public void onTouchScreenSeekPosition(String url, Object... objects) {

            }

            @Override
            public void onTouchScreenSeekLight(String url, Object... objects) {

            }

            @Override
            public void onPlayError(String url, Object... objects) {
                Toast.makeText(PreviewAudioActivity.this, "播放错误，请检查网络", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickStartThumb(String url, Object... objects) {

            }

            @Override
            public void onClickBlank(String url, Object... objects) {

            }

            @Override
            public void onClickBlankFullscreen(String url, Object... objects) {

            }
        });

        viewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        viewPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewAudio.getCurrentState() != GSYVideoView.CURRENT_STATE_PLAYING) {
                    play();
                } else if (viewAudio.getCurrentState() != GSYVideoView.CURRENT_STATE_PAUSE) {
                    pause();
                }
            }
        });

        viewSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewAudio.getGSYVideoManager().seekTo(seekBar.getProgress());
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }


    // ———————————————————————————私有方法———————————————————————————————

    /**
     * 播放
     */
    private void play() {
        viewPlayPause.setImageResource(R.drawable.preview_ic_pause_circle_filled_black_24dp);
        if (viewAudio.getCurrentState() != GSYVideoView.CURRENT_STATE_PLAYING) {
            viewAudio.onVideoResume();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                objectAnimator.resume();
            } else {
                objectAnimator.start();
            }
        }
    }

    /**
     * 暂停
     */
    private void pause() {
        viewPlayPause.setImageResource(R.drawable.preview_ic_play_circle_filled_black_24dp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            objectAnimator.pause();
        } else {
            objectAnimator.end();
        }
        if (viewAudio.getCurrentState() != GSYVideoView.CURRENT_STATE_PAUSE) {
            viewAudio.onVideoPause();
        }
    }


    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    private String formatTime(int time) {
        int seconds = time / 1000;
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d", min) + ":" + String.format("%02d", sec);
    }


    /**
     * 创建一个旋转的动画
     *
     * @return
     */
    private ObjectAnimator createRotateAnimator() {
        objectAnimator = ObjectAnimator.ofFloat(viewImageRecord, "rotation", 0.0f, 360.0f);
        objectAnimator.setDuration(12000);//设定转一圈的时间
        objectAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
        objectAnimator.setInterpolator(new LinearInterpolator());// 匀速

        return objectAnimator;
    }


}
