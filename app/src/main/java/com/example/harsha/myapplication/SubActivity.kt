package com.example.harsha.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.harsha.myapplication.R.id.textView
import kotlinx.android.synthetic.main.activity_sub.*

class SubActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        val textView
                = findViewById<TextView>(R.id.textView).apply {
            text = message
        }

        textView.setOnClickListener {
            Log.d("textView::setOnClickListener : ", "called")
            intent.putExtra("MyData", true)
            setResult(RESULT_OK, intent)
            finish()
        }

    }
}
