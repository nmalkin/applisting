package edu.berkeley.cs.study

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils.join
import android.view.View
import android.widget.EditText

val APPS = "cs.berkeley.edu.study.apps"

class AppListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        val appBox = findViewById<EditText>(R.id.applist_content)
        appBox.setText(join("\n", collectApplicationList()))
    }

    /**
     * Collect list of installed applications
     */
    private fun collectApplicationList(): List<String> {
        val applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val appNames = applications
                .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 1 } // filter out system applications
                .map { it.processName } // get just the package names
        return appNames
    }

    fun proceed(view: View) {
        val appField = findViewById<View>(R.id.applist_content) as EditText
        val approvedApps = appField.text.toString()

        val intent = Intent(this, UploadActivity::class.java)
        intent.putExtra(APPS, approvedApps)

        startActivity(intent)
    }
}
