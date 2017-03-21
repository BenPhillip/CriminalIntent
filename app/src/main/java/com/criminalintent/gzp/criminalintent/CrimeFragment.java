package com.criminalintent.gzp.criminalintent;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;



public class CrimeFragment extends Fragment {
    public static final String TAG = "CrimeFragment";
    private static final  String ARG_CRIME_ID="crime_id";
    private static final String DIALOG_DATE="DialogDate";
    public static final String DIALOG_IMAGE = "image";
    private  static final int REQUEST_DATE=0;
    private static final int REQUEST_CONTACT=1;
    public static final int REQUEST_PHOTO=2;

    private Callbacks mCallbacks;
    private Crime mCrime;

    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mTelButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    private int width;
    private int height;

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args=new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment=new CrimeFragment();
        Log.i(TAG, "newInstance: ");
        fragment.setArguments(args);
        return  fragment;
    }
    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Context activity){
        super.onAttach(activity);
        mCallbacks=(Callbacks)activity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        mCrime=new Crime();
//        UUID crimeId=(UUID)getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        UUID crimeId=(UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime=CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

    }
    @Override
    public void onPause(){
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,Bundle savedInstanceState){
        final View v=inflater.inflate(R.layout.fragment_crime,parent,false);

//        ActionBar actionBar=( (AppCompatActivity)getActivity()).getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);


        mTitleField=(EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton=(Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               FragmentManager fm=getActivity().getSupportFragmentManager();
               DatePickerFragment dialog=DatePickerFragment.newIntent(mCrime.getDate());
               dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
               dialog.show(fm, DIALOG_DATE);
           }
       });

        mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton=(Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
              ShareCompat.IntentBuilder.from(getActivity()).setType("text/plain").
                  setText(getCrimeReport()).setSubject(getString(R.string.crime_report_subject)).
                  setChooserTitle(R.string.send_report).
                  startChooser();
                //Intent i=new Intent(Intent.ACTION_SEND);
                //i.setType("text/plain");
                //i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                //i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                //i=Intent.createChooser(i,getString(R.string.send_report));
                //startActivity(i);
            }
        });


        mSuspectButton=(Button)v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS)!=
                   PackageManager.PERMISSION_GRANTED ){
                  ActivityCompat.requestPermissions(getActivity(),
                      new String[]{Manifest.permission.READ_CONTACTS},REQUEST_CONTACT);
                }else{
                    read_Contaacts();
                }


            }
        });
        if(mCrime.getSuspect()!=null)
            mSuspectButton.setText(mCrime.getSuspect());


        mTelButton=(Button)v.findViewById(R.id.crime_tel);
        if(mCrime.getPhoneNumber()==null){
          mTelButton.setEnabled(false);
        }
        else
            mTelButton.setEnabled(true);
        mTelButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mCrime.getPhoneNumber()));
                if(intent.resolveActivity(getActivity().getPackageManager())!=null)
                    startActivity(intent);
            }
        });


        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto=
                mPhotoFile!=null&&captureImage.resolveActivity(getActivity().getPackageManager())!=null;
        mPhotoButton.setEnabled(canTakePhoto);
        if(canTakePhoto){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//            全尺寸照片保存在MediaStore.EXTRA_OUTPUT中的指向存储路径的Uri
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPhotoFile.exists())
                {
                    Log.i(TAG, "onClick: no photo");
                    return;
                }
                
                ImageFragment.newInstance(mPhotoFile.getPath()).show(getFragmentManager(),DIALOG_IMAGE);
            }
        });
        ViewTreeObserver observer=mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                height=v.getHeight();
                width=v.getWidth();
                updatePhotoView(width,height);
                Log.d(TAG, "onGlobalLayout: Height="+height+" width="+width);
            }
        });
        return v;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_crime,menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).removeCrime(mCrime);
                if(getActivity().findViewById(R.id.activity_crime_pager_view_pager)==null)
                     getFragmentManager().beginTransaction().remove(this).commit();
               else
                      getActivity().finish();
                mCallbacks.onCrimeUpdated(mCrime);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode!=Activity.RESULT_OK)
            return;
        if(requestCode==REQUEST_DATE){
            Date date=(Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }else if (requestCode==REQUEST_CONTACT&data!=null){
            Uri contactUri=data.getData();
            String []queryFields=new String[]{ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor c=getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            //FROM table_name, col,where="?",  ,ordeby

            try{
                if(c.getCount()==0)
                    return;

                c.moveToFirst();
                String suspect=c.getString(0);
                String number=c.getString(1);
                mCrime.setSuspect(suspect);
                updateCrime();
                mCrime.setPhoneNumber(number);
                Log.d(TAG, "The Suspect is "+ suspect);
                mSuspectButton.setText(suspect);
                mTelButton.setEnabled(true);
            }finally {
                c.close();
            }
        }else if(requestCode==REQUEST_PHOTO){
            updatePhotoView(width,height);
            Log.d(TAG, "onActivityResult: return photo");
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks=null;
    }
  @Override
  public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
    switch (requestCode){
      case REQUEST_CONTACT:
        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
          read_Contaacts();
        }else{
          Toast.makeText(this.getContext(),"You deined the permission",Toast.LENGTH_SHORT).show();
          Log.d(TAG, "onRequestPermissionsResult: Toast()");
        }
        break;
      default:
    }
  }
  private void read_Contaacts() {
    final Intent pickContact=new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
    try{
      startActivityForResult(pickContact,REQUEST_CONTACT);
    }catch (SecurityException e){
      e.printStackTrace();
    }
    final PackageManager packageManager=getActivity().getPackageManager();
    if(packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY)==null)
      mSuspectButton.setEnabled(false);
  }

    private void updateDate() {
        mDateButton.setText(DateFormat.format("yyyy年MM月dd日E",mCrime.getDate()));
    }
    private String getCrimeReport(){
        String solvedString=null;
        if(mCrime.isSolved()){
            solvedString=getString(R.string.crime_report_solved);
        }else{
            solvedString=getString(R.string.crime_report_unsolved);
        }
        String dateFormat="EEE,MM月dd日";
        String dateString=DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspect=mCrime.getSuspect();
        if(suspect==null){
            suspect=getString(R.string.crime_report_no_suspect);
        }else
            suspect=getString(R.string.crime_report_suspect,suspect);

        String report=getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);

        return report;
    }
    private void updatePhotoView(int width,int height){
        if(mPhotoFile==null||!mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), width,height);
            mPhotoView.setImageBitmap(bitmap);
            Log.i(TAG, "updatePhotoView: ");
        }
    }
    private void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

}
