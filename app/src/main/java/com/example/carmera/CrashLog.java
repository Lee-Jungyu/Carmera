package com.example.carmera;

import android.support.multidex.MultiDexApplication;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "CrashLog")
public class CrashLog extends MultiDexApplication {
    private String crash_log_id;

    public CrashLog() {}
    @DynamoDBHashKey(attributeName = "crash_log_id")
    @DynamoDBAttribute(attributeName = "crash_log_id")
    public String getCrashLogId() {
        return crash_log_id;
    }
    public void setCrashLogId(final String crash_log_id){
        this.crash_log_id = crash_log_id;
    }


}

