package com.yifeng.ads.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yifeng.ads.R;

import java.util.HashSet;
import java.util.Set;

public class NoReuseAdapter extends BaseAdapter {

    private Tab1Fragment fragment;      // Tab1Fragment 的引用
    private int[] imageId;
    private String[] imageName;
    private Set<Integer> selectedItems = new HashSet<>();  // 存储被点击项的位置集合
    private SharedPreferences sharedPreferences;

    // 构造方法，接收 Tab1Fragment 引用、图片资源和图片名称数组
    public NoReuseAdapter(Tab1Fragment fragment, int[] imageId, String[] imageName) {
        this.fragment = fragment;
        this.imageId = imageId;
        this.imageName = imageName;

        // 初始化SharedPreferences
        sharedPreferences = fragment.requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    // 获取列表项数量
    @Override
    public int getCount() {
        return imageId.length;
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

        // 根据布局文件 R.layout.sub 创建列表项的视图
        View itemView = inflater.inflate(R.layout.sub, parent, false);

        // 获取视图中的控件
        ImageView subImage = itemView.findViewById(R.id.subImage);
        TextView imageText = itemView.findViewById(R.id.imageText);
        ImageView watermark = itemView.findViewById(R.id.watermark);

        // 设置图片资源和文本内容
        subImage.setImageResource(imageId[position]);
        imageText.setText(imageName[position]);

        // 从SharedPreferences中获取已读状态
        boolean isRead = sharedPreferences.getBoolean("item_" + position, false);

        // 根据已读状态设置水印的可见性
        watermark.setVisibility(isRead ? View.VISIBLE : View.GONE);

        // 点击事件处理
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动新活动，并传递点击项的位置和文本
                Intent intent = new Intent(fragment.requireContext(), EssayActivity.class);
                intent.putExtra("position", position);
                //Toast.makeText(fragment.requireActivity(), ""+position, Toast.LENGTH_SHORT).show();
                intent.putExtra("title", imageText.getText().toString());
                fragment.startActivityForResult(intent, EssayActivity.REQUEST_CODE);

                // 将已读状态保存到SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("item_" + position, true);
                editor.apply();
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

