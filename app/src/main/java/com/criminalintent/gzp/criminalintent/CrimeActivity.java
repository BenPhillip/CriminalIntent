package com.criminalintent.gzp.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity{
    private static  final String EXTRA_CRIME_ID="criminalintent.crime_id";
    public static Intent newIntent(Context packageContext,UUID crimeId){
        Intent i=new Intent(packageContext,CrimeActivity.class);
        i.putExtra(EXTRA_CRIME_ID,crimeId);
        return i;
    }
    @Override
    protected Fragment createFragment(){
//        return new CrimeFragment();
        UUID crimeId=(UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return  CrimeFragment.newInstance(crimeId);
    }


}
