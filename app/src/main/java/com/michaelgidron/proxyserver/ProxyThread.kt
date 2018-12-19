package com.michaelgidron.proxyserver

import android.util.Log
import java.net.*
import java.io.*
//import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import com.good.gd.net.GDSocket
//import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil.getOutputStream
import java.util.*
import com.good.gd.apache.http.client.methods.HttpGet
import com.good.gd.net.GDHttpClient




class ProxyThread:Thread {

    private var socket:Socket? = null
    private var httpHeaders:String = ""

    constructor(socket:Socket):super(){
        this.socket = socket
    }

    override fun run() {
        try
        {
            var outputStream = DataOutputStream(socket?.getOutputStream())
            var inputStream = socket?.getInputStream()

            try
            {
                var reader: Scanner = Scanner(inputStream)
                var content = reader?.nextLine()

                Log.i(LOG_KEY, content)

                var url = parseUrl(content)
                //var response = routeRequest(socket!!, url, url)

                var body = routeHttpRequest(url)
                var head = "HTTP/1.x 200 OK\r\n ${this.httpHeaders} \r\n\r\n"
                var response = "$head $body"

                outputStream.writeBytes(response)
                outputStream.flush()
            }
            catch (e:Exception) {
                Log.e(LOG_KEY, e.message)
            }

            outputStream?.close()
            inputStream?.close()
            socket?.close()
        }
        catch (e:IOException) {
            Log.e(LOG_KEY, e.message)
        }
    }

    fun parseUrl(header: String): String {
        val regex = "\\/url\\/.+(?=HTTP)".toRegex()
        var match = regex.find(header)

        if (match != null && match.value?.length > 0)
            return match.value.substring(5, match.value.length-1)

        return ""
    }

    private fun routeRequest(webViewSocket:Socket, host:String, url:String):String {
        var socket:GDSocket? = null
        var inputStream:InputStream? = null
        var outputStream:OutputStream? = null
        try
        {
            socket = GDSocket()
            socket?.connect(url, 80, 1000)
            var response = ""
            //We'll make an HTTP request over the socket connection.
            var output = ("GET $url HTTPS/1.1\r\n" +
                    "Host: $host:443\r\n" +
                    "Connection: close\r\n" +
                    "\r\n")
            var byteArrayOutputStream = ByteArrayOutputStream(1024)
            var buffer = ByteArray(1024)
            var bytesRead:Int
            inputStream = socket?.getInputStream()
            outputStream = socket?.getOutputStream()
            outputStream.write(output.toByteArray())
            do
            {
                bytesRead = inputStream.read(buffer)

                if (bytesRead == -1) break;  // terminate

                byteArrayOutputStream.write(buffer, 0, bytesRead)
                response += byteArrayOutputStream.toString("UTF-8")
                if (response.length > 1000)
                {
                    //Stop reading after we've reached 1000 characters.
                    break
                }
            } while (true)
            //Close all connections.
            inputStream?.close()
            outputStream?.close()
            byteArrayOutputStream?.close()
            return response
        }
        finally
        {
            inputStream?.close()
            outputStream?.close()
            socket?.close()
        }
    }


    private fun routeHttpRequest(urlString: String): String {
        var stream: InputStream? = null
        var str = ""

        try {
            stream = sendRequest(urlString)
            str = readStream(BufferedInputStream(stream))
        } finally {
            stream?.close()
        }
        return str
    }

    private fun sendRequest(urlString: String): InputStream {

        var httpclient = GDHttpClient()
        var request = HttpGet(urlString)

        var response = httpclient.execute(request)

        this.httpHeaders = ""
        var headers = response.allHeaders
        for (header in headers)
        {
            this.httpHeaders += "${header.name}: ${header.value} \r\n"
        }

        var inputStream = response.entity.content

        //httpclient.connectionManager.shutdown()

        return inputStream
    }

    private fun readStream(inputStream: BufferedInputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }

    private fun readIt(stream: InputStream, len: Int): String {
        var reader: Reader? = null
        reader = InputStreamReader(stream, "UTF-8")
        var buffer = CharArray(len)
        reader.read(buffer)
        return String(buffer)
    }

}