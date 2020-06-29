package com.example.dani_pc.bobo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<SpinnerData> {

    private Context context;
    private List<SpinnerData> spinnerDatas;

    public CustomSpinnerAdapter(Context context, int resource, List<SpinnerData> spinnerDatas){
        super(context,resource,spinnerDatas);
        this.context = context;
        this.spinnerDatas = spinnerDatas;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return myCustomSpinnerView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return myCustomSpinnerView(position, convertView, parent);
    }

    private View myCustomSpinnerView(int position, View myView, ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.spinner_layout, parent,false);

        TextView tv = (TextView)customView.findViewById(R.id.spinnerText);
        ImageView imv = (ImageView)customView.findViewById(R.id.spinnerImage);

        tv.setText(spinnerDatas.get(position).getIconName());
        imv.setImageResource(spinnerDatas.get(position).getIcon());

        return customView;

    }
}

