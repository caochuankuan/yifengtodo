package com.yifeng.ads.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.yifeng.ads.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tab2Fragment extends Fragment {

    private ListView listView;
    private String[] imageName;
    private NoReuseAdapter1 adapter;

    private static final int REQUEST_CODE = 1;

    public Tab2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        listView = view.findViewById(R.id.listView);


        imageName = new String[]{
                "(选择)第一期党课学习",
                "(填空)第二期党课学习",
                "第三期党课学习",
                "第四期党课学习",
                "第五期党课学习",
                "第六期党课学习",
                "第七期党课学习",
                "第八期党课学习",
                "第九期党课学习",
                "第十期党课学习",
                "第十一期党课学习",
                "第十二期党课学习",
                "第十三期党课学习",
                "",
                ""
        };

        List<Map<String, Object>> listItem = new ArrayList<>();
        for (int i = 0; i < imageName.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", imageName[i]);
            listItem.add(map);
        }

        adapter = new NoReuseAdapter1(this, imageName);
        listView.setAdapter(adapter);

        return view;
    }

    // 点击事件处理方法
    public void onItemClick(int position, String toString, View itemView) {
        // Handle item click if needed
        // Example: Open a new activity with the clicked item
        // Intent intent = new Intent(requireActivity(), YourActivity.class);
        // startActivity(intent);
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

