/*
 * Copyright (C) 2015 Drakeet <drakeet.me@gmail.com>
 *
 * This file is part of Meizhi
 *
 * Meizhi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Meizhi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Meizhi.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hyfun.preview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 保存图片，重复插入图片提示已存在
 *
 * @author jingbin
 */
public class RxSaveImage {

    /**
     * 使用Glide进行下载图片
     *
     * @param context
     * @param savePath
     * @param url
     */
    public static void saveImageToGallery(final Context context, final String savePath, final Object url) {
        Observable.just(url)
                .flatMap(new Function<Object, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(Object s) throws Exception {
                        if (TextUtils.isEmpty(savePath) || TextUtils.isEmpty(url.toString())) {
                            throw new NullPointerException("请检查图片路径");
                        }
                        // 检查保存的路径是否存在
                        File saveDir = new File(savePath);
                        if (!saveDir.exists()) {
                            saveDir.mkdirs();
                        }
                        File resource = Glide.with(context)
                                .download(url)
                                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();

                        if (resource == null) {
                            throw new Exception("无法下载到图片");
                        }
                        return Observable.just(resource);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(final File resource) throws Exception {
                        final File file = new File(savePath, resource.getName() + ".jpg");
                        if (file.exists()) {
                            // 询问是否覆盖
                            new AlertDialog.Builder(context)
                                    .setTitle("文件已存在")
                                    .setMessage("是否覆盖已经存在的文件?")
                                    .setNegativeButton("另存", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            File newFile = new File(savePath, UUID.randomUUID().toString().replaceAll("-", "") + ".jpg");
                                            try {
                                                newFile.createNewFile();
                                                saveImage(context, resource, newFile);
                                            } catch (IOException e) {
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }


                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                file.delete();
                                                file.createNewFile();
                                                saveImage(context, resource, file);
                                            } catch (IOException e) {
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(context, "开始下载图片", Toast.LENGTH_SHORT).show();
                            file.createNewFile();
                            saveImage(context, resource, file);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 拷贝文件
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }


    /**
     * 通知图库刷新
     * @param context
     * @param source
     * @param dest
     * @throws IOException
     */
    private static void saveImage(Context context, File source, File dest) throws IOException {
        copyFileUsingFileChannels(source, dest);
        Uri uri = Uri.fromFile(dest);
        // 通知图库更新
        Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scannerIntent);
        Toast.makeText(context, "图片下载成功：" + dest.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }

}
