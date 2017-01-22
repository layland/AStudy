package com.samsung.astudy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class StudyPhoneBookAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<PersonData> mPerson = new ArrayList<>();

    public StudyPhoneBookAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mPerson.size();
    }

    @Override
    public PersonData getItem(int position) {
        return mPerson.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = mInflater.inflate(R.layout.study_phonebook_item, parent, false);
            bindView(v, position);
        }

        return v;
    }

    public void bindView(View view, int position) {
        PersonDataHolder holder = new PersonDataHolder();
        holder.mMW = (ImageView) view.findViewById(R.id.phonebook_mw);
        holder.mName = (TextView) view.findViewById(R.id.phonebook_name);
        holder.mNumber = (TextView) view.findViewById(R.id.phonebook_number);

        if (mPerson.get(position).ismIsWoman()) {
            holder.mMW.setImageResource(R.drawable.girl);
        } else {
            holder.mMW.setImageResource(R.drawable.boy);
        }

        holder.mName.setText(mPerson.get(position).getmName());
        holder.mNumber.setText(mPerson.get(position).getmTelNum());

        view.setTag(holder);
    }

    public class PersonDataHolder {
        ImageView mMW;
        TextView mName;
        TextView mNumber;
    }

    public void addList(PersonData data) {
        mPerson.add(data);
    }

    public void clear () {
        mPerson.clear();
    }

    public void printLog() {
        for(PersonData p : mPerson) {
            Log.d("hi","in mPerson:"+p.getmName()+", "+p.getmStudyName());
        }
    }
}
