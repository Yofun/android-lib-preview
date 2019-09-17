package com.hyfun.preview;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class PreviewAudioActivity extends BaseActivity {


    private StandardGSYVideoPlayer viewAudio;


    private String title = "";
    private String audioPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_audio);
        viewAudio = findViewById(R.id.preview_view_play_audio);

        Intent intent = getIntent();
        title = intent.getStringExtra(Const.AUDIO_TITLE);
        audioPath = intent.getStringExtra(Const.AUDIO_PATH);
        if (TextUtils.isEmpty(title)) title = "";

        viewAudio.setUp(audioPath,true,title);
        //是否可以滑动调整
        viewAudio.setIsTouchWiget(false);
        // 设置是否循环播放
        viewAudio.setLooping(true);
        viewAudio.startPlayLogic();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }
}
