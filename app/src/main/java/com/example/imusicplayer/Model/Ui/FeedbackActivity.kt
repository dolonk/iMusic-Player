package com.example.imusicplayer.Model.Ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.Toast
import com.example.imusicplayer.R
import com.example.imusicplayer.databinding.ActivityFeadbackBinding
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeadbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeadbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Feedback"

        setInitial()
    }

    private fun setInitial() {

        binding.sendFAId.setOnClickListener {
            val feedbackMsg =
                binding.feedbackMsgFAId.text.toString() + "\n" + binding.emailFAId.text.toString()
            val subject = binding.topicFAId.text.toString()

            // for send mail mail
            val userName = "santokmd01@gmail.com"
            val pass = "944893253do"

            // for internet checking
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (feedbackMsg.isNotEmpty() && subject.isNotEmpty() && (cm.activeNetworkInfo?.isConnectedOrConnecting == true)) {
                try {

                    val emailIntent = Intent(Intent.ACTION_SEND)
                    emailIntent.type = "plain/text"
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(userName))
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                    emailIntent.putExtra(Intent.EXTRA_TEXT, feedbackMsg)
                    startActivity(Intent.createChooser(emailIntent, "Send Feedback:"))
                    Toast.makeText(
                        this,
                        "Plz Send the mail on your gmail account!",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()

//                        Thread {
//                            try{
                    // not be work here now because the gmail don permission less secure app access
//                        val props = Properties().apply {
//                            put("mail.smtp.host", "smtp.gmail.com")
//                            put("mail.smtp.socketFactory.port", "465")
//                            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
//                            put("mail.smtp.auth", "true")
//                            put("mail.smtp.port", "465")
//                        }
//
//                        val session = javax.mail.Session.getDefaultInstance(props,
//                            object : javax.mail.Authenticator() {
//                                override fun getPasswordAuthentication(): PasswordAuthentication {
//                                    return PasswordAuthentication(userName, pass)
//                                }
//                            })
//
//                        val message = MimeMessage(session).apply {
//                            setFrom(InternetAddress(userName))
//                            setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(userName))
//                            setSubject(subject)
//                            setText(feedbackMsg)
//                        }
//                        Transport.send(message)
                } catch (e: Exception) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
