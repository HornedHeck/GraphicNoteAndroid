package com.hornedheck.graphicnote

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.PrintStream
import java.net.Socket
import java.net.SocketException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class MainViewModel(application: Application) : AndroidViewModel(application){

    private var socket: Socket? = null
    private var writer: PrintStream? = null
    private var address: Pair<String, Int>? = null

    private val errorHandler = CoroutineExceptionHandler { c, t ->
        Log.d("" , null , t)
        viewModelScope.launch {
            if (t is SocketException){
                errors.send(application.getString(R.string.errors_socket))
            }else{
                errors.send(application.getString(R.string.error_unknown))
            }
        }
    }

    private val socketScope = CoroutineScope(Dispatchers.IO + errorHandler)

    val errors = Channel<String>()

    fun isConnectRequired() = address == null

    fun connect(address: String, port: Int) {
        this.address = address to port
        reconnect()
    }

    fun reconnect() {
        address?.let {
            socketScope.launch {
                socket = Socket(it.first, it.second)
                writer = PrintStream(socket?.getOutputStream(), true)
            }
        }
    }

    fun sendData(x: Float, y: Float, pressure: Float) {
        socketScope.launch {
            writer?.println("$x $y $pressure")
        }
    }

    fun disconnect() {
        writer?.close()
        socket?.close()
    }

}