package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp.fragments.AccountFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PayActivity extends AppCompatActivity {

    private static final String TAG = "MyTag";
    private PaymentButtonContainer paymentButtonContainer;
    private EditText amount;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        paymentButtonContainer = findViewById(R.id.payment_button_container);
        amount = findViewById(R.id.payAmount);
        backButton = findViewById(R.id.backBtn);

        backButton.setOnClickListener(view -> onBackPressed());

        paymentButtonContainer.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        Log.d(TAG, "create: ");
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value(amount.getText().toString())
                                                        .build()
                                        )
                                        .build()
                        );
                        OrderRequest order = new OrderRequest(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = auth.getCurrentUser();
                        String userId = (currentUser != null) ? currentUser.getUid() : null;

                        Expense.Companion.getNextNumberSequence(PayActivity.this, new Function1<Integer, Unit>() {
                            @Override
                            public Unit invoke(Integer counter) {
                                int numberSequence = counter;

                                // Now you can use the numberSequence as needed
                                // Create an Expense object with payment details
                                Expense expense = new Expense(
                                        "",
                                        "Payment",
                                        Double.parseDouble(amount.getText().toString()),
                                        getTodaysDate(),
                                        "General", false, "",
                                        userId, numberSequence
                                );

                                // Store the expense in Firestore
                                storeExpenseInFirestore(expense);

                                // Navigate back to MainActivity
                                Intent backIntent = new Intent(PayActivity.this, MainActivity.class);
                                startActivity(backIntent);

                                return null;
                            }
                        });
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void storeExpenseInFirestore(Expense expense) {
        // Generate a unique ID for the new expense
        String documentId = FirebaseFirestore.getInstance().collection("Expense").document().getId();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null && documentId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Set the generated ID to the expense
            expense.setId(documentId);

            // Add the expense to Firestore
            db.collection("Expense")
                    .document(documentId)
                    .set(expense)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(PayActivity.this, "Expense stored in Firestore", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PayActivity.this, "Error storing expense in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year) {
        return String.format("%02d-%02d-%04d", day, month, year);
    }
}
