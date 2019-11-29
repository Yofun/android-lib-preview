package com.hyfun.preview;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hyfun.preview.widget.SlideCloseLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;

import io.reactivex.functions.Consumer;


public class PreviewImageActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private SlideCloseLayout viewSlideLayout;

    private ViewPager viewPager;

    private TextView tvPager;

    private TextView tvSave;


    private int position;
    private List imageList;
    private boolean isLocal = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setFullScreen(this);
        setContentView(R.layout.activity_preview_image);
        //设置activity的背景为黑色
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);


        Intent intent = getIntent();

        position = intent.getIntExtra(Const.IMAGE_POSITION, 0);
        imageList = (List) intent.getSerializableExtra(Const.IMAGE_LIST);
        isLocal = intent.getBooleanExtra(Const.IMAGE_IS_LOCAL, false);


        viewSlideLayout = findViewById(R.id.preview_view_big_image_slide_view);
        viewPager = findViewById(R.id.preview_view_big_image_viewpager);
        tvPager = findViewById(R.id.preview_view_big_image_tv_pager);
        tvSave = findViewById(R.id.preview_view_big_image_tv_save);


        // 设置背景
        //给控件设置需要渐变的背景。如果没有设置这个，则背景不会变化
        viewSlideLayout.setGradualBackground(getWindow().getDecorView().getBackground());
        viewSlideLayout.setLayoutScrollListener(new SlideCloseLayout.LayoutScrollListener() {
            @Override
            public void onLayoutClosed() {
                finish();
            }

            @Override
            public void onScroll(int alpha) {
                if (alpha != 255) {
                    tvPager.setVisibility(View.GONE);
                    tvSave.setVisibility(View.GONE);
                }else {
                    tvPager.setVisibility(View.VISIBLE);
                    tvSave.setVisibility(View.VISIBLE);
                }
            }
        });

        tvSave.setVisibility(isLocal ? View.GONE : View.VISIBLE);
        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        if (position < imageList.size() && position >= 0) {
            viewPager.setCurrentItem(position);
            onPageSelected(position);
        }


        viewPager.addOnPageChangeListener(this);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isNetworkConnected(PreviewImageActivity.this)) {
                    Toast.makeText(PreviewImageActivity.this, "当前网络不可用，请检查你的网络设置", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 授权
                new RxPermissions(PreviewImageActivity.this)
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    RxSaveImage.saveImageToGallery(PreviewImageActivity.this, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "FunPreview", imageList.get(position));
                                } else {
                                    Toast.makeText(PreviewImageActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        tvPager.setText((position + 1) + "/" + imageList.size());
        this.position = position;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(PreviewImageActivity.this).inflate(R.layout.preview_layout_vp_item_preview_image, container, false);
            final SubsamplingScaleImageView imageView = view.findViewById(R.id.preview_view_big_image_item_photoview);
            final ProgressBar progressBar = view.findViewById(R.id.preview_view_big_image_item_progress);
            progressBar.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(PreviewImageActivity.this);
            imageView.setMinimumDpi(50);
            imageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
            Glide.with(PreviewImageActivity.this)
                    .download(imageList.get(position))
                    .into(new SimpleTarget<File>(SimpleTarget.SIZE_ORIGINAL, SimpleTarget.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            progressBar.setVisibility(View.GONE);
                            // 显示图片
                            imageView.setImage(ImageSource.uri(Uri.fromFile(resource)));
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            Toast.makeText(PreviewImageActivity.this, "资源加载失败", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
            container.addView(view);
            return view;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
