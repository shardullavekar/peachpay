package devsupport.ai.peachpay.REST;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by shardullavekar on 19/08/17.
 */

public class Post {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public String getId(String url, String amount, String currency, String type) throws IOException {
        Request request = new Request.Builder()
                .url(url + "?action=checkout&currency=" + currency + "&type=" + type + "&amount=" + amount)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
