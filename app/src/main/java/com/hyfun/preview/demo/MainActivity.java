package com.hyfun.preview.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hyfun.preview.Preview;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void previewImage(View view) {
        List images = new ArrayList<>();
        images.add("http://b.hiphotos.baidu.com/imgad/pic/item/9f510fb30f2442a750999faed943ad4bd1130221.jpg");
        images.add("http://b.hiphotos.baidu.com/imgad/pic/item/80cb39dbb6fd5266ac09511fa318972bd407369e.jpg");
        images.add("http://b.hiphotos.baidu.com/imgad/pic/item/8644ebf81a4c510f372b80116859252dd52aa5ca.jpg");
        images.add("http://b.hiphotos.baidu.com/imgad/pic/item/500fd9f9d72a6059d4ff83112034349b023bbaca.jpg");
        images.add("http://b.hiphotos.baidu.com/imgad/pic/item/29381f30e924b89914e91b3366061d950b7bf6e0.jpg");
        images.add("http://b.hiphotos.baidu.com/imgad/pic/item/09fa513d269759eee9c702c0bafb43166d22df22.jpg");
        images.add("http://b.hiphotos.baidu.com/imgad/pic/item/2e2eb9389b504fc2f3b1d20eeddde71191ef6dca.jpg");
        images.add("https://img.zcool.cn/community/012bc0585250e8a801219c77cf3db4.jpg@1280w_1l_0o_100sh.jpg");
        images.add("https://gss3.bdstatic.com/-Po3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike220%2C5%2C5%2C220%2C73/sign=e2b9f9478013632701e0ca61f0e6cb89/8644ebf81a4c510f2a9f87816a59252dd52aa5d6.jpg");
        images.add(R.mipmap.big_width);
        Preview.previewImage(this, 0, images);
    }
}
