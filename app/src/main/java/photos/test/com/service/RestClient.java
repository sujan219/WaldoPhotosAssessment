package photos.test.com.service;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by sujan on 11/28/2016.
 */

public class RestClient {

    private static String url = "https://auth.dev.waldo.photos/";
    private static final String API_URL = "https://core-graphql.dev.waldo.photos/gql";

    private static final String TEST_DATA = "{\"query\":\"{album(id: \\\"YWxidW06ZjNjNWE4ZTQtMzRhNy00NWI0LWFmZGQtOTIxNTJhZmNmZTgz\\\") {id,name,photos {records {urls {size_code, url, width, height, quality, mime}}}}}";

    public static String login(Activity activity) throws Exception {

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "andy");
                params.put("password", "1234");

                return params;
            }

        };

        Volley.newRequestQueue(activity).add(jsonObjReq);
        return null;
    }

    public static String getData(Activity activity) throws Exception {

        JSONObject jObj = new JSONObject();
        jObj.put("query", TEST_DATA);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                API_URL, jObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) /*{

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("query", TEST_DATA);
                return params;
            }

        }*/;

        Volley.newRequestQueue(activity).add(jsonObjReq);
        return null;
    }
}