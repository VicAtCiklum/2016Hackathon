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
  /*
    "id" : "46ef3f0a-e810-460c-ad37-c161adb48195",
            "personId" : "49465565-f6db-432f-ab41-34b15f544a36",
            "personEmail" : "matt@example.com",
            "roomId" : "24aaa2aa-3dcc-11e5-a152-fe34819cdc9a",
            "text" : "PROJECT UPDATE - A new project project plan has been published on Box",
            "files" : [ "http://www.example.com/images/media.png" ],
            "toPersonId" : "Y2lzY29zcGFyazovL3VzL1BFT1BMRS9mMDZkNzFhNS0wODMzLTRmYTUtYTcyYS1jYzg5YjI1ZWVlMmX",
            "toPersonEmail" : "julie@example.com",
            "created" : "2015-10-18T14:26:16+00:00"
            */

    public interface OnParseMessageListener {
         void onIncidentOpen(String address);
         void onBpmUpdate(int bpm);
         void onIncidentStop();
         void onAttachment(String urls[]);
    }

    private static String TAG_CLOSED = "#close";
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
                        mListener.onIncidentOpen(parseAddress(message.getText()));
                        break;
                    } else if (message.hasAttachment()) {
                        String files[] = parseAttachmentUrls(message.getAttachments());
                        mListener.onAttachment(files);
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

    private String parseAddress(String text) {
        int index = text.indexOf(TAG_OPEN);
        String address = text.substring(index + TAG_BPM.length() + 1);
        Log.v("VIC:", "string address:" + address);
        return address;
    }

    private String[] parseAttachmentUrls(JSONArray array) {
        String urls[] = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            try {
                urls[i] = array.getString(i);
            } catch (JSONException e) {
                urls[i] = null;
                e.printStackTrace();
            }
        }

        return urls;
    }
}
