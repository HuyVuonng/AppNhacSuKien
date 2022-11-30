package com.example.nhacngay;

public class CLASSNGAYDUONGLICH {
    String tenSk,ngayDuong;
    int maNgayDuong;

    public CLASSNGAYDUONGLICH(String tenSk, String ngayDuong) {
        this.tenSk = tenSk;
        this.ngayDuong = ngayDuong;
    }

    public CLASSNGAYDUONGLICH(String tenSk, String ngayDuong, int maNgayDuong) {
        this.tenSk = tenSk;
        this.ngayDuong = ngayDuong;
        this.maNgayDuong = maNgayDuong;
    }

    public int getMaNgayDuong() {
        return maNgayDuong;
    }

    public void setMaNgayDuong(int maNgayDuong) {
        this.maNgayDuong = maNgayDuong;
    }

    public String getTenSk() {
        return tenSk;
    }

    public void setTenSk(String tenSk) {
        this.tenSk = tenSk;
    }

    public String getNgayDuong() {
        return ngayDuong;
    }

    public void setNgayDuong(String ngayDuong) {
        this.ngayDuong = ngayDuong;
    }
}
