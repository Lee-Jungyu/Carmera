package com.example.carmera;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

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
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import java.util.Arrays;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    /*setting part*/
    MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationSource locationSource; //지도에 위치를 제공해주는 인터페이스
    UiSettings uiSettings; //UI와 관련된 클래스
    LocationOverlay locationOverlay; //사용자의 위치를 나타내는 마커
    Marker marker = new Marker(); //자동차의 위치를 나타내는 마커

    private static AmazonDynamoDBClient ddb = null;
    static DynamoDBMapper dynamoDBMapper;

    static double latitude = 0;
    static double longitude = 0;
    static double order = -1;
    static boolean isFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        order = -1;
        isFinish = false;

        //dynamodb setting
        AWSMobileClient.getInstance().initialize(this).execute();

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        CognitoCachingCredentialsProvider credentials = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "idpoolid",
                Regions.US_WEST_2
        );

        if(ddb == null) {
            ddb = new AmazonDynamoDBClient(credentials);
            ddb.setRegion(Region.getRegion(Regions.US_WEST_2));
        }

        if(dynamoDBMapper == null) {
            dynamoDBMapper = new DynamoDBMapper(ddb);
        }

        queryLog();



        //naver map setting
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("navermapKey"));


        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        while(isFinish == false) {}


        mapFragment.getMapAsync(this);

        //locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        setContentView(R.layout.activity_map);


    }

    public void queryLog() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

                PaginatedScanList<GPSLog> paginatedScanList = dynamoDBMapper.scan(GPSLog.class, scanExpression);

                for(int i = 0; i < paginatedScanList.size(); i++)
                {
                    GPSLog g = paginatedScanList.get(i);
                    String id = g.getGPSLogId();
                    double o = g.getOrder();
                    if(o >= order) {
                        latitude = g.getLatitude();
                        longitude = g.getLongitude();
                        order = o;
                        System.out.println("changed: " + latitude + "\t" + longitude + "\t" + o);
                    }
                    String datetime = g.getDateTime();
                    System.out.println(g.getGPSLogId()+"\t"+g.getOrder()+"\t"+g.getLatitude() + "\t" + g.getLongitude()+"\t"+g.getDateTime());
                }


                isFinish = true;


            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onDestroy() {
        super.onDestroy();
        locationSource = null;
    }

    @Override //NaverMap객체 준비시 호출됨
    public void onMapReady(@NonNull NaverMap naverMap) {

        //ui세팅
        uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);


        //locationSource세팅
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Face);

        //현재위치 표시
        locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        LatLng pos =  locationOverlay.getPosition();
        System.out.println(pos.latitude + "," + pos.longitude + "\n");

        //marker 표시
        LatLng carpos = new LatLng(latitude, longitude);
        marker = new Marker();
        marker.setPosition(new LatLng(latitude,longitude));
        marker.setIcon(MarkerIcons.RED);
        marker.setMap(naverMap);

        //경로선 표시
        PathOverlay path = new PathOverlay();
        path.setCoords(Arrays.asList(
                pos,
                carpos
        ));
        path.setMap(naverMap);

        naverMap.addOnLocationChangeListener(locationSource->
                path.setCoords(Arrays.asList(
                        new LatLng(locationSource.getLatitude(),locationSource.getLongitude())
                        ,carpos
                ))

        );


        //좌표로 화면 이동
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(pos);
        naverMap.moveCamera(cameraUpdate);
    }
}