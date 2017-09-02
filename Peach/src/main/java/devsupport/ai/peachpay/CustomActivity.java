package devsupport.ai.peachpay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class CustomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
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
}
