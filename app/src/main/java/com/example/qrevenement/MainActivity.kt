package com.example.qrevenement


import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import java.lang.reflect.Array.get
import com.example.qrevenement.DBHandler


private const  val CAMERA_REQUEST_CODE = 101
private const  val READ_WRITE_REQUEST_CODE = 102
private lateinit var codeScanner: CodeScanner
private lateinit var xls_var : xls
private lateinit var dbHandler:DBHandler
//private lateinit var sharedPreference: SharedPreferences
//private val KEY_SESSION_COUNT : String = "nombre_scan"



class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // declarer variable
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        val btnsave = findViewById<Button>(R.id.button_save)
        var textQrScan  = ""
        val textScan = findViewById<TextView>(R.id.text_scan)
        var msgErrorSucced : String


        // fonction pour la permission
        setupPermissions()
        // alert diag
        basicAlert(null)
        // DB SQLITE
        dbHandler = DBHandler(this)
        xls_var = xls(this)
        // SHARED PREFERRENCE
       // sharedPreference =  getSharedPreferences("db_scan_nombre", Context.MODE_PRIVATE)
        //var editor = sharedPreference.edit()

        // btn pour la sauvegarde dans le fichier xls
        btnsave.setOnClickListener {
            // permission de write et de read un xls
            setupForWriteReadPermissions()
            // nombre de scan
            //val scanCount: Int  = sharedPreference.getInt(KEY_SESSION_COUNT, 0)
            // utilisation de la class xls pour l'ecriture
            msgErrorSucced = xls_var.xls_write(textQrScan)
            // utilisation pour la future entre dans le fichier xls
            //editor.putInt(KEY_SESSION_COUNT,scanCount + 1)
            //editor.commit()
            Toast.makeText(this,  msgErrorSucced, Toast.LENGTH_LONG).show()
        }
        // PROGRAMME DE SCANNE QR CODE
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
                    textQrScan = it.text

                 }

            }
            codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                 runOnUiThread {
                     val txt : String = "camera init ${it.message}"
                     textScan.text = txt
                 }

            }

        }
        /*scannerView.setOnClickListener {
            codeScanner.startPreview()
        }*/
    }

    // start function laert diag
    fun basicAlert(view: View?){
        // ALERT DIAG
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            // permission de write et de read un xls
            setupForWriteReadPermissions()
            /*editor.putInt(KEY_SESSION_COUNT,0)
            editor.commit()*/
            Toast.makeText(applicationContext,
                "Continue..", Toast.LENGTH_SHORT).show()
        }

        // alertdiag for
        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
            //editor.clear()
            // demande la permission
            setupForWriteReadPermissions()
            // acces a la base de donnee sqlite
            dbHandler = DBHandler(this)
            dbHandler.deleteAllDataTables()
            Toast.makeText(applicationContext,
                "reinitialisation..", Toast.LENGTH_SHORT).show()
        }

        val neutralButtonClick = { dialog: DialogInterface, which: Int ->
            /*editor.putInt(KEY_SESSION_COUNT,0)
            editor.commit()*/
            setupForWriteReadPermissions()
            Toast.makeText(applicationContext,
                "Permiere fois", Toast.LENGTH_SHORT).show()
        }

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Message")
            setMessage("Voulez vous continuez avec le meme fichier xls ou reinitialiser")
            setPositiveButton("Continue", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton("reinitialiser", negativeButtonClick)
            setNeutralButton("Commencer", neutralButtonClick)
            show()
        }


    }
    /* REPRISE APRES SORTI DU PROGRAMME   */
        override fun onResume() {
            super.onResume()
            codeScanner.startPreview()
        }

        override fun onPause() {
            super.onPause()
            codeScanner.releaseResources()
        }

    /* PERMISSION POUR OUVERTURE DE CAMERA   */
      private fun setupPermissions(){
        val  permissions  = ContextCompat.checkSelfPermission(this ,android.Manifest.permission.CAMERA)

        //val  permissionsForRead  = ContextCompat.checkSelfPermission(this ,android.Manifest.permission.READ_EXTERNAL_STORAGE)
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
            READ_WRITE_REQUEST_CODE->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You need  permission to read and write", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, "You have  permission to read and write", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

   /* PERMISSION POUR ECRITURE DANS FICHIER EXCEL  */
   private fun setupForWriteReadPermissions(){
       val  permissionsForWrite  = ContextCompat.checkSelfPermission(this ,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
       val  permissionsForRead  = ContextCompat.checkSelfPermission(this ,android.Manifest.permission.READ_EXTERNAL_STORAGE)
       if ( permissionsForWrite != PackageManager.PERMISSION_GRANTED || permissionsForRead != PackageManager.PERMISSION_GRANTED){
           makeRequestForWriteReadCVS()
       }
   }
    private fun makeRequestForWriteReadCVS(){
        ActivityCompat.requestPermissions(this, arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE),READ_WRITE_REQUEST_CODE )
    }





}







