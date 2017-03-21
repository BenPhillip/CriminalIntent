package database.CrimeDBSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.criminalintent.gzp.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

import database.CrimeDBSchema.CrimeDBSchema.CrimeTable;

/**
 * Cursor 表处理工具，任务就是封装数据表中的原始字段值
 */

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Crime getCrime(){
        String uuidString=getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title=getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date=getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSovled=getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect=getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        String phoneNumer = getString(getColumnIndex(CrimeTable.Cols.PHONENUMBER));

        Crime crime=new Crime(UUID.fromString(uuidString));
        crime.setDate(new Date(date));
        crime.setSolved(isSovled!=0);
        crime.setTitle(title);
        crime.setSuspect(suspect);
        crime.setPhoneNumber(phoneNumer);
        return crime;
    }

}
