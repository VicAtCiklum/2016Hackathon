package com.ciklum.firstresponder;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by victorllana on 5/11/16.
 */
public class MessageParser {

    public interface OnParseMessageListener {
         void onIncidentOpen();
         void onBpmUpdate(int bpm);
         void onIncidentStop();
    }

    private static String TAG_CLOSED = "#closed";
    private static String TAG_OPEN = "#open";
    private static String TAG_BPM = "#bpm";

    private OnParseMessageListener mListener;
    public MessageParser() {

    }

    public void setOnMessageListener(OnParseMessageListener listener) {
        mListener = listener;
    }
    public void parseMessage(JSONObject jsonObject) {
        Message message;

        if (jsonObject != null) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                int count = jsonArray.length();
                for (int i = 0; i < count; i++) {
                    message = new Message(jsonArray.getJSONObject(i));
                    if (message.getText().contains(TAG_CLOSED)) {
                        if (i == 0) {
                            //reset
                            mListener.onIncidentStop();
                        }
                        break;
                        //done
                    } else if (message.getText().contains(TAG_BPM)) {
                        mListener.onBpmUpdate(parseBpm(message.getText()));
                        break;
                    } else if (message.getText().contains(TAG_OPEN)) {
                        mListener.onIncidentOpen();
                    }
                }
                Log.v("VIC:", "message count:" + count);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private int parseBpm(String text) {
        int index = text.indexOf(TAG_BPM);
        String num = text.substring(index + TAG_BPM.length() + 1);
        Log.v("VIC:", "string num:" + num);
        return Integer.parseInt(num);
    }
}
