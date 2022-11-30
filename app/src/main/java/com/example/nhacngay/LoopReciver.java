package com.example.nhacngay;

import android.app.AlarmManager;
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
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LoopReciver extends WakefulBroadcastReceiver {
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {

        database= new Database(context, "QuanLySK.sqlite",null,1);
        arrayListNgayDUONG= new ArrayList<>();
        arrayListNgayAM=new ArrayList<>();

        getDataNgayDuong();
        getDataNgayAm();

        Calendar ngayHienTai= Calendar.getInstance();
        ngayDuongCuahientai= ngayHienTai.get(Calendar.DATE);
        thangDuongCuaHienTai=ngayHienTai.get(Calendar.MONTH)+1;
        namDuongCuaHienTai=ngayHienTai.get(Calendar.YEAR);

        ChineseCalendar ngayam = new ChineseCalendar(ngayHienTai.getTime());
        ngayAmCuaHienTai=ngayam.get(ChineseCalendar.DATE);
        thangAmCuaHienTai=ngayam.get(ChineseCalendar.MONTH)+1;
        namAmCuaHienTai=ngayam.get(Calendar.YEAR);

        tinhngaysapdenLichAm(context);
        tinhngaysapdenLichDuong(context);

    }

    private void tinhngaysapdenLichDuong(Context context) {
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
                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcherevent);
                Notification notificationA=new NotificationCompat.Builder(context,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 5 ngày nữa đến"+arrayListNgayDUONG.get(i).tenSk+" vào: "+thuTRongTuan)
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notificationA);
                }
            }
            else if(ngayduongconlaidensk==3){
                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcherevent);
                Notification notification= new NotificationCompat.Builder(context,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 3 ngày nữa đến "+arrayListNgayDUONG.get(i).tenSk+" vào: "+thuTRongTuan)
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notification);
                }
            }
            else if(ngayduongconlaidensk==1){
                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcherevent);
                Notification notification= new NotificationCompat.Builder(context,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 1 ngày nữa đến "+arrayListNgayDUONG.get(i).tenSk+" vào : "+thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notification);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void tinhngaysapdenLichAm(Context context) {
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
                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcherevent);
                Notification notificationA=new NotificationCompat.Builder(context,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 5 ngày nữa đến"+arrayListNgayAM.get(i).tenSk+" vào : "+thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notificationA);
                }
            }
            else if(ngayconlaidensk==3){
                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcherevent);
                Notification notification= new NotificationCompat.Builder(context,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 3 ngày nữa đến "+arrayListNgayAM.get(i).tenSk+" vào : "+thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notification);
                }
            }
            else if(ngayconlaidensk==1){
                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcherevent);
                Notification notification= new NotificationCompat.Builder(context,MyApplication.CHANNEL_ID)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 1 ngày nữa đến "+arrayListNgayAM.get(i).tenSk+" vào : "+thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager != null){
                    notificationManager.notify(getNotificationid(),notification);
                }
            }
        }
    }

    private int getNotificationid(){
        return (int) new Date().getTime();
    }
    private void getDataNgayDuong() {
        Cursor datangayduong = database.GetData("SELECT * FROM NgayDuong");
        arrayListNgayDUONG.clear();
        while (datangayduong.moveToNext()) {
            String tenSK = datangayduong.getString(1);
            String Ngay = datangayduong.getString(2);
            arrayListNgayDUONG.add(new CLASSNGAYDUONGLICH(tenSK,Ngay));
        }
    }
    private void getDataNgayAm() {
        Cursor datangayam = database.GetData("SELECT * FROM NgayAm");
        arrayListNgayAM.clear();
        while (datangayam.moveToNext()) {
            String tenSK = datangayam.getString(1);
            String Ngay = datangayam.getString(2);
            arrayListNgayAM.add(new CLASSNGAYAMLICH(tenSK,Ngay));
        }
    }
}
