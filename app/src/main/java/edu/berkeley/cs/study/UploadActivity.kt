package edu.berkeley.cs.study

import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.JsonWriter
import android.util.Log
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.StringWriter

private val LOG = "UploadActivity"

private val URL = "https://secserv.cs.berkeley.edu/upload"

enum class UploadStatus {
    SUCCESS, FAIL
}

typealias UploadResult = Pair<UploadStatus, String?>

private val JSON = MediaType.parse("application/json; charset=utf-8")
private val client = OkHttpClient()


/**
 * Make a POST request with the given URL and content using okhttp
 */
fun post(url: String, json: String): String? {
    val body = RequestBody.create(JSON, json)
    val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
    client.newCall(request).execute().use { response ->
        return response.body()?.string()
    }
}

/**
 * Encode payload as a JSON string
 */
fun encodePayload(apps: String): String {
    val out = StringWriter()
    val json = JsonWriter(out)
    json.beginObject()
            .name("listing").value(apps)
            .endObject()

    return out.toString()
}

/**
 * Parse response as JSON
 */
private fun parseResponse(response: String): UploadResult {
    try {
        val json = JSONObject(response)
        val key = json.get("key")
        return UploadResult(UploadStatus.SUCCESS, key.toString())
    } catch (e: JSONException) {
        Log.e(LOG, response)
        return UploadResult(UploadStatus.FAIL, null)
    }
}



class UploadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        uploadData()
    }

    private fun uploadData() {
        val data = this.intent.getStringExtra(APPS)
        UploadTask().execute(data)
    }

    fun showErrorDialog() {
        AlertDialog.Builder(this)
                .setMessage("Oh no! Something went wrong.")
                .setPositiveButton("OK", { dialog: DialogInterface, which: Int -> })
                .show()
    }


    inner class UploadTask : AsyncTask<String, Void, UploadResult>() {
        /**
         * Upload data to the server
         */
        override fun doInBackground(vararg args: String?): UploadResult {
            val data = args[0]!!
            val payload = encodePayload(data)
            val response = post(URL, payload) ?: ""
            return parseResponse(response)
        }

        override fun onPostExecute(result: UploadResult) {
            when (result.first) {
                UploadStatus.SUCCESS -> {
                    val successIntent = Intent(this@UploadActivity, SuccessActivity::class.java)
                    successIntent.putExtra(SUCCESS_KEY, result.second)
                    startActivity(successIntent)
                }
                UploadStatus.FAIL -> showErrorDialog()
            }
        }


    }
}
