package com.abhijeet.pincatbot.vision
import android.graphics.Bitmap
import android.graphics.PointF
import kotlin.math.abs
data class PinTarget(val cat:PointF,val tap:PointF,val dx:Int,val dy:Int,val confidence:Float)
data class VisionResult(val targets:List<PinTarget>,val board:android.graphics.Rect)
class PinVision{
 private data class C(val x:Int,val y:Int,val w:Int,val h:Int,val area:Int,val cx:Float,val cy:Float)
 fun detect(bitmap:Bitmap):VisionResult{val w=bitmap.width;val h=bitmap.height;val left=(w*.10f).toInt();val right=(w*.90f).toInt();val top=(h*.39f).toInt();val bottom=(h*.80f).toInt();val bw=right-left;val bh=bottom-top;val m=BooleanArray(bw*bh);val px=IntArray(w*h);bitmap.getPixels(px,0,w,0,0,w,h)
  for(y in 0 until bh)for(x in 0 until bw){val c=px[(top+y)*w+left+x];val r=c shr 16 and 255;val g=c shr 8 and 255;val b=c and 255;m[y*bw+x]=b-r>16&&b>145&&r>95&&g<225}
  val comps=connected(m,bw,bh).filter{it.area>=500&&it.w in 30..110&&it.h in 30..110};val raw=comps.map{toTarget(it,left,top)};val out=raw.fold(mutableListOf<PinTarget>()){a,t->if(a.none{abs(it.cat.x-t.cat.x)<24&&abs(it.cat.y-t.cat.y)<24})a.add(t);a};return VisionResult(out,android.graphics.Rect(left,top,right,bottom))}
 private fun toTarget(c:C,ox:Int,oy:Int):PinTarget{if(c.w>=c.h){val l=(c.cx-c.x)/c.w<.49f;val cx=ox+(if(l)c.x+c.h*.48f else c.x+c.w-c.h*.48f);val cy=oy+c.y+c.h*.5f;return PinTarget(PointF(cx,cy),PointF(ox+(if(l)c.x+c.w*.72f else c.x+c.w*.28f),cy),if(l)-1 else 1,0,.86f)};val t=(c.cy-c.y)/c.h<.49f;val cx=ox+c.x+c.w*.5f;val cy=oy+(if(t)c.y+c.w*.48f else c.y+c.h-c.w*.48f);return PinTarget(PointF(cx,cy),PointF(cx,oy+(if(t)c.y+c.h*.72f else c.y+c.h*.28f)),0,if(t)-1 else 1,.86f)}
 private fun connected(m:BooleanArray,w:Int,h:Int):List<C>{val seen=BooleanArray(m.size);val out=ArrayList<C>();val qx=IntArray(m.size.coerceAtMost(20000));val qy=IntArray(qx.size);for(sy in 0 until h)for(sx in 0 until w){val st=sy*w+sx;if(!m[st]||seen[st])continue;var head=0;var tail=0;qx[tail]=sx;qy[tail++]=sy;seen[st]=true;var minX=sx;var maxX=sx;var minY=sy;var maxY=sy;var area=0;var sumX=0L;var sumY=0L;while(head<tail){val x=qx[head];val y=qy[head++];area++;sumX+=x;sumY+=y;minX=minOf(minX,x);maxX=maxOf(maxX,x);minY=minOf(minY,y);maxY=maxOf(maxY,y);val ns=intArrayOf((y-1)*w+x,(y+1)*w+x,y*w+x-1,y*w+x+1);val ok=booleanArrayOf(y>0,y<h-1,x>0,x<w-1);for(k in 0..3)if(ok[k]){val n=ns[k];if(m[n]&&!seen[n]&&tail<qx.size){seen[n]=true;qx[tail]=n%w;qy[tail++]=n/w}}};if(area>=900)out.add(C(minX,minY,maxX-minX+1,maxY-minY+1,area,sumX.toFloat()/area,sumY.toFloat()/area))};return out}
}