package com.abhijeet.pincatbot.service
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityEvent
class PinAccessibilityService: AccessibilityService(){
 companion object{ @Volatile var instance:PinAccessibilityService?=null }
 override fun onServiceConnected(){super.onServiceConnected();instance=this}
 override fun onAccessibilityEvent(event:AccessibilityEvent?){}
 override fun onInterrupt(){}
 override fun onDestroy(){instance=null;super.onDestroy()}
 fun tap(x:Float,y:Float,durationMs:Long=35L):Boolean{
  if(Build.VERSION.SDK_INT<24)return false
  val path=Path().apply{moveTo(x,y)}
  return dispatchGesture(GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(path,0,durationMs)).build(),null,null)
 }
}