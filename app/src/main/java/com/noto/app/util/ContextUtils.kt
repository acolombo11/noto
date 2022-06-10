package com.noto.app.util

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.view.Window
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Folder

private const val IconSize = 512
private const val IconSpacing = 128

fun Context.createPinnedShortcut(folder: Folder): ShortcutInfoCompat {
    val intent = Intent(Constants.Intent.ActionCreateNote, null, this, AppActivity::class.java).apply {
        putExtra(Constants.FolderId, folder.id)
    }
    val backgroundColor = folder.color.toResource().let(this::colorResource)
    val iconColor = colorResource(android.R.color.white)
    val bitmap = createBitmap(IconSize, IconSize).applyCanvas {
        drawColor(backgroundColor)
        drawableResource(R.drawable.ic_round_edit_24)?.mutate()?.let { drawable ->
            drawable.setTint(iconColor)
            drawable.setBounds(IconSpacing, IconSpacing, width - IconSpacing, height - IconSpacing)
            drawable.draw(this)
        }
    }
    return ShortcutInfoCompat.Builder(this, folder.id.toString())
        .setIntent(intent)
        .setShortLabel(folder.getTitle(this))
        .setLongLabel(folder.getTitle(this))
        .setIcon(IconCompat.createWithBitmap(bitmap))
        .build()
}

fun Context.applyNightModeConfiguration(window: Window) {
    val insetsController = WindowInsetsControllerCompat(window, window.decorView.rootView)
    val currentConfiguration = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
    if (currentConfiguration == Configuration.UI_MODE_NIGHT_YES) {
        // For API 21 and above
        insetsController.isAppearanceLightStatusBars = false

        // For API 27 and above
        insetsController.isAppearanceLightNavigationBars = false
    } else if (currentConfiguration == Configuration.UI_MODE_NIGHT_NO) {
        // For API 21 and above
        insetsController.isAppearanceLightStatusBars = true

        // For API 27 and above
        insetsController.isAppearanceLightNavigationBars = true
    }
}

fun Context.applySystemBarsColorsForApiLessThan23(window: Window) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        window.statusBarColor = colorResource(android.R.color.black)
        window.navigationBarColor = colorResource(android.R.color.black)
    }
}