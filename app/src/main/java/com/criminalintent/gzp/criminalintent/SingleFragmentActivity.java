package com.criminalintent.gzp.criminalintent;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


public abstract class SingleFragmentActivity extends AppCompatActivity {//抽象类 一个模板
    protected  abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());//将FrameLayout 容器视图装载为视图

        FragmentManager fm=getSupportFragmentManager();   //获取Fragment管理器；
        // FragmentManager管理着fragment队列（事务操作着队列里面的fragement）和回退栈（管理多个事务）
        Fragment fragment=fm.findFragmentById(R.id.fragment_container);
        // /获取activity中存在的fragment；使用其容器视图Framement的ID作为fragment的ID
        if(fragment==null){//activity中没有fragment
            fragment=createFragment();//创建一个Fragment
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
            //Fragment管理器创建一个事务；事务添加了一个添加一个fragment到FrameLayout中；提交事务；
        }

    }
}
