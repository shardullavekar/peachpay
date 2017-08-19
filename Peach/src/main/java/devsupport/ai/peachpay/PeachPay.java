package devsupport.ai.peachpay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PeachPay extends AppCompatActivity {
    String ordernauth_url,
            amountstr, email, phone, name, description, purpose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
