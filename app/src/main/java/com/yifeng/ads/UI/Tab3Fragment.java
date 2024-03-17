package com.yifeng.ads.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.yifeng.ads.R;

public class Tab3Fragment extends Fragment {

    String n, x ,y ,z;
    TextView name,yuanxi,xuehao,zhengzhi;
    Button exit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        // 初始化视图和处理逻辑
        name = view.findViewById(R.id.name);
        yuanxi = view.findViewById(R.id.yuanxi);
        xuehao = view.findViewById(R.id.xuehao);
        zhengzhi = view.findViewById(R.id.zhengzhi);
        exit = view.findViewById(R.id.exit);

        final SharedPreferences sp = getActivity().getSharedPreferences("yifeng", Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed11 = sp.edit();
        if (sp.getString("xingming",n) != null && sp.getString("xuehao",x) !=null) {
            name.setText(sp.getString("xingming",n));
            yuanxi.setText(sp.getString("yuanxi",y));
            xuehao.setText(sp.getString("xuehao",x));
            zhengzhi.setText(sp.getString("zhengzhi",z));
            exit.setText("退出登录");
        }

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sp.getString("xingming",n) != null && sp.getString("xuehao",x) !=null) {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("温馨提示")
                            .setMessage("是否清除已读状态并退出？")
                            .setNeutralButton("清除并退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // 使用clear()方法清除所有数据
                                    ed11.clear().commit();
                                    getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit().clear().commit();
                                    Toast.makeText(requireActivity(), "已清除并退出登录", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                                }
                            })
                            .setNegativeButton("取消",null)
                            .setPositiveButton("仅退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ed11.clear().commit();
                                    Toast.makeText(requireActivity(), "已退出登录", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                                }
                            }).show();
                }
            }
        });

        return view;
    }
}
