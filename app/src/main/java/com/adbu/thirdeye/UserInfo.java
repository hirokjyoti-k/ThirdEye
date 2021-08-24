package com.adbu.thirdeye;

public class UserInfo {

    public double Latitude;
    public  double Longitude;
    public String Email;
    public String Name;
    public String Vehicle_No;
    public int Speed;
    public int Satellite;
    public int Altitude;
    public  int Temperature;
    public boolean admin;
    public boolean accident;

    UserInfo(){}


    public UserInfo(double latitude, double longitude, String email, String name, String vehicle_No, int speed, int satellite, int altitude, int temperature, boolean admin, boolean accident) {
        Latitude = latitude;
        Longitude = longitude;
        Email = email;
        Name = name;
        Vehicle_No = vehicle_No;
        Speed = speed;
        Satellite = satellite;
        Altitude = altitude;
        Temperature = temperature;
        this.admin = admin;
        this.accident = accident;
    }
}
