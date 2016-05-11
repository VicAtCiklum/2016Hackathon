package com.ciklum.firstresponder;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by victorllana on 5/11/16.
 */
public class MessagesAdapter extends BaseAdapter {

    public static final String ROOM_ID = "Y2lzY29zcGFyazovL3VzL1JPT00vNzVkMTdlNjAtMTdiMi0xMWU2LWJjOTMtZmQ1ZTk5ZjRmMTgw";
    public static final String AUTHORIZATION = "Bearer MTA5YWFhZjYtOWM2MS00NTdkLWFkMzctYWQwMzI2OWZlYjZlZTM3ZjUxZTctODQy";
    public static final String MESSAGES_URL = "https://api.ciscospark.com/v1/messages";
    public static final String ROOM_ID_QUERY = "roomId";

    private Handler mHandler = new Handler();
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

    private class Message {
        JSONObject mJsonObject;
        public Message(JSONObject jsonObject) {
            mJsonObject = jsonObject;
        }

        public String getText() {
            String ret = null;
            try {
                ret = mJsonObject.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return ret;
        }

    }

    private ArrayList<Message> mMessages;

    public MessagesAdapter () {
        new GetJson(null).execute();
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
