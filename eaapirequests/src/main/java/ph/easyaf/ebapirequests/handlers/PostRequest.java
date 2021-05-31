package ph.easyaf.ebapirequests.handlers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostRequest extends APIRequest<String, Integer, JSONObject> {

    private JSONObject objectToPass;

    public PostRequest(int requestCode, String methodPath,
                          JSONObject objectToPass, AsyncListener<JSONObject> listener) {
        super(requestCode, methodPath, listener);
        this.objectToPass = objectToPass;
    }

    protected JSONObject doInBackground(String... params) {
        String responseMessage = "";
        try {
            URL url = new URL(WEB_PATH + "/" + methodPath);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setRequestMethod("POST");
            urlCon.setInstanceFollowRedirects(false);
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
            urlCon.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
            wr.write(objectToPass.toString().getBytes("UTF-8"));
            wr.flush();
            wr.close();

            responseCode = urlCon.getResponseCode();
            responseMessage = readContent(urlCon);
            urlCon.disconnect();
        } catch (IOException e) { e.printStackTrace(); }

        JSONObject responseJSON = new JSONObject();
        try {
            responseJSON = new JSONObject(responseMessage);
        } catch (JSONException e) { e.printStackTrace(); }
        return responseJSON;
    }

    protected void onReadContentProgress(int progress, int size) {
        publishProgress(progress, size);
    }

    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        listener.onRequestProgress(requestCode, values[0], values[1]);
    }

    public void onPostExecute(JSONObject responseObject) {
        listener.onRequestFinish(requestCode, responseCode, responseObject);
    }
}
