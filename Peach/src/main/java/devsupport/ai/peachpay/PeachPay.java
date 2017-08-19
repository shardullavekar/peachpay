package devsupport.ai.peachpay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.io.IOException;

import devsupport.ai.peachpay.REST.CheckoutId;

public class PeachPay extends AppCompatActivity {
    String server_url,
            amount, currency, type, env;

    ApplicationInfo app;

    Bundle bundle;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = null;

        dialog = new ProgressDialog(PeachPay.this);

        try {
            app = getApplicationContext().getPackageManager()
                    .getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        bundle = app.metaData;

        amount = getIntent().getStringExtra("amount");

        currency = getIntent().getStringExtra("currency");

        type = getIntent().getStringExtra("type");

        env = getIntent().getStringExtra("env");

        server_url = bundle.getString(Config.SERVER_URL);

        if (checkValidation()) {
            setContentView(R.layout.activity_main);
            getCheckoutId();
        }
    }

    private void getCheckoutId() {
        CheckoutId checkoutId = new CheckoutId();
        try {
            String id = checkoutId.getId(server_url, amount, currency, type);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkValidation() {
        if (TextUtils.isEmpty(server_url)) {
            endActivity(Config.FAILED, "Invalid Order URL");
            return false;
        }

        if (TextUtils.isEmpty(amount)) {
            endActivity(Config.FAILED, "Invalid Amount");
            return false;
        }

        if (TextUtils.isEmpty(currency)) {
            endActivity(Config.FAILED, "Invalid Currency");
            return false;
        }

        if (TextUtils.isEmpty(type)) {
            endActivity(Config.FAILED, "Invalid Type");
            return false;
        }

        if (TextUtils.isEmpty(env)) {
            endActivity(Config.FAILED, "Invalid Enviro");
            return false;
        }

        return true;
    }

    private void endActivity(int resultCode, String message) {
        Intent data = new Intent();
        data.putExtra("response", message);
        setResult(resultCode, data);
        PeachPay.this.finish();
        return;
    }
}
