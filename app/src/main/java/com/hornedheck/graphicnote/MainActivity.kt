package com.hornedheck.graphicnote

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.hornedheck.graphicnote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "EVENT"
    }

    private lateinit var binding: ActivityMainBinding
    private var min = 1.0f
    private var max = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.screen.setOnGenericMotionListener { v, event ->
            eventListener(event)
            true
        }*/
        binding.screen.setOnTouchListener { v, event ->
            eventListener(event)
            true
        }
    }

    private fun eventListener(event: MotionEvent) {
        if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
            val pressure = event.pressure
            max = maxOf(max, pressure)
            min = minOf(min, pressure)
            Log.d(TAG, "$min , $max")
        }
    }
}