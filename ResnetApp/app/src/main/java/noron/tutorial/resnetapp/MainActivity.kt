package noron.tutorial.resnetapp

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import noron.tutorial.resnetapp.ml.MobilenetV110224Quant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class MainActivity : AppCompatActivity() {
    lateinit var selectBtn: Button
    lateinit var predBtn: Button
    lateinit var resView: TextView
    lateinit var imageView: ImageView
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        predBtn = findViewById(R.id.predictBtn)
        selectBtn = findViewById(R.id.selectBtn)
        resView = findViewById(R.id.resView)
        imageView = findViewById(R.id.imageView)

        var labels = application.assets.open("labels.txt").bufferedReader().readLines()

        var imageProcessor = ImageProcessor.Builder()
//            .add(NormalizeOp(0.0f, 255.0f))
//            .add(TransformToGrayscaleOp())
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        selectBtn.setOnClickListener{
            var intent: Intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, 100)
        }

        predBtn.setOnClickListener {
            var tensorImage = TensorImage(DataType.UINT8)
            tensorImage.load(bitmap)

            tensorImage = imageProcessor.process(tensorImage)
            val model = MobilenetV110224Quant.newInstance(this)

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
            inputFeature0.loadBuffer(tensorImage.buffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            var maxId = 0
            outputFeature0.forEachIndexed() { index, fl->
               if(outputFeature0[maxId] < fl){
                   maxId = index
               }
             }

            resView.setText(labels[maxId])
            // Releases model resources if no longer used.
            model.close()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            var uri = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            imageView.setImageBitmap(bitmap)
        }
    }
}