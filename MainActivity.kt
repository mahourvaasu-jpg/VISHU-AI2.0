package com.vishu.ai

import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.content.pm.PackageManager
import android.Manifest
import android.os.Build
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var web: WebView
    private var tts: TextToSpeech? = null
    private var speech: SpeechRecognizer? = null
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.webChromeClient = WebChromeClient()
        web.webViewClient = WebViewClient()
        web.addJavascriptInterface(Bridge(), "Android")
        setContentView(web)
        web.loadUrl("file:///android_asset/index.html")
        if (Build.VERSION.SDK_INT >= 23 &&
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 77)
        }
    }

    override fun onInit(status: Int) {
        ttsReady = status == TextToSpeech.SUCCESS
        if (ttsReady) {
            tts?.language = Locale("en", "IN")
            tts?.setSpeechRate(0.96f)
        }
    }

    private fun startRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            runOnUiThread { web.evaluateJavascript("showReply('Is device par speech recognition available nahi hai.')", null) }
            return
        }
        runOnUiThread {
            speech?.destroy()
            speech = SpeechRecognizer.createSpeechRecognizer(this)
            speech?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {
                    web.evaluateJavascript("setListening(false)", null)
                    web.evaluateJavascript("showReply('Awaaz clear nahi mili. Please dobara try karo.')", null)
                }
                override fun onResults(results: Bundle?) {
                    web.evaluateJavascript("setListening(false)", null)
                    val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    if (text != null) {
                        val safe = org.json.JSONObject.quote(text)
                        web.evaluateJavascript("onVoiceResult($safe)", null)
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "VISHU AI ko bolo...")
            }
            web.evaluateJavascript("setListening(true)", null)
            speech?.startListening(intent)
        }
    }

    inner class Bridge {
        @JavascriptInterface fun listen() { startRecognition() }
        @JavascriptInterface fun speak(text: String) {
            runOnUiThread {
                if (ttsReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "vishu_reply")
            }
        }
    }

    override fun onDestroy() {
        speech?.destroy()
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
