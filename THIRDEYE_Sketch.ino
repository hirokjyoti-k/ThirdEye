#include <Wire.h>
#include <ESP8266WiFi.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>
#include <FirebaseArduino.h>
#include <TinyGPS++.h>
#include <SoftwareSerial.h>

WiFiManager wifiManager;
TinyGPSPlus gps;
SoftwareSerial gpsSerial(D1, D2);
//Firebase Secrets
#define FIREBASE_HOST "thirdeye-4c7b1.firebaseio.com"
#define FIREBASE_AUTH "jyoxP3s5hL1mQt5jIOSFL6ILoucGtlRXQqKcFZzC"

// MPU6050 Slave Device Address
const uint8_t MPU6050SlaveAddress = 0x68;

// Select SDA and SCL pins for I2C communication 
const uint8_t scl = D6;
const uint8_t sda = D7;
double gforce,T;
float latitude,longitude;
int speeds,satellite,altitudes;

// sensitivity scale factor respective to full scale setting provided in datasheet 
const uint16_t AccelScaleFactor = 16384;
const uint16_t GyroScaleFactor = 131;

// MPU6050 few configuration register addresses
const uint8_t MPU6050_REGISTER_SMPLRT_DIV   =  0x19;
const uint8_t MPU6050_REGISTER_USER_CTRL    =  0x6A;
const uint8_t MPU6050_REGISTER_PWR_MGMT_1   =  0x6B;
const uint8_t MPU6050_REGISTER_PWR_MGMT_2   =  0x6C;
const uint8_t MPU6050_REGISTER_CONFIG       =  0x1A;
const uint8_t MPU6050_REGISTER_GYRO_CONFIG  =  0x1B;
const uint8_t MPU6050_REGISTER_ACCEL_CONFIG =  0x1C;
const uint8_t MPU6050_REGISTER_FIFO_EN      =  0x23;
const uint8_t MPU6050_REGISTER_INT_ENABLE   =  0x38;
const uint8_t MPU6050_REGISTER_ACCEL_XOUT_H =  0x3B;
const uint8_t MPU6050_REGISTER_SIGNAL_PATH_RESET  = 0x68;

int16_t AccelX, AccelY, AccelZ, Temperature, GyroX, GyroY, GyroZ;

void setup() {
  Serial.begin(9600);
 
  wifiManager.autoConnect("Third Eye");   
  Serial.println("connected... :)");
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  gpsSerial.begin(9600);
  Wire.begin(sda, scl);
  MPU6050_Init();
}

void loop() {
  double  Gx, Gy, Gz;
  
  Read_RawValue(MPU6050SlaveAddress, MPU6050_REGISTER_ACCEL_XOUT_H);
  
  //divide each with their sensitivity scale factor
  T = (double)Temperature/340+36.53; //temperature formula
  Gx = (double)GyroX/GyroScaleFactor;
  Gy = (double)GyroY/GyroScaleFactor;
  Gz = (double)GyroZ/GyroScaleFactor;
  
  gforce = sqrt((Gx*Gx)+(Gy*Gy)+(Gz*Gz))/9.8;
  Serial.print(" GForce: "); Serial.println(gforce);
  if(gforce > 10){
    Firebase.setBool("UserDetails/THRDEYE001/accident", true);
  }
  getGps();
  Firebase.setBool("UserDetails/THRDEYE001/accident", false);//Reset Accident value
}

void I2C_Write(uint8_t deviceAddress, uint8_t regAddress, uint8_t data){
  Wire.beginTransmission(deviceAddress);
  Wire.write(regAddress);
  Wire.write(data);
  Wire.endTransmission();
}

// read all 14 register
void Read_RawValue(uint8_t deviceAddress, uint8_t regAddress){
  Wire.beginTransmission(deviceAddress);
  Wire.write(regAddress);
  Wire.endTransmission();
  Wire.requestFrom(deviceAddress, (uint8_t)14);
  AccelX = (((int16_t)Wire.read()<<8) | Wire.read());
  AccelY = (((int16_t)Wire.read()<<8) | Wire.read());
  AccelZ = (((int16_t)Wire.read()<<8) | Wire.read());
  Temperature = (((int16_t)Wire.read()<<8) | Wire.read());
  GyroX = (((int16_t)Wire.read()<<8) | Wire.read());
  GyroY = (((int16_t)Wire.read()<<8) | Wire.read());
  GyroZ = (((int16_t)Wire.read()<<8) | Wire.read());
}

//configure MPU6050
void MPU6050_Init(){
  delay(150);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_SMPLRT_DIV, 0x07);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_PWR_MGMT_1, 0x01);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_PWR_MGMT_2, 0x00);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_CONFIG, 0x00);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_GYRO_CONFIG, 0x00);//set +/-250 degree/second full scale
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_ACCEL_CONFIG, 0x00);// set +/- 2g full scale
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_FIFO_EN, 0x00);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_INT_ENABLE, 0x01);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_SIGNAL_PATH_RESET, 0x00);
  I2C_Write(MPU6050SlaveAddress, MPU6050_REGISTER_USER_CTRL, 0x00);
}


void getGps(){
  while (gpsSerial.available() > 0){
      // get the byte data from the GPS
      byte gpsData = gpsSerial.read();
      gps.encode(gpsData);
    
      latitude = gps.location.lat();
      longitude = gps.location.lng();
      speeds = gps.speed.kmph();
      altitudes = gps.altitude.meters();
      satellite - gps.satellites.value();

      if (gps.time.isUpdated()){

        Serial.print("Latittude: "); Serial.print(latitude);       
        Serial.print(" , Longititude: "); Serial.print(longitude);
        Serial.print(" , Speed: "); Serial.print(speeds);
        Serial.print(" , Altitude: "); Serial.print(altitudes);
        Serial.print(" , satellite: "); Serial.print(satellite);
        Serial.print(" , Time: "); Serial.println(gps.time.value());

        Firebase.setInt("UserDetails/THRDEYE001/Temperature",T);
        Firebase.setFloat("UserDetails/THRDEYE001/Latitude",latitude);
        Firebase.setFloat("UserDetails/THRDEYE001/Longitude",longitude);
        Firebase.setInt("UserDetails/THRDEYE001/Speed",speeds);
        Firebase.setInt("UserDetails/THRDEYE001/Satellite",satellite);
        Firebase.setInt("UserDetails/THRDEYE001/Altitude",altitudes);
      }
    }
  }
