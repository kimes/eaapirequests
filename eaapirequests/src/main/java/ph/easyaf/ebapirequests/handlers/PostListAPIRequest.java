package ph.easyaf.ebapirequests.handlers;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostListAPIRequest extends APIRequest<String, Integer, JSONArray> {

    private JSONArray objectToPass;

    public PostListAPIRequest(int requestCode, String methodPath,
                               JSONArray objectToPass, AsyncListener<JSONArray> listener) {
        super(requestCode, methodPath, listener);
        this.objectToPass = objectToPass;
    }

    protected JSONArray doInBackground(String... strings) {
        String responseMessage = "";
        try {
            URL url = new URL(API_PATH + "/" + methodPath);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setRequestMethod("POST");
            urlCon.setInstanceFollowRedirects(false);
            urlCon.setRequestProperty("Content-Type", "application/json");
            urlCon.setRequestProperty("Accept", "application/json");
            urlCon.setRequestProperty("charset", "UTF-8");
            urlCon.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
            wr.write(objectToPass.toString().getBytes("UTF-8"));
            wr.flush();
            wr.close();

            responseCode = urlCon.getResponseCode();
            responseMessage = readContent(urlCon);
            urlCon.disconnect();
        } catch (IOException e) { e.printStackTrace(); }

        JSONArray responseJSON = new JSONArray();
        try {
            responseJSON = new JSONArray(responseMessage);
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

    public void onPostExecute(JSONArray responseObject) {
        listener.onRequestFinish(requestCode, responseCode, responseObject);
    }
}
