package devsupport.ai.peachpay;

/**
 * Created by shardullavekar on 11/09/17.
 */

public interface PeachListener {
    void onSuccess(String response);
    void onFailure(int code, String reason);
}