package com.ciklum.firstresponder;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by victorllana on 5/11/16.
 */
public class PostJson extends AsyncTask<JSONObject, Void, JSONObject> {
    public interface PostJsonListener {
        public void onJsonReceived(JSONObject jsonObject);
    }

    private PostJsonListener mListener;
    private String mUrl;
    public PostJson(String url, PostJsonListener listener) {
        mUrl = url;
        mListener = listener;
    }

    @Override
    protected JSONObject doInBackground(JSONObject... params) {

        URL fileURL = null;
        JSONObject jsonObject = null;
        try {
            fileURL = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection) fileURL
                    .openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Authorization", Urls.AUTHORIZATION);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            Log.v("VIC:", "Post Body: \n" + params[0].toString());
            osw.write(params[0].toString());
            osw.flush();

            int responseCode = connection.getResponseCode();
            Log.v("VIC:", "responseCode:" + responseCode);
            BufferedInputStream inputStream = new BufferedInputStream(
                    connection.getInputStream());

            //Log.v("VIC:", "contentLength: " + connection.getContentLength());
            byte[] data = new byte[1024];
            int readSize = inputStream.read(data);
            StringBuilder stringData = new StringBuilder();
            while (readSize >= 0) {
                //Log.v("VIC:", "readSize " + readSize);
                String temp_read = new String(data, 0, readSize);
                //Log.v("VIC:", "temp_read" + temp_read);
                stringData.append(temp_read);
                readSize = inputStream.read(data);
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

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (mListener != null) {
            mListener.onJsonReceived(jsonObject);
        }
    }
}
