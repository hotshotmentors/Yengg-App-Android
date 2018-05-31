package `in`.yeng.user.Activities

import `in`.yeng.user.API.APIClient
import `in`.yeng.user.R
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.barteksc.pdfviewer.PDFView
import com.wang.avi.AVLoadingIndicatorView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.InputStream

class PdfViewer : AppCompatActivity() {

    lateinit var loadingIndicator: AVLoadingIndicatorView

    lateinit var downloadUrl: String

    lateinit var pdfViewer: PDFView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = "PDF Viewer"
        }

        downloadUrl = intent.getStringExtra("url")

        pdfViewer = findViewById(R.id.pdf_view)

        loadingIndicator = findViewById(R.id.loading_indicator)


        getFileStream(APIClient.BASE_URL + "/" + downloadUrl) {
            pdfViewer.fromStream(it).load()
            loadingIndicator.smoothToHide()

        }


    }


    /*
    For showing Backbutton over Toolbar
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()
        }
        return true
    }

    /*
    Get bytestream from
     */
    private fun getFileStream(link: String, func: (InputStream) -> Unit) {

        doAsync {
            val client = OkHttpClient()
            val request = Request.Builder().url(link).build()
            val response = client.newCall(request).execute()

            uiThread { response.body()?.byteStream()?.let { func(it) } }
        }


    }
}
