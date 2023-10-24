package com.example.nhacngay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.util.ChineseCalendar;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ForegroundService extends Service {
    String thuTRongTuan;
    Database database;

    int ngayDuongCuahientai, thangDuongCuaHienTai, namDuongCuaHienTai, ngayAmCuaHienTai, thangAmCuaHienTai, namAmCuaHienTai;
    ArrayList<CLASSNGAYAMLICH> arrayListNgayAM;
    ArrayList<CLASSNGAYDUONGLICH> arrayListNgayDUONG;
    final String NOTIFICATION_CHANNEL_ID = "com.example.nhacngay";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            Log.e("Services", "service is running...");


                            SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                            Integer date = sharedPreferences.getInt("date", 0);


                            database = new Database(ForegroundService.this, "QuanLySK.sqlite", null, 1);
                            arrayListNgayDUONG = new ArrayList<>();
                            arrayListNgayAM = new ArrayList<>();

                            Cursor datangayduong = database.GetData("SELECT * FROM NgayDuong");
                            arrayListNgayDUONG.clear();
                            while (datangayduong.moveToNext()) {
                                String tenSK = datangayduong.getString(1);
                                String Ngay = datangayduong.getString(2);
                                arrayListNgayDUONG.add(new CLASSNGAYDUONGLICH(tenSK, Ngay));
                            }

                            Cursor datangayam = database.GetData("SELECT * FROM NgayAm");
                            arrayListNgayAM.clear();
                            while (datangayam.moveToNext()) {
                                String tenSK = datangayam.getString(1);
                                String Ngay = datangayam.getString(2);
                                arrayListNgayAM.add(new CLASSNGAYAMLICH(tenSK, Ngay));
                            }
                            Calendar ngayHienTai = Calendar.getInstance();
                            ngayDuongCuahientai = ngayHienTai.get(Calendar.DATE);
                            thangDuongCuaHienTai = ngayHienTai.get(Calendar.MONTH) + 1;
                            namDuongCuaHienTai = ngayHienTai.get(Calendar.YEAR);
                            if (date != ngayDuongCuahientai && ngayHienTai.get(Calendar.HOUR_OF_DAY) >= 9) {
                                SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();

                                ChineseCalendar ngayam = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    ngayam = new ChineseCalendar(ngayHienTai.getTime());
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    ngayAmCuaHienTai = ngayam.get(ChineseCalendar.DATE);
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    thangAmCuaHienTai = ngayam.get(ChineseCalendar.MONTH) + 1;
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    namAmCuaHienTai = ngayam.get(Calendar.YEAR);
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    ChineseCalendar ngayAmdienraSk = new ChineseCalendar();
                                    long ngayconlaidensk = 0;

                                    ChineseCalendar ngayamLichHienTai = new ChineseCalendar(ngayHienTai.getTime());
                                    for (int i = 0; i < arrayListNgayAM.size(); i++) {
                                        String catngayam[] = arrayListNgayAM.get(i).getNgayAm().split("\\/");

                                        ngayAmdienraSk.set(namAmCuaHienTai, Integer.parseInt(catngayam[1]) - 1, Integer.parseInt(catngayam[0]));
                                        ngayconlaidensk = (long) (float) ((ngayAmdienraSk.getTimeInMillis() - ngayamLichHienTai.getTimeInMillis()) / (1000 * 60 * 60 * 24));
                                        if (Integer.parseInt(catngayam[0]) < ngayAmCuaHienTai && Integer.parseInt(catngayam[1]) - 1 <= thangAmCuaHienTai) {
                                            ngayconlaidensk = -1;
                                        }
                                        switch (ngayAmdienraSk.get(Calendar.DAY_OF_WEEK)) {
                                            case 1:
                                                thuTRongTuan = "Chủ nhật";
                                                break;
                                            case 2:
                                                thuTRongTuan = "Thứ hai";
                                                break;
                                            case 3:
                                                thuTRongTuan = "Thứ ba";
                                                break;
                                            case 4:
                                                thuTRongTuan = "Thứ tư";
                                                break;
                                            case 5:
                                                thuTRongTuan = "Thứ năm";
                                                break;
                                            case 6:
                                                thuTRongTuan = "Thứ sáu";
                                                break;
                                            case 7:
                                                thuTRongTuan = "Thứ bảy";
                                                break;
                                        }


                                        if (ngayconlaidensk == 7) {
                                            Bitmap bitmap = BitmapFactory.decodeResource(ForegroundService.this.getResources(), R.mipmap.ic_launcherevent);
                                            Notification notification = new NotificationCompat.Builder(ForegroundService.this, NotificationChannelCreate.CHANNEL_ID)
                                                    .setContentTitle("Nhắc nhở")
                                                    .setContentText("Còn 7 ngày nữa đến " + arrayListNgayAM.get(i).tenSk + " vào : " + thuTRongTuan.toString())
                                                    .setSmallIcon(R.mipmap.ic_launcherevent)
                                                    .setLargeIcon(bitmap)
                                                    .build();
                                            NotificationManager notificationManager = (NotificationManager) ForegroundService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                            if (notificationManager != null) {
                                                notificationManager.notify((int) new Date().getTime(), notification);
                                            }
                                            editor.putInt("date", ngayDuongCuahientai);
                                            editor.apply();
                                        } else if (ngayconlaidensk == 3) {
                                            Bitmap bitmap = BitmapFactory.decodeResource(ForegroundService.this.getResources(), R.mipmap.ic_launcherevent);
                                            Notification notification = new NotificationCompat.Builder(ForegroundService.this, NotificationChannelCreate.CHANNEL_ID)
                                                    .setContentTitle("Nhắc nhở")
                                                    .setContentText("Còn 3 ngày nữa đến " + arrayListNgayAM.get(i).tenSk + " vào : " + thuTRongTuan.toString())
                                                    .setSmallIcon(R.mipmap.ic_launcherevent)
                                                    .setLargeIcon(bitmap)
                                                    .build();
                                            NotificationManager notificationManager = (NotificationManager) ForegroundService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                            if (notificationManager != null) {
                                                notificationManager.notify((int) new Date().getTime(), notification);
                                            }
                                            editor.putInt("date", ngayDuongCuahientai);
                                            editor.apply();
                                        } else if (ngayconlaidensk == 0) {
                                            Bitmap bitmap = BitmapFactory.decodeResource(ForegroundService.this.getResources(), R.mipmap.ic_launcherevent);
                                            Notification notification = new NotificationCompat.Builder(ForegroundService.this, NotificationChannelCreate.CHANNEL_ID)
                                                    .setContentTitle("Nhắc nhở")
                                                    .setContentText("Hôm nay là " + arrayListNgayAM.get(i).tenSk)
                                                    .setSmallIcon(R.mipmap.ic_launcherevent)
                                                    .setLargeIcon(bitmap)
                                                    .build();
                                            NotificationManager notificationManager = (NotificationManager) ForegroundService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                            if (notificationManager != null) {
                                                notificationManager.notify((int) new Date().getTime(), notification);
                                            }
                                            editor.putInt("date", ngayDuongCuahientai);
                                            editor.apply();
                                        }
                                    }
                                }
                                Calendar ngayDuongdienraSk = Calendar.getInstance();
                                long ngayduongconlaidensk = 0;
                                ngayHienTai.set(namDuongCuaHienTai, thangDuongCuaHienTai - 1, ngayDuongCuahientai);
                                for (int i = 0; i < arrayListNgayDUONG.size(); i++) {
                                    String catngayduong[] = arrayListNgayDUONG.get(i).getNgayDuong().split("\\/");
                                    ngayDuongdienraSk.set(namDuongCuaHienTai, Integer.parseInt(catngayduong[1]) - 1, Integer.parseInt(catngayduong[0]));
                                    ngayduongconlaidensk = (long) ((float) (ngayDuongdienraSk.getTimeInMillis() - ngayHienTai.getTimeInMillis()) / (1000 * 60 * 60 * 24));
                                    if (Integer.parseInt(catngayduong[0]) < ngayDuongCuahientai && Integer.parseInt(catngayduong[1]) - 1 <= thangDuongCuaHienTai - 1) {
                                        ngayduongconlaidensk = -1;
                                    }
                                    switch (ngayDuongdienraSk.get(Calendar.DAY_OF_WEEK)) {
                                        case 1:
                                            thuTRongTuan = "Chủ nhật";
                                            break;
                                        case 2:
                                            thuTRongTuan = "Thứ hai";
                                            break;
                                        case 3:
                                            thuTRongTuan = "Thứ ba";
                                            break;
                                        case 4:
                                            thuTRongTuan = "Thứ tư";
                                            break;
                                        case 5:
                                            thuTRongTuan = "Thứ năm";
                                            break;
                                        case 6:
                                            thuTRongTuan = "Thứ sáu";
                                            break;
                                        case 7:
                                            thuTRongTuan = "Thứ bảy";
                                            break;
                                    }
                                    if (ngayduongconlaidensk == 7) {
                                        Bitmap bitmap = BitmapFactory.decodeResource(ForegroundService.this.getResources(), R.mipmap.ic_launcherevent);
                                        Notification notification = new NotificationCompat.Builder(ForegroundService.this, NotificationChannelCreate.CHANNEL_ID)
                                                .setContentTitle("Nhắc nhở")
                                                .setContentText("Còn 7 ngày nữa đến " + arrayListNgayDUONG.get(i).tenSk + " vào: " + thuTRongTuan)
                                                .setSmallIcon(R.mipmap.ic_launcherevent)
                                                .setLargeIcon(bitmap)
                                                .build();
                                        NotificationManager notificationManager = (NotificationManager) ForegroundService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (notificationManager != null) {
                                            notificationManager.notify((int) new Date().getTime(), notification);
                                        }
                                        editor.putInt("date", ngayDuongCuahientai);
                                        editor.apply();
                                    } else if (ngayduongconlaidensk == 3) {
                                        Bitmap bitmap = BitmapFactory.decodeResource(ForegroundService.this.getResources(), R.mipmap.ic_launcherevent);
                                        Notification notification = new NotificationCompat.Builder(ForegroundService.this, NotificationChannelCreate.CHANNEL_ID)
                                                .setContentTitle("Nhắc nhở")
                                                .setContentText("Còn 3 ngày nữa đến " + arrayListNgayDUONG.get(i).tenSk + " vào : " + thuTRongTuan.toString())
                                                .setSmallIcon(R.mipmap.ic_launcherevent)
                                                .setLargeIcon(bitmap)
                                                .build();
                                        NotificationManager notificationManager = (NotificationManager) ForegroundService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (notificationManager != null) {
                                            notificationManager.notify((int) new Date().getTime(), notification);
                                        }
                                        editor.putInt("date", ngayDuongCuahientai);
                                        editor.apply();
                                    } else if (ngayduongconlaidensk == 0) {
                                        Bitmap bitmap = BitmapFactory.decodeResource(ForegroundService.this.getResources(), R.mipmap.ic_launcherevent);
                                        Notification notification = new NotificationCompat.Builder(ForegroundService.this, NotificationChannelCreate.CHANNEL_ID)
                                                .setContentTitle("Nhắc nhở")
                                                .setContentText("Hôm nay là " + arrayListNgayDUONG.get(i).tenSk)
                                                .setSmallIcon(R.mipmap.ic_launcherevent)
                                                .setLargeIcon(bitmap)
                                                .build();
                                        NotificationManager notificationManager = (NotificationManager) ForegroundService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (notificationManager != null) {
                                            notificationManager.notify((int) new Date().getTime(), notification);
                                        }
                                        editor.putInt("date", ngayDuongCuahientai);
                                        editor.apply();
                                    }
                                }
                            }

                            try {
                                Thread.sleep(900000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Bitmap bitmap = BitmapFactory.decodeResource(ForegroundService.this.getResources(), R.mipmap.ic_launcherevent);
                                Notification notification = new NotificationCompat.Builder(ForegroundService.this, NotificationChannelCreate.CHANNEL_ID)
                                        .setContentTitle("Nhắc nhở")
                                        .setContentText("Lỗi")
                                        .setSmallIcon(R.mipmap.ic_launcherevent)
                                        .setLargeIcon(bitmap)
                                        .build();
                                NotificationManager notificationManager = (NotificationManager) ForegroundService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                if (notificationManager != null) {
                                    notificationManager.notify((int) new Date().getTime(), notification);
                                }
                            }
                        }
                    }
                }
        ).start();


        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_LOW
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running in background");
        startForeground(2502, notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Intent serviceIntent = new Intent(getApplicationContext(), ForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void getDataNgayDuong() {
        Cursor datangayduong = database.GetData("SELECT * FROM NgayDuong");
        arrayListNgayDUONG.clear();
        while (datangayduong.moveToNext()) {
            String tenSK = datangayduong.getString(1);
            String Ngay = datangayduong.getString(2);
            arrayListNgayDUONG.add(new CLASSNGAYDUONGLICH(tenSK, Ngay));
        }
    }

    private void getDataNgayAm() {
        Cursor datangayam = database.GetData("SELECT * FROM NgayAm");
        arrayListNgayAM.clear();
        while (datangayam.moveToNext()) {
            String tenSK = datangayam.getString(1);
            String Ngay = datangayam.getString(2);
            arrayListNgayAM.add(new CLASSNGAYAMLICH(tenSK, Ngay));
        }
    }

    private int getNotificationid() {
        return (int) new Date().getTime();
    }

    private void tinhngaysapdenLichDuong(Context context) {
        final String NOTIFICATION_CHANNEL_IDDuong = "LichDuong";
        Calendar ngayDuongdienraSk = Calendar.getInstance();
        int ngayduongconlaidensk = 0;
        Calendar ngayLichDuonghientai = Calendar.getInstance();
        ngayLichDuonghientai.set(namDuongCuaHienTai, thangDuongCuaHienTai - 1, ngayDuongCuahientai);
        for (int i = 0; i < arrayListNgayDUONG.size(); i++) {
            String catngayduong[] = arrayListNgayDUONG.get(i).getNgayDuong().split("\\/");
            ngayDuongdienraSk.set(namDuongCuaHienTai, Integer.parseInt(catngayduong[1]) - 1, Integer.parseInt(catngayduong[0]));
            ngayduongconlaidensk = (int) ((ngayDuongdienraSk.getTimeInMillis() - ngayLichDuonghientai.getTimeInMillis()) / (1000 * 60 * 60 * 24));
            switch (ngayDuongdienraSk.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    thuTRongTuan = "Chủ nhật";
                    break;
                case 2:
                    thuTRongTuan = "Thứ hai";
                    break;
                case 3:
                    thuTRongTuan = "Thứ ba";
                    break;
                case 4:
                    thuTRongTuan = "Thứ tư";
                    break;
                case 5:
                    thuTRongTuan = "Thứ năm";
                    break;
                case 6:
                    thuTRongTuan = "Thứ sáu";
                    break;
                case 7:
                    thuTRongTuan = "Thứ bảy";
                    break;
            }


            if (ngayduongconlaidensk == 3) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcherevent);
                Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_IDDuong)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 3 ngày nữa đến " + arrayListNgayDUONG.get(i).tenSk + " vào: " + thuTRongTuan)
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.notify(getNotificationid(), notification);
                }
            } else if (ngayduongconlaidensk == 1) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcherevent);
                Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_IDDuong)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 1 ngày nữa đến " + arrayListNgayDUONG.get(i).tenSk + " vào : " + thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.notify(getNotificationid(), notification);
                }
            } else if (ngayduongconlaidensk == 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcherevent);
                Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_IDDuong)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Hôm nay là " + arrayListNgayDUONG.get(i).tenSk)
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.notify(getNotificationid(), notification);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void tinhngaysapdenLichAm(Context context) {
        final String NOTIFICATION_CHANNEL_IDAm = "LichAm";

        ChineseCalendar ngayAmdienraSk = new ChineseCalendar();
        int ngayconlaidensk = 0;
        Calendar ngayHienTai = Calendar.getInstance();

        ChineseCalendar ngayamLichHienTai = new ChineseCalendar(ngayHienTai.getTime());
        for (int i = 0; i < arrayListNgayAM.size(); i++) {
            String catngayam[] = arrayListNgayAM.get(i).getNgayAm().split("\\/");
            ngayAmdienraSk.set(namAmCuaHienTai, Integer.parseInt(catngayam[1]) - 1, Integer.parseInt(catngayam[0]));
            ngayconlaidensk = (int) ((ngayAmdienraSk.getTimeInMillis() - ngayamLichHienTai.getTimeInMillis()) / (1000 * 60 * 60 * 24));

            switch (ngayAmdienraSk.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    thuTRongTuan = "Chủ nhật";
                    break;
                case 2:
                    thuTRongTuan = "Thứ hai";
                    break;
                case 3:
                    thuTRongTuan = "Thứ ba";
                    break;
                case 4:
                    thuTRongTuan = "Thứ tư";
                    break;
                case 5:
                    thuTRongTuan = "Thứ năm";
                    break;
                case 6:
                    thuTRongTuan = "Thứ sáu";
                    break;
                case 7:
                    thuTRongTuan = "Thứ bảy";
                    break;
            }


            if (ngayconlaidensk == 3) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcherevent);
                Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_IDAm)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 3 ngày nữa đến " + arrayListNgayAM.get(i).tenSk + " vào : " + thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.notify(getNotificationid(), notification);
                }
            } else if (ngayconlaidensk == 1) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcherevent);
                Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_IDAm)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Còn 1 ngày nữa đến " + arrayListNgayAM.get(i).tenSk + " vào : " + thuTRongTuan.toString())
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.notify(getNotificationid(), notification);
                }
            } else if (ngayconlaidensk == 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcherevent);
                Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_IDAm)
                        .setContentTitle("Nhắc nhở")
                        .setContentText("Hôm nay là " + arrayListNgayAM.get(i).tenSk)
                        .setSmallIcon(R.mipmap.ic_launcherevent)
                        .setLargeIcon(bitmap)
                        .build();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.notify(getNotificationid(), notification);
                }
            }
        }
    }
}
