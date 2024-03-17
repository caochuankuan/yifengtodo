package com.yifeng.ads.UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yifeng.ads.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText ed1,ed2;
    RadioGroup rg;
    RadioButton rb1,rb2,rb3;
    Spinner sp1;
    Button bt;
    String n,x,sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed1 = findViewById(R.id.xingming);
        ed2 = findViewById(R.id.xuehao);
        rg = findViewById(R.id.zhengzhig);
        sp1 = findViewById(R.id.yuanxi);
        bt = findViewById(R.id.login);

        final SharedPreferences sp11 = getSharedPreferences("yifeng",MODE_PRIVATE);
        final SharedPreferences.Editor ed11 = sp11.edit();
        if (sp11.getString("xingming",n) != null && sp11.getString("xuehao",x) !=null && Objects.equals(sp11.getString("sign", sign), "1")) {
            Intent intent = new Intent(LoginActivity.this, ChoiceActivity.class);
            startActivity(intent);
            finish();
        }

        CheckBox rememberLoginCheckbox = findViewById(R.id.rememberLoginCheckbox);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean rememberLogin = rememberLoginCheckbox.isChecked();
                String st1 = ed1.getText().toString();
                String st2 = ed2.getText().toString();
                int rgc = rg.getCheckedRadioButtonId();
                rb1 = findViewById(rgc);
                String st3 = "";
                if (rb1 != null){
                    st3 = rb1.getText().toString();
                }
                String st4 = sp1.getSelectedItem().toString();

                if (st1.equals("")) {
                    Toast.makeText(LoginActivity.this, "请填写姓名再提交", Toast.LENGTH_SHORT).show();
                } else if (!st1.matches("^[\u4e00-\u9fa5]{2,5}$")) {
                    Toast.makeText(LoginActivity.this, "请输入真实中文姓名", Toast.LENGTH_SHORT).show();
                } else if (st2.equals("")) {
                    Toast.makeText(LoginActivity.this, "请输入学号再提交", Toast.LENGTH_SHORT).show();
                } else if (st2.length() != 10 || !st2.matches("\\d+")) {
                    Toast.makeText(LoginActivity.this, "学号必须是10位数字", Toast.LENGTH_SHORT).show();
                } else if (st3.equals("")) {
                    Toast.makeText(LoginActivity.this, "请选择政治面貌再提交", Toast.LENGTH_SHORT).show();
                } else if (st4.equals("请选择院系")) {
                    Toast.makeText(LoginActivity.this, "请选择所属院系再提交", Toast.LENGTH_SHORT).show();
                } else if (!(st2.startsWith("311", 2) && st4.equals("电子信息系"))) {
                    Toast.makeText(LoginActivity.this, "学号匹配院系失败，请检查", Toast.LENGTH_SHORT).show();
                }else {
                    if (!rememberLogin){
                        String aa = st3;
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("温馨提示")
                                .setMessage("是否记住登录信息")
                                .setNeutralButton("记住", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent it = new Intent(LoginActivity.this, StartActivity.class);
                                        Bundle bd = new Bundle();
                                        bd.putCharSequence("xingming",st1);
                                        bd.putCharSequence("xuehao",st2);
                                        bd.putCharSequence("zhengzhi",aa);
                                        bd.putCharSequence("yuanxi",st4);
                                        it.putExtras(bd);
                                        ed11.putString("xingming",st1);
                                        ed11.putString("xuehao",st2);
                                        ed11.putString("yuanxi",st4);
                                        ed11.putString("zhengzhi",aa);
                                        ed11.putString("sign","1");
                                        ed11.commit();
                                        Toast.makeText(LoginActivity.this, "已添加自动登录信息", Toast.LENGTH_SHORT).show();
                                        startActivity(it);
                                        finish();
                                    }
                                })
                                .setPositiveButton("不记住", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent it = new Intent(LoginActivity.this, StartActivity.class);
                                        Bundle bd = new Bundle();
                                        bd.putCharSequence("xingming",st1);
                                        bd.putCharSequence("xuehao",st2);
                                        bd.putCharSequence("zhengzhi",aa);
                                        bd.putCharSequence("yuanxi",st4);
                                        it.putExtras(bd);
                                        ed11.putString("xingming",st1);
                                        ed11.putString("xuehao",st2);
                                        ed11.putString("yuanxi",st4);
                                        ed11.putString("zhengzhi",aa);
                                        ed11.putString("sign","0");
                                        ed11.commit();
                                        startActivity(it);
                                        finish();
                                    }
                                }).show();
                    }else {
                        Intent it = new Intent(LoginActivity.this, StartActivity.class);
                        Bundle bd = new Bundle();
                        bd.putCharSequence("xingming",st1);
                        bd.putCharSequence("xuehao",st2);
                        bd.putCharSequence("zhengzhi",st3);
                        bd.putCharSequence("yuanxi",st4);
                        it.putExtras(bd);
                        ed11.putString("xingming",st1);
                        ed11.putString("xuehao",st2);
                        ed11.putString("yuanxi",st4);
                        ed11.putString("zhengzhi",st3);
                        ed11.putString("sign","1");
                        ed11.commit();
                        Toast.makeText(LoginActivity.this, "已添加自动登录信息", Toast.LENGTH_SHORT).show();
                        startActivity(it);
                    }
                }
            }
        });
    }
}