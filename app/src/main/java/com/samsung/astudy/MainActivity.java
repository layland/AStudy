package com.samsung.astudy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.samsung.astudy.phoneBookDB.PhoneBookDBHelper;

public class MainActivity extends Activity implements View.OnClickListener {

    private final int PERMISSION_ALL = 1;
    private String[] PERMISSIONS =
            {Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Loading Image
        //startActivity(new Intent(this, Intro.class));
        setContentView(R.layout.activity_main);

        LinearLayout firstLayout = (LinearLayout) findViewById(R.id.first_container);
        LinearLayout secondLayout = (LinearLayout) findViewById(R.id.second_container);
        LinearLayout thirdLayout = (LinearLayout) findViewById(R.id.third_container);

        firstLayout.setOnClickListener(this);
        secondLayout.setOnClickListener(this);
        thirdLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_container:
                Intent phoneBook = new Intent(this, StudyPhoneBook.class);
                startActivity(phoneBook);
                break;
            case R.id.second_container:
                Intent findMember = new Intent(this, FindMember.class);
                if(hasPermissions(getApplicationContext(), PERMISSIONS)){
                    startActivity(findMember);
                }else{
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                }

                break;
            case R.id.third_container:
                Intent myDailyWord = new Intent(this,myDailyWord.class);
                startActivity(myDailyWord);
                break;
            default:
                break;
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                boolean isAllGranted = true;
                for (int i=0; i<grantResults.length;i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        isAllGranted = false;
                    }
                }
                if(isAllGranted){
                    Intent findMember = new Intent(this, FindMember.class);
                    startActivity(findMember);
                }else{
                    Toast.makeText(getApplicationContext(), R.string.need_findmember_permission, Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

}
