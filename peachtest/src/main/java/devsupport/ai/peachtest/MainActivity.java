package devsupport.ai.peachtest;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import devsupport.ai.peachpay.Config;
import devsupport.ai.peachpay.Peach;
import devsupport.ai.peachpay.PeachListener;

public class MainActivity extends AppCompatActivity {
    Button pay;

    PeachListener peachListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Uncomment the following line to see the payments working*/

        callPeachPayments("92.00", "EUR", "DB", Config.TEST);
    }

    private void callPeachPayments(String amount, String currency, String type, String env) {
        final Activity activity = this;

        JSONObject pay = new JSONObject();
        try {
            pay.put("amount", amount);
            pay.put("currency", currency);
            pay.put("type", type);
            pay.put("env", env);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initListener();

        Peach peachPay = new Peach(pay, activity, peachListener);
        IntentFilter filter = new IntentFilter("ai.devsupport.peachpay");
        registerReceiver(peachPay, filter);

    }

    private void initListener() {
        peachListener = new PeachListener() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), "Success:" + response, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed Reason:" + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }


}
