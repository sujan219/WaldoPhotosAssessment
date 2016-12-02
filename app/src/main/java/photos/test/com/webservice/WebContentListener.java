package photos.test.com.webservice;

/**
 * Created by sujan on 12/1/2016.
 */

public interface WebContentListener{
    public void onSuccess(String response);
    public void onError(String message);
}
