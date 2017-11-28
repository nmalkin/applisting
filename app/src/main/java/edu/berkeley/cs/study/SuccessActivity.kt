package edu.berkeley.cs.study

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

val SUCCESS_KEY = "cs.berkeley.edu.study.success_key"

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val successKey = intent.getStringExtra(SUCCESS_KEY)
        val view = findViewById<TextView>(R.id.success_key)
        view.text = successKey
    }
}
