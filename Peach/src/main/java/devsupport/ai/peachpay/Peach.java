package devsupport.ai.peachpay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shardullavekar on 11/09/17.
 */

public class Peach extends BroadcastReceiver {
    String amount, currency, type, env;

    JSONObject payment;
    Activity activity;
    PeachListener listener;

    public Peach(JSONObject payment, Activity activity, PeachListener listener) {
        this.payment = payment;
        this.activity = activity;
        this.listener = listener;
    }

    public void start() {
        try {
            amount = payment.getString("amount");
            currency = payment.getString("currency");
            type = payment.getString("type");
            env = payment.getString("env");
            startActivity(amount, currency, type, env);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailure(Config.FAILED, "Incorrect Value passed for amount/currency/type");
        }

    }

    private void startActivity(String amount, String currency, String type, String env) {
        Intent intent = new Intent(activity, PeachPay.class);
        intent.putExtra("amount", amount);
        intent.putExtra("currency", currency);
        intent.putExtra("type", true);
        intent.putExtra("env", env);
        activity.startActivity(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
