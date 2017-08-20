package devsupport.ai.peachpay.API;

import android.os.AsyncTask;

import java.io.IOException;

import devsupport.ai.peachpay.Callback;
import devsupport.ai.peachpay.REST.Post;

/**
 * Created by shardullavekar on 19/08/17.
 */

public class Checkout {
    public Checkout() {

    }

    public void post(String url, String amount, String currency, String type, Callback callback) {
        CheckoutAsynch checkoutAsynch = new CheckoutAsynch(callback);
        checkoutAsynch.execute(new String[]{url, amount, currency, type});
    }

    private class CheckoutAsynch extends AsyncTask<String, Void, String> {
        Callback callback;
        public CheckoutAsynch(Callback callback) {
            this.callback = callback;
        }
        @Override
        protected String doInBackground(String... params) {
            Post post = new Post();
            String checkoutResponse;
            try {
                checkoutResponse = post.getId(params[0], params[1], params[2], params[3]);
            } catch (IOException e) {
                e.printStackTrace();
                return "ioException - check your net connection";
            }
            return checkoutResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.callback.onResponse(s);
        }
    }
}
