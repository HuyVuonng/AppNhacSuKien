package com.example.nhacngay;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.ChineseCalendar;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private PendingIntent repeat_Pending;
    Context context;
    TextView TVngayam,tvNgayconlai;
    String thuTRongTuan;
    Database database;
    EditText ngayNhap;
    int ngayDuongCuahientai,thangDuongCuaHienTai,namDuongCuaHienTai,ngayAmCuaHienTai,thangAmCuaHienTai,namAmCuaHienTai;
    ListView ngayDuonglv,ngayAMLv;
    adapterNgayDuong adapterNgayDuongg;
    adapterNgayAm adapterNgayamm;
    FloatingActionButton add;
    ArrayList<CLASSNGAYAMLICH> arrayListNgayAM;
    ArrayList<CLASSNGAYDUONGLICH> arrayListNgayDUONG;
    Button chuyendoi;

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//            Intent serviceIntent = new Intent(MainActivity.this, ForegroundService.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(serviceIntent);
//            }
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!forceServicesRunning()){
            Intent serviceIntent= new Intent(MainActivity.this, ForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }
        }

        anhxa();



        arrayListNgayAM=new ArrayList<>();
        arrayListNgayDUONG= new ArrayList<>();
        adapterNgayDuongg = new adapterNgayDuong(this,R.layout.dongsk,arrayListNgayDUONG);
        ngayDuonglv.setAdapter(adapterNgayDuongg);
        adapterNgayamm = new adapterNgayAm(this,R.layout.dongsk,arrayListNgayAM);
        ngayAMLv.setAdapter(adapterNgayamm);
        //Tao DB
        database= new Database(this, "QuanLySK.sqlite",null,1);
        database.QuerryData("CREATE TABLE IF NOT EXISTS NgayDuong (maNgayDuong INTEGER PRIMARY KEY AUTOINCREMENT,SuKienNgayDuong TEXT,NgayDuong varchar(20))");
        database.QuerryData("CREATE TABLE IF NOT EXISTS NgayAm (maNgayAm INTEGER PRIMARY KEY AUTOINCREMENT,SuKienNgayAm TEXT,NgayAm varchar(20))");


        getDataNgayDuong();
        getDataNgayAm();




        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog= new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialogthemsukien);
                dialog.show();

                Button Them= dialog.findViewById(R.id.buttonThem);
                Button Huy= dialog.findViewById(R.id.buttonHuy);
                RadioButton duonglich=dialog.findViewById(R.id.radioButtonDuongLich);
                RadioButton amlich=dialog.findViewById(R.id.radioButtonAmLich);
                EditText tenSK=dialog.findViewById(R.id.editTexttenSK);
                EditText ngay=dialog.findViewById(R.id.editTextNgay);

                Huy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                Them.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tensk= tenSK.getText().toString().trim();
                        String ngaySk=ngay.getText().toString().trim();
                        String checkngay[]=ngaySk.split("\\/");
                        if(TextUtils.isEmpty(tensk)||TextUtils.isEmpty(ngaySk)){
                            Toast.makeText(MainActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        }
                        else if(checkngay.length<2){
                            Toast.makeText(MainActivity.this, "Bạn đang nhập sai ngày", Toast.LENGTH_SHORT).show();
                        }
                        else if(duonglich.isChecked()){
                            database.QuerryData("INSERT INTO NgayDuong VALUES(null,'"+tensk+"','"+ngaySk+"')");
                        }
                        else{
                            database.QuerryData("INSERT INTO NgayAm VALUES(null,'"+tensk+"','"+ngaySk+"')");
                        }
                        dialog.dismiss();
                        Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(refresh);
                        MainActivity.this.finish();
                    }
                });
            }
        });


    }

    private void getDataNgayAm() {
        Cursor datangayam = database.GetData("SELECT * FROM NgayAm");
        arrayListNgayAM.clear();
        while (datangayam.moveToNext()) {
            int mangay= datangayam.getInt(0);
            String tenSK = datangayam.getString(1);
            String Ngay = datangayam.getString(2);
            arrayListNgayAM.add(new CLASSNGAYAMLICH(tenSK,Ngay,mangay));
        }
        adapterNgayamm.notifyDataSetChanged();
    }

    private void getDataNgayDuong() {
        Cursor datangayduong = database.GetData("SELECT * FROM NgayDuong");
        arrayListNgayDUONG.clear();
        while (datangayduong.moveToNext()) {
            int mangay= datangayduong.getInt(0);
            String tenSK = datangayduong.getString(1);
            String Ngay = datangayduong.getString(2);
            arrayListNgayDUONG.add(new CLASSNGAYDUONGLICH(tenSK,Ngay,mangay));
        }
        adapterNgayDuongg.notifyDataSetChanged();
    }

    private void anhxa() {
        add=findViewById(R.id.floatingActionButton);
        ngayDuonglv=findViewById(R.id.lvNgayDuong);
        ngayAMLv=findViewById(R.id.lvNgayAm);
    }



    public boolean forceServicesRunning(){
        ActivityManager activityManager= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(ForegroundService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    };
}