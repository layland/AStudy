package com.samsung.astudy;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Song on 2016-12-28.
 */

public class FindMember extends Activity implements OnMapReadyCallback{

    private Context mContext;
    private ListView mListView = null;
    private FindMemberAdapter mAdapter = null;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private SmsManager mSmsManager;
    private double mMyLat = 0.0f;
    private double mMyLng = 0.0f;
    private CheckBox mCheckBox;
    private ArrayList<PersonData> mMembers;
    private AlarmManager mAlarmManager;
    private boolean mLocationEnabled = false;
    private String TAG = "Astudy";

    private static final int REPORT_LOCATION = 1;
    private static final int FIND_SERVICE_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.findfriend);

        mContext = getApplicationContext();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mListView = (ListView) findViewById(R.id.member_loc_list);
        mAdapter = new FindMemberAdapter(this);
        mListView.setAdapter(mAdapter);

        mSmsManager = SmsManager.getDefault();
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mCheckBox = (CheckBox) findViewById(R.id.checkbox_find);
        loadPreference();

        IntentFilter incomingFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mBroadcastReceiver, incomingFilter);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mCheckBox.isChecked()){
                    if(checkLocationEnabled())
                        registerAlarm();
                    else{
                        openLocationDialog();
                        mCheckBox.setChecked(false);
                    }
                }else{
                    cancelAlarm();
                }
            }
        });


    }

    public void registerAlarm(){
        Log.d(TAG, "registerAlarm");
        Intent intent = new Intent(this, FindMemberService.class);
        PendingIntent pIntent = PendingIntent.getService(this, FIND_SERVICE_CODE, intent,0);

        try {
            // 내일 아침 8시 10분에 처음 시작해서, 24시간 마다 실행되게
            Date tomorrow = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2017-01-15 08:30:00");
            mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, tomorrow.getTime(), 24 * 60 * 60 * 1000, pIntent);
            Toast.makeText(this, "스터디날 아침 8시 30분에 스터디원들에게 위치가 발송됩니다.",Toast.LENGTH_LONG).show();
        } catch (ParseException e){
            e.printStackTrace();
        }

    }

    public void cancelAlarm(){
        Log.d(TAG, "cancelAlarm");
        Intent intent = new Intent(this, FindMemberService.class);
        PendingIntent pIntent = PendingIntent.getService(this, FIND_SERVICE_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pIntent);
        pIntent.cancel();
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REPORT_LOCATION:

                    mLocationManager.removeUpdates(mLocationListener);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mMyLat = location.getLatitude();
            mMyLng = location.getLongitude();
            LatLng latLng = new LatLng(mMyLat, mMyLng);
            updatePosition("나", latLng);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            openLocationDialog();
        }
    };

    private boolean checkLocationEnabled(){
        if(!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return false;
        }else{
            return true;
        }
    }
    private void openLocationDialog(){
        new AlertDialog.Builder(FindMember.this)
                .setMessage("위치설정을 켜주세요")
                .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소",null).show();
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("ACTION_SMS_SENT")){
                if(getResultCode() == Activity.RESULT_OK){
                    Toast.makeText(mContext, "전송성공", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "전송실패", Toast.LENGTH_SHORT).show();
                }
            }else if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Toast.makeText(mContext, "메시지수신", Toast.LENGTH_SHORT).show();
                Bundle bundle = intent.getExtras();
                if(bundle!=null){
                    String message = null;
                    String sender = null;

                    Object pdus[] = (Object[]) bundle.get("pdus");
                    for (Object pdu : pdus){
                        SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);
                        message = msg.getMessageBody();
                        sender = msg.getOriginatingAddress();
                    }


                    updatePosition(sender, Utils.getLatLng(mContext, "대한민국 "+ message));    //TODO: sender -> name
                    mAdapter.addItem(false, sender, message);
                }
            }

        }
    };

    private void updatePosition(String name, LatLng latLng){
        MarkerOptions options = new MarkerOptions().position(latLng)
                .title(name);
        Marker marker = mMap.addMarker(options);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
    }

    private void savePreference(){
        SharedPreferences prefs = getSharedPreferences("location",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();


        editor.putBoolean("setTimer",mCheckBox.isChecked());
        editor.commit();
    }

    private void loadPreference(){
        SharedPreferences prefs = getSharedPreferences("location",MODE_PRIVATE);
        boolean isChecked = prefs.getBoolean("setTimer",false);
        mCheckBox.setChecked(isChecked);

    }

    @Override
    protected void onDestroy() {
        savePreference();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(!checkLocationEnabled()){
            openLocationDialog();
        }else{
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, Looper.myLooper());
        }
        super.onResume();
    }






}


