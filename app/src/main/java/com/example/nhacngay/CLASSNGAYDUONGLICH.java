package com.example.nhacngay;

public class CLASSNGAYDUONGLICH {
    String tenSk,ngayDuong;
    int maNgayDuong,madb;

    public CLASSNGAYDUONGLICH() {
    }

    public CLASSNGAYDUONGLICH(String tenSk, String ngayDuong) {
        this.tenSk = tenSk;
        this.ngayDuong = ngayDuong;
    }

    public CLASSNGAYDUONGLICH(String tenSk, String ngayDuong, int maNgayDuong) {
        this.tenSk = tenSk;
        this.ngayDuong = ngayDuong;
        this.maNgayDuong = maNgayDuong;
    }

    public CLASSNGAYDUONGLICH(String tenSk, String ngayDuong, int maNgayDuong, int madb) {
        this.tenSk = tenSk;
        this.ngayDuong = ngayDuong;
        this.maNgayDuong = maNgayDuong;
        this.madb = madb;
    }

    public int getMadb() {
        return madb;
    }

    public void setMadb(int madb) {
        this.madb = madb;
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
