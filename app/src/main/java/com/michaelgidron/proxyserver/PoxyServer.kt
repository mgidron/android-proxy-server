package com.michaelgidron.proxyserver

import android.util.Log
import java.io.IOException
import java.net.ServerSocket
import kotlin.concurrent.thread

const val PORT = 8081
const val LOG_KEY = "PROXY SERVER: "


class ProxyServer() {
    companion object {
        fun runServer() {
            var serverSocket: ServerSocket = ServerSocket()
            var listening = true

            try {
                serverSocket = ServerSocket(PORT)
                Log.i(LOG_KEY, "Start Proxy Server... $PORT")
            } catch (e: IOException) {
                Log.i(LOG_KEY, "Could not listen on port: ")
                //System.exit(-1)
            }
            Log.i(LOG_KEY, "listening outside")
            while (listening) {
                Log.i(LOG_KEY, "listening inside")
                var thread = ProxyThread(serverSocket.accept())
                thread.start()
            }
            serverSocket.close()
        }
    }
}