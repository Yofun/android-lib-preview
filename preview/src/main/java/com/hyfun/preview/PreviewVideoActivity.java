package com.hyfun.preview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.hyfun.preview.widget.SlideCloseLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class PreviewVideoActivity extends BaseActivity {

    private SlideCloseLayout slideCloseLayout;
    private StandardGSYVideoPlayer videoView;
    private OrientationUtils orientationUtils;


    private String title = "";
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setFullScreen(this);
        setContentView(R.layout.fun_activity_preview_video);
        //设置activity的背景为黑色
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        initIntent();
        initView();
        initData();
    }

    private void initIntent() {
        Intent intent = getIntent();
        title = intent.getStringExtra(Const.VIDEO_TITLE);
        videoPath = intent.getStringExtra(Const.VIDEO_PATH);
        if (TextUtils.isEmpty(title)) title = "";
    }

    private void initView() {
        slideCloseLayout = findViewById(R.id.preview_view_slide_view);
        videoView = findViewById(R.id.preview_view_play_video);
    }

    private void initData() {
        orientationUtils = new OrientationUtils(this, videoView);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        videoView.setUp(videoPath, true, title);
        videoView.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        videoView.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        videoView.setIsTouchWiget(false);
        // 设置是否循环播放
        videoView.setLooping(true);
        videoView.startPlayLogic();


        // 设置背景
        //给控件设置需要渐变的背景。如果没有设置这个，则背景不会变化
        slideCloseLayout.setGradualBackground(getWindow().getDecorView().getBackground());
        slideCloseLayout.setLayoutScrollListener(new SlideCloseLayout.LayoutScrollListener() {
            @Override
            public void onLayoutClosed() {
                finish();
            }

            @Override
            public void onScroll(int alpha) {
            }
        });
    }

    // —————————————————————生命周期—————————————————————————


    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils != null && orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoView.getFullscreenButton().performClick();
            return;
        }
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null)
            videoView.onVideoPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoView != null)
            videoView.onVideoResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (slideCloseLayout != null) {
                slideCloseLayout.lock();
            }
        } else {
            if (slideCloseLayout != null) {
                slideCloseLayout.unLock();
            }
        }
    }

}
