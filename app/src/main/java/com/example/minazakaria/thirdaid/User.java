package com.example.minazakaria.thirdaid;

/**
 * Created by Mina Zakaria on 2/16/2018.
 */

public class User {

    private String userID;
    private String userFName;
    private String userLName;
    private String userToken;
    private String userDName;
    private String userRole;
    private String userStatus;
    private String userIsCertified;
    private String userNotificationStatus;
    private String userPhoneNumber;

    public User(String id, String fname, String lname, String token, String userdname, String role, String status, String isCertified, String userNotificationStatus, String pnum){
        userID = id;
        userFName = fname;
        userLName = lname;
        userToken = token;
        userDName = userdname;
        userRole = role;
        userStatus = status;
        userIsCertified = isCertified;
        userPhoneNumber = pnum;
        this.userNotificationStatus = userNotificationStatus;
    }

    //SETTERS
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserFName(String userFName) {
        this.userFName = userFName;
    }

    public void setUserLName(String userLName) {
        this.userLName = userLName;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public void setUserDName(String userDName) {
        this.userDName = userDName;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public void setUserIsCertified(String userIsCertified) {
        this.userIsCertified = userIsCertified;
    }

    public void setUserNotificationStatus(String userNotificationStatus) {
        this.userNotificationStatus = userNotificationStatus;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    //GETTERS
    public String getUserID() {
        return userID;
    }

    public String getUserFName() {
        return userFName;
    }

    public String getUserLName()
    {
        return userLName;
    }

    public String getUserToken() {

        return userToken;
    }

    public String getUserIsCertified() {
        return userIsCertified;
    }

    public String getUserDName() {
        return userDName;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public String getUserNotificationStatus() {
        return userNotificationStatus;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

}
