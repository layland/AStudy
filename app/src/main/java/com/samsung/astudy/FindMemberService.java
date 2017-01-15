package com.samsung.astudy;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Song on 2017-01-10.
 */

public class FindMemberService extends Service {

    private Context mContext;
    private String TAG = "FindMemberService";
    private LocationManager mLocationManager;
    private SmsManager mSmsManager;
    private double mMyLat = 0.0f;
    private double mMyLng = 0.0f;
    private static final int REPORT_LOCATION = 1;
    private static final int DESTROY_SERVICE = 2;
    private ArrayList<String> mDestList;
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mSmsManager = SmsManager.getDefault();
        mContext = getApplicationContext();
        Intent sentIntent = new Intent("ACTION_SMS_SENT");
        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent, 0);
        IntentFilter incomingFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mBroadcastReceiver, incomingFilter);

        mDestList = new ArrayList<String>();
        mDestList.add(Utils.getMyPhoneNumber(getApplicationContext()));
        mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, Looper.myLooper());
        mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener,Looper.myLooper());
        mHandler.sendEmptyMessageDelayed(DESTROY_SERVICE,30000);


    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REPORT_LOCATION:
                    String message = Utils.getAddress(mContext,mMyLat, mMyLng);
                    for (int i = 0 ; i<mDestList.size() ; i++){
                        sendSMS(mDestList.get(i), message);
                    }
                    mLocationManager.removeUpdates(mLocationListener);
                case DESTROY_SERVICE:
                    Log.d(TAG,"stopSelf");
                    stopSelf();
                default:
                    break;
            }
        }
    };

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mMyLat = location.getLatitude();
            mMyLng = location.getLongitude();

            mHandler.sendEmptyMessage(REPORT_LOCATION);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void sendSMS(String dest, String message){
        Intent sentIntent = new Intent("ACTION_SMS_SENT");
        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent, 0);

        mSmsManager.sendTextMessage(dest,null,message,sentPI, null);
        IntentFilter sentIntentFilter = new IntentFilter("ACTION_SMS_SENT");
        registerReceiver(mBroadcastReceiver, sentIntentFilter);

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("ACTION_SMS_SENT")){
                if(getResultCode() == Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(), "전송성공", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "전송실패", Toast.LENGTH_SHORT).show();
                }
            }else if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Toast.makeText(getApplicationContext(), "메시지수신", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "보낸사람:"+sender+" 내용:"+message, Toast.LENGTH_LONG).show();

                    Float lat = Float.parseFloat(message.split(",")[0]);
                    Float lng =  Float.parseFloat(message.split(",")[1]);

                }
            }

        }
    };



}
