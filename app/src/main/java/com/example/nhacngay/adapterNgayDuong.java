package com.example.nhacngay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class adapterNgayDuong extends BaseAdapter {
    private MainActivity context;
    private int layout;
    private List<CLASSNGAYDUONGLICH> NgayDuongLichList;
    Database database;
    Intent intent;

    public adapterNgayDuong(MainActivity context, int layout, List<CLASSNGAYDUONGLICH> ngayDuongLichList) {
        this.context = context;
        this.layout = layout;
        NgayDuongLichList = ngayDuongLichList;
    }

    @Override
    public int getCount() {
        return NgayDuongLichList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{
        TextView ngay,tensk;
        ImageView btnxoask;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        database= new Database(context, "QuanLySK.sqlite",null,1);
        if(view==null){
            viewHolder= new ViewHolder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(layout,null);

            //Anh xa
            viewHolder.ngay= view.findViewById(R.id.Ngaydienrask);
            viewHolder.tensk=view.findViewById(R.id.tvtensk);
            viewHolder.btnxoask=view.findViewById(R.id.btndelete);
            view.setTag(viewHolder);

        }
        else{
            viewHolder= (ViewHolder) view.getTag();
        }
        CLASSNGAYDUONGLICH ngayDuong= NgayDuongLichList.get(i);
        viewHolder.tensk.setText(ngayDuong.getTenSk());
        viewHolder.ngay.setText("Ngày: "+ngayDuong.getNgayDuong());
        viewHolder.btnxoask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.QuerryData("Delete from NgayDuong Where maNgayDuong="+ngayDuong.getMaNgayDuong());
                intent= new Intent(context,MainActivity.class);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
