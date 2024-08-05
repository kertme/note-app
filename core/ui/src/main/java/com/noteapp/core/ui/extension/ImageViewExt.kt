package com.noteapp.core.ui.extension

import android.widget.ImageView
import coil.Coil
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation

/**
 * @author: R. Cemre Ünal,
 * created on 9/22/2022
 */

fun ImageView.loadImage(url: String?) {
    if (url.isNullOrEmpty()) hide()
    else {
        Coil.imageLoader(context)
        load(url) {
            transformations(CircleCropTransformation())
            scale(Scale.FIT)
        }
    }
}