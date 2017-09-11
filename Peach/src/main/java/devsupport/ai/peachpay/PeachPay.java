package devsupport.ai.peachpay;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.exception.PaymentException;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;
import com.oppwa.mobile.connect.service.ConnectService;
import com.oppwa.mobile.connect.service.IProviderBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashSet;
import java.util.Set;

import devsupport.ai.peachpay.API.Checkout;
import devsupport.ai.peachpay.Receiver.CheckoutBroadcastReceiver;

public class PeachPay extends AppCompatActivity {
    private IProviderBinder binder;

    private ServiceConnection serviceConnection;

    String server_url,
            amount, currency, type, env, checkoutId;

    ApplicationInfo app;

    Bundle bundle;

    private ProgressDialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ConnectService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        stopService(new Intent(this, ConnectService.class));
    }

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
            initEnv();
            setContentView(R.layout.activity_main);
            getCheckoutId();
        }
    }

    private void initEnv() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (IProviderBinder) service;

                try {
                    if (TextUtils.equals(env, Config.TEST)) {
                        binder.initializeProvider(Connect.ProviderMode.TEST);
                    }
                    else if (TextUtils.equals(env, Config.TEST)){
                        binder.initializeProvider(Connect.ProviderMode.LIVE);
                    }
                    else {
                        endActivity(Config.FAILED, "Invalid Environment");
                    }
                } catch (PaymentException ee) {
	                endActivity(Config.FAILED, ee.getMessage());
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                binder = null;
            }
        };
    }

    private void getCheckoutId() {
        Checkout checkout = new Checkout();
        checkout.post(server_url, amount, currency, type, new Callback() {
            @Override
            public void onResponse(String response) {
                dismissDialogue();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    JSONObject result = jsonObject.getJSONObject("result");
                    if (TextUtils.equals(result.getString("code"), Config.PEACH_SUCCESS)) {
                        configCheckout(jsonObject.getString("id"));
                    }
                    else {
                        endActivity(Config.FAILED, result.getString("description"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    endActivity(Config.FAILED, "Unable to fetch failure reason");
                }

            }
        });
        showDialogue("Getting Checkout Id");
    }

    private void configCheckout(String checkoutId) {
        this.checkoutId = checkoutId;
        Set<String> paymentBrands = new LinkedHashSet<>();

        paymentBrands.add("VISA");
        paymentBrands.add("MASTER");
        paymentBrands.add("DIRECTDEBIT_SEPA");

        CheckoutSettings checkoutSettings = new CheckoutSettings(checkoutId, paymentBrands);
        checkoutSettings.setWebViewEnabledFor3DSecure(true);

        ComponentName componentName = new ComponentName(BuildConfig.APPLICATION_ID,
                CheckoutBroadcastReceiver.class.getCanonicalName());

        Intent intent = new Intent(PeachPay.this, CheckoutActivity.class);
        intent.putExtra(CheckoutActivity.CHECKOUT_SETTINGS, checkoutSettings);
        //intent.putExtra(CheckoutActivity.CHECKOUT_RECEIVER, componentName);

        startActivityForResult(intent, CheckoutActivity.CHECKOUT_ACTIVITY);
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
            endActivity(Config.FAILED, "Invalid Environment");
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case CheckoutActivity.RESULT_OK:
            /* transaction completed */
                Transaction transaction = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);

            /* resource path if needed */
                String resourcePath = data.getStringExtra(CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);

                if (transaction.getTransactionType() == TransactionType.SYNC) {
                /* check the result of synchronous transaction */
                endActivity(Config.SUCCESS, "checkoutId=" + checkoutId);
                } else {
                /* wait for the asynchronous transaction callback in the onNewIntent() */
                }

                break;
            case CheckoutActivity.RESULT_CANCELED:
                endActivity(Config.FAILED, "Shoper cancelled transaction");
                break;
            case CheckoutActivity.RESULT_ERROR:
                PaymentError error = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_ERROR);
                endActivity(Config.FAILED, error.getErrorMessage());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getScheme().equals("devsupport")) {
            String checkoutId = intent.getData().getQueryParameter("id");
            Toast.makeText(getApplicationContext(), checkoutId, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void showDialogue(String message) {
        dialog.setMessage(message);
        dialog.show();
    }

    private void dismissDialogue() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    private void endActivity(int resultCode, String message) {
        Intent data = new Intent();
        data.putExtra("response", message);
        setResult(resultCode, data);
        PeachPay.this.finish();
        return;
    }
}
