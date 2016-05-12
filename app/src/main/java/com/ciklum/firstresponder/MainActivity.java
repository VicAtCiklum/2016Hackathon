package com.ciklum.firstresponder;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MessageParser.OnParseMessageListener{
    private static final int MSG_POLL_INTERVAL = 5000;

    private Handler mHandler = new Handler();

    //ListView mListView;
    //MessagesAdapter mAdapter;
    private boolean mIsActive;
    private MessageParser mMessageParser;
    private TextView mBPMTextView;
    private ImageView mHeartImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBPMTextView = (TextView)findViewById(R.id.bpm);
        mBPMTextView.setText("0");
        mHeartImage = (ImageView) findViewById(R.id.heart_image);

        //mListView = (ListView)findViewById(R.id.message_list);
        //mAdapter = new MessagesAdapter(this);
        //mListView.setAdapter(mAdapter);
        startMessagePolling();
        mMessageParser = new MessageParser();
        mMessageParser.setOnMessageListener(this);
    }


    private GetJson.GetJsonListener mOnGetRoomMessagesJson = new GetJson.GetJsonListener() {
        @Override
        public void onJsonReceived(JSONObject jsonObject) {
            //mAdapter.updateMessages(jsonObject);
            //mAdapter.notifyDataSetChanged();
            //mListView.setSelection(mAdapter.getCount() - 1);
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
    public void onIncidentOpen() {
        //ignore if we're already active
        if(!mIsActive) {
            mIsActive = true;
            Log.v("VIC:", "onIncidentOpen");
        }
    }

    @Override
    public void onBpmUpdate(int bpm) {
        Log.v("VIC:", "onBpmUpdate:" + bpm);
        if (mIsActive) {
            mBPMTextView.setText("" + bpm);
        }
    }

    @Override
    public void onIncidentStop() {
        Log.v("VIC:", "onIncidentStop");
        mIsActive = false;
        mBPMTextView.setText("0");
    }
}
