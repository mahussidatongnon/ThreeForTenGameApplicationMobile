package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.network

import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach

class WebSocketClient private constructor() {

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }
    private var session: DefaultClientWebSocketSession? = null
    private var listenJob: Job? = null

    companion object {
        val instance: WebSocketClient by lazy { WebSocketClient() }
    }

    suspend fun connect(host: String, port: Int, path: String) {
        if (session != null) {
            println("WebSocket already connected.")
            return
        }

        client.webSocket(
            method = HttpMethod.Get,
            host = host,
            port = port,
            path = path
        ) {
            println("Connected to WebSocket")

            session = this

            sendStompConnectFrame()

            listenJob = listenIncoming()
        }
    }

    private suspend fun sendStompConnectFrame() {
        val frame = buildString {
            append("CONNECT\n")
            append("accept-version:1.2\n")
            append("host:${GameManager.SERVER_HOST}\n")
            append("\n")
            append('\u0000') // null terminator for STOMP frame
        }
        session?.send(Frame.Text(frame))
        println("STOMP CONNECT frame sent")
    }

    suspend fun subscribe(destination: String) {
        val frame = buildString {
            append("SUBSCRIBE\n")
            append("id:sub-0\n") // ID can be made dynamic if needed
            append("destination:$destination\n")
            append("\n")
            append('\u0000')
        }
        println("session: $session")
        println("frame: $frame")
        session?.send(Frame.Text(frame))
        println("Subscribed to $destination")
    }

    private suspend fun listenIncoming(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            try {
                println(session)
                println(session?.incoming)
                println("Before consume each")
                session?.incoming?.consumeEach { frame ->
                    println("frame: $frame")
                    if (frame is Frame.Text) {
                        println("Message reçu : ${frame.readText()}")
                    }
                }
            } catch (e: Exception) {
                println("Error listening to incoming frames: ${e.message}")
            }
        }
    }

    suspend fun disconnect() {
        listenJob?.cancel()
        session?.close()
        session = null
        println("WebSocket disconnected")
    }
}
