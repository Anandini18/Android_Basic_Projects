package com.nandini.android.drawdoodle

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private var drawingView:DrawingClass?=null
    private var brush_btn:ImageButton?=null
    private var mCurrentPaintBtn:ImageButton?=null
    private var picture_btn : ImageButton?=null
    private var undo_btn:ImageButton?=null
    private var save_btn:ImageButton?=null
    private var share_btn:ImageButton?=null
    private var customProgressDialog:Dialog?=null

    val openGalleryLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if(result.resultCode== RESULT_OK && result.data!=null){
            var imageBackground:ImageView=findViewById(R.id.iv_background)
            imageBackground.setImageURI(result.data?.data)
        }
    }

    val requestPermission: ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions())
    {
        permissions->
        permissions.entries.forEach{
               val permissionName=it.key
               val isGranted=it.value

            if(isGranted){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show()

                val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                openGalleryLauncher.launch(pickIntent)
            }
            else{
                if(permissionName== Manifest.permission.READ_EXTERNAL_STORAGE){
                Toast.makeText(this,"Permission denied.",Toast.LENGTH_LONG).show()
            }}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView=findViewById(R.id.drawingId)
        picture_btn=findViewById(R.id.picture_btn)
        undo_btn=findViewById(R.id.undo_btn)
        save_btn=findViewById(R.id.save_btn)
        share_btn=findViewById(R.id.share_btn)

        val colour_linear_layout=findViewById<LinearLayout>(R.id.colour_linear_layout)
        mCurrentPaintBtn=colour_linear_layout[1] as ImageButton
        mCurrentPaintBtn!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallete_selected))
        brush_btn=findViewById(R.id.brush_btn)
        brush_btn?.setOnClickListener{
            showBrushSizeDialog()
        }
        picture_btn?.setOnClickListener{
            requestStoragePermission()
        }
        undo_btn?.setOnClickListener{
            drawingView?.onUndo()
        }
        save_btn?.setOnClickListener{
            if(isStorageAllowed()){
                lifecycleScope.launch {
                    val flDrawingView:FrameLayout=findViewById(R.id.drawing_frame_layout)
                    saveBitmapFile(getBitmapFromView(flDrawingView))
                }
            }
        }
    }

    fun showBrushSizeDialog(){
        val BrushSizeDialog=Dialog(this)
        BrushSizeDialog.setContentView(R.layout.dialog_brush_size)
        BrushSizeDialog.setTitle("Brush Size")
        val smallBtn:ImageButton=BrushSizeDialog.findViewById(R.id.small_brush)
        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
            BrushSizeDialog.dismiss()
        }
        val mediumBtn:ImageButton=BrushSizeDialog.findViewById(R.id.medium_brush)
        mediumBtn.setOnClickListener{
            drawingView?.setSizeForBrush(20.toFloat())
            BrushSizeDialog.dismiss()
        }
        val LargeBtn:ImageButton=BrushSizeDialog.findViewById(R.id.large_brush)
        LargeBtn.setOnClickListener{
            drawingView?.setSizeForBrush(30.toFloat())
            BrushSizeDialog.dismiss()
        }
        BrushSizeDialog.show()
    }

fun paintClicked(view: View){
    if(view!==mCurrentPaintBtn){
        val imageButton=view as ImageButton
        val colorTag=imageButton.tag.toString()
        drawingView?.setColor(colorTag)
        imageButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallete_selected))
        mCurrentPaintBtn?.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallete))
        mCurrentPaintBtn=view
    }
}

    fun isStorageAllowed():Boolean{
        showProgressDialog()
        val result=ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        return result==PackageManager.PERMISSION_GRANTED
    }

    fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            showRationaleDialog("Draw Doodle","Draw Doodle requires access to External Storage.")
        }else{
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    fun showRationaleDialog(
        title : String,
        message: String
    ){
        val builder:AlertDialog.Builder=AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message).setPositiveButton("Cancel"){dialog,_->dialog.dismiss()}
        builder.create().show()
    }

private fun getBitmapFromView(view:View): Bitmap? {
    val returnedBitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
    val canvas=Canvas(returnedBitmap)
    val bgDrawable=view.background
    if(bgDrawable !=null){
        bgDrawable.draw(canvas)
    }else{
        canvas.drawColor(Color.WHITE)
    }
    view.draw(canvas)
    return returnedBitmap
}
    private suspend fun saveBitmapFile(mBitmap: Bitmap?):String{
        var result =""
        withContext(Dispatchers.IO){
            if(mBitmap !=null){
                try {
                    val bytes=ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)

                    val f = File(externalCacheDir?.absoluteFile?.toString()+ File.separator+"Draw_Doodle"+System.currentTimeMillis()/1000+".png")

                    val fo=FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()

                    result=f.absolutePath

                    runOnUiThread{
                        cancelProgressDialog()
                        if(result.isNotEmpty()){
                            Toast.makeText(this@MainActivity,"File saved successfully : $result",Toast.LENGTH_LONG).show()
                            share_btn?.setOnClickListener{shareImage(result)}
                        }else{
                            Toast.makeText(this@MainActivity,"Something went wrong.",Toast.LENGTH_LONG).show()
                        }
                    }
                }
                catch (e:Exception){
                    result=""
                    e.printStackTrace()
                }
            }
        }
        return result
    }

private fun showProgressDialog(){
    customProgressDialog=Dialog(this@MainActivity)
    customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
    customProgressDialog?.show()
}

    private fun cancelProgressDialog(){
        if(customProgressDialog !=null){
            customProgressDialog?.dismiss()
            customProgressDialog=null
        }
    }

    private fun shareImage(result:String){
         MediaScannerConnection.scanFile(this, arrayOf(result),null){
             path,uri->
             val shareIntent=Intent()
             shareIntent.action=Intent.ACTION_SEND
             shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
             shareIntent.type="image/png"
             startActivity(Intent.createChooser(shareIntent,"Share"))
         }
    }
}