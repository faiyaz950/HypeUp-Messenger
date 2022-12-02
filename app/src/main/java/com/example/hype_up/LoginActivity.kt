package com.example.hype_up

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var countryCode:String
    private lateinit var phoneNumber:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        phoneNumberEt.addTextChangedListener {
            nextBtn.isEnabled = !(it.isNullOrBlank() || it.length < 10 || it.length > 10)
         }
        nextBtn.setOnClickListener {
             checkNumber()
        }
    }

    private fun checkNumber() {

       countryCode = ccp.selectedCountryCodeWithPlus
       phoneNumber = countryCode+phoneNumberEt.text.toString()

        notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone number :$phoneNumber\n" +
                      "Is this OK, or would you like to edit the number?")
            setPositiveButton("OK"){_,_ ->
                showOtpActivity()
            }
            setNegativeButton("EDIT"){ dialog, _ ->

                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showOtpActivity() {

        startActivity(Intent(this,OtpActivity::class.java).putExtra(PHONE_NUMBER,phoneNumber))
    }
}