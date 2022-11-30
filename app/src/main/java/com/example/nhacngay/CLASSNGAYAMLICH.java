package com.example.nhacngay;

public class CLASSNGAYAMLICH {
    String tenSk,ngayAm;
    int maNgayAm;

    public CLASSNGAYAMLICH(String tenSk, String ngayAm, int maNgayAm) {
        this.tenSk = tenSk;
        this.ngayAm = ngayAm;
        this.maNgayAm = maNgayAm;
    }

    public CLASSNGAYAMLICH(String tenSk, String ngayAm) {
        this.tenSk = tenSk;
        this.ngayAm = ngayAm;
    }

    public int getMaNgayAm() {
        return maNgayAm;
    }

    public void setMaNgayAm(int maNgayAm) {
        this.maNgayAm = maNgayAm;
    }

    public String getTenSk() {
        return tenSk;
    }

    public void setTenSk(String tenSk) {
        this.tenSk = tenSk;
    }

    public String getNgayAm() {
        return ngayAm;
    }

    public void setNgayAm(String ngayAm) {
        this.ngayAm = ngayAm;
    }
}
