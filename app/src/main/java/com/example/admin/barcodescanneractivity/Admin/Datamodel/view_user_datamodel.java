package com.example.admin.barcodescanneractivity.Admin.Datamodel;

public class view_user_datamodel {

    public  view_user_datamodel(){

    }

    String name;
    String email;
    String phno;
    String usertype;

    public view_user_datamodel(String name, String email, String phno, String usertype) {
        this.name = name;
        this.email = email;
        this.phno = phno;
        this.usertype = usertype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }
}
