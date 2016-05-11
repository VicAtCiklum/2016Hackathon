package com.ciklum.firstresponder;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by victorllana on 5/11/16.
 */
public class GetJson extends AsyncTask<Void, Void, JSONObject> {
    private String mUrl;
    public GetJson(String url) {
        mUrl = url;
    }

    public static String getRoomMessages() {
        return MessagesAdapter.MESSAGES_URL +
                "?" +
                MessagesAdapter.ROOM_ID_QUERY +
                "=" +
                MessagesAdapter.ROOM_ID;
    }
    @Override
    protected JSONObject doInBackground(Void... params) {

        URL fileURL = null;
        JSONObject jsonObject = null;
        try {
            fileURL = new URL(getRoomMessages());
            HttpURLConnection connection = (HttpURLConnection) fileURL
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedInputStream inputStream = new BufferedInputStream(
                    connection.getInputStream());

            Log.v("VIC:", "contentLength: " + connection.getContentLength());
            byte[] data = new byte[1024];
            int read = inputStream.read(data);
            StringBuilder stringData = new StringBuilder();
            while (read >= 0) {
                stringData.append(new String(data, 0, read));
            }
            Log.v("VIC:",stringData.toString());
            inputStream.close();
                jsonObject = new JSONObject(stringData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }   catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
