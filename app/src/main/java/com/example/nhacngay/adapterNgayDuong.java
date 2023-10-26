package com.example.nhacngay;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class adapterNgayDuong extends BaseAdapter {
    private MainActivity context;
    private int layout;
    private List<CLASSNGAYDUONGLICH> NgayDuongLichList;
    private static DatabaseReference mDatabase;
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
        Log.e("onClick: ", String.valueOf(ngayDuong.getMadb()));
        viewHolder.btnxoask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("data", MODE_PRIVATE);
                String uid = sharedPreferences.getString("uid", "");
                database.QuerryData("Delete from NgayDuong Where maNgayDuong="+ngayDuong.getMaNgayDuong());
                Log.e("onClick: ", String.valueOf(ngayDuong.getMadb()));
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(uid).child("DuongLich").child(String.valueOf(ngayDuong.getMadb())).removeValue();

                intent= new Intent(context,MainActivity.class);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
