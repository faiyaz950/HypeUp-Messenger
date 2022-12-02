package com.example.hype_up

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

const val PHONE_NUMBER = "phoneNumber"
class OtpActivity : AppCompatActivity(), View.OnClickListener {

     private lateinit var auth: FirebaseAuth
     lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
     var phoneNumber:String?= null
     private lateinit var progressDialog:ProgressDialog
     var mVerificationId:String? = null
     private var mCounterDown : CountDownTimer? = null
     private var mResendToken:PhoneAuthProvider.ForceResendingToken? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        initViews()
        startVerify()
    }

    private fun startVerify() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber!!,60,TimeUnit.SECONDS,this,callbacks)

        showTimer(60000)
        progressDialog = createProgressDialog("Sending a verification code", false)
        progressDialog.show()
    }

    private fun showTimer(milliSecInFuture:Long) {
        resendBtn.isEnabled = false
         mCounterDown = object : CountDownTimer(milliSecInFuture,1000){

            override fun onFinish() {
                resendBtn.isEnabled= true
                counterTv.isEnabled= false
            }
             override fun onTick(millisUntilFinished: Long) {
                 counterTv.isVisible = true
                 counterTv.text  = getString(R.string.second_remaining,millisUntilFinished/1000)

             }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mCounterDown != null){
            mCounterDown!!.cancel()
        }
    }


    private fun initViews() {
       phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        verifyTv.text = getString(R.string.verify_number,phoneNumber)
        setSpannableString()

        verificationBtn.setOnClickListener(this)
        resendBtn.setOnClickListener(this)

        auth = Firebase.auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                if (::progressDialog.isInitialized){
                    progressDialog.dismiss()
                }
                val smsCode = credential.smsCode
                if (!smsCode.isNullOrBlank())
                    sentcodeEt.setText(smsCode)
                signInWithPhoneAuthCredential(credential)
            }


            override fun onVerificationFailed(e: FirebaseException) {


                if (::progressDialog.isInitialized){
                    progressDialog.dismiss()
                }
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
                Log.e("ERROR_FIREBASE", e.localizedMessage)
                notifyUserAndRetry("Your Phone Number might be wrong or connection error.Retry again!")
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
                counterTv.isVisible = false

                mVerificationId = verificationId
                mResendToken = token
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful){

                    startActivity(Intent(this, SignUpActivity::class.java))
                }else{
                    notifyUserAndRetry("Your Phone Number verification failed.Try again!!")
                }
            }

    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("OK"){ _,_ ->
                ShowLoginActivity()
            }
            setNegativeButton("Cancel"){dialog, _->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun setSpannableString() {
        val span = SpannableString(getString(R.string.waiting_next, phoneNumber))
        val clickableSpan = object : ClickableSpan(){
            override fun onClick(widget: View) {

                ShowLoginActivity()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }
        }
        span.setSpan(clickableSpan, span.length - 13, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waitingTv.movementMethod = LinkMovementMethod.getInstance()
        waitingTv.text= span
    }

    private fun ShowLoginActivity() {
        startActivity(Intent(this,LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    override fun onBackPressed() {

    }

    override fun onClick(v: View?) {

        when(v){
            verificationBtn ->{
                val  code = sentcodeEt.text.toString()
                if (code.isNotEmpty() && !mVerificationId.isNullOrBlank()){
                    progressDialog = createProgressDialog("Please wait....",false)
                    progressDialog.show()
                    val credential = PhoneAuthProvider.getCredential(mVerificationId!!,code)
                    signInWithPhoneAuthCredential(credential)
                }
            }
            resendBtn ->{
                if (mResendToken != null){
                    showTimer(60000)
                    progressDialog = createProgressDialog("Sending a verification code again",false)
                    progressDialog.show()

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber!!,60,TimeUnit.SECONDS,this,callbacks,mResendToken)
                }
            }
        }
    }
}
fun Context.createProgressDialog(message:String,isCancelable:Boolean):ProgressDialog{
    return ProgressDialog(this).apply {
        setCancelable(false)
        setMessage(message)
        setCanceledOnTouchOutside(false)
    }
}