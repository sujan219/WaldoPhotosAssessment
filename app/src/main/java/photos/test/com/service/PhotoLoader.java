package photos.test.com.service;

import android.widget.ImageView;

/**
 * Created by sujan on 11/29/2016.
 */

public interface PhotoLoader {
    public void loadPhoto(ImageView imageView, int index);
    public int getSize();
    public void loadMore(PhotoLoadListener listener);
    public boolean isFullyLoaded();
}
