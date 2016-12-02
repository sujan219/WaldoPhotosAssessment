package photos.test.com.service;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import photos.test.com.waldophotos.R;
import photos.test.com.webservice.RestClient;

/**
 * Created by sujan on 12/1/2016.
 */

public class WaldoPhotoLoader implements PhotoLoader {

    private static final int LIMIT = 50;

    private Context ctx;
    private List<String> imageUrls;
    private int offset = 0;
    private boolean isFullyLoaded = false;
    private ImageLoader imageLoader;
    private int lastSize = 0;
    private DisplayImageOptions imgDisplayOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.image)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    public WaldoPhotoLoader(Context context) {
        ctx = context;
        imageLoader = ImageLoader.getInstance();
        imageUrls = new ArrayList<String>();
    }

    @Override
    public void loadPhoto(ImageView imageView, int index) {
        imageLoader.displayImage(imageUrls.get(index), imageView, imgDisplayOptions);
    }

    @Override
    public int getSize() {
        return imageUrls.size();
    }

    @Override
    public void loadMore(final PhotoLoadListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                String response = RestClient.INSTANCE.loadData(LIMIT, offset * LIMIT);
                List<String> tempList = new ArrayList<String>();
                if (response != null) {
                    try {
                        loadUrlsFromResponse(response, tempList);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }

                    if(tempList.size() == 0){
                        isFullyLoaded = true;
                    }else{
                        lastSize = imageUrls.size();
                        imageUrls.addAll(tempList);
                    }
                    return true;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    listener.loaded(lastSize);
                    ++offset;
                } else {
                    listener.loadFailed();
                }
            }
        }.execute();
    }

    private void loadUrlsFromResponse(String response, List<String> list) throws JSONException {
        JSONObject mainObj = new JSONObject(response);
        JSONArray recordArray = mainObj.getJSONObject("data").getJSONObject("album")
                .getJSONObject("photos").getJSONArray("records");
        for(int i=0; i<recordArray.length(); ++i){
            JSONArray urlArr = recordArray.getJSONObject(i).getJSONArray("urls");
            for(int j=0; j<urlArr.length(); ++j){
                JSONObject urlObj = urlArr.getJSONObject(j);
                //we only need medium size images
                if(urlObj.getString("size_code").equalsIgnoreCase("small2x")){
                    list.add(urlObj.getString("url"));
                    break;
                }
            }
        }
    }

    @Override
    public boolean isFullyLoaded() {
        return isFullyLoaded;
    }
}