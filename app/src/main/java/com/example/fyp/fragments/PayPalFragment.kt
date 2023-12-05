//package com.example.fyp.fragments
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import com.example.fyp.R
//import com.google.androidbrowserhelper.playbilling.provider.PaymentActivity
//import com.paypal.android.sdk.payments.*
//
//import org.json.JSONException
//import org.json.JSONObject
//import java.math.BigDecimal
//
//
//class PaypalFragment : Fragment() {
//
//    private lateinit var editAmount: EditText
//    private lateinit var btnPayment: Button
//    private var PAYPAL_REQUEST_CODE: Int = 123
//    private var config: PayPalConfiguration = PayPalConfiguration()
//        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
//        .clientId("Ad_E40m2d08jzJ14fru_R5TDYXSQfxaMXmVaS95aH6f4egLseBE9T2lOAHOHRvKAJhyHfgu9iXhvtGVK")
//    private lateinit var txtId : TextView
//    private lateinit var txtStatus : TextView
//
//    private fun getPayment() {
//        val amount: String = editAmount.text.toString()
//        val payment = PayPalPayment(
//            BigDecimal(amount),
//            "USD",
//            "Total Price",
//            PayPalPayment.PAYMENT_INTENT_SALE
//        )
//
//
//        val intent = Intent(requireContext(), PaymentActivity::class.java)
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
//
//        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PAYPAL_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                val paymentConfirmation: PaymentConfirmation? =
//                    data?.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
//
//                if (paymentConfirmation != null) {
//                    try {
//                        val paymentDetails: String = paymentConfirmation.toJSONObject().toString()
//                        val paymentState: String = paymentConfirmation.proofOfPayment.state
//                        val paymentId: String = paymentConfirmation.proofOfPayment.paymentId
//                        val jsonObject = JSONObject(paymentDetails)
//                        // Process your payment details as needed
////                        showDetails(jsonObject.getJSONObject("response"))
//                        Toast.makeText(requireContext(), "Payment successful. Payment ID: $paymentId", Toast.LENGTH_SHORT).show()
//                    } catch (e: JSONException) {
//                        Toast.makeText(
//                            requireContext(),
//                            "Error parsing payment details",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        e.printStackTrace()
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(requireContext(), "Payment canceled", Toast.LENGTH_SHORT).show()
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                Toast.makeText(requireContext(), "Invalid payment", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val rootView = inflater.inflate(R.layout.fragment_paypal, container, false)
//
//        val intent = Intent(requireContext(), PayPalService::class.java)
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
//        requireContext().startService(intent)
//
//        editAmount = rootView.findViewById(R.id.paypalAmount)
//        btnPayment = rootView.findViewById(R.id.payButton)
//        txtId = rootView.findViewById(R.id.txtPayId)
//        txtStatus = rootView.findViewById(R.id.txtStatus)
//
////        config = PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
////            .clientId(clientId)
//
//        btnPayment.setOnClickListener {
//            getPayment()
//        }
//
//        return rootView
//    }
//
//    override fun onDestroy() {
//        // Stop PayPal service to prevent memory leaks
//        val intent = Intent(requireContext(), PayPalService::class.java)
//        requireContext().stopService(intent)
//        super.onDestroy()
//    }
//}