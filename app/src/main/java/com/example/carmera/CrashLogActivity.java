package com.example.carmera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;

public class CrashLogActivity extends AppCompatActivity {

    DynamoDBMapper dynamoDBMapper;
    ListView listView;
    ArrayList<String> items = new ArrayList<String>();

    private AmazonDynamoDBClient ddb = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crashlog);

        listView = (ListView) findViewById(R.id.listView2);

        AWSMobileClient.getInstance().initialize(this).execute();

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        CognitoCachingCredentialsProvider credentials = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "idpoolid",
                Regions.US_WEST_2
        );

        ddb = new AmazonDynamoDBClient(credentials);
        ddb.setRegion(Region.getRegion(Regions.US_WEST_2));

        //AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        //this.dynamoDBMapper = DynamoDBMapper.builder().dynamoDBClient(dynamoDBClient).awsConfiguration(configuration).build();
        //dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
        dynamoDBMapper = new DynamoDBMapper(ddb);

        queryLog();

        while(items.isEmpty());

        ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                items);

        listView.setAdapter(aa);
    }

    public void queryLog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

                PaginatedScanList<CrashLog> paginatedScanList = dynamoDBMapper.scan(CrashLog.class, scanExpression);

                for(CrashLog c : paginatedScanList) {
                    items.add(c.getCrashLogId());
                }


            }
        }).start();
    }


}

