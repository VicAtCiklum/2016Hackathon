package com.ciklum.firstresponder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by victorllana on 5/11/16.
 */
public class MessagesAdapter extends BaseAdapter {

    public static final String ROOM_ID = "Y2lzY29zcGFyazovL3VzL1JPT00vYzI1ZWIzZjAtMTdiMy0xMWU2LTllNDQtYzlkMTE2OTMzMGY4";
    public static final String AUTHORIZATION = "Bearer MTA5YWFhZjYtOWM2MS00NTdkLWFkMzctYWQwMzI2OWZlYjZlZTM3ZjUxZTctODQy";
    public static final String MESSAGES_URL = "https://api.ciscospark.com/v1/messages";
    public static final String ROOM_ID_QUERY = "roomId";




    private ArrayList<Message> mMessages;
    private Context mContext;

    public MessagesAdapter(Context context) {
        mMessages = new ArrayList<>();
        mContext = context;
    }

    public void updateMessages(JSONObject jsonObject) {
        mMessages.clear();

        if (jsonObject != null) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                int count = jsonArray.length();
                for (int i = 0; i < count; i++) {
                    mMessages.add(new Message(jsonArray.getJSONObject(i)));
                }

                Log.v("VIC:", "message count:" + count);
                notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Message getItem(int position) {
        return mMessages.get(mMessages.size());
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static final int USER_KEY = R.id.user;
    private static final int MSG_KEY = R.id.message;
    private static final int TIME_KEY = R.id.date;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView userView, msgView, timeView;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.layout_message, null);
            userView = (TextView)v.findViewById(R.id.user);
            msgView = (TextView)v.findViewById(R.id.message);
            timeView = (TextView)v.findViewById(R.id.date);
            v.setTag(USER_KEY, userView);
            v.setTag(MSG_KEY, msgView);
            v.setTag(TIME_KEY, timeView);
        } else {
            userView = (TextView)v.getTag(USER_KEY);
            msgView = (TextView)v.getTag(MSG_KEY);
            timeView = (TextView)v.getTag(TIME_KEY);
        }

        Message msg = getItem(position);
        msgView.setText(msg.getText());
        userView.setText(msg.getFrom());
        timeView.setText(msg.getTime());
        return v;
    }

}
