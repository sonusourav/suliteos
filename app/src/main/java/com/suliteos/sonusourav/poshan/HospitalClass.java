package com.suliteos.sonusourav.poshan;

public class HospitalClass {


    private String hosName;
    private String hosEmail;
    private String hosMobile;
    private String hosPass;
    private String hosBlood;
    private String hosAddress;
    private String hosIncharge;


    public HospitalClass(String dName, String dEmail, String dMobile, String dPass, String dBlood,String dAddress,String dDOD) {
        this.hosName = dName;
        this.hosEmail = dEmail;
        this.hosMobile = dMobile;
        this.hosPass = dPass;
        this.hosBlood = dBlood;
        this.hosAddress =dAddress;
        this.hosIncharge =dDOD;

    }

    public String getHosName(){
        return hosName;
    }

    public void setText(String doName) {
        this.hosName = doName;
    }

    public String getHosEmail() {
        return hosEmail;
    }

    public void setDate(String doEmail) {
        this.hosEmail = doEmail;
    }

    public String getHosMobile() {
        return hosMobile;
    }

    public void setHosMobile(String doMobile) {
        this.hosMobile = doMobile;
    }

    public String getHosPass() {
        return hosPass;
    }

    public void setHosPass(String doPass) {
        this.hosPass = doPass;
    }

    public String getHosBlood() {
        return hosBlood;
    }

    public void setHosBlood(String doBlood) {
        this.hosBlood = doBlood;
    }

    public String getHosAddress() {
        return hosAddress;
    }

    public void setHosAddress(String doAddress) {
        this.hosAddress = doAddress;
    }

    public String getHosIncharge() {
        return hosIncharge;
    }

    public void setHosIncharge(String doDOD) {
        this.hosIncharge = doDOD;
    }








}
