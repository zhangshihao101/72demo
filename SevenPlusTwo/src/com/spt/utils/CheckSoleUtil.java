package com.spt.utils;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class CheckSoleUtil {

    public static String selectKeys;
    public static String isRepeats;

    public static String checkSole(String selectKey, String selectValue) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("selectKey", selectKey);
        params.put("selectValue", selectValue);
        params.put("client_id", "localhost");

        String url = MtsUrls.base + MtsUrls.sso_issole + "?selectKey=" + selectKey + "&selectValue=" + selectValue
                + "&client_id=localhost&sign=" + SignUtil.genSign(params, "localhost");

        OkHttpManager.client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonStr = response.body().string();
                isRepeats = jsonStr;
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("=====" + "查重结果" + "======" + jsonStr + "=======");
//                        try {
//                            JSONObject object = new JSONObject(jsonStr);
////                            selectKeys = object.optString("selectKey");
////                            isRepeats = object.optString("isRepeat");
//                        } catch (JSONException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
                    }
                });
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                    }
                });

            }
        });

        return isRepeats;

    }

}
