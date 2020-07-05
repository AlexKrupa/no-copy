package dev.ahmedmourad.nocopy.sample_android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("lololololololol", Some.of("lololol").copy().toString())
    }
}

data class Some private constructor(val v: String) {
    companion object {
        fun of(v: String) = Some(v)
    }
}
