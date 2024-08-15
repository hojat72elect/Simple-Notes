package com.simplemobiletools.notes.pro.extensions

import android.content.Context
import com.simplemobiletools.notes.pro.models.FileDirItem


fun FileDirItem.isRecycleBinPath(context: Context): Boolean {
    return path.startsWith(context.recycleBinPath)
}
