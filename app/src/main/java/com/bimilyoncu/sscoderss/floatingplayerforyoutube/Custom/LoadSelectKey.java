package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom;

import android.content.Context;
import android.os.AsyncTask;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.YoutubeConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class LoadSelectKey {

    Context context;

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void API_KEY() throws JSONException {
            HttpHandler HH = new HttpHandler();

            String JSON_FULL = HH.makeServiceCall(context.getString(R.string.apiKeyLink));
            if (JSON_FULL != null) {
                JSONArray jsonItems = new JSONObject(JSON_FULL).getJSONArray("KEYS");
                if (jsonItems != null) {
                    Integer Count = jsonItems.length();

                    if (Count > 0) {
                        keyList = new String[Count];
                        for (int i=0; i<Count; i++) {
                            keyList[i] = jsonItems.getString(i);
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
    }
}