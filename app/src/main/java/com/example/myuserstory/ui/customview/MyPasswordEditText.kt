package com.example.myuserstory.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.myuserstory.R

class MyPasswordEditText : AppCompatEditText {
    private lateinit var iconPasswordInput: Drawable
    private var passLength: Int = 0
    private val minPassLength: Int = 8


    init {
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        iconPasswordInput = ContextCompat.getDrawable(context, R.drawable.baseline_password_24) as Drawable
        showIconPasswordInput()
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                passLength = s.length
                if (s.isNotEmpty() && passLength < minPassLength) {
                    error = context.getString(R.string.invalid_password)
                }
            }

            override fun afterTextChanged(ed: Editable?) {
                // Do nothing.
            }
        })

    }

    private fun showIconPasswordInput() {
        setButtonDrawables(startOfTheText = iconPasswordInput)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        setSelection(text?.length ?: 0)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        maxLines = 1
        hint = resources.getString(R.string.password_hint)
    }
}