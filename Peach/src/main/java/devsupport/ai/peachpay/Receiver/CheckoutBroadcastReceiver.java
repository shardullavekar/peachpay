package devsupport.ai.peachpay.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;

/**
 * Created by shardullavekar on 19/08/17.
 */

public class CheckoutBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String paymentBrand = intent.getStringExtra(CheckoutActivity.EXTRA_PAYMENT_BRAND);
        String checkoutId = intent.getStringExtra(CheckoutActivity.EXTRA_CHECKOUT_ID);

        intent = new Intent(context, CheckoutActivity.class);
        intent.setAction(CheckoutActivity.ACTION_PAYMENT_METHOD_SELECTED);
        intent.putExtra(CheckoutActivity.EXTRA_CHECKOUT_ID, checkoutId);

        context.startActivity(intent);
    }

}
