package com.example.aikeyboard

import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.hypot

class SmartKeyboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : KeyboardView(context, attrs) {

    // Bán kính mở rộng vùng nhận diện (pixel)
    private val proximityRadius = 60 // Có thể điều chỉnh cho phù hợp
    
    init {
        // Sử dụng popup tùy chỉnh thay vì popup mặc định
        isPreviewEnabled = true
    }

    override fun onTouchEvent(me: MotionEvent): Boolean {
        if (keyboard == null) return super.onTouchEvent(me)
        if (me.action == MotionEvent.ACTION_DOWN || me.action == MotionEvent.ACTION_UP) {
            val key = findNearestKey(me.x, me.y)
            if (key != null) {
                // Giả lập sự kiện chạm đúng tâm phím
                val fakeEvent = MotionEvent.obtain(
                    me.downTime, me.eventTime, me.action, key.x + key.width / 2f, key.y + key.height / 2f, me.metaState
                )
                val handled = super.onTouchEvent(fakeEvent)
                fakeEvent.recycle()
                return handled
            }
        }
        return super.onTouchEvent(me)
    }

    private fun findNearestKey(x: Float, y: Float): Keyboard.Key? {
        val keys = keyboard.keys
        var minDist = Float.MAX_VALUE
        var nearest: Keyboard.Key? = null
        for (key in keys) {
            // Nếu điểm nằm trong phím, trả về luôn
            if (x >= key.x && x < key.x + key.width && y >= key.y && y < key.y + key.height) {
                return key
            }
            // Nếu không, tính khoảng cách tới tâm phím
            val cx = key.x + key.width / 2f
            val cy = key.y + key.height / 2f
            val dist = hypot(x - cx, y - cy)
            if (dist < minDist) {
                minDist = dist
                nearest = key
            }
        }
        return nearest
    }
}