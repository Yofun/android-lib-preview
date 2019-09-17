package com.hyfun.preview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by HyFun on 2019/9/17
 * Email: 775183940@qq.com
 * Description:
 */
class Util {
    /**
     * 判断网络是否连通
     */
    public static boolean isNetworkConnected(Context context) {
//        Context context = SDR_LIBRARY.getInstance().getApplication().getApplicationContext();
        try {
            if (context != null) {
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                return info != null && info.isConnected();
            } else {
                /**如果context为空，就返回false，表示网络未连接*/
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
