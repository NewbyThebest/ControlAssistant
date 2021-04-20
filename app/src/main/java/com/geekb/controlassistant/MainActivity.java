package com.geekb.controlassistant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends BaseActivity {
    private RecyclerView mMenuRv;
    private MenuAdapter mMenuAdapter;
    private ImageView mMenuBtn;
    private List<BindData> mList = new ArrayList<>();
    private PopupMenu popupMenu;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        mMenuRv = findViewById(R.id.rv_menu);
        mMenuBtn = findViewById(R.id.menu);
        mMenuAdapter = new MenuAdapter(this,mList);
        mMenuRv.setAdapter(mMenuAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mMenuRv.setLayoutManager(layoutManager);
        mMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });


        popupMenu = new PopupMenu(this, mMenuBtn);
        //将 R.menu.menu_main 菜单资源加载到popup中
        getMenuInflater().inflate(R.menu.menu_main,popupMenu.getMenu());
        //为popupMenu选项添加监听器
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bluetooth_setting:
                        Intent intent = new Intent(MainActivity.this,BluetoothActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.commit_setting:

                        break;

                    case R.id.exit_app:
                        finish();
                        break;
                    default:
                }
                popupMenu.dismiss();
                return true;
            }
        });


    }
    void initData(){
        mList.add(new BindData("空调开关"));
        mList.add(new BindData("电冰箱开关"));
        mList.add(new BindData("电视开关"));
        mList.add(new BindData("热水器开关"));
        mList.add(new BindData("电饭煲开关"));
    }


}