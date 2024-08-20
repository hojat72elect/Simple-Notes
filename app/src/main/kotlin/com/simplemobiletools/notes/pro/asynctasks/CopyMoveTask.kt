package com.simplemobiletools.notes.pro.asynctasks

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.util.Pair
import androidx.documentfile.provider.DocumentFile
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.activities.BaseActivity
import com.simplemobiletools.notes.pro.extensions.baseConfig
import com.simplemobiletools.notes.pro.extensions.canManageMedia
import com.simplemobiletools.notes.pro.extensions.createDirectorySync
import com.simplemobiletools.notes.pro.extensions.deleteFromMediaStore
import com.simplemobiletools.notes.pro.extensions.getAndroidSAFFileItems
import com.simplemobiletools.notes.pro.extensions.getDocumentFile
import com.simplemobiletools.notes.pro.extensions.getDocumentSdk30
import com.simplemobiletools.notes.pro.extensions.getDoesFilePathExist
import com.simplemobiletools.notes.pro.extensions.getFileInputStreamSync
import com.simplemobiletools.notes.pro.extensions.getFileOutputStreamSync
import com.simplemobiletools.notes.pro.extensions.getFileUrisFromFileDirItems
import com.simplemobiletools.notes.pro.extensions.getFilenameFromPath
import com.simplemobiletools.notes.pro.extensions.getIntValue
import com.simplemobiletools.notes.pro.extensions.getLongValue
import com.simplemobiletools.notes.pro.extensions.getMimeType
import com.simplemobiletools.notes.pro.extensions.isAccessibleWithSAFSdk30
import com.simplemobiletools.notes.pro.extensions.isMediaFile
import com.simplemobiletools.notes.pro.extensions.isPathOnOTG
import com.simplemobiletools.notes.pro.extensions.isRestrictedSAFOnlyRoot
import com.simplemobiletools.notes.pro.extensions.isRestrictedWithSAFSdk30
import com.simplemobiletools.notes.pro.extensions.needsStupidWritePermissions
import com.simplemobiletools.notes.pro.extensions.notificationManager
import com.simplemobiletools.notes.pro.extensions.rescanPath
import com.simplemobiletools.notes.pro.extensions.showErrorToast
import com.simplemobiletools.notes.pro.extensions.toFileDirItem
import com.simplemobiletools.notes.pro.helpers.CONFLICT_KEEP_BOTH
import com.simplemobiletools.notes.pro.helpers.CONFLICT_SKIP
import com.simplemobiletools.notes.pro.helpers.getConflictResolution
import com.simplemobiletools.notes.pro.helpers.isOreoPlus
import com.simplemobiletools.notes.pro.interfaces.CopyMoveListener
import com.simplemobiletools.notes.pro.models.FileDirItem
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.ref.WeakReference

@RequiresApi(Build.VERSION_CODES.O)
class CopyMoveTask(
    val activity: BaseActivity,
    private val copyOnly: Boolean,
    private val copyMediaOnly: Boolean,
    private val conflictResolutions: LinkedHashMap<String, Int>,
    listener: CopyMoveListener,
    private val copyHidden: Boolean
) : AsyncTask<Pair<ArrayList<FileDirItem>, String>, Void, Boolean>() {


    private var mListener: WeakReference<CopyMoveListener>? = null
    private var mTransferredFiles = ArrayList<FileDirItem>()
    private var mFileDirItemsToDelete =
        ArrayList<FileDirItem>()        // confirm the deletion of files on Android 11 from Downloads and Android at once
    private var mDocuments = LinkedHashMap<String, DocumentFile?>()
    private var mFiles = ArrayList<FileDirItem>()
    private var mFileCountToCopy = 0
    private var mDestinationPath = ""

    // progress indication
    private var notificationBuilder: NotificationCompat.Builder
    private var currFilename = ""
    private var currentProgress = 0L
    private var maxSize = 0
    private var notificationId = 0
    private var isTaskOver = false
    private var progressHandler = Handler()

    init {
        mListener = WeakReference(listener)
        notificationBuilder = NotificationCompat.Builder(activity)
    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Pair<ArrayList<FileDirItem>, String>): Boolean {
        if (params.isEmpty()) {
            return false
        }

        val pair = params[0]
        mFiles = pair.first!!
        mDestinationPath = pair.second!!
        mFileCountToCopy = mFiles.size
        notificationId = (System.currentTimeMillis() / 1000).toInt()
        maxSize = 0
        for (file in mFiles) {
            if (file.size == 0L) {
                file.size = file.getProperSize(activity, copyHidden)
            }

            val newPath = "$mDestinationPath/${file.name}"
            val fileExists = activity.getDoesFilePathExist(newPath)
            if (getConflictResolution(
                    conflictResolutions,
                    newPath
                ) != CONFLICT_SKIP || !fileExists
            ) {
                maxSize += (file.size / 1000).toInt()
            }
        }

        progressHandler.postDelayed({
            initProgressNotification()
            updateProgress()
        }, INITIAL_PROGRESS_DELAY)

        for (file in mFiles) {
            try {
                val newPath = "$mDestinationPath/${file.name}"
                var newFileDirItem =
                    FileDirItem(newPath, newPath.getFilenameFromPath(), file.isDirectory)
                if (activity.getDoesFilePathExist(newPath)) {
                    val resolution = getConflictResolution(conflictResolutions, newPath)
                    if (resolution == CONFLICT_SKIP) {
                        mFileCountToCopy--
                        continue
                    } else if (resolution == CONFLICT_KEEP_BOTH) {
                        val newFile = activity.getAlternativeFile(File(newFileDirItem.path))
                        newFileDirItem =
                            FileDirItem(newFile.path, newFile.name, newFile.isDirectory)
                    }
                }

                copy(file, newFileDirItem)
            } catch (e: Exception) {
                activity.showErrorToast(e)
                return false
            }
        }

        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(success: Boolean) {
        if (activity.isFinishing || activity.isDestroyed) {
            return
        }

        deleteProtectedFiles()
        progressHandler.removeCallbacksAndMessages(null)
        activity.notificationManager.cancel(notificationId)
        val listener = mListener?.get() ?: return

        if (success) {
            listener.copySucceeded(
                copyOnly,
                mTransferredFiles.size >= mFileCountToCopy,
                mDestinationPath,
                mTransferredFiles.size == 1
            )
        } else {
            listener.copyFailed()
        }
    }

    private fun initProgressNotification() {
        val channelId = "Copy/Move"
        val title = activity.getString(if (copyOnly) R.string.copying else R.string.moving)
        if (isOreoPlus()) {
            val importance = NotificationManager.IMPORTANCE_LOW
            NotificationChannel(channelId, title, importance).apply {
                enableLights(false)
                enableVibration(false)
                activity.notificationManager.createNotificationChannel(this)
            }
        }

        notificationBuilder.setContentTitle(title)
            .setSmallIcon(R.drawable.ic_copy_vector)
            .setChannelId(channelId)
    }

    private fun updateProgress() {
        if (isTaskOver) {
            activity.notificationManager.cancel(notificationId)
            cancel(true)
            return
        }

        notificationBuilder.apply {
            setContentText(currFilename)
            setProgress(maxSize, (currentProgress / 1000).toInt(), false)
            activity.notificationManager.notify(notificationId, build())
        }

        progressHandler.removeCallbacksAndMessages(null)
        progressHandler.postDelayed({
            updateProgress()

            if (currentProgress / 1000 >= maxSize) {
                isTaskOver = true
            }
        }, PROGRESS_RECHECK_INTERVAL)
    }

    private fun copy(source: FileDirItem, destination: FileDirItem) {
        if (source.isDirectory) {
            copyDirectory(source, destination.path)
        } else {
            copyFile(source, destination)
        }
    }


    private fun copyDirectory(source: FileDirItem, destinationPath: String) {
        if (!activity.createDirectorySync(destinationPath)) {
            val error =
                String.format(activity.getString(R.string.could_not_create_folder), destinationPath)
            activity.showErrorToast(error)
            return
        }

        if (activity.isPathOnOTG(source.path)) {
            val children = activity.getDocumentFile(source.path)?.listFiles() ?: return
            for (child in children) {
                val newPath = "$destinationPath/${child.name}"
                if (File(newPath).exists()) {
                    continue
                }

                val oldPath = "${source.path}/${child.name}"
                val oldFileDirItem =
                    FileDirItem(oldPath, child.name!!, child.isDirectory, 0, child.length())
                val newFileDirItem = FileDirItem(newPath, child.name!!, child.isDirectory)
                copy(oldFileDirItem, newFileDirItem)
            }
            mTransferredFiles.add(source)
        } else if (activity.isRestrictedSAFOnlyRoot(source.path)) {
            activity.getAndroidSAFFileItems(source.path, true) { files ->
                for (child in files) {
                    val newPath = "$destinationPath/${child.name}"
                    if (activity.getDoesFilePathExist(newPath)) {
                        continue
                    }

                    val oldPath = "${source.path}/${child.name}"
                    val oldFileDirItem =
                        FileDirItem(oldPath, child.name, child.isDirectory, 0, child.size)
                    val newFileDirItem = FileDirItem(newPath, child.name, child.isDirectory)
                    copy(oldFileDirItem, newFileDirItem)
                }
                mTransferredFiles.add(source)
            }
        } else if (activity.isAccessibleWithSAFSdk30(source.path)) {
            val children = activity.getDocumentSdk30(source.path)?.listFiles() ?: return
            for (child in children) {
                val newPath = "$destinationPath/${child.name}"
                if (File(newPath).exists()) {
                    continue
                }

                val oldPath = "${source.path}/${child.name}"
                val oldFileDirItem =
                    FileDirItem(oldPath, child.name!!, child.isDirectory, 0, child.length())
                val newFileDirItem = FileDirItem(newPath, child.name!!, child.isDirectory)
                copy(oldFileDirItem, newFileDirItem)
            }
            mTransferredFiles.add(source)
        } else {
            val children = File(source.path).list()
            for (child in children) {
                val newPath = "$destinationPath/$child"
                if (activity.getDoesFilePathExist(newPath)) {
                    continue
                }

                val oldFile = File(source.path, child)
                val oldFileDirItem = oldFile.toFileDirItem(activity)
                val newFileDirItem =
                    FileDirItem(newPath, newPath.getFilenameFromPath(), oldFile.isDirectory)
                copy(oldFileDirItem, newFileDirItem)
            }
            mTransferredFiles.add(source)
        }
    }

    private fun copyFile(source: FileDirItem, destination: FileDirItem) {
        if (copyMediaOnly && !source.path.isMediaFile()) {
            currentProgress += source.size
            return
        }

        val directory = destination.getParentPath()
        if (!activity.createDirectorySync(directory)) {
            val error =
                String.format(activity.getString(R.string.could_not_create_folder), directory)
            activity.showErrorToast(error)
            currentProgress += source.size
            return
        }

        currFilename = source.name
        var inputStream: InputStream? = null
        var out: OutputStream? = null
        try {
            if (!mDocuments.containsKey(directory) && activity.needsStupidWritePermissions(
                    destination.path
                )
            ) {
                mDocuments[directory] = activity.getDocumentFile(directory)
            }

            out = activity.getFileOutputStreamSync(
                destination.path,
                source.path.getMimeType(),
                mDocuments[directory]
            )
            inputStream = activity.getFileInputStreamSync(source.path)!!

            var copiedSize = 0L
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = inputStream.read(buffer)
            while (bytes >= 0) {
                out!!.write(buffer, 0, bytes)
                copiedSize += bytes
                currentProgress += bytes
                bytes = inputStream.read(buffer)
            }

            out?.flush()

            if (source.size == copiedSize && activity.getDoesFilePathExist(destination.path)) {
                mTransferredFiles.add(source)
                if (copyOnly) {
                    activity.rescanPath(destination.path) {
                        if (activity.baseConfig.keepLastModified) {
                            updateLastModifiedValues(source, destination)
                            activity.rescanPath(destination.path)
                        }
                    }
                } else if (activity.baseConfig.keepLastModified) {
                    updateLastModifiedValues(source, destination)
                    activity.rescanPath(destination.path)
                    inputStream.close()
                    out?.close()
                    deleteSourceFile(source)
                } else {
                    inputStream.close()
                    out?.close()
                    deleteSourceFile(source)
                }
            }
        } catch (e: Exception) {
            activity.showErrorToast(e)
        } finally {
            inputStream?.close()
            out?.close()
        }
    }

    private fun updateLastModifiedValues(source: FileDirItem, destination: FileDirItem) {
        copyOldLastModified(source.path, destination.path)
        val lastModified = File(source.path).lastModified()
        if (lastModified != 0L) {
            File(destination.path).setLastModified(lastModified)
        }
    }

    private fun deleteSourceFile(source: FileDirItem) {
        if (activity.isRestrictedWithSAFSdk30(source.path) && !activity.canManageMedia()) {
            mFileDirItemsToDelete.add(source)
        } else {
            activity.deleteFileBg(source, isDeletingMultipleFiles = false)
            activity.deleteFromMediaStore(source.path)
        }
    }

    // if we delete multiple files from Downloads folder on Android 11 or 12 without being a Media Management app, show the confirmation dialog just once
    private fun deleteProtectedFiles() {
        if (mFileDirItemsToDelete.isNotEmpty()) {
            val fileUris = activity.getFileUrisFromFileDirItems(mFileDirItemsToDelete)
            activity.deleteSDK30Uris(fileUris) { success ->
                if (success) {
                    mFileDirItemsToDelete.forEach {
                        activity.deleteFromMediaStore(it.path)
                    }
                }
            }
        }
    }

    private fun copyOldLastModified(sourcePath: String, destinationPath: String) {
        val projection = arrayOf(
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_MODIFIED
        )

        val uri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.MediaColumns.DATA} = ?"
        var selectionArgs = arrayOf(sourcePath)
        val cursor = activity.applicationContext.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            if (cursor.moveToFirst()) {
                val dateTaken = cursor.getLongValue(MediaStore.Images.Media.DATE_TAKEN)
                val dateModified = cursor.getIntValue(MediaStore.Images.Media.DATE_MODIFIED)

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DATE_TAKEN, dateTaken)
                    put(MediaStore.Images.Media.DATE_MODIFIED, dateModified)
                }

                selectionArgs = arrayOf(destinationPath)
                activity.applicationContext.contentResolver.update(
                    uri,
                    values,
                    selection,
                    selectionArgs
                )
            }
        }
    }

    companion object {
        private const val INITIAL_PROGRESS_DELAY = 3_000L
        private const val PROGRESS_RECHECK_INTERVAL = 500L
    }
}
