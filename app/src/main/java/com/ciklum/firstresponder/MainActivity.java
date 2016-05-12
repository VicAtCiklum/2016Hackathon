package com.ciklum.firstresponder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MessageParser.OnParseMessageListener, JpegDownloader.JpegDownloadListener {
    private static final int MSG_POLL_INTERVAL = 5000;
    private static final int MAX_BPM = 200;
    private Handler mHandler = new Handler();

    private boolean mIsActive;
    private MessageParser mMessageParser;
    private TextView mBPMTextView;
    private ImageView mHeartImage, mThumb, mStreetImage;
    private TextView mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBPMTextView = (TextView)findViewById(R.id.bpm);
        mBPMTextView.setText("0");
        mHeartImage = (ImageView) findViewById(R.id.heart_image);
        mAddress = (TextView) findViewById(R.id.address);
        mThumb = (ImageView) findViewById(R.id.thumb);
        mThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onThumbClicked();
            }
        });
        mStreetImage = (ImageView) findViewById(R.id.street_image);
        mStreetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStreetImageClicked();
            }
        });
        mAddress.setText("Address:");
        startMessagePolling();
        mMessageParser = new MessageParser();
        mMessageParser.setOnMessageListener(this);

    }

    private void onStreetImageClicked() {
        mStreetImage.setVisibility(View.GONE);
    }

    private void onThumbClicked() {
        mStreetImage.setVisibility(View.VISIBLE);
    }

    private GetJson.GetJsonListener mOnGetRoomMessagesJson = new GetJson.GetJsonListener() {
        @Override
        public void onJsonReceived(JSONObject jsonObject) {
            mIsPolling = false;
            mMessageParser.parseMessage(jsonObject);
            mHandler.postDelayed(mPollMessages, MSG_POLL_INTERVAL);
        }
    };

    private void startMessagePolling() {
        mHandler.post(mPollMessages);
    }

    private boolean mIsPolling = false;
    private void pollMessage() {
        mIsPolling = true;
        new GetJson(Urls.getRoomMessagesUrl(), mOnGetRoomMessagesJson).execute();
    }
    private Runnable mPollMessages = new Runnable() {
        @Override
        public void run() {
            // so we don't end up queueing up the polling
            if (!mIsPolling) {
                pollMessage();
            }
        }
    };

    @Override
    public void onIncidentOpen(String address) {
        //ignore if we're already active
        if(!mIsActive) {
            mIsActive = true;
            Log.v("VIC:", "onIncidentOpen: " + address);
            mAddress.setText("Address:\n" + address);
        }
    }

    @Override
    public void onBpmUpdate(int bpm) {
        Log.v("VIC:", "onBpmUpdate:" + bpm);
        if (mIsActive) {
            if (bpm > MAX_BPM) {
                bpm = MAX_BPM;
            }
            mBPMTextView.setText("" + bpm);
        }
    }

    @Override
    public void onIncidentStop() {
        Log.v("VIC:", "onIncidentStop");
        mIsActive = false;
        mBPMTextView.setText("0");
        mAddress.setText("Address:");
    }

    @Override
    public void onAttachment(String urls[]) {
        for (int i = 0; i < urls.length; i++) {
            Log.v("VIC:", "onAttachment: " + urls[i]);
            JpegDownloader jpgdl = new JpegDownloader(this);
            jpgdl.execute(urls[i]);
        }
    }

    @Override
    public void onResult(JpegDownloader.Result result) {
        Bitmap bmp = BitmapFactory.decodeByteArray(result.data.array(),
                0,
                result.data.capacity());
        Bitmap thumb = ThumbnailUtils.extractThumbnail(bmp, 50, 50);
        mThumb.setImageBitmap(thumb);
        mStreetImage.setImageBitmap(bmp);
    }

    @Override
    public void onBackPressed() {
        if (mStreetImage.getVisibility() == View.VISIBLE) {
            mStreetImage.setVisibility(View.GONE);
        }
    }
}
