package com.example.nhacngay;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

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
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhxa();
        Intent intent= new Intent(this,MainActivity.class);
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

        Calendar ngayHienTai= Calendar.getInstance();
        ngayDuongCuahientai= ngayHienTai.get(Calendar.DATE);
        thangDuongCuaHienTai=ngayHienTai.get(Calendar.MONTH)+1;
        namDuongCuaHienTai=ngayHienTai.get(Calendar.YEAR);
        String ngayDuongCuahientaiString= Integer.toString(ngayDuongCuahientai);
        String thangDuongCuaHienTaiString= Integer.toString(thangDuongCuaHienTai);


        ChineseCalendar ngayam = new ChineseCalendar(ngayHienTai.getTime());
        ngayAmCuaHienTai=ngayam.get(ChineseCalendar.DATE);
        thangAmCuaHienTai=ngayam.get(ChineseCalendar.MONTH)+1;
        namAmCuaHienTai=ngayam.get(Calendar.YEAR);
        String ngayAmCuaHienTaiString= Integer.toString(ngayAmCuaHienTai);
        String thangAmCuaHienTaiString= Integer.toString(thangAmCuaHienTai);

        tinhngaysapdenLichAm();
        tinhngaysapdenLichDuong();

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

        //ĐK reciver nhận thông báo
        Intent reciver=new Intent(this,LoopReciver.class);
        repeat_Pending=PendingIntent.getBroadcast(this,0,reciver,0);

        Loop();
//        TVngayam.setText("Ngày"+ngayDuongCuahientaiString+"Tháng"+thangDuongCuaHienTaiString);
//        tvNgayconlai.setText("Ngày"+ngayAmCuaHienTaiString+"Tháng"+thangAmCuaHienTaiString);
//

//        chuyendoi.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View view) {
//                String ngay= ngayNhap.getText().toString();
//                Date dateNhap= null, ngayhomnay=null;
//                try {
//                    ngayhomnay= new SimpleDateFormat("dd/MM/yyyy").parse(String.valueOf(ngayHienTai.getTime()));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    dateNhap = new SimpleDateFormat("dd/MM/yyyy").parse(ngay);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                ChineseCalendar ngayam = new ChineseCalendar(dateNhap);
//
//                Calendar ngayNhapVao= Calendar.getInstance();
//                String DateNhapString= dateNhap.toString();
//                String datenhapsplit[]=ngay.split("\\/");
//                ngayNhapVao.set(Integer.parseInt(datenhapsplit[2]),Integer.parseInt(datenhapsplit[1]),Integer.parseInt(datenhapsplit[0]));
//                int ngayconlai= (int) ((ngayHienTai.getTimeInMillis()-ngayNhapVao.getTimeInMillis())/(1000*60*60*24));
//                int thangnhan=ngayam.get(ChineseCalendar.MONTH);
//                int thangam=thangnhan+1;
//                TVngayam.setText("Ngày"+ngayam.get(ChineseCalendar.DATE)+"Tháng"+thangam);
//                tvNgayconlai.setText("còn lại: "+ngayconlai);
//            }
//        });
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

    public  void tinhngaysapdenLichDuong(){
        Calendar ngayDuongdienraSk= Calendar.getInstance();
        int ngayduongconlaidensk=0;

        Calendar ngayLichDuonghientai=Calendar.getInstance();
        ngayLichDuonghientai.set(namDuongCuaHienTai,thangDuongCuaHienTai-1,ngayDuongCuahientai);
        for (int i=0;i<arrayListNgayDUONG.size();i++){
            String catngayduong[]=arrayListNgayDUONG.get(i).getNgayDuong().split("\\/");
            ngayDuongdienraSk.set(namDuongCuaHienTai,Integer.parseInt(catngayduong[1])-1,Integer.parseInt(catngayduong[0]));
            ngayduongconlaidensk= (int) ((ngayDuongdienraSk.getTimeInMillis()-ngayLichDuonghientai.getTimeInMillis())/(1000*60*60*24));

            switch (ngayDuongdienraSk.get(Calendar.DAY_OF_WEEK)){
                case 1: thuTRongTuan="Chủ nhật";
                    break;
                case 2:   thuTRongTuan="Thứ hai";
                    break;
                case 3:   thuTRongTuan="Thứ ba";
                    break;
                case 4:   thuTRongTuan="Thứ tư";
                    break;
                case 5:   thuTRongTuan="Thứ năm";
                    break;
                case 6:   thuTRongTuan="Thứ sáu";
                    break;
                case 7:   thuTRongTuan="Thứ bảy";
                    break;
            }



            if(ngayduongconlaidensk==5){
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcherevent);
                Notification notificationA=new NotificationCompat.Builder(MainActivity.this,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 5 ngày nữa đến"+arrayListNgayDUONG.get(i).tenSk+" vào: "+thuTRongTuan)
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notificationA);
                }
            }
            else if(ngayduongconlaidensk==3){
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcherevent);
                Notification notification= new NotificationCompat.Builder(MainActivity.this,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 3 ngày nữa đến "+arrayListNgayDUONG.get(i).tenSk+" vào: "+thuTRongTuan)
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notification);
                }
            }
            else if(ngayduongconlaidensk==1){
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcherevent);
                Notification notification= new NotificationCompat.Builder(MainActivity.this,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 1 ngày nữa đến "+arrayListNgayDUONG.get(i).tenSk+" vào : "+thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notification);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void tinhngaysapdenLichAm(){
        ChineseCalendar ngayAmdienraSk= new ChineseCalendar();
        int ngayconlaidensk=0;
        Calendar ngayHienTai= Calendar.getInstance();

        ChineseCalendar ngayamLichHienTai = new ChineseCalendar(ngayHienTai.getTime());
        for (int i=0;i<arrayListNgayAM.size();i++){
            String catngayam[]=arrayListNgayAM.get(i).getNgayAm().split("\\/");
            ngayAmdienraSk.set(namAmCuaHienTai,Integer.parseInt(catngayam[1])-1,Integer.parseInt(catngayam[0]));
            ngayconlaidensk= (int) ((ngayAmdienraSk.getTimeInMillis()-ngayamLichHienTai.getTimeInMillis())/(1000*60*60*24));

            switch (ngayAmdienraSk.get(Calendar.DAY_OF_WEEK)){
                case 1: thuTRongTuan="Chủ nhật";
                         break;
                case 2:   thuTRongTuan="Thứ hai";
                    break;
                case 3:   thuTRongTuan="Thứ ba";
                    break;
                case 4:   thuTRongTuan="Thứ tư";
                    break;
                case 5:   thuTRongTuan="Thứ năm";
                    break;
                case 6:   thuTRongTuan="Thứ sáu";
                    break;
                case 7:   thuTRongTuan="Thứ bảy";
                    break;
            }


            if(ngayconlaidensk==5){
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcherevent);
                Notification notificationA=new NotificationCompat.Builder(MainActivity.this,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 5 ngày nữa đến"+arrayListNgayAM.get(i).tenSk+" vào : "+thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notificationA);
                }
            }
            else if(ngayconlaidensk==3){
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcherevent);
                Notification notification= new NotificationCompat.Builder(MainActivity.this,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 3 ngày nữa đến "+arrayListNgayAM.get(i).tenSk+" vào : "+thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notification);
                }
            }
            else if(ngayconlaidensk==1){
                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcherevent);
                Notification notification= new NotificationCompat.Builder(MainActivity.this,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 1 ngày nữa đến "+arrayListNgayAM.get(i).tenSk+" vào : "+thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notification);
                }
            }
        }
    }
    private int getNotificationid(){
        return (int) new Date().getTime();
    }

    private void Loop(){
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar cal= Calendar.getInstance();
        cal.set(Calendar.MINUTE,10);
        cal.set(Calendar.HOUR_OF_DAY,20);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,repeat_Pending);
        Toast.makeText(this, "Đã thiết lập", Toast.LENGTH_SHORT).show();
    }
}