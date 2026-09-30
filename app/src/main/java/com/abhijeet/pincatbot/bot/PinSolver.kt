package com.abhijeet.pincatbot.bot
import com.abhijeet.pincatbot.vision.PinTarget
import kotlin.math.abs
object PinSolver{
 fun choose(targets:List<PinTarget>,board:android.graphics.Rect):PinTarget?{
  if(targets.isEmpty())return null
  val safe=targets.filter{t->targets.none{o->if(o===t)false else when{
   t.dx<0->abs(o.cat.y-t.cat.y)<30f&&o.cat.x<t.cat.x-28f
   t.dx>0->abs(o.cat.y-t.cat.y)<30f&&o.cat.x>t.cat.x+28f
   t.dy<0->abs(o.cat.x-t.cat.x)<30f&&o.cat.y<t.cat.y-28f
   else->abs(o.cat.x-t.cat.x)<30f&&o.cat.y>t.cat.y+28f}}}
  return safe.maxByOrNull{t->when{t.dx<0->board.right-t.cat.x;t.dx>0->t.cat.x-board.left;t.dy<0->board.bottom-t.cat.y;else->t.cat.y-board.top}}
 }
}