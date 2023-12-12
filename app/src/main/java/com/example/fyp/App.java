package com.example.fyp;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Application;
import android.util.Log;

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
        Log.d(TAG, "onCreate: Setting up PayPal SDK");
        PayPalCheckout.setConfig(new CheckoutConfig(
                this,
                "Aakwbag7yDJ7ZcL6KzQZhEQ42HO7muUz9V5Wg1205KYGd655GPX-nd5hDiE696CJZB7P0NiEeGj8TZf5",
                Environment.SANDBOX,
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                "com.devshiv.paypaltestjava://paypalpay"
        ));
        Log.d(TAG, "onCreate: PayPal SDK setup complete");

    }

}