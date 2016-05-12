package com.ciklum.firstresponder;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * Created by victorllana on 4/16/16.
 */
public class JpegDownloader extends AsyncTask<String, Void, JpegDownloader.Result> {
    public class Result {
        public boolean success = false;
        public ByteBuffer data = null;
    }

    public interface JpegDownloadListener {
        public void onResult(Result result);
    }

    private JpegDownloadListener mListener;

    public JpegDownloader(JpegDownloadListener listener) {
        mListener = listener;
    }
    /**
     *
     * @param params index 0 = server url/ip address.  index 1 = filename
     * @return  Result
     */
    @Override
    protected Result doInBackground(String[] params) {
        Result result = new Result();
        Log.v("VIC:", "downloading file: " + params[0]);
        try {
            URL fileURL = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) fileURL
                    .openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", Urls.AUTHORIZATION);
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedInputStream inputStream = new BufferedInputStream(
                    connection.getInputStream());
            result.data = ByteBuffer.allocate(connection.getContentLength());
            Log.v("VIC:", "contentLength: " + connection.getContentLength());
            byte[] data = new byte[1024];
            int read = inputStream.read(data);
            int total = read;
            while (read >= 0) {
                result.data.put(data, 0, read);
                read = inputStream.read(data);
                total += read;
                //Log.v("VIC:", "Total: " + total);
            }
            inputStream.close();
            result.success = true;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        if (mListener != null) {
            mListener.onResult(result);
        }
    }
}
