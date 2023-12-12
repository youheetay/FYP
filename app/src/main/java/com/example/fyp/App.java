package com.example.fyp;


import android.app.Application;

import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.pyplcheckout.BuildConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PayPalCheckout.setConfig(new CheckoutConfig(
                this,
                "AVYMFOlO-xATU9gvFeKFNKnfK6y-HP53GCuRWRf5X_nHwDyjT7zqiD5e4U85bvdHdGpMQ1dOyrarAMnG",
                Environment.SANDBOX,
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                "com.example.fyp://paypalpay"
        ));
    }
}
