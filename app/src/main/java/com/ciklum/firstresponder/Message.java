package com.ciklum.firstresponder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by victorllana on 5/11/16.
 */
public class Message {
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

    public String getFrom() {
        String ret = null;
        try {
            ret = mJsonObject.getString("personEmail");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public String getTime() {
        String ret = null;
        try {
            ret = mJsonObject.getString("created");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public boolean hasAttachment() {
        return mJsonObject.has("files");
    }

    public JSONArray getAttachments() {
        JSONArray arr = null;
        try {
            arr = mJsonObject.getJSONArray("files");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }
}

