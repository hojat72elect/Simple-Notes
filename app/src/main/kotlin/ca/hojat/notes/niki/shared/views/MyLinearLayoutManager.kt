package ca.hojat.notes.niki.shared.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * warning : Don't remove any of the constructors of this class. You will face Run-Time errors.
 */
class MyLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    // fixes crash java.lang.IndexOutOfBoundsException: Inconsistency detected...
    // taken from https://stackoverflow.com/a/33985508/1967672
    override fun supportsPredictiveItemAnimations() = false
}
