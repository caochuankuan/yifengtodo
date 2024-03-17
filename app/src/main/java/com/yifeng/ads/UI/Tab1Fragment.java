package com.yifeng.ads.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.yifeng.ads.R;

public class Tab1Fragment extends Fragment {

    private ListView listView;
    private int[] imageId;
    private String[] imageName;
    private NoReuseAdapter adapter;
    private static final int REQUEST_CODE = 1;
    private SharedPreferences sharedPreferences;

    public Tab1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        listView = view.findViewById(R.id.listView);

        // 初始化SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        imageId = new int[]{
                R.mipmap.img01,
                R.mipmap.img02,
                R.mipmap.img03,
                R.mipmap.img04,
                R.mipmap.img05,
                R.mipmap.img06,
                R.mipmap.img07,
                R.mipmap.img08,
                R.mipmap.img09,
                R.mipmap.img10,
                R.mipmap.img11,
                R.mipmap.img12,
                R.mipmap.img13,
        };

        imageName = new String[]{
                "“不忘初心 牢记使命”专题学习",
                "巾帼心向党 礼赞新中国",
                "每日强军",
                "廉政教有",
                "2019年度最敬业劳动者",
                "寻找最美教师",
                "辩论最强电信系",
                "探寻最牛编程学霸",
                "默默耕耘 播种希望",
                "电计协活雷锋",
                "守护一线的“扫地僧“",
                "展风采书画摄影微视频大赛",
                "工匠精神 路人篆刻"
        };

        adapter = new NoReuseAdapter(this, imageId, imageName);
        listView.setAdapter(adapter);

        return view;
    }

    // 接收新活动返回的结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // 获取新活动传递的位置
            int position = data.getIntExtra("position", -1);
            if (position != -1) {
                // 更新选中项的水印状态
                adapter.updateSelectedItem(position);
            }
        }
    }
}
