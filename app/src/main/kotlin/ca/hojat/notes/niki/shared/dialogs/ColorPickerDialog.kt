@file:SuppressLint("ClickableViewAccessibility")

package ca.hojat.notes.niki.shared.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import ca.hojat.notes.niki.R
import ca.hojat.notes.niki.databinding.DialogColorPickerBinding
import ca.hojat.notes.niki.shared.extensions.applyColorFilter
import ca.hojat.notes.niki.shared.extensions.baseConfig
import ca.hojat.notes.niki.shared.extensions.beVisible
import ca.hojat.notes.niki.shared.extensions.copyToClipboard
import ca.hojat.notes.niki.shared.extensions.getAlertDialogBuilder
import ca.hojat.notes.niki.shared.extensions.getProperTextColor
import ca.hojat.notes.niki.shared.extensions.onGlobalLayout
import ca.hojat.notes.niki.shared.extensions.onTextChangeListener
import ca.hojat.notes.niki.shared.extensions.setFillWithStroke
import ca.hojat.notes.niki.shared.extensions.setupDialogStuff
import ca.hojat.notes.niki.shared.extensions.toHex
import ca.hojat.notes.niki.shared.extensions.value
import ca.hojat.notes.niki.shared.helpers.isQPlus
import java.util.LinkedList

private const val RECENT_COLORS_NUMBER = 5

@JvmInline
private value class Hsv(val value: FloatArray) {
    fun getColor() = Color.HSVToColor(value)
    fun getHue() = value[0]
    fun setHue(hue: Float) {
        value[0] = hue
    }

    fun getSat() = value[1]
    fun setSat(sat: Float) {
        value[1] = sat
    }

    fun getVal() = value[2]
    fun setVal(v: Float) {
        value[2] = v
    }
}

// forked from https://github.com/yukuku/ambilwarna
class ColorPickerDialog(
    val activity: Activity,
    color: Int,
    private val removeDimmedBackground: Boolean = false,
    private val addDefaultColorButton: Boolean = false,
    private val currentColorCallback: ((color: Int) -> Unit)? = null,
    val callback: (wasPositivePressed: Boolean, color: Int) -> Unit
) {
    private val baseConfig = activity.baseConfig
    private val currentColorHsv = Hsv(FloatArray(3))
    private val backgroundColor = baseConfig.backgroundColor
    private var wasDimmedBackgroundRemoved = false
    private var dialog: AlertDialog? = null
    private val binding = DialogColorPickerBinding.inflate(activity.layoutInflater, null, false)

    init {
        Color.colorToHSV(color, currentColorHsv.value)

        binding.init(
            color = color,
            backgroundColor = backgroundColor,
            recentColors = baseConfig.colorPickerRecentColors,
            hsv = currentColorHsv,
            currentColorCallback = {
                if (removeDimmedBackground && !wasDimmedBackgroundRemoved) {
                    dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    wasDimmedBackgroundRemoved = true
                }

                currentColorCallback?.invoke(it)
            }
        )

        val textColor = activity.getProperTextColor()
        val builder = activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { _, _ -> confirmNewColor() }
            .setNegativeButton(R.string.cancel) { _, _ -> dialogDismissed() }
            .setOnCancelListener { dialogDismissed() }
            .apply {
                if (addDefaultColorButton) {
                    setNeutralButton(R.string.default_color) { _, _ -> confirmDefaultColor() }
                }
            }

        builder.apply {
            activity.setupDialogStuff(binding.root, this) { alertDialog ->
                dialog = alertDialog
                binding.colorPickerArrow.applyColorFilter(textColor)
                binding.colorPickerHexArrow.applyColorFilter(textColor)
                binding.colorPickerHueCursor.applyColorFilter(textColor)
            }
        }
    }

    private fun dialogDismissed() {
        callback(false, 0)
    }

    private fun confirmDefaultColor() {
        callback(true, 0)
    }

    private fun confirmNewColor() {
        val hexValue = binding.colorPickerNewHex.value
        val newColor = if (hexValue.length == 6) {
            Color.parseColor("#$hexValue")
        } else {
            currentColorHsv.getColor()
        }

        activity.addRecentColor(newColor)
        callback(true, newColor)
    }
}

@SuppressLint("SetTextI18n")
private fun DialogColorPickerBinding.init(
    color: Int,
    backgroundColor: Int,
    recentColors: List<Int>,
    hsv: Hsv,
    currentColorCallback: ((color: Int) -> Unit)
) {
    var isHueBeingDragged = false

    if (isQPlus()) {
        root.isForceDarkAllowed = false
    }

    colorPickerSquare.setHue(hsv.getHue())

    colorPickerNewColor.setFillWithStroke(color, backgroundColor)
    colorPickerOldColor.setFillWithStroke(color, backgroundColor)

    val hexCode = getHexCode(color)
    colorPickerOldHex.text = "#$hexCode"
    colorPickerOldHex.setOnLongClickListener {
        root.context.copyToClipboard(hexCode)
        true
    }
    colorPickerNewHex.setText(hexCode)
    setupRecentColors(backgroundColor, recentColors)

    colorPickerHue.setOnTouchListener(OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            isHueBeingDragged = true
        }

        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
            var y = event.y
            if (y < 0f)
                y = 0f

            if (y > colorPickerHue.measuredHeight) {
                y =
                    colorPickerHue.measuredHeight - 0.001f // to avoid jumping the cursor from bottom to top.
            }
            var hue = 360f - 360f / colorPickerHue.measuredHeight * y
            if (hue == 360f)
                hue = 0f

            hsv.setHue(hue)
            updateHue(hsv, backgroundColor, currentColorCallback)
            colorPickerNewHex.setText(getHexCode(hsv.getColor()))

            if (event.action == MotionEvent.ACTION_UP) {
                isHueBeingDragged = false
            }
            return@OnTouchListener true
        }
        false
    })

    colorPickerSquare.setOnTouchListener(OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
            var x = event.x
            var y = event.y

            if (x < 0f)
                x = 0f
            if (x > colorPickerSquare.measuredWidth)
                x = colorPickerSquare.measuredWidth.toFloat()
            if (y < 0f)
                y = 0f
            if (y > colorPickerSquare.measuredHeight)
                y = colorPickerSquare.measuredHeight.toFloat()

            hsv.setSat(1f / colorPickerSquare.measuredWidth * x)
            hsv.setVal(1f - 1f / colorPickerSquare.measuredHeight * y)

            moveColorPicker(hsv)
            colorPickerNewColor.setFillWithStroke(hsv.getColor(), backgroundColor)
            colorPickerNewHex.setText(getHexCode(hsv.getColor()))
            return@OnTouchListener true
        }
        false
    })

    colorPickerNewHex.onTextChangeListener {
        if (it.length == 6 && !isHueBeingDragged) {
            try {
                val newColor = Color.parseColor("#$it")
                Color.colorToHSV(newColor, hsv.value)
                updateHue(hsv, backgroundColor, currentColorCallback)
                moveColorPicker(hsv)
            } catch (ignored: Exception) {
            }
        }
    }

    root.onGlobalLayout {
        moveHuePicker(hsv)
        moveColorPicker(hsv)
    }
}

private fun DialogColorPickerBinding.setupRecentColors(
    backgroundColor: Int,
    recentColors: List<Int>
) {
    if (recentColors.isNotEmpty()) {
        this.recentColors.beVisible()
        val childrenToRemove = this.recentColors.children.filter { it is ImageView }.toList()
        childrenToRemove.forEach {
            this.recentColors.removeView(it)
            recentColorsFlow.removeView(it)
        }
        val squareSize = root.context.resources.getDimensionPixelSize(R.dimen.colorpicker_hue_width)
        recentColors.take(RECENT_COLORS_NUMBER).forEach { recentColor ->
            val recentColorView = ImageView(root.context)
            recentColorView.id = View.generateViewId()
            recentColorView.layoutParams = ViewGroup.LayoutParams(squareSize, squareSize)
            recentColorView.setFillWithStroke(recentColor, backgroundColor)
            recentColorView.setOnClickListener { colorPickerNewHex.setText(getHexCode(recentColor)) }
            this.recentColors.addView(recentColorView)
            recentColorsFlow.addView(recentColorView)
        }
    }
}

private fun DialogColorPickerBinding.updateHue(
    hsv: Hsv,
    backgroundColor: Int,
    currentColorCallback: ((color: Int) -> Unit)
) {
    colorPickerSquare.setHue(hsv.getHue())
    moveHuePicker(hsv)
    colorPickerNewColor.setFillWithStroke(hsv.getColor(), backgroundColor)

    currentColorCallback.invoke(hsv.getColor())
}

private fun DialogColorPickerBinding.moveHuePicker(hsv: Hsv) {
    var y = colorPickerHue.measuredHeight - hsv.getHue() * colorPickerHue.measuredHeight / 360f
    if (y == colorPickerHue.measuredHeight.toFloat())
        y = 0f

    colorPickerHueCursor.x = (colorPickerHue.left - colorPickerHueCursor.width).toFloat()
    colorPickerHueCursor.y = colorPickerHue.top + y - colorPickerHueCursor.height / 2
}

private fun DialogColorPickerBinding.moveColorPicker(hsv: Hsv) {
    val x = hsv.getSat() * colorPickerSquare.measuredWidth
    val y = (1f - hsv.getVal()) * colorPickerSquare.measuredHeight
    colorPickerCursor.x = colorPickerSquare.left + x - colorPickerCursor.width / 2
    colorPickerCursor.y = colorPickerSquare.top + y - colorPickerCursor.height / 2
}

private fun getHexCode(color: Int) = color.toHex().substring(1)

private fun Context.addRecentColor(color: Int) {
    var recentColors = baseConfig.colorPickerRecentColors

    recentColors.remove(color)
    if (recentColors.size >= RECENT_COLORS_NUMBER) {
        val numberOfColorsToDrop = recentColors.size - RECENT_COLORS_NUMBER + 1
        recentColors = LinkedList(recentColors.dropLast(numberOfColorsToDrop))
    }
    recentColors.addFirst(color)

    baseConfig.colorPickerRecentColors = recentColors
}

