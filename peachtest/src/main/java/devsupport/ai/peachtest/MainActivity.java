package devsupport.ai.peachtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import devsupport.ai.peachpay.Config;
import devsupport.ai.peachpay.PeachPay;

public class MainActivity extends AppCompatActivity {
    Button pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pay = (Button) this.findViewById(R.id.pay_button);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PeachPay.class);
                intent.putExtra("amount", "92.00");
                intent.putExtra("currency", "EUR");
                intent.putExtra("type", "DB");
                intent.putExtra("env", Config.TEST);
                startActivityForResult(intent, Config.PEACHPAY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.PEACHPAY:
                switch (resultCode) {
                    case Config.SUCCESS: {
                        Toast.makeText(getApplicationContext(), data.getStringExtra("response"), Toast.LENGTH_LONG)
                                .show();
                        break;
                    }
                    case Config.FAILED: {
                        Toast.makeText(getApplicationContext(), data.getStringExtra("response"), Toast.LENGTH_LONG)
                                .show();
                        break;
                    }
                    default:
                        break;
                }
                break;

            default:
                break;
        }
    }
}
