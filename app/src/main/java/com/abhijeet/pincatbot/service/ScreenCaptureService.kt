package com.abhijeet.pincatbot.service
import android.app.*
import android.content.*
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import com.abhijeet.pincatbot.bot.PinSolver
import com.abhijeet.pincatbot.vision.PinVision
class ScreenCaptureService:Service(){
 companion object{const val ACTION_START="start_capture";const val ACTION_STOP="stop_capture";const val EXTRA_RESULT="result_code";const val EXTRA_DATA="result_data"}
 private var projection:MediaProjection?=null;private var reader:ImageReader?=null;private var display:android.hardware.display.VirtualDisplay?=null
 private val handler=Handler(Looper.getMainLooper());private val vision=PinVision();private var enabled=false;private var busy=false;private var lastTap=0L
 override fun onBind(intent:Intent?)=null
 override fun onCreate(){super.onCreate();createChannel();if(Build.VERSION.SDK_INT>=29)startForeground(7,notification("Pin Cat Bot ready"),ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)else @Suppress("DEPRECATION") startForeground(7,notification("Pin Cat Bot ready"))}
 override fun onStartCommand(i:Intent?,f:Int,s:Int):Int{when(i?.action){ACTION_START->{enabled=true;startCapture(i)};ACTION_STOP->{enabled=false;stopSelf()}};return START_NOT_STICKY}
 @Suppress("DEPRECATION") private fun legacy(i:Intent)=i.getParcelableExtra(EXTRA_DATA)
 private fun startCapture(i:Intent){if(projection!=null)return;val code=i.getIntExtra(EXTRA_RESULT,Activity.RESULT_CANCELED);val data=if(Build.VERSION.SDK_INT>=33)i.getParcelableExtra(EXTRA_DATA,Intent::class.java)else legacy(i);if(code!=Activity.RESULT_OK||data==null)return
  projection=getSystemService(MediaProjectionManager::class.java).getMediaProjection(code,data);val dm=resources.displayMetrics
  reader=ImageReader.newInstance(dm.widthPixels,dm.heightPixels,PixelFormat.RGBA_8888,2)
  display=projection!!.createVirtualDisplay("PinCatBot",dm.widthPixels,dm.heightPixels,dm.densityDpi,android.hardware.display.DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,reader!!.surface,null,handler)
  reader!!.setOnImageAvailableListener({process(it)},handler)
 }
 private fun process(r:ImageReader){if(!enabled||busy)return;val image=r.acquireLatestImage()?:return;try{val bmp=imageToBitmap(image)?:return;val result=vision.detect(bmp);val target=PinSolver.choose(result.targets,result.board)
  if(target!=null&&SystemClock.uptimeMillis()-lastTap>750L){PinAccessibilityService.instance?.let{busy=true;lastTap=SystemClock.uptimeMillis();it.tap(target.tap.x,target.tap.y);handler.postDelayed({busy=false},550L)}}}finally{image.close()}}
 private fun imageToBitmap(image:Image):Bitmap?{val p=image.planes.firstOrNull()?:return null;val w=image.width;val h=image.height;val stride=p.pixelStride;val padding=p.rowStride-stride*w;val tmp=Bitmap.createBitmap(w+padding/stride,h,Bitmap.Config.ARGB_8888);tmp.copyPixelsFromBuffer(p.buffer);return if(tmp.width==w)tmp else Bitmap.createBitmap(tmp,0,0,w,h)}
 private fun createChannel(){if(Build.VERSION.SDK_INT>=26)getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel("bot","Pin Cat Bot",NotificationManager.IMPORTANCE_LOW))}
 private fun notification(t:String):Notification{@Suppress("DEPRECATION") val b=if(Build.VERSION.SDK_INT>=26)Notification.Builder(this,"bot")else Notification.Builder(this);return b.setContentTitle("Pin Cat Bot").setContentText(t).setSmallIcon(android.R.drawable.ic_media_play).build()}
 override fun onDestroy(){reader?.close();display?.release();projection?.stop();projection=null;super.onDestroy()}
}