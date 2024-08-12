package com.simplemobiletools.notes.pro.extensions

import android.content.Context
import com.simplemobiletools.commons.models.FileDirItem


fun FileDirItem.isRecycleBinPath(context: Context): Boolean {
    return path.startsWith(context.recycleBinPath)
}
