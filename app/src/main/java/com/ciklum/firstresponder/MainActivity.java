package com.ciklum.firstresponder;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final int MSG_POLL_INTERVAL = 5000;

    private Handler mHandler = new Handler();

    ListView mListView;
    MessagesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView)findViewById(R.id.message_list);
        mAdapter = new MessagesAdapter(this);
        mListView.setAdapter(mAdapter);
        startMessagePolling();
    }


    private GetJson.GetJsonListener mOnGetRoomMessagesJson = new GetJson.GetJsonListener() {
        @Override
        public void onJsonReceived(JSONObject jsonObject) {
            mAdapter.updateMessages(jsonObject);
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(mAdapter.getCount() - 1);
            mIsPolling = false;
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
}
