package com.hyfun.preview;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HyFun on 2019/9/16
 * Email: 775183940@qq.com
 * Description:
 */
public class Preview {


    /**
     *
     * @param context
     * @param position
     * @param imageList
     */
    public static final void previewImage(Context context, int position, List imageList) {
        Intent intent = new Intent(context, PreviewImageActivity.class);
        intent.putExtra(Const.IMAGE_POSITION, position);
        intent.putExtra(Const.IMAGE_LIST, (Serializable) imageList);
        context.startActivity(intent);
    }
}
