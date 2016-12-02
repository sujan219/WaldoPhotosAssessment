package photos.test.com.service;

import android.content.Context;

/**
 * Created by sujan on 11/29/2016.
 */

public class PhotoLoaderFactory {
    public static PhotoLoader createPhotoLoader(Context ctx){
        return new WaldoPhotoLoader(ctx);
    }
}
