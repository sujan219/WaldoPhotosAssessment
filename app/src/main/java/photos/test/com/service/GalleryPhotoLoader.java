package photos.test.com.service;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sujan on 11/29/2016.
 */

public class GalleryPhotoLoader implements PhotoLoader {

    private List<Integer> photoIdList;
    private List<Integer> cache;
    private Context ctx;
    private boolean fullyLoaded;
    private static final int LOAD_FACTOR = 15;

    GalleryPhotoLoader(Context ctx) {
        photoIdList = new ArrayList<Integer>();
        cache = new ArrayList<Integer>();
        this.ctx = ctx;

        loadPhotos();
    }

    private void loadPhotos() {
        Cursor cursor = ctx.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            photoIdList.add(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void loadPhoto(ImageView imageView, int index) {
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                ctx.getContentResolver(), cache.get(index), MediaStore.Images.Thumbnails.MICRO_KIND, null);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getSize() {
        return cache.size();
    }

    @Override
    public void loadMore(final PhotoLoadListener listener) {

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(2500); //to fake the load time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int len = cache.size();
                for(int i=len; i<len+LOAD_FACTOR && i<photoIdList.size(); ++i){
                    cache.add(photoIdList.get(i));
                }

                if(cache.size() == photoIdList.size()){
                    fullyLoaded = true;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(listener != null) {
                    listener.loaded(cache.size()-LOAD_FACTOR);
                }
            }
        }.execute();
    }

    @Override
    public boolean isFullyLoaded() {
        return fullyLoaded;
    }
}
