package photos.test.com.waldophotos;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

        try {
            //System.out.println(RestClient.getData(null));
        }catch (Exception e){
            e.printStackTrace();
        }

        assertEquals(4, 2 + 2);
    }
}