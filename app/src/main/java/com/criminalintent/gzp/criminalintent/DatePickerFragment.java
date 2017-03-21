package com.criminalintent.gzp.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DatePickerFragment extends DialogFragment {
    private static  final String ARG_DATE="date";
    public static final String EXTRA_DATE="com.gzp.criminalintent.date";
    private DatePicker mDatePicker;
    public static DatePickerFragment newIntent(Date date){
        Bundle args=new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment fragment=new DatePickerFragment();
        fragment.setArguments(args);
        return  fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Date mDate=(Date)getArguments().getSerializable(ARG_DATE);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(mDate);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        View v=getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        mDatePicker=(DatePicker)v.findViewById(R.id.dialog_date_datePicker);
        mDatePicker.init(year,month,day,null);
        return new AlertDialog.Builder(getActivity()).               //返回一个AlertDialog.Builder的实例
                setView(v).
                setTitle(R.string.date_picker_title).
                setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year=mDatePicker.getYear();
                        int month=mDatePicker.getMonth();
                        int day=mDatePicker.getDayOfMonth();
                        Date date=new GregorianCalendar(year,month,day).getTime();
                        sendResult(Activity.RESULT_OK,date);
                    }
                }).      //ok常量的ID资源和实现DialogInterface.OnClickListener接口的对象。
                create();
    }
    private void sendResult(int resultCode,Date date){
        if (getTargetFragment()==null)
            return ;
        Intent intent=new Intent();
        intent.putExtra(EXTRA_DATE,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
