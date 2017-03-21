package com.criminalintent.gzp.criminalintent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

import static android.text.format.DateFormat.*;

public class CrimeListFragment extends Fragment {
    private static final String TAG="CrimeListFragment";
    private static final String SAVED_SUBTITLE_VISIBLE="subtitle";
    private static final int REQUEST_CRIME = 1;

    private Callbacks mCallBacks;
    private boolean mSubtitleVisible;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private RelativeLayout mRelativeLayout;
    private Button mNewCrime;

    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }
    @Override
    public void onAttach(Context activity){
        super.onAttach(activity);
        mCallBacks=(Callbacks)activity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_crime_list,container,false);//设置Fragment布局
        /*fragment的布局视图ID；要将fragment布局要插入的父GroupView；是否将生成的视图添加给父视图
        RccyclerView；父布局FrameLayout；通过代码添加*/
        mCrimeRecyclerView=(RecyclerView)view.findViewById(R.id.crime_recycler_view);
        //获取RecyclerView的视图控件
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //LayoutManager负责定位屏幕上的列表项，以及屏幕滚动行为；LinearLayoutManager以竖直列表的形式展示列表项
        mRelativeLayout=(RelativeLayout)view.findViewById(R.id.crime_no_record);
//        mToolBar=(Toolbar)view.findViewById(R.id.toolbar);
//        ( (AppCompatActivity)getActivity()).setSupportActionBar(mToolBar);
        mNewCrime=(Button)view.findViewById(R.id.new_crime_button);
        mNewCrime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
//                Intent intent=CrimePagerActivity.newIntent(getActivity(),crime.getId());
//                startActivity(intent);
                updateUI();
                mCallBacks.onCrimeSelected(crime);
            }

        });
        if(savedInstanceState!=null)
            mSubtitleVisible=savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);

        updateUI();
        return view;
    }

    private  class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private  Crime mCrime;

        public void bindCrime(Crime crime){
            mCrime=crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(DateFormat.format("yyyy年MM月dd日E,kk:mm",mCrime.getDate()));
            mSolvedCheckBox.setChecked(crime.isSolved());
        }
        public CrimeHolder(View itemView){
            super(itemView);
//            mTitleTextView=(TextView)itemView;
            itemView.setOnClickListener(this);
            mTitleTextView=(TextView)itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView=(TextView)itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox=(CheckBox)itemView.findViewById(R.id.list_item_crime_solved_check_box);
            mSolvedCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCrime.setSolved(!mCrime.isSolved());
                    CrimeLab.get(getActivity()).updateCrime(mCrime);
                    updateUI();
//                    mCallBacks.onCrimeSelected(mCrime);
                }
            });
        }
        @Override
        public void onClick(View v){
//            Intent intent=CrimePagerActivity.newIntent(getActivity(),mCrime.getId());
//            startActivityForResult(intent,REQUEST_CRIME);
            mCallBacks.onCrimeSelected(mCrime);

        }
    }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime>mCrimes;
        public CrimeAdapter(List<Crime> crimes){
            mCrimes=crimes;//从模型获取数据
        }
        //1.获取对象数目
        @Override
        public int getItemCount(){
            return mCrimes.size();
        }
        //2.Recycler调用。创建一个ViewHolder以及ViewHolder要显示的视图
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent,int ViewType){
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            View view=layoutInflater.inflate(R.layout.list_item_crime,parent,false);
            //将具体条目视图放入父视图
            return  new CrimeHolder(view);
        }
        //3.RecyclerView传入ViewHolder及其位置。调用此方法。
        @Override
        public void onBindViewHolder(CrimeHolder holder,int position){
            Crime crime=mCrimes.get(position);//adpter会找到目标位置的数据。
            holder.bindCrime(crime);//并绑定到ViewHolder的视图上；
        }
        public void setCrimes(List<Crime>crimes){
            mCrimes=crimes;
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }
    @Override
    public void onDetach(){
        super.onDetach();
        mCallBacks=null;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);//没什么用
        inflater.inflate(R.menu.fragment_crime_list,menu);

        MenuItem subtitleItem=menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
//                Intent intent=CrimePagerActivity.newIntent(getActivity(),crime.getId());
//                startActivity(intent);
                updateUI();
                mCallBacks.onCrimeSelected(crime);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible=!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        if(requestCode==REQUEST_CRIME){

        }
    }

    private void updateSubtitle(){
        CrimeLab crimeLab=CrimeLab.get(getActivity());
        int crimeCount=crimeLab.getCrimes().size();
        Log.d(TAG, "updateSubtitle: Count"+crimeCount);
        String subtitle=getResources().getQuantityString(R.plurals.subtitle_plural,crimeCount,crimeCount);
        Log.d(TAG, "updateSubtitle:subtitle: "+subtitle);
        //第一次传入的参数count是为plural提供数量quantity作判断的，第二次传入的count是插入到%d的占位符的。
        if(!mSubtitleVisible)
            subtitle=null;
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);

    }
    public  void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(mAdapter==null) {
            mAdapter=new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
        if(crimes.size()!=0)
            mRelativeLayout.setVisibility(View.GONE);
        else
            mRelativeLayout.setVisibility(View.VISIBLE);
        Log.d(TAG, "updateUI: ");
    }


}
