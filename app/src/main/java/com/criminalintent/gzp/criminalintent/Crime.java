package com.criminalintent.gzp.criminalintent;


import java.util.Date;
import java.util.UUID;

public class Crime  {
    private Date mDate;
    private boolean mSolved;
    private UUID mId;
    private String mTitle;
    private String mSuspect;
    private String mPhoneNumber;

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String mMPhoneNumber) {
        mPhoneNumber = mMPhoneNumber;
    }
    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }


    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {

        return mId;
    }
    public Crime(){
        this(UUID.randomUUID());
    }
    public Crime(UUID id){
        mId=id;
        mDate=new Date();

    }
}
