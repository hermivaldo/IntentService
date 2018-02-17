package com.example.logonrm.intentservice

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.ResultReceiver
import android.text.TextUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Created by logonrm on 17/02/2018.
 */
class DownloadService : IntentService(DownloadService::class.java.name) {
    companion object {
        val STATUS_RUNNING = 0
        val STATUS_FINISHED = 1
        val STATUS_ERROR = 2
    }

    @SuppressLint("RestrictedApi")
    override fun onHandleIntent(intent: Intent?) {
        val receiver = intent!!.getParcelableExtra<ResultReceiver>("receiver")
        val url = intent.getStringExtra("url")
        val bundle = Bundle()

        if (!TextUtils.isEmpty(url)){
            receiver.send(STATUS_RUNNING, Bundle.EMPTY)

            try {
                val results = downloadData(url)

                if (null != results && results.isNotEmpty()){
                    bundle.putStringArray("result", results.toTypedArray())
                    receiver.send(STATUS_FINISHED, bundle)
                }
            }catch (e: Exception){
                receiver.send(STATUS_ERROR, bundle)
            }

        }
    }

    private fun downloadData(url: String?): List<String?> {
        var inputStream: InputStream?
        var urlConnection: HttpsURLConnection?
        val url = URL(url)

        urlConnection = url.openConnection() as HttpsURLConnection?
        urlConnection!!.setRequestProperty("Content-Type", "application/json")
        urlConnection!!.setRequestProperty("Accept", "application/json")
        urlConnection!!.requestMethod = "GET"

        val statusCode = urlConnection.responseCode

        if (statusCode == 200){
            inputStream = BufferedInputStream(urlConnection.inputStream)
            val response = converInputStramToString(inputStream)
            val result = parseResult(response)
            return result.toList()
        }else{
            throw Exception("Falha ao fazer download do arquivo")
        }

    }

    private fun converInputStramToString(inputStream: BufferedInputStream): String {
        val bufferReader = BufferedReader(InputStreamReader(inputStream))

        var line = bufferReader.readLine()
        var result = ""

        while (line != null){
            result += line
            line = bufferReader.readLine()
        }

        return result
    }

    private fun parseResult(result: String) : Array<String?>{
        var nomePokemons : Array<String?> = arrayOf()
        try {

            val response = JSONObject(result)
            val pokemons = response.optJSONArray("results")
            nomePokemons = arrayOfNulls(pokemons.length())

            for(i in 0 until pokemons.length()){
                val pokemon = pokemons.optJSONObject(i)
                val nomePokemon = pokemon.optString("name")
                nomePokemons[i] = nomePokemon
            }

        }catch (e: JSONException){
            e.printStackTrace()
        }

        return nomePokemons
    }
}