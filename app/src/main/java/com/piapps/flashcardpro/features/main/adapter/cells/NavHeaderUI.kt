package com.piapps.flashcardpro.features.main.adapter.cells

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.piapps.flashcardpro.R
import com.piapps.flashcardpro.core.extension.appTheme
import com.piapps.flashcardpro.core.extension.ellipsize
import com.piapps.flashcardpro.core.extension.getLocalizedString
import com.piapps.flashcardpro.core.extension.makeBold
import com.piapps.flashcardpro.features.main.adapter.cells.NavMenuUI.Companion.tvId
import org.jetbrains.anko.*

/**
 * Created by abduaziz on 2019-09-26 at 12:34.
 */

class NavHeaderUI : AnkoComponent<ViewGroup> {

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        linearLayout {
            lparams(matchParent, dip(56))
            gravity = Gravity.BOTTOM

            textView {
                id = tvId
                layoutParams = LinearLayout.LayoutParams(matchParent, dip(56))
                gravity = Gravity.CENTER_VERTICAL
                horizontalPadding = dip(16)
                text = ctx.getLocalizedString(R.string.app_name)
                textColorResource = ctx.appTheme().colorPrimaryText
                textSize = 16F
                ellipsize(1)
                makeBold()
            }
        }
    }
}