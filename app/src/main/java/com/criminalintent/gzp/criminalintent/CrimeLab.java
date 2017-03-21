package com.criminalintent.gzp.criminalintent;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.CrimeDBSchema.CrimeBaseHelper;
import database.CrimeDBSchema.CrimeCursorWrapper;
import database.CrimeDBSchema.CrimeDBSchema;
import database.CrimeDBSchema.CrimeDBSchema.CrimeTable;

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context){   //别的类无法创建CrimeLab对象；
        mContext=context.getApplicationContext();
        mDatabase=new CrimeBaseHelper(mContext).getWritableDatabase();
    }
    public static CrimeLab get(Context c){ //创建CrimeLab对象；获取对象；
        if(sCrimeLab==null)
            sCrimeLab=new CrimeLab(c.getApplicationContext());
        return sCrimeLab;

    }
    public void addCrime(Crime crime){
       ContentValues values=getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME,null,values);
        /*第二个参数为nullColumnHack 当第三个参数为空时 则方法只能失败
        当第二个参数为uuid时，第三参数为空时传入uuid且值为空的ContentValues，成功插入一条记录*/
    }
    public void removeCrime(Crime crime){
        String uuidString=crime.getId().toString();
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID+"=?",new String[]{uuidString});
    }
    public List<Crime> getCrimes() {
        List<Crime> crimes=new ArrayList<>();
        CrimeCursorWrapper cursor=queryCrimes(null,null);//where Clause ,whereArgus
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }
    public void updateCrime(Crime crime){
        String uuidString =crime.getId().toString();
        ContentValues values=getContentValues(crime);
        mDatabase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID+"=?",new String[]{uuidString});
    }
    private static ContentValues getContentValues(Crime crime){
        ContentValues values=new ContentValues();
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE,crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved());
        values.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());
        values.put(CrimeTable.Cols.PHONENUMBER,crime.getPhoneNumber());
        return values;
    }
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgus){
        Cursor cursor=mDatabase.query(
                CrimeTable.NAME,
                null,           //select *
                whereClause,
                whereArgus,
                null,//groupby
                null,//having
                null//orderby
        );
        return new CrimeCursorWrapper(cursor);
    };
    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor=queryCrimes(CrimeTable.Cols.UUID+"=?",new String[]{id.toString()});
        try{
            if(cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }
    public File getPhotoFile(Crime crime){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFilesDir==null)
            return null;
        return new File(externalFilesDir,crime.getPhotoFilename());
//        创建了File对象 并没有生成文件
    }
}
