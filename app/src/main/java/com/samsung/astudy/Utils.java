package com.samsung.astudy;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Song on 2017-01-15.
 */

public class Utils {

    public static String getMyPhoneNumber(Context context){
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

    public static LatLng getLatLng(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        Address addr;
        LatLng location = null;
        try {
            List<Address> listAddress = geocoder.getFromLocationName(address, 1);

            if (listAddress.size() > 0) { // 주소값이 존재 하면
                addr = listAddress.get(0); // Address형태로
                Log.e("Astudy",address + " : " + addr);
                location = new LatLng(addr.getLatitude(), addr.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    public static String getAddress(Context context, double lat, double lng){
        String curAddress = "현재 위치를 확인할 수 없습니다";
        Geocoder geocoder = new Geocoder(context, Locale.KOREA);
        List<Address> address;
        try {
            if(geocoder != null){
                address = geocoder.getFromLocation(lat, lng, 1);
                if(address != null && address.size()>0){
                    curAddress = address.get(0).getAddressLine(0).toString().substring(5);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return curAddress;
    }
}
