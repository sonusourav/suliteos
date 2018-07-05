package com.suliteos.sonusourav.poshan;

public class DonorClass {


    private String donorName;
    private String donorEmail;
    private String donorMobile;
    private String donorPass;
    private String donorBloodgroup;
    private String donorAddress;
    private String donorDOD;


    public DonorClass(String dName, String dEmail, String dMobile, String dPass, String dBloodGrp,String dAddress,String dDOD) {
            this.donorName = dName;
            this.donorEmail = dEmail;
            this.donorMobile = dMobile;
            this.donorPass = dPass;
            this.donorBloodgroup = dBloodGrp;
            this.donorAddress=dAddress;
            this.donorDOD=dDOD;

        }

        public String getDonorName(){
            return donorName ;
        }

        public void setText(String doName) {
            this.donorName = doName;
        }

        public String getDonorEmail() {
            return donorEmail;
        }

        public void setDate(String doEmail) {
            this.donorEmail = doEmail;
        }

    public String getDonorMobile() {
        return donorMobile;
    }

    public void setDonorMobile(String doMobile) {
        this.donorMobile = doMobile;
    }

    public String getDonorPass() {
        return donorPass;
    }

    public void setDonorPass(String doPass) {
        this.donorPass = doPass;
    }

        public String getDonorBloodgroup() {
            return donorBloodgroup;
        }

        public void setDonorBloodgroup(String doBlood) {
            this.donorBloodgroup = doBlood;
        }

    public String getDonorAddress() {
        return donorAddress;
    }

    public void setDonorAddress(String doAddress) {
        this.donorBloodgroup = doAddress;
    }
    public String getDonorDOD() {
        return donorDOD;
    }

    public void setDonorDOD(String doDOD) {
        this.donorBloodgroup = doDOD;
    }








}
