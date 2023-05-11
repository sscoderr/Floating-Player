package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector.YoutubeConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.keysHashList;

public class LoadSelectKey {

    Context context;
    private String txtValueFromServer="777";

    public LoadSelectKey(Context context) {
        this.context = context;

        new GET_APIKEY().execute();
    }


    private class GET_APIKEY extends AsyncTask<Void, Void, Void> {
        String[] keyList;
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                API_KEY();
                versionController();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SharedPreferences preferences = MSettings.activeActivity.getSharedPreferences("keysList", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putInt("apiKeysVersion", Integer.parseInt(txtValueFromServer));


            editor.putStringSet("keysList", null);
            editor.putStringSet("keysList", keysHashList);
            editor.apply();
            editor.commit();
            super.onPostExecute(aVoid);
        }

        private void API_KEY() throws JSONException {
            HttpHandler HH = new HttpHandler();
            keysHashList = new HashSet<String>();
            String JSON_FULL = HH.makeServiceCall(context.getResources().getString(R.string.apiKeyLink));
            if (JSON_FULL != null) {
                JSONArray jsonItems = new JSONObject(JSON_FULL).getJSONArray("KEYS");
                if (jsonItems != null) {
                    Integer Count = jsonItems.length();

                    if (Count > 0) {
                        keyList = new String[Count];
                        for (int i=0; i<Count; i++) {
                            keyList[i] = jsonItems.getString(i);
                            keysHashList.add(keyList[i]);
                        }

                        if (keyList != null) {
                            if (keyList.length > 0) {
                                YoutubeConnector.KEY = keyList[(new Random()).nextInt(keyList.length)];
                            }
                        }
                    }
                }
            }
        }

        public void versionController()
        {
            URL url;
            try {
                //create url object to point to the file location on internet
                url = new URL(context.getResources().getString(R.string.apiKeyUpdateLink));
                //make a request to server
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //get InputStream instance
                InputStream is = con.getInputStream();
                //create BufferedReader object
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                //read content of the file line by line
                txtValueFromServer=br.readLine();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
                //close dialog if error occurs
            }
        }
    }
}