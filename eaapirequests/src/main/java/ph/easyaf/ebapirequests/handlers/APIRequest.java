package ph.easyaf.ebapirequests.handlers;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import ph.easyaf.ebapirequests.BuildConfig;

public abstract class APIRequest<A, B, C> extends AsyncTask<A, B, C> {

    protected static final int HTTP_REQUEST_TIMEOUT = 3000;
    //"https://easyafph.herokuapp.com"
    protected static final String WEB_PATH = BuildConfig.WEB_PATH,
            API_PATH = WEB_PATH + "/api";
    protected int requestCode, responseCode = 0;
    protected String methodPath = "";
    protected AsyncListener<C> listener;

    public APIRequest(int requestCode, String methodPath, AsyncListener<C> listener) {
        this.requestCode = requestCode;
        this.methodPath = methodPath;
        this.listener = listener;
    }

    protected String readContent(HttpURLConnection urlCon) {
        String returnValue = "";
        try {
            int contentLength = urlCon.getContentLength();

            BufferedReader br = null;
            if (responseCode >= 400) {
                br = new BufferedReader(new InputStreamReader(urlCon.getErrorStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            }

            if (contentLength > 0) {
                int bytesToReadPerLoop = 100, currBytesRead = 0;
                char[] buffer = new char[contentLength];
                while (currBytesRead < buffer.length) {
                    int count = bytesToReadPerLoop;
                    if ((currBytesRead + bytesToReadPerLoop) > buffer.length)
                        count = buffer.length - currBytesRead;

                    int readCount = br.read(buffer, currBytesRead, count);
                    currBytesRead += readCount;
                    onReadContentProgress(currBytesRead, contentLength);
                }
                returnValue = new String(buffer);
            } else {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                returnValue = sb.toString();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return returnValue;
    }

    protected abstract void onReadContentProgress(int progress, int size);

    public interface AsyncListener<T> {
        void onRequestProgress(int requestCode, int progress, int size);
        void onRequestFinish(int requestCode, int responseCode, T responseObject);
    }
}
