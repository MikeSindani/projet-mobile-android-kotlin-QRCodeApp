package com.example.qrevenement

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode


private const  val CAMERA_REQUEST_CODE = 101
private lateinit var codeScanner: CodeScanner


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        val textScan = findViewById<TextView>(R.id.text_scan)
        setupPermissions()
        codeScanner = CodeScanner(this, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false//Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()

            // Callbacks
            codeScanner.decodeCallback = DecodeCallback {
                runOnUiThread {
                    //Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                    textScan.text = it.text // the date of scan
                 }

            }
            codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                 runOnUiThread {
                     textScan.text = "camera init ${it.message}"
                 }

            }

        }
        /*scannerView.setOnClickListener {
            codeScanner.startPreview()
        }*/
    }
        override fun onResume() {
            super.onResume()
            codeScanner.startPreview()
        }

        override fun onPause() {
            super.onPause()
            codeScanner.releaseResources()
        }
      private fun setupPermissions(){
          val  permissions  = ContextCompat.checkSelfPermission(this ,android.Manifest.permission.CAMERA)
          if (permissions != PackageManager.PERMISSION_GRANTED){
              makeRequest()
          }
      }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You need the camera permission", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, "You have  camera permission", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    }





