package com.example.carmera;

import android.support.multidex.MultiDexApplication;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "GPSLog")
public class GPSLog extends MultiDexApplication {
    private String gps_log_id;
    private String datetime;
    private double order;
    private double latitude;
    private double longitude;

    public GPSLog() {}

    @DynamoDBHashKey(attributeName = "gps_log_id")
    @DynamoDBAttribute(attributeName = "gps_log_id")
    public String getGPSLogId() {
        return gps_log_id;
    }
    public void setGPSLogId(final String gps_log_id){
        this.gps_log_id = gps_log_id;
    }

    @DynamoDBIndexHashKey(attributeName = "order")
    @DynamoDBAttribute(attributeName = "order")
    public double getOrder() { return order; }
    public void setOrder(double order) { this.order = order; }

    @DynamoDBAttribute(attributeName = "datetime")
    public String getDateTime() { return datetime; }
    public void setDateTime(final String datetime) { this.datetime = datetime; }

    @DynamoDBAttribute(attributeName = "latitude")
    public double getLatitude() { return latitude; }
    public void setLatitude(final double latitude) { this.latitude = latitude; }

    @DynamoDBAttribute(attributeName = "longitude")
    public double getLongitude() { return longitude; }
    public void setLongitude(final double longitude) { this.longitude = longitude; }
}