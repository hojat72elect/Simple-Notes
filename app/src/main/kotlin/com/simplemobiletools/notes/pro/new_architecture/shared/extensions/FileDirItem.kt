package com.simplemobiletools.notes.pro.new_architecture.shared.extensions

import android.content.Context
import com.simplemobiletools.notes.pro.new_architecture.shared.data.models.FileDirItem


fun FileDirItem.isRecycleBinPath(context: Context): Boolean {
    return path.startsWith(context.recycleBinPath)
}
