package ca.hojat.messenger.niki.shared.extensions

import android.content.Context
import ca.hojat.messenger.niki.shared.data.models.FileDirItem


fun FileDirItem.isRecycleBinPath(context: Context): Boolean {
    return path.startsWith(context.recycleBinPath)
}
