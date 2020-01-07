package com.example.carmera;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.net.URL;
import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {

    String accessKey = "accessKey";
    String secretKey = "secretKey";

    String bucketName = "carmera";
    ListView listView;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        AWSMobileClient.getInstance().initialize(this).execute();

        // KEY and SECRET are gotten when we create an IAM user above
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        final AmazonS3Client s3Client = new AmazonS3Client(credentials);
        if(s3Client.equals(null)) { System.out.println("this is null object!"); }
        s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));

        //s3에 있는 bucket의 리스트를 가져오는 코드
        System.out.println(s3Client.getS3AccountOwner());
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(2);
        ListObjectsV2Result result;

        listView = (ListView) findViewById(R.id.listView);

        final ArrayList<String> items = new ArrayList<>();
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        do {
            result = s3Client.listObjectsV2(req);

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                items.add(objectSummary.getKey());
            }
            // If there are more than maxKeys keys in the bucket, get a continuation token
            // and list the next objects.
            String token = result.getNextContinuationToken();
            req.setContinuationToken(token);
        } while (result.isTruncated());

        listView.setAdapter(aa);
        final Object[] objects = items.toArray();

        final VideoActivity thisClass = this;

        videoView = (VideoView) findViewById(R.id.videoView);

        final MediaPlayer mediaPlayer = new MediaPlayer();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String objectName = (String) objects[position];
                GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
                URL objectURL = s3Client.generatePresignedUrl(request);

                getWindow().setFormat(PixelFormat.TRANSLUCENT);
                MediaController mediaCtrl = new MediaController(thisClass);
                mediaCtrl.setMediaPlayer(videoView);
                Uri clip = Uri.parse(objectURL.toString());

                videoView.setVideoURI(clip);
                videoView.requestFocus();
                videoView.start();
            }
        });
        /*GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
        URL objectURL = s3Client.generatePresignedUrl(request);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);



        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        String fileName = "1";
        File localFile = new File("/");
        TransferObserver downloadObserver = transferUtility.download("jsaS3/" + fileName, new File(""));

        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });*/
    }




}
