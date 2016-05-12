package com.ciklum.firstresponder;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MessageParser.OnParseMessageListener, JpegDownloader.JpegDownloadListener {
    private static final int MSG_POLL_INTERVAL = 5000;
    private static final int MAX_BPM = 200;
    private Handler mHandler = new Handler();

    private boolean mIsActive;
    private MessageParser mMessageParser;
    private TextView mBPMTextView;
    private ImageView mHeartImage, mThumb, mStreetImage, mHeartImageInactive;
    private TextView mAddress;
    private int mInterval = 0;
    private String mAddressString;
    private AlertDialog mDialog;

    private static final float HEART_ALPHA = 0.6f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBPMTextView = (TextView)findViewById(R.id.bpm);
        mBPMTextView.setText("0");
        mHeartImage = (ImageView) findViewById(R.id.heart_image);
        mHeartImageInactive = (ImageView) findViewById(R.id.heart_image_inactive);
        mAddress = (TextView) findViewById(R.id.address);
        mThumb = (ImageView) findViewById(R.id.thumb);
        mThumb.setEnabled(false);
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
        mAddress.setText("Not Available");
        startMessagePolling();
        mMessageParser = new MessageParser();
        mMessageParser.setOnMessageListener(this);
        mHeartImage.setVisibility(View.INVISIBLE);
        mHeartImage.setAlpha(HEART_ALPHA);
        mHeartImageInactive.setAlpha(HEART_ALPHA);

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

    private void showIncidentDialog(String address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incident Alert!");
        builder.setMessage("Emergency at Address:\n\n" + address + "\n");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onAcceptAssignment();
            }
        });

        mDialog = builder.create();
        mDialog.show();
    }

    private PostJson.PostJsonListener mPostAcceptListener = new PostJson.PostJsonListener() {
        @Override
        public void onJsonReceived(JSONObject jsonObject) {
            Log.v("VIC:", "onPostDone");
        }
    };
    private void postAccept() {
        PostJson post = new PostJson(Urls.getPostRoomMessageUrl(), mPostAcceptListener);
        post.execute(generateMessageBody("#accepted"));
    }

    private void postClosed() {
        PostJson post = new PostJson(Urls.getPostRoomMessageUrl(), mPostAcceptListener);
        post.execute(generateMessageBody("#done"));
    }


    private JSONObject generateMessageBody(String message) {
        JSONObject j = new JSONObject();
        try {
            j.put(Urls.ROOM_ID_QUERY, Urls.ROOM_ID);
            j.put(Urls.TEXT, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return j;
    }

    private void onAcceptAssignment() {
        mIsActive = true;
        Log.v("VIC:", "onIncidentOpen: " + mAddressString);
        mAddress.setText(mAddressString);
        mHandler.post(mBeatRunnable);
        mHeartImageInactive.setVisibility(View.VISIBLE);
        mDialog = null;
        postAccept();
    }

    @Override
    public void onIncidentOpen(String address) {
        //ignore if we're already active
        if(!mIsActive && mDialog == null) {
            showIncidentDialog(address);
            mAddressString = address;
        }
    }

    @Override
    public void onBpmUpdate(int bpm) {
        Log.v("VIC:", "onBpmUpdate:" + bpm);
        if (mIsActive) {
            if (bpm > MAX_BPM) {
                bpm = MAX_BPM;
            } else if (bpm < 0) {
                bpm = 0;
            }
            mBPMTextView.setText("" + bpm);
            int interval = bpmToIntervalMs(bpm);
            //if (mInterval == 0 && interval > 0) {
                mHandler.removeCallbacks(mBeatRunnable);
                mHandler.post(mBeatRunnable);
            //}
            mInterval = interval;
            if (mInterval == 0) {
                mHeartImage.setVisibility(View.INVISIBLE);
                mHeartImageInactive.setVisibility(View.VISIBLE);
            } else {
                mHeartImage.setVisibility(View.VISIBLE);
                mHeartImageInactive.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onIncidentStop() {
        Log.v("VIC:", "onIncidentStop");
        mIsActive = false;
        mBPMTextView.setText("0");
        mAddress.setText("Not Available");
        mHandler.removeCallbacks(null);
        mInterval = 0;
        mHeartImage.setVisibility(View.INVISIBLE);
        mHeartImageInactive.setVisibility(View.INVISIBLE);
        mThumb.setImageResource(R.drawable.ic_satellite_black_24dp);
        mThumb.setEnabled(false);
        postClosed();
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
        mThumb.setEnabled(true);
        mStreetImage.setImageBitmap(bmp);
    }

    @Override
    public void onBackPressed() {
        if (mStreetImage.getVisibility() == View.VISIBLE) {
            mStreetImage.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public int bpmToIntervalMs(int bpm) {
        float bps = (float)bpm/60.0f;
        int ret = (int)(1000/bps);
        return ret;
    }

    private Runnable mBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.v("VIC:", "onBeat interval = " + mInterval);
            mHeartImage.setAlpha(HEART_ALPHA);
            mHeartImage.setScaleX(1.0f);
            mHeartImage.setScaleY(1.0f);
            mHeartImage.animate().
                    setDuration(mInterval/2).scaleX(0.5f).scaleY(0.5f);
            if (mInterval > 0) {
                mHandler.postDelayed(this, mInterval);
            }
        }
    };
}
