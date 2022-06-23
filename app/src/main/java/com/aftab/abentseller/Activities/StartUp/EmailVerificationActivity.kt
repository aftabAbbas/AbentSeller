package com.aftab.abentseller.Activities.StartUp

import android.app.ActionBar
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.Functions
import com.aftab.abentseller.Utils.LoadingDialog
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.ActivityEmailVerificationBinding
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Suppress("deprecation")
class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailVerificationBinding

    private lateinit var rq: RequestQueue

    private lateinit var sh: SharedPref
    private lateinit var loadingDialog: LoadingDialog

    private var send = false
    private lateinit var email: String
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }

    private fun initUI() {

        sh = SharedPref(this)

        rq = Volley.newRequestQueue(this)
        loadingDialog = LoadingDialog(this, "Loading")

        email = sh.getString(Constants.EMAIL)


        if (!sh.getBoolean(Constants.IS_EMAIL_SENT)) {

            sendMail()

        } else {

            Toast.makeText(this, "" + sh.getString(Constants.CODE), Toast.LENGTH_LONG).show()

            openDialog()

        }

    }

    private fun clickListeners() {

        binding.btnDone.setOnClickListener {

            Functions.hideKeyBoard(this)

            val savedCode = sh.getString(Constants.CODE)
            val inputtedCode: String = Objects.requireNonNull(binding.pinView.text).toString()

            if (savedCode == inputtedCode) {

                Toast.makeText(this, resources.getString(R.string.matched), Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, CreateAccountActivity::class.java))

            } else {

                Toast.makeText(this, resources.getString(R.string.not_match), Toast.LENGTH_SHORT)
                    .show()

            }

        }

        binding.tvResend.setOnClickListener {

            sendMail()

        }

        binding.tvChangeEmail.setOnClickListener {

            sh.putString(Constants.FIRST_NAME, "")
            sh.putString(Constants.LAST_NAME, "")
            sh.putString(Constants.EMAIL, "")
            sh.putString(Constants.PHONE, "")
            sh.putString(Constants.PASSWORD, "")
            sh.putString(Constants.CODE, "")
            sh.putBoolean(Constants.IS_EMAIL_SENT, false)


            finish()
            startActivity(Intent(this, SignUpActivity::class.java))

        }
    }

    private fun sendMail() {

        code = Functions.getRandomRequestCode().toString() + ""
        sh.putString(Constants.CODE, code)

        Toast.makeText(this, "" + code, Toast.LENGTH_LONG).show()

        loadingDialog.show()

        send = false
        val request: StringRequest = object : StringRequest(
            Method.POST, Constants.MAIL_URL,
            Response.Listener { response: String ->
                try {

                    loadingDialog.dismiss()

                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("message")
                    sh.putBoolean(Constants.IS_EMAIL_SENT, true)
                    sh.putString(Constants.CODE, code)

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    loadingDialog.dismiss()
                    Toast.makeText(this, "Catch: " + e.message, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error: VolleyError ->
               /* if (error.message == null) {
                    binding.tvResend.performClick()
                } else {
                    loadingDialog.dismiss()
                    Toast.makeText(this, "Error: " + error.message, Toast.LENGTH_SHORT).show()
                }
*/
                loadingDialog.dismiss()

            }) {
            override fun getParams(): Map<String, String> {
                val map: MutableMap<String, String> = HashMap()
                val message =
                    "Hello new user! You’re one step closer to learning how to maximize the productivity of your Abent!  Simply input this code $code into the Abent Seller App for verification to access all of Abent’s content and functions."
                map["to"] = email
                map["subject"] = "Abent Seller Email Verification"
                map["message"] = message
                return map
            }
        }
        if (!send) {
            send = true

        }

        val mRetryPolicy: RetryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        request.retryPolicy = mRetryPolicy


        rq.add(request)
    }

    private fun openDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.email_send_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        val tvText = dialog.findViewById<TextView>(R.id.tv_email_text)
        val btnOpenEmail = dialog.findViewById<Button>(R.id.btn_open_email)
        val ivCancel = dialog.findViewById<ImageView>(R.id.iv_cancel)
        val text = ("Email sent to your "
                + "<b>" + email + "</b> email successfully")
        tvText.text = Html.fromHtml(text)

        btnOpenEmail.setOnClickListener {
            dialog.dismiss()
            try {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivityForResult(intent, 32)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "There is no email app installed.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        ivCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
    }

}