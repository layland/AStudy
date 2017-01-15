package com.samsung.astudy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.astudy.FindMember;
import com.samsung.astudy.PersonData;
import com.samsung.astudy.R;

import java.util.ArrayList;

/**
 * Created by Song on 2017-01-15.
 */

public class FindMemberAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<PersonData> mListData = new ArrayList<PersonData>();

    public FindMemberAdapter(Context mContext){
        super();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int idx) {
        return mListData.get(idx);
    }

    @Override
    public long getItemId(int idx) {
        return idx;
    }

    @Override
    public View getView(int idx, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.study_phonebook_item,null);

            holder.mName = (TextView) convertView.findViewById(R.id.phonebook_name);
            holder.mLoc = (TextView) convertView.findViewById(R.id.phonebook_number);
            holder.mMW = (ImageView) convertView.findViewById(R.id.phonebook_mw);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        PersonData member = mListData.get(idx);

        holder.mName.setText(member.getmName());
        holder.mLoc.setText(member.getmTelNum());

        if (mListData.get(idx).ismIsWoman()) {
            holder.mMW.setImageResource(R.drawable.girl);
        } else {
            holder.mMW.setImageResource(R.drawable.boy);
        }

        return convertView;
    }

    public void addItem(boolean isWoman, String name, String loc){
        PersonData addInfo = null;
        addInfo = new PersonData(false, "astudy", name, loc);   // TODO: 2017-01-01
        mListData.add(addInfo);
        dataChange();
    }

    public void remove(int idx){
        mListData.remove(idx);
        dataChange();
    }

    public void dataChange(){
        notifyDataSetChanged();
    }

    public class ViewHolder {
        public ImageView mMW;
        public TextView mName;
        public TextView mLoc;
    }
}
