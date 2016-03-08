package y2k.joyreactor.common

import android.content.res.TypedArray
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.view.View
import android.view.ViewGroup

/**
 * Created by y2k on 2/6/16.
 */

inline fun View.setOnClickListener(id: Int, crossinline onClick: () -> Unit) {
    findViewById(id).setOnClickListener { onClick() }
}

fun TypedArray.use(f: TypedArray.() -> Unit) {
    try {
        f()
    } finally {
        recycle()
    }
}

fun View.compatAnimate(): ViewPropertyAnimatorCompat {
    return ViewCompat.animate(this)
}

var View.compatScaleY: Float
    get() = ViewCompat.getScaleY(this)
    set(value) = ViewCompat.setScaleY(this, value)

fun ViewGroup.switchByScaleFromTo(firstPosition: Int, secondPosition: Int) {
    val first = getChildAt(firstPosition)
    val second = getChildAt(secondPosition)
    first.compatAnimate()
        .scaleY(0f)
        .withEndAction {
            first.isVisible = false
            second.isVisible = true
            second.compatScaleY = 0f
            second.compatAnimate().scaleY(1f)
        }
}

@Suppress("UNCHECKED_CAST")
inline fun <T> View.find(id: Int): T {
    return findViewById(id) as T
}

@Suppress("UNCHECKED_CAST")
fun <T> View.findOrNull(id: Int): T? {
    return findViewById(id) as T?
}

fun ViewGroup.getChildren(): List<View> {
    return (0..childCount - 1).map { getChildAt(it) }
}

fun View.updateMargin(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    val lp = layoutParams as ViewGroup.MarginLayoutParams
    if (left != null) lp.leftMargin = left
    if (top != null) lp.topMargin = top
    if (right != null) lp.rightMargin = right
    if (bottom != null) lp.bottomMargin = bottom
    layoutParams = lp
}