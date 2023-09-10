package noron.tutorial.objectdetection

import android.annotation.SuppressLint
import android.content.AsyncQueryHandler
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.widget.TextView
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    lateinit var textureView: TextureView
    lateinit var cameraManager: CameraManager
    lateinit var handler: Handler
    lateinit var cameraDevice: CameraDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        get_permission()

        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)


        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                open_camera()
            }
            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                TODO("Not yet implemented")
            }
        }

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

    }


    fun get_permission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    @SuppressLint("MissingPermission")
    fun open_camera(){
        cameraManager.openCamera(cameraManager.cameraIdList[0], object:CameraDevice.StateCallback(){
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera

                var surfaceTexture = textureView.surfaceTexture
                var surface = Surface(surfaceTexture)

                var captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback(){
                    override fun onConfigured(session: CameraCaptureSession) {
                        session.setRepeatingRequest(captureRequest.build(), null, null)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        TODO("Not yet implemented")
                    }
                }, handler)
            }

            override fun onDisconnected(camera: CameraDevice) {
                TODO("Not yet implemented")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                TODO("Not yet implemented")
            }
        }, handler)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            get_permission()
        }
    }
}
