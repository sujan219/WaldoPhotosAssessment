package photos.test.com.webservice;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sujan on 11/28/2016.
 */

public enum RestClient {
    INSTANCE;

    private static final String USERNAME = "andy";
    private static final String PASSWORD = "1234";
    private static final String LOGIN_URL = "https://auth.staging.waldo.photos";
    private static final String API_URL = "https://core-graphql.staging.waldo.photos/gql";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;
    private boolean isLoggedIn = false;

    private RestClient(){

        client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private List<Cookie> cookies = new ArrayList<Cookie>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        if(url.toString().contains(LOGIN_URL)){
                            this.cookies = cookies;
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
    }

    public String loadData(int limit, int offset){
        //request the auth cookie if not done yet. This will be done only one time.
        if(!isLoggedIn){
            if(login() == null){
                return null;
            }
            isLoggedIn = true;
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("query", getGraphQLQuery(limit, offset));
        return doGet(API_URL, map);
    }

    private String getGraphQLQuery(int limit, int offset){
        return "{album(id: \"YWxidW06YTQwYzc5ODEtMzE1Zi00MWIyLTk5NjktMTI5NjIyZDAzNjA5\") { id name photos(slice: { limit: "+limit+", offset: "+offset+" }) {records {id urls {size_code url width height quality mime}}}}}";
    }

    private String login(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", USERNAME);
        map.put("password", PASSWORD);
        return doPost(LOGIN_URL, map);
    }

    private String doGet(final String url, final Map<String, String> params){
        try {
            HttpUrl.Builder queryBuilder = HttpUrl.parse(url).newBuilder();
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String, String> entry = iterator.next();
                queryBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }

            HttpUrl queryUrl = queryBuilder.build();
            Request req = new Request.Builder().url(queryUrl).get().build();
            Response response = client.newCall(req).execute();
            return new String(response.body().bytes());
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String doPost(final String url, final Map<String, String> params){
        try {
            HttpUrl queryUrl = HttpUrl.parse(url).newBuilder().build();
            RequestBody requestBody = RequestBody.create(JSON, getJSONString(params));
            Request req = new Request.Builder().url(queryUrl).post(requestBody).build();
            Response response = client.newCall(req).execute();
            List<Cookie> list = client.cookieJar().loadForRequest(queryUrl);
            for(Cookie c:list){
                System.out.println(c.toString());
            }
            return new String(response.body().bytes());
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String getJSONString(Map<String, String> params) throws JSONException {
        JSONObject obj = new JSONObject();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            obj.put(entry.getKey(), entry.getValue());
        }
        return obj.toString();
    }
}