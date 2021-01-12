package com.hornedheck.graphicnote

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hornedheck.graphicnote.databinding.ActivityMainBinding
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel : MainViewModel by viewModels()

    companion object {
        private const val TAG = "EVENT"
    }

    private fun hideUi() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.isConnectRequired()) {
            val view = EditText(this)
            AlertDialog.Builder(this)
                .setView(view)
                .setTitle(R.string.dialog_connect_title)
                .setMessage(R.string.dialog_connect_message)
                .setPositiveButton("Ok") { _, _ ->
                    viewModel.connect(view.text.toString(), 8080)
                }
                .show()
        } else {
            viewModel.reconnect()
        }
    }

    private lateinit var binding: ActivityMainBinding
    private var min = 1.0f
    private var max = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideUi()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.errors.consumeEach {
                Toast.makeText(this@MainActivity , it , Toast.LENGTH_SHORT).show()
            }
        }

        binding.screen.setOnTouchListener { v, event ->
            eventListener(event)
            true
        }
    }

    private fun eventListener(event: MotionEvent) {
        if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
            val pressure = event.pressure
            val x = event.x
            val y = event.y
            viewModel.sendData(x, y, pressure)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.disconnect()
    }
}