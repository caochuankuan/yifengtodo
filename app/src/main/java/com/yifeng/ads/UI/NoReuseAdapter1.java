package com.yifeng.ads.UI;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yifeng.ads.R;

import java.util.HashSet;
import java.util.Set;

public class NoReuseAdapter1 extends BaseAdapter {

    private Tab2Fragment fragment;      // Tab2Fragment 的引用
    private String[] imageName;
    private Set<Integer> selectedItems = new HashSet<>();  // 存储被点击项的位置集合

    // 构造方法，接收 Tab2Fragment 引用、图片资源和图片名称数组
    public NoReuseAdapter1(Tab2Fragment fragment, String[] imageName) {
        this.fragment = fragment;
        this.imageName = imageName;
    }

    // 获取列表项数量
    @Override
    public int getCount() {
        return imageName.length;
    }

    // 获取指定位置的数据项
    @Override
    public Object getItem(int position) {
        return null;  // 不使用，返回 null
    }

    // 获取指定位置的数据项 ID
    @Override
    public long getItemId(int position) {
        return 0;  // 不使用，返回 0
    }

    // 创建和返回每个列表项的视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取布局填充器，用于将 XML 布局转换为 View 对象
        LayoutInflater inflater = LayoutInflater.from(fragment.requireContext());

        // 根据布局文件 R.layout.sub2 创建列表项的视图
        View itemView = inflater.inflate(R.layout.sub1, parent, false);

        // 获取视图中的控件
        TextView imageText = itemView.findViewById(R.id.imageText);
        ImageView watermark = itemView.findViewById(R.id.watermark);

        // 设置文本内容
        imageText.setText(imageName[position]);

        /// 根据选中的位置设置水印状态
        watermark.setVisibility(selectedItems.contains(position) ? View.VISIBLE : View.GONE);

        // 点击事件处理
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动新活动，并传递点击项的位置和文本
                Intent intent = new Intent(fragment.requireContext(), TestActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("timu", "0");
                intent.putExtra("title", imageText.getText().toString());
                fragment.startActivityForResult(intent, TestActivity.REQUEST_CODE);
            }
        });

        // 返回创建的列表项视图
        return itemView;
    }

    // 更新选中项的方法
    public void updateSelectedItem(int position) {
        selectedItems.add(position);
        notifyDataSetChanged();
    }
}
