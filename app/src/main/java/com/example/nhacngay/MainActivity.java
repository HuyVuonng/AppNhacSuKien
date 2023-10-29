package com.example.nhacngay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.ChineseCalendar;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private PendingIntent repeat_Pending;
    Context context;
    TextView TVngayam, tvNgayconlai;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String thuTRongTuan;
    Database database;
    EditText ngayNhap;
    int ngayDuongCuahientai, thangDuongCuaHienTai, namDuongCuaHienTai, ngayAmCuaHienTai, thangAmCuaHienTai, namAmCuaHienTai;
    ListView ngayDuonglv, ngayAMLv;
    adapterNgayDuong adapterNgayDuongg;
    private GoogleSignInClient client;
    adapterNgayAm adapterNgayamm;
    FloatingActionButton add;
    ArrayList<CLASSNGAYAMLICH> arrayListNgayAM;
    ArrayList<CLASSNGAYDUONGLICH> arrayListNgayDUONG;
    private static DatabaseReference mDatabase;
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

        if (!forceServicesRunning()) {
            Intent serviceIntent = new Intent(MainActivity.this, ForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }
        }

        anhxa();


        arrayListNgayAM = new ArrayList<>();
        arrayListNgayDUONG = new ArrayList<>();
        adapterNgayDuongg = new adapterNgayDuong(this, R.layout.dongsk, arrayListNgayDUONG);
        ngayDuonglv.setAdapter(adapterNgayDuongg);
        adapterNgayamm = new adapterNgayAm(this, R.layout.dongsk, arrayListNgayAM);
        ngayAMLv.setAdapter(adapterNgayamm);
        //Tao DB
        database = new Database(this, "QuanLySK.sqlite", null, 1);
        database.QuerryData("CREATE TABLE IF NOT EXISTS NgayDuong (maNgayDuong INTEGER PRIMARY KEY AUTOINCREMENT,SuKienNgayDuong TEXT,NgayDuong varchar(20),maDB INTEGER)");
        database.QuerryData("CREATE TABLE IF NOT EXISTS NgayAm (maNgayAm INTEGER PRIMARY KEY AUTOINCREMENT,SuKienNgayAm TEXT,NgayAm varchar(20),maDB INTEGER)");


        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("347262153348-bm8i11f03npv622tvq6jdo1367iaq7rg.apps.googleusercontent.com")
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, options);
        SharedPreferences sharedPreferences = getSharedPreferences("data1", MODE_PRIVATE);
        String uid = sharedPreferences.getString("uid", "");
        if (uid == "") {
            Intent i = client.getSignInIntent();
            startActivityForResult(i, 123546789);
        }


//        FirebaseAuth.getInstance().getCurrentUser().getUid();

        setDataFireBaseToDBLocalDl(uid);
        setDataFireBaseToDBLocalAl(uid);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uid != "") {
                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialogthemsukien);
                    dialog.show();

                    Button Them = dialog.findViewById(R.id.buttonThem);
                    Button Huy = dialog.findViewById(R.id.buttonHuy);
                    RadioButton duonglich = dialog.findViewById(R.id.radioButtonDuongLich);
                    RadioButton amlich = dialog.findViewById(R.id.radioButtonAmLich);
                    EditText tenSK = dialog.findViewById(R.id.editTexttenSK);
                    EditText ngay = dialog.findViewById(R.id.editTextNgay);

                    Huy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    Them.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Boolean checkNumber = false;
                            String tensk = tenSK.getText().toString().trim();
                            String ngaySk = ngay.getText().toString().trim();
                            String checkngay[] = ngaySk.split("\\/");
                            if (TextUtils.isEmpty(tensk) || TextUtils.isEmpty(ngaySk)) {
                                Toast.makeText(MainActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                            } else if (checkngay.length < 2) {
                                Toast.makeText(MainActivity.this, "Bạn đang nhập sai ngày", Toast.LENGTH_SHORT).show();
                            }
                            try {
                                Integer.parseInt(checkngay[0]);
                                Integer.parseInt(checkngay[1]);
                            } catch (Exception e) {
                                checkNumber = true;
                            }
                            if (checkNumber) {
                                Toast.makeText(MainActivity.this, "Bạn đang nhập sai ngày", Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(checkngay[0]) < 1 || Integer.parseInt(checkngay[0]) > 31) {
                                Toast.makeText(MainActivity.this, "Ngày phải >0 và <31", Toast.LENGTH_SHORT).show();
                            } else if (Integer.parseInt(checkngay[1]) < 0 || Integer.parseInt(checkngay[1]) > 12) {
                                Toast.makeText(MainActivity.this, "Tháng phải >0 và <13", Toast.LENGTH_SHORT).show();
                            } else if (duonglich.isChecked()) {
                                int id = (int) (new Date().getTime() / 1000);
                                database.QuerryData("INSERT INTO NgayDuong VALUES(null,'" + tensk + "','" + ngaySk + "','" + id + "')");
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                CLASSNGAYDUONGLICH duonglich = new CLASSNGAYDUONGLICH(tensk, ngaySk, id, id);
                                mDatabase.child(uid).child("DuongLich").child(String.valueOf(id)).setValue(duonglich);
                            } else {
                                int id = (int) (new Date().getTime() / 1000);
                                database.QuerryData("INSERT INTO NgayAm VALUES(null,'" + tensk + "','" + ngaySk + "','" + id + "')");
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                CLASSNGAYAMLICH amlich = new CLASSNGAYAMLICH(tensk, ngaySk, id, id);
                                mDatabase.child(uid).child("AmLich").child(String.valueOf(id)).setValue(amlich);
                            }
                            dialog.dismiss();
                            Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(refresh);
                            MainActivity.this.finish();
                        }
                    });
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Vui lòng đăng nhập tài khoản.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });
    }

    private void setDataFireBaseToDBLocalDl(String uid) {


        mDatabase = FirebaseDatabase.getInstance().getReference();
        Cursor datangayduong = database.GetData("SELECT * FROM NgayDuong");
        arrayListNgayDUONG.clear();
        while (datangayduong.moveToNext()) {
            String tenSK = datangayduong.getString(1);
            String Ngay = datangayduong.getString(2);
            int madb = datangayduong.getInt(3);
            CLASSNGAYDUONGLICH duonglich = new CLASSNGAYDUONGLICH(tenSK, Ngay, madb, madb);
            mDatabase.child(uid).child("DuongLich").child(String.valueOf(madb)).setValue(duonglich);
        }


        mDatabase = FirebaseDatabase.getInstance().getReference(uid).child("DuongLich");
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database.QuerryData("DROP TABLE IF EXISTS NgayDuong");
                database.QuerryData("CREATE TABLE IF NOT EXISTS NgayDuong (maNgayDuong INTEGER PRIMARY KEY AUTOINCREMENT,SuKienNgayDuong TEXT,NgayDuong varchar(20),maDB INTEGER)");

                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    CLASSNGAYDUONGLICH ngayduong = datasnapshot.getValue(CLASSNGAYDUONGLICH.class);
                    Log.e("onDataChange: ", String.valueOf(ngayduong.getMadb()));
                    database.QuerryData("INSERT INTO NgayDuong VALUES(null,'" + ngayduong.getTenSk() + "','" + ngayduong.getNgayDuong() + "','" + ngayduong.getMadb() + "')");
                }
                getDataNgayDuong();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                getDataNgayDuong();

            }
        });

    }

    private void setDataFireBaseToDBLocalAl(String uid) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Cursor datangayam = database.GetData("SELECT * FROM NgayAm");
        arrayListNgayAM.clear();
        while (datangayam.moveToNext()) {
            String tenSK = datangayam.getString(1);
            String Ngay = datangayam.getString(2);
            int madb = datangayam.getInt(3);
            CLASSNGAYAMLICH amlich = new CLASSNGAYAMLICH(tenSK, Ngay, madb, madb);
            mDatabase.child(uid).child("AmLich").child(String.valueOf(madb)).setValue(amlich);
        }


        mDatabase = FirebaseDatabase.getInstance().getReference(uid).child("AmLich");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database.QuerryData("DROP TABLE IF EXISTS NgayAm");
                database.QuerryData("CREATE TABLE IF NOT EXISTS NgayAm (maNgayAm INTEGER PRIMARY KEY AUTOINCREMENT,SuKienNgayAm TEXT,NgayAm varchar(20),maDB INTEGER)");

                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    CLASSNGAYAMLICH ngayam = datasnapshot.getValue(CLASSNGAYAMLICH.class);
                    database.QuerryData("INSERT INTO NgayAm VALUES(null,'" + ngayam.getTenSk() + "','" + ngayam.getNgayAm() + "','" + ngayam.getMadb() + "')");
                }
                getDataNgayAm();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                getDataNgayAm();
            }
        });

    }


    private void getDataNgayAm() {
        Cursor datangayam = database.GetData("SELECT * FROM NgayAm");
        arrayListNgayAM.clear();
        while (datangayam.moveToNext()) {
            int mangay = datangayam.getInt(0);
            String tenSK = datangayam.getString(1);
            String Ngay = datangayam.getString(2);
            int madb = datangayam.getInt(3);
            arrayListNgayAM.add(new CLASSNGAYAMLICH(tenSK, Ngay, mangay, madb));
        }
        adapterNgayamm.notifyDataSetChanged();
    }

    private void getDataNgayDuong() {
        Cursor datangayduong = database.GetData("SELECT * FROM NgayDuong");
        arrayListNgayDUONG.clear();
        while (datangayduong.moveToNext()) {
            int mangay = datangayduong.getInt(0);
            String tenSK = datangayduong.getString(1);
            String Ngay = datangayduong.getString(2);
            int madb = datangayduong.getInt(3);
            arrayListNgayDUONG.add(new CLASSNGAYDUONGLICH(tenSK, Ngay, mangay, madb));
        }
        adapterNgayDuongg.notifyDataSetChanged();
    }

    private void anhxa() {
        add = findViewById(R.id.floatingActionButton);
        ngayDuonglv = findViewById(R.id.lvNgayDuong);
        ngayAMLv = findViewById(R.id.lvNgayAm);
    }

    public boolean forceServicesRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123546789) {
            Log.e("onActivityResulthere: ", String.valueOf(requestCode));

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    SharedPreferences.Editor editor = getSharedPreferences("data1", MODE_PRIVATE).edit();
                                    editor.putString("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    editor.putBoolean("isLogin", true);
                                    editor.putString("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    editor.putString("type", "google");
                                    editor.apply();
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("data1", MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);
        if (isLogin) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                gsc = GoogleSignIn.getClient(this, gso);

                SharedPreferences editor1 = MainActivity.this.getSharedPreferences("data1", MODE_PRIVATE);
                editor1.edit().clear().apply();

                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                if (acct != null) {
                    SharedPreferences.Editor editor = getSharedPreferences("data1", MODE_PRIVATE).edit();
                    editor.putString("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    editor.putBoolean("isLogin", true);
                    editor.putString("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.putString("type", "google");
                    editor.apply();
                }
            }
        }
    }
}