package ph.easyaf.ebapirequests.handlers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetAPIRequest extends APIRequest<String, Integer, String> {

    public GetAPIRequest(int requestCode, String methodPath, AsyncListener<String> listener) {
        super(requestCode, methodPath, listener);
        this.listener = listener;
    }

    /**
     *
     * @param params array of headers, must be consists of value and header separated by '='
     * @return
     */
    public String doInBackground(String... params) {
        String result = "";
        try {
            methodPath = methodPath.replace(" ", "%20");
            URL url = new URL(API_PATH + "/" + methodPath);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setRequestMethod("GET");
            urlCon.setRequestProperty("Content-Type", "application/json");
            urlCon.setRequestProperty("Accept", "application/json");
            urlCon.setRequestProperty("charset", "UTF-8");
            if (params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    String[] split = params[i].split("=");
                    if (split.length > 1) {
                        urlCon.setRequestProperty(split[0], split[1]);
                    }
                }
            }
            responseCode = urlCon.getResponseCode();

            result = readContent(urlCon);
            urlCon.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    protected void onReadContentProgress(int progress, int size) {
        publishProgress(progress, size);
    }

    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        listener.onRequestProgress(requestCode, values[0], values[1]);
    }

    public void onPostExecute(String result) {
        if (Thread.interrupted()) { return; }
        listener.onRequestFinish(requestCode, responseCode, result);
    }

}
