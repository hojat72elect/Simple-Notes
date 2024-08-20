package com.simplemobiletools.notes.pro.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.RecoverableSecurityException
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.telecom.TelecomManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.util.Pair
import androidx.core.view.ScrollingView
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.asynctasks.CopyMoveTask
import com.simplemobiletools.notes.pro.compose.extensions.DEVELOPER_PLAY_STORE_URL
import com.simplemobiletools.notes.pro.dialogs.ConfirmationAdvancedDialog
import com.simplemobiletools.notes.pro.dialogs.ConfirmationDialog
import com.simplemobiletools.notes.pro.dialogs.ExportSettingsDialog
import com.simplemobiletools.notes.pro.dialogs.FeatureLockedDialog
import com.simplemobiletools.notes.pro.dialogs.FileConflictDialog
import com.simplemobiletools.notes.pro.dialogs.PermissionRequiredDialog
import com.simplemobiletools.notes.pro.dialogs.WhatsNewDialog
import com.simplemobiletools.notes.pro.dialogs.WritePermissionDialog
import com.simplemobiletools.notes.pro.dialogs.WritePermissionDialog.WritePermissionDialogMode
import com.simplemobiletools.notes.pro.extensions.addBit
import com.simplemobiletools.notes.pro.extensions.adjustAlpha
import com.simplemobiletools.notes.pro.extensions.applyColorFilter
import com.simplemobiletools.notes.pro.extensions.baseConfig
import com.simplemobiletools.notes.pro.extensions.buildDocumentUriSdk30
import com.simplemobiletools.notes.pro.extensions.canManageMedia
import com.simplemobiletools.notes.pro.extensions.createAndroidDataOrObbPath
import com.simplemobiletools.notes.pro.extensions.createAndroidDataOrObbUri
import com.simplemobiletools.notes.pro.extensions.createAndroidSAFFile
import com.simplemobiletools.notes.pro.extensions.createDirectorySync
import com.simplemobiletools.notes.pro.extensions.createDocumentUriUsingFirstParentTreeUri
import com.simplemobiletools.notes.pro.extensions.createFirstParentTreeUri
import com.simplemobiletools.notes.pro.extensions.createFirstParentTreeUriUsingRootTree
import com.simplemobiletools.notes.pro.extensions.createSAFFileSdk30
import com.simplemobiletools.notes.pro.extensions.deleteAndroidSAFDirectory
import com.simplemobiletools.notes.pro.extensions.deleteDocumentWithSAFSdk30
import com.simplemobiletools.notes.pro.extensions.deleteFromMediaStore
import com.simplemobiletools.notes.pro.extensions.doesThisOrParentHaveNoMedia
import com.simplemobiletools.notes.pro.extensions.formatSize
import com.simplemobiletools.notes.pro.extensions.getAndroidSAFUri
import com.simplemobiletools.notes.pro.extensions.getAndroidTreeUri
import com.simplemobiletools.notes.pro.extensions.getAppIconColors
import com.simplemobiletools.notes.pro.extensions.getAvailableStorageB
import com.simplemobiletools.notes.pro.extensions.getColoredDrawableWithColor
import com.simplemobiletools.notes.pro.extensions.getColoredMaterialStatusBarColor
import com.simplemobiletools.notes.pro.extensions.getContrastColor
import com.simplemobiletools.notes.pro.extensions.getCurrentFormattedDateTime
import com.simplemobiletools.notes.pro.extensions.getDocumentFile
import com.simplemobiletools.notes.pro.extensions.getDoesFilePathExist
import com.simplemobiletools.notes.pro.extensions.getFileInputStreamSync
import com.simplemobiletools.notes.pro.extensions.getFileOutputStreamSync
import com.simplemobiletools.notes.pro.extensions.getFileUrisFromFileDirItems
import com.simplemobiletools.notes.pro.extensions.getFilenameFromPath
import com.simplemobiletools.notes.pro.extensions.getFirstParentLevel
import com.simplemobiletools.notes.pro.extensions.getFirstParentPath
import com.simplemobiletools.notes.pro.extensions.getIntValue
import com.simplemobiletools.notes.pro.extensions.getIsPathDirectory
import com.simplemobiletools.notes.pro.extensions.getLongValue
import com.simplemobiletools.notes.pro.extensions.getMimeType
import com.simplemobiletools.notes.pro.extensions.getParentPath
import com.simplemobiletools.notes.pro.extensions.getPermissionString
import com.simplemobiletools.notes.pro.extensions.getProperBackgroundColor
import com.simplemobiletools.notes.pro.extensions.getProperStatusBarColor
import com.simplemobiletools.notes.pro.extensions.getSomeDocumentFile
import com.simplemobiletools.notes.pro.extensions.getThemeId
import com.simplemobiletools.notes.pro.extensions.hasAllPermissions
import com.simplemobiletools.notes.pro.extensions.hasPermission
import com.simplemobiletools.notes.pro.extensions.hasProperStoredAndroidTreeUri
import com.simplemobiletools.notes.pro.extensions.hasProperStoredDocumentUriSdk30
import com.simplemobiletools.notes.pro.extensions.hasProperStoredFirstParentUri
import com.simplemobiletools.notes.pro.extensions.hasProperStoredTreeUri
import com.simplemobiletools.notes.pro.extensions.hideKeyboard
import com.simplemobiletools.notes.pro.extensions.humanizePath
import com.simplemobiletools.notes.pro.extensions.internalStoragePath
import com.simplemobiletools.notes.pro.extensions.isAccessibleWithSAFSdk30
import com.simplemobiletools.notes.pro.extensions.isAppInstalledOnSDCard
import com.simplemobiletools.notes.pro.extensions.isOrWasThankYouInstalled
import com.simplemobiletools.notes.pro.extensions.isPathOnInternalStorage
import com.simplemobiletools.notes.pro.extensions.isPathOnOTG
import com.simplemobiletools.notes.pro.extensions.isPathOnSD
import com.simplemobiletools.notes.pro.extensions.isRecycleBinPath
import com.simplemobiletools.notes.pro.extensions.isRestrictedSAFOnlyRoot
import com.simplemobiletools.notes.pro.extensions.isRestrictedWithSAFSdk30
import com.simplemobiletools.notes.pro.extensions.isSDCardSetAsDefaultStorage
import com.simplemobiletools.notes.pro.extensions.isUsingGestureNavigation
import com.simplemobiletools.notes.pro.extensions.launchViewIntent
import com.simplemobiletools.notes.pro.extensions.navigationBarHeight
import com.simplemobiletools.notes.pro.extensions.needsStupidWritePermissions
import com.simplemobiletools.notes.pro.extensions.onApplyWindowInsets
import com.simplemobiletools.notes.pro.extensions.openDeviceSettings
import com.simplemobiletools.notes.pro.extensions.openNotificationSettings
import com.simplemobiletools.notes.pro.extensions.random
import com.simplemobiletools.notes.pro.extensions.removeBit
import com.simplemobiletools.notes.pro.extensions.renameAndroidSAFDocument
import com.simplemobiletools.notes.pro.extensions.renameDocumentSdk30
import com.simplemobiletools.notes.pro.extensions.rescanAndDeletePath
import com.simplemobiletools.notes.pro.extensions.rescanPath
import com.simplemobiletools.notes.pro.extensions.rescanPaths
import com.simplemobiletools.notes.pro.extensions.scanPathRecursively
import com.simplemobiletools.notes.pro.extensions.scanPathsRecursively
import com.simplemobiletools.notes.pro.extensions.showErrorToast
import com.simplemobiletools.notes.pro.extensions.showFileCreateError
import com.simplemobiletools.notes.pro.extensions.statusBarHeight
import com.simplemobiletools.notes.pro.extensions.storeAndroidTreeUri
import com.simplemobiletools.notes.pro.extensions.toFileDirItem
import com.simplemobiletools.notes.pro.extensions.toast
import com.simplemobiletools.notes.pro.extensions.trySAFFileDelete
import com.simplemobiletools.notes.pro.extensions.updateInMediaStore
import com.simplemobiletools.notes.pro.extensions.updateLastModified
import com.simplemobiletools.notes.pro.extensions.updateOTGPathFromPartition
import com.simplemobiletools.notes.pro.extensions.writeLn
import com.simplemobiletools.notes.pro.helpers.APP_FAQ
import com.simplemobiletools.notes.pro.helpers.APP_ICON_IDS
import com.simplemobiletools.notes.pro.helpers.APP_LAUNCHER_NAME
import com.simplemobiletools.notes.pro.helpers.APP_LICENSES
import com.simplemobiletools.notes.pro.helpers.APP_NAME
import com.simplemobiletools.notes.pro.helpers.APP_VERSION_NAME
import com.simplemobiletools.notes.pro.helpers.CONFLICT_KEEP_BOTH
import com.simplemobiletools.notes.pro.helpers.CONFLICT_SKIP
import com.simplemobiletools.notes.pro.helpers.CREATE_DOCUMENT_SDK_30
import com.simplemobiletools.notes.pro.helpers.DARK_GREY
import com.simplemobiletools.notes.pro.helpers.EXTERNAL_STORAGE_PROVIDER_AUTHORITY
import com.simplemobiletools.notes.pro.helpers.EXTRA_SHOW_ADVANCED
import com.simplemobiletools.notes.pro.helpers.HIGHER_ALPHA
import com.simplemobiletools.notes.pro.helpers.MEDIUM_ALPHA
import com.simplemobiletools.notes.pro.helpers.MyContextWrapper
import com.simplemobiletools.notes.pro.helpers.NavigationIcon
import com.simplemobiletools.notes.pro.helpers.OPEN_DOCUMENT_TREE_FOR_ANDROID_DATA_OR_OBB
import com.simplemobiletools.notes.pro.helpers.OPEN_DOCUMENT_TREE_FOR_SDK_30
import com.simplemobiletools.notes.pro.helpers.OPEN_DOCUMENT_TREE_OTG
import com.simplemobiletools.notes.pro.helpers.OPEN_DOCUMENT_TREE_SD
import com.simplemobiletools.notes.pro.helpers.PERMISSION_POST_NOTIFICATIONS
import com.simplemobiletools.notes.pro.helpers.PERMISSION_READ_MEDIA_VISUAL_USER_SELECTED
import com.simplemobiletools.notes.pro.helpers.PERMISSION_WRITE_STORAGE
import com.simplemobiletools.notes.pro.helpers.REQUEST_CODE_SET_DEFAULT_CALLER_ID
import com.simplemobiletools.notes.pro.helpers.REQUEST_CODE_SET_DEFAULT_DIALER
import com.simplemobiletools.notes.pro.helpers.SD_OTG_SHORT
import com.simplemobiletools.notes.pro.helpers.SELECT_EXPORT_SETTINGS_FILE_INTENT
import com.simplemobiletools.notes.pro.helpers.SHOW_FAQ_BEFORE_MAIL
import com.simplemobiletools.notes.pro.helpers.ensureBackgroundThread
import com.simplemobiletools.notes.pro.helpers.getConflictResolution
import com.simplemobiletools.notes.pro.helpers.isOreoPlus
import com.simplemobiletools.notes.pro.helpers.isQPlus
import com.simplemobiletools.notes.pro.helpers.isRPlus
import com.simplemobiletools.notes.pro.helpers.isTiramisuPlus
import com.simplemobiletools.notes.pro.helpers.isUpsideDownCakePlus
import com.simplemobiletools.notes.pro.helpers.sumByLong
import com.simplemobiletools.notes.pro.interfaces.CopyMoveListener
import com.simplemobiletools.notes.pro.models.Android30RenameFormat
import com.simplemobiletools.notes.pro.models.FAQItem
import com.simplemobiletools.notes.pro.models.FileDirItem
import com.simplemobiletools.notes.pro.models.Release
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
abstract class BaseSimpleActivity : AppCompatActivity() {
    private var materialScrollColorAnimation: ValueAnimator? = null
    var copyMoveCallback: ((destinationPath: String) -> Unit)? = null
    private var actionOnPermission: ((granted: Boolean) -> Unit)? = null
    private var isAskingPermissions = false
    var useDynamicTheme = true
    private var showTransparentTop = false
    var isMaterialActivity =
        false      // by material activity we mean translucent navigation bar and opaque status and action bars.
    private var checkedDocumentPath = ""
    private var currentScrollY = 0
    private var configItemsToExport = LinkedHashMap<String, Any>()

    private var mainCoordinatorLayout: CoordinatorLayout? = null
    private var nestedView: View? = null
    private var scrollingView: ScrollingView? = null
    private var toolbar: Toolbar? = null
    private var useTransparentNavigation = false
    private var useTopSearchMenu = false


    companion object {
        var funAfterSAFPermission: ((success: Boolean) -> Unit)? = null
        var funAfterSdk30Action: ((success: Boolean) -> Unit)? = null
        var funAfterUpdate30File: ((success: Boolean) -> Unit)? = null
        var funAfterTrash30File: ((success: Boolean) -> Unit)? = null
        var funRecoverableSecurity: ((success: Boolean) -> Unit)? = null
        var funAfterManageMediaPermission: (() -> Unit)? = null

        private const val GENERIC_PERM_HANDLER = 100
        private const val DELETE_FILE_SDK_30_HANDLER = 300
        private const val RECOVERABLE_SECURITY_HANDLER = 301
        private const val UPDATE_FILE_SDK_30_HANDLER = 302
        private const val MANAGE_MEDIA_RC = 303
        private const val TRASH_FILE_SDK_30_HANDLER = 304
    }

    abstract fun getAppIconIDs(): ArrayList<Int>

    abstract fun getAppLauncherName(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        if (useDynamicTheme) {
            setTheme(getThemeId(showTransparentTop = showTransparentTop))
        }

        super.onCreate(savedInstanceState)
        if (!packageName.startsWith("com.simplemobiletools.", true)) {
            if ((0..50).random() == 10 || baseConfig.appRunCount % 100 == 0) {
                val label =
                    "You are using a fake version of the app. For your own safety download the original one from www.simplemobiletools.com. Thanks"
                ConfirmationDialog(this, label, positive = R.string.ok, negative = 0) {
                    launchViewIntent(DEVELOPER_PLAY_STORE_URL)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        if (useDynamicTheme) {
            setTheme(getThemeId(showTransparentTop = showTransparentTop))

            val backgroundColor = if (baseConfig.isUsingSystemTheme) {
                resources.getColor(R.color.you_background_color, theme)
            } else {
                baseConfig.backgroundColor
            }

            updateBackgroundColor(backgroundColor)
        }

        if (showTransparentTop) {
            window.statusBarColor = Color.TRANSPARENT
        } else if (!isMaterialActivity) {
            val color = if (baseConfig.isUsingSystemTheme) {
                resources.getColor(R.color.you_status_bar_color)
            } else {
                getProperStatusBarColor()
            }

            updateActionbarColor(color)
        }

        updateRecentsAppIcon()

        var navBarColor = getProperBackgroundColor()
        if (isMaterialActivity) {
            navBarColor = navBarColor.adjustAlpha(HIGHER_ALPHA)
        }

        updateNavigationBarColor(navBarColor)
    }

    override fun onDestroy() {
        super.onDestroy()
        funAfterSAFPermission = null
        actionOnPermission = null
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        handleNavigationAndScrolling()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                finish()
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun attachBaseContext(newBase: Context) {
        if (newBase.baseConfig.useEnglish && !isTiramisuPlus()) {
            super.attachBaseContext(MyContextWrapper(newBase).wrap(newBase, "en"))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    fun updateBackgroundColor(color: Int = baseConfig.backgroundColor) {
        window.decorView.setBackgroundColor(color)
    }

    private fun updateStatusBarColor(color: Int) {
        window.statusBarColor = color

        if (color.getContrastColor() == DARK_GREY) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility.addBit(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        } else {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility.removeBit(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
    }

    fun updateActionbarColor(color: Int = getProperStatusBarColor()) {
        updateStatusBarColor(color)
        setTaskDescription(ActivityManager.TaskDescription(null, null, color))
    }

    private fun updateNavigationBarColor(color: Int) {
        window.navigationBarColor = color
        updateNavigationBarButtons(color)
    }

    private fun updateNavigationBarButtons(color: Int) {
        if (isOreoPlus()) {
            if (color.getContrastColor() == DARK_GREY) {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility.addBit(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
            } else {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility.removeBit(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
            }
        }
    }

    // use translucent navigation bar, set the background color to action and status bars
    fun updateMaterialActivityViews(
        mainCoordinatorLayout: CoordinatorLayout?,
        nestedView: View?,
        useTransparentNavigation: Boolean,
        useTopSearchMenu: Boolean,
    ) {
        this.mainCoordinatorLayout = mainCoordinatorLayout
        this.nestedView = nestedView
        this.useTransparentNavigation = useTransparentNavigation
        this.useTopSearchMenu = useTopSearchMenu
        handleNavigationAndScrolling()

        val backgroundColor = getProperBackgroundColor()
        updateStatusBarColor(backgroundColor)
        updateActionbarColor(backgroundColor)
    }

    private fun handleNavigationAndScrolling() {
        if (useTransparentNavigation) {
            if (navigationBarHeight > 0 || isUsingGestureNavigation()) {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility.addBit(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                updateTopBottomInsets(statusBarHeight, navigationBarHeight)
                // Don't touch this. Window Inset API often has a domino effect and things will most likely break.
                onApplyWindowInsets {
                    val insets =
                        it.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
                    updateTopBottomInsets(insets.top, insets.bottom)
                }
            } else {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility.removeBit(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                updateTopBottomInsets(0, 0)
            }
        }
    }

    private fun updateTopBottomInsets(top: Int, bottom: Int) {
        nestedView?.run {
            setPadding(paddingLeft, paddingTop, paddingRight, bottom)
        }
        (mainCoordinatorLayout?.layoutParams as? FrameLayout.LayoutParams)?.topMargin = top
    }

    // colorize the top toolbar and statusbar at scrolling down a bit
    fun setupMaterialScrollListener(scrollingView: ScrollingView?, toolbar: Toolbar) {
        this.scrollingView = scrollingView
        this.toolbar = toolbar
        if (scrollingView is RecyclerView) {
            scrollingView.setOnScrollChangeListener { _, _, _, _, _ ->
                val newScrollY = scrollingView.computeVerticalScrollOffset()
                scrollingChanged(newScrollY, currentScrollY)
                currentScrollY = newScrollY
            }
        } else if (scrollingView is NestedScrollView) {
            scrollingView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                scrollingChanged(scrollY, oldScrollY)
            }
        }
    }

    private fun scrollingChanged(newScrollY: Int, oldScrollY: Int) {
        if (newScrollY > 0 && oldScrollY == 0) {
            val colorFrom = window.statusBarColor
            val colorTo = getColoredMaterialStatusBarColor()
            animateTopBarColors(colorFrom, colorTo)
        } else if (newScrollY == 0 && oldScrollY > 0) {
            val colorFrom = window.statusBarColor
            val colorTo = getRequiredStatusBarColor()
            animateTopBarColors(colorFrom, colorTo)
        }
    }

    private fun animateTopBarColors(colorFrom: Int, colorTo: Int) {
        if (toolbar == null) {
            return
        }

        materialScrollColorAnimation?.end()
        materialScrollColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        materialScrollColorAnimation!!.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            if (toolbar != null) {
                updateTopBarColors(toolbar!!, color)
            }
        }

        materialScrollColorAnimation!!.start()
    }

    private fun getRequiredStatusBarColor(): Int {
        return if ((scrollingView is RecyclerView || scrollingView is NestedScrollView) && scrollingView?.computeVerticalScrollOffset() == 0) {
            getProperBackgroundColor()
        } else {
            getColoredMaterialStatusBarColor()
        }
    }

    fun updateTopBarColors(toolbar: Toolbar, color: Int) {
        val contrastColor = if (useTopSearchMenu) {
            getProperBackgroundColor().getContrastColor()
        } else {
            color.getContrastColor()
        }

        if (!useTopSearchMenu) {
            updateStatusBarColor(color)
            toolbar.setBackgroundColor(color)
            toolbar.setTitleTextColor(contrastColor)
            toolbar.navigationIcon?.applyColorFilter(contrastColor)
            toolbar.collapseIcon = resources.getColoredDrawableWithColor(
                R.drawable.ic_arrow_left_vector,
                contrastColor
            )
        }

        toolbar.overflowIcon =
            resources.getColoredDrawableWithColor(R.drawable.ic_three_dots_vector, contrastColor)

        val menu = toolbar.menu
        for (i in 0 until menu.size()) {
            try {
                menu.getItem(i)?.icon?.setTint(contrastColor)
            } catch (ignored: Exception) {
            }
        }
    }

    fun updateStatusBarOnPageChange() {
        if (scrollingView is RecyclerView || scrollingView is NestedScrollView) {
            val scrollY = scrollingView!!.computeVerticalScrollOffset()
            val colorFrom = window.statusBarColor
            val colorTo = if (scrollY > 0) {
                getColoredMaterialStatusBarColor()
            } else {
                getRequiredStatusBarColor()
            }
            animateTopBarColors(colorFrom, colorTo)
            currentScrollY = scrollY
        }
    }

    fun setupToolbar(
        toolbar: Toolbar,
        toolbarNavigationIcon: NavigationIcon = NavigationIcon.None,
        statusBarColor: Int = getRequiredStatusBarColor(),
        searchMenuItem: MenuItem? = null
    ) {
        val contrastColor = statusBarColor.getContrastColor()
        if (toolbarNavigationIcon != NavigationIcon.None) {
            val drawableId =
                if (toolbarNavigationIcon == NavigationIcon.Cross) R.drawable.ic_cross_vector else R.drawable.ic_arrow_left_vector
            toolbar.navigationIcon =
                resources.getColoredDrawableWithColor(drawableId, contrastColor)
            toolbar.setNavigationContentDescription(toolbarNavigationIcon.accessibilityResId)
        }

        toolbar.setNavigationOnClickListener {
            hideKeyboard()
            finish()
        }

        updateTopBarColors(toolbar, statusBarColor)

        if (!useTopSearchMenu) {
            searchMenuItem?.actionView?.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
                ?.apply {
                    applyColorFilter(contrastColor)
                }

            searchMenuItem?.actionView?.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                ?.apply {
                    setTextColor(contrastColor)
                    setHintTextColor(contrastColor.adjustAlpha(MEDIUM_ALPHA))
                    hint = "${getString(R.string.search)}…"

                    if (isQPlus()) {
                        textCursorDrawable = null
                    }
                }

            // search underline
            searchMenuItem?.actionView?.findViewById<View>(androidx.appcompat.R.id.search_plate)
                ?.apply {
                    background.setColorFilter(contrastColor, PorterDuff.Mode.MULTIPLY)
                }
        }
    }

    private fun updateRecentsAppIcon() {
        if (baseConfig.isUsingModifiedAppIcon) {
            val appIconIDs = getAppIconIDs()
            val currentAppIconColorIndex = getCurrentAppIconColorIndex()
            if (appIconIDs.size - 1 < currentAppIconColorIndex) {
                return
            }

            val recentsIcon =
                BitmapFactory.decodeResource(resources, appIconIDs[currentAppIconColorIndex])
            val title = getAppLauncherName()
            val color = baseConfig.primaryColor

            val description = ActivityManager.TaskDescription(title, recentsIcon, color)
            setTaskDescription(description)
        }
    }

    fun updateMenuItemColors(
        menu: Menu?,
        baseColor: Int = getProperStatusBarColor(),
        forceWhiteIcons: Boolean = false
    ) {
        if (menu == null) {
            return
        }

        var color = baseColor.getContrastColor()
        if (forceWhiteIcons) {
            color = Color.WHITE
        }

        for (i in 0 until menu.size()) {
            try {
                menu.getItem(i)?.icon?.setTint(color)
            } catch (ignored: Exception) {
            }
        }
    }

    private fun getCurrentAppIconColorIndex(): Int {
        val appIconColor = baseConfig.appIconColor
        getAppIconColors().forEachIndexed { index, color ->
            if (color == appIconColor) {
                return index
            }
        }
        return 0
    }

    fun setTranslucentNavigation() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        val partition = try {
            checkedDocumentPath.substring(9, 18)
        } catch (e: Exception) {
            ""
        }

        val sdOtgPattern = Pattern.compile(SD_OTG_SHORT)
        if (requestCode == CREATE_DOCUMENT_SDK_30) {
            if (resultCode == Activity.RESULT_OK && resultData != null && resultData.data != null) {

                val treeUri = resultData.data
                val checkedUri = buildDocumentUriSdk30(checkedDocumentPath)

                if (treeUri != checkedUri) {
                    toast(getString(R.string.wrong_folder_selected, checkedDocumentPath))
                    return
                }

                val takeFlags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                applicationContext.contentResolver.takePersistableUriPermission(treeUri, takeFlags)
                val funAfter = funAfterSdk30Action
                funAfterSdk30Action = null
                funAfter?.invoke(true)
            } else {
                funAfterSdk30Action?.invoke(false)
            }

        } else if (requestCode == OPEN_DOCUMENT_TREE_FOR_SDK_30) {
            if (resultCode == Activity.RESULT_OK && resultData != null && resultData.data != null) {
                val treeUri = resultData.data
                val checkedUri = createFirstParentTreeUri(checkedDocumentPath)

                if (treeUri != checkedUri) {
                    val level = getFirstParentLevel(checkedDocumentPath)
                    val firstParentPath = checkedDocumentPath.getFirstParentPath(this, level)
                    toast(getString(R.string.wrong_folder_selected, humanizePath(firstParentPath)))
                    return
                }

                val takeFlags =
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                applicationContext.contentResolver.takePersistableUriPermission(treeUri, takeFlags)
                val funAfter = funAfterSdk30Action
                funAfterSdk30Action = null
                funAfter?.invoke(true)
            } else {
                funAfterSdk30Action?.invoke(false)
            }

        } else if (requestCode == OPEN_DOCUMENT_TREE_FOR_ANDROID_DATA_OR_OBB) {
            if (resultCode == Activity.RESULT_OK && resultData != null && resultData.data != null) {
                if (isProperAndroidRoot(checkedDocumentPath, resultData.data!!)) {
                    if (resultData.dataString == baseConfig.otgTreeUri || resultData.dataString == baseConfig.sdTreeUri) {
                        val pathToSelect = createAndroidDataOrObbPath(checkedDocumentPath)
                        toast(getString(R.string.wrong_folder_selected, pathToSelect))
                        return
                    }

                    val treeUri = resultData.data
                    storeAndroidTreeUri(checkedDocumentPath, treeUri.toString())

                    val takeFlags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    applicationContext.contentResolver.takePersistableUriPermission(
                        treeUri!!,
                        takeFlags
                    )
                    funAfterSAFPermission?.invoke(true)
                    funAfterSAFPermission = null
                } else {
                    toast(
                        getString(
                            R.string.wrong_folder_selected,
                            createAndroidDataOrObbPath(checkedDocumentPath)
                        )
                    )
                    Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                        if (isRPlus()) {
                            putExtra(
                                DocumentsContract.EXTRA_INITIAL_URI,
                                createAndroidDataOrObbUri(checkedDocumentPath)
                            )
                        }

                        try {
                            startActivityForResult(this, requestCode)
                        } catch (e: Exception) {
                            showErrorToast(e)
                        }
                    }
                }
            } else {
                funAfterSAFPermission?.invoke(false)
            }
        } else if (requestCode == OPEN_DOCUMENT_TREE_SD) {
            if (resultCode == Activity.RESULT_OK && resultData != null && resultData.data != null) {
                val isProperPartition = partition.isEmpty() || !sdOtgPattern.matcher(partition)
                    .matches() || (sdOtgPattern.matcher(partition)
                    .matches() && resultData.dataString!!.contains(partition))
                if (isProperSDRootFolder(resultData.data!!) && isProperPartition) {
                    if (resultData.dataString == baseConfig.otgTreeUri) {
                        toast(R.string.sd_card_usb_same)
                        return
                    }

                    saveTreeUri(resultData)
                    funAfterSAFPermission?.invoke(true)
                    funAfterSAFPermission = null
                } else {
                    toast(R.string.wrong_root_selected)
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

                    try {
                        startActivityForResult(intent, requestCode)
                    } catch (e: Exception) {
                        showErrorToast(e)
                    }
                }
            } else {
                funAfterSAFPermission?.invoke(false)
            }
        } else if (requestCode == OPEN_DOCUMENT_TREE_OTG) {
            if (resultCode == Activity.RESULT_OK && resultData != null && resultData.data != null) {
                val isProperPartition = partition.isEmpty() || !sdOtgPattern.matcher(partition)
                    .matches() || (sdOtgPattern.matcher(partition)
                    .matches() && resultData.dataString!!.contains(partition))
                if (isProperOTGRootFolder(resultData.data!!) && isProperPartition) {
                    if (resultData.dataString == baseConfig.sdTreeUri) {
                        funAfterSAFPermission?.invoke(false)
                        toast(R.string.sd_card_usb_same)
                        return
                    }
                    baseConfig.otgTreeUri = resultData.dataString!!
                    baseConfig.otgPartition =
                        baseConfig.otgTreeUri.removeSuffix("%3A").substringAfterLast('/')
                            .trimEnd('/')
                    updateOTGPathFromPartition()

                    val takeFlags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    applicationContext.contentResolver.takePersistableUriPermission(
                        resultData.data!!,
                        takeFlags
                    )

                    funAfterSAFPermission?.invoke(true)
                    funAfterSAFPermission = null
                } else {
                    toast(R.string.wrong_root_selected_usb)
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

                    try {
                        startActivityForResult(intent, requestCode)
                    } catch (e: Exception) {
                        showErrorToast(e)
                    }
                }
            } else {
                funAfterSAFPermission?.invoke(false)
            }
        } else if (requestCode == SELECT_EXPORT_SETTINGS_FILE_INTENT && resultCode == Activity.RESULT_OK && resultData != null && resultData.data != null) {
            val outputStream = contentResolver.openOutputStream(resultData.data!!)
            exportSettingsTo(outputStream, configItemsToExport)
        } else if (requestCode == DELETE_FILE_SDK_30_HANDLER) {
            funAfterSdk30Action?.invoke(resultCode == Activity.RESULT_OK)
        } else if (requestCode == RECOVERABLE_SECURITY_HANDLER) {
            funRecoverableSecurity?.invoke(resultCode == Activity.RESULT_OK)
            funRecoverableSecurity = null
        } else if (requestCode == UPDATE_FILE_SDK_30_HANDLER) {
            funAfterUpdate30File?.invoke(resultCode == Activity.RESULT_OK)
        } else if (requestCode == MANAGE_MEDIA_RC) {
            funAfterManageMediaPermission?.invoke()
        } else if (requestCode == TRASH_FILE_SDK_30_HANDLER) {
            funAfterTrash30File?.invoke(resultCode == Activity.RESULT_OK)
        }
    }

    private fun saveTreeUri(resultData: Intent) {
        val treeUri = resultData.data
        baseConfig.sdTreeUri = treeUri.toString()

        val takeFlags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        applicationContext.contentResolver.takePersistableUriPermission(treeUri!!, takeFlags)
    }

    private fun isProperSDRootFolder(uri: Uri) =
        isExternalStorageDocument(uri) && isRootUri(uri) && !isInternalStorage(uri)

    private fun isProperSDFolder(uri: Uri) =
        isExternalStorageDocument(uri) && !isInternalStorage(uri)

    private fun isProperOTGRootFolder(uri: Uri) =
        isExternalStorageDocument(uri) && isRootUri(uri) && !isInternalStorage(uri)

    private fun isProperOTGFolder(uri: Uri) =
        isExternalStorageDocument(uri) && !isInternalStorage(uri)

    private fun isRootUri(uri: Uri) = uri.lastPathSegment?.endsWith(":") ?: false

    private fun isInternalStorage(uri: Uri) =
        isExternalStorageDocument(uri) && DocumentsContract.getTreeDocumentId(uri)
            .contains("primary")

    private fun isAndroidDir(uri: Uri) =
        isExternalStorageDocument(uri) && DocumentsContract.getTreeDocumentId(uri)
            .contains(":Android")

    private fun isInternalStorageAndroidDir(uri: Uri) = isInternalStorage(uri) && isAndroidDir(uri)
    private fun isOTGAndroidDir(uri: Uri) = isProperOTGFolder(uri) && isAndroidDir(uri)
    private fun isSDAndroidDir(uri: Uri) = isProperSDFolder(uri) && isAndroidDir(uri)
    private fun isExternalStorageDocument(uri: Uri) =
        EXTERNAL_STORAGE_PROVIDER_AUTHORITY == uri.authority

    private fun isProperAndroidRoot(path: String, uri: Uri): Boolean {
        return when {
            isPathOnOTG(path) -> isOTGAndroidDir(uri)
            isPathOnSD(path) -> isSDAndroidDir(uri)
            else -> isInternalStorageAndroidDir(uri)
        }
    }

    fun startAboutActivity(
        appNameId: Int,
        licenseMask: Long,
        versionName: String,
        faqItems: ArrayList<FAQItem>,
        showFAQBeforeMail: Boolean
    ) {
        hideKeyboard()
        Intent(applicationContext, AboutActivity::class.java).apply {
            putExtra(APP_ICON_IDS, getAppIconIDs())
            putExtra(APP_LAUNCHER_NAME, getAppLauncherName())
            putExtra(APP_NAME, getString(appNameId))
            putExtra(APP_LICENSES, licenseMask)
            putExtra(APP_VERSION_NAME, versionName)
            putExtra(APP_FAQ, faqItems)
            putExtra(SHOW_FAQ_BEFORE_MAIL, showFAQBeforeMail)
            startActivity(this)
        }
    }

    fun startCustomizationActivity() {
        if (!packageName.contains("slootelibomelpmis".reversed(), true)) {
            if (baseConfig.appRunCount > 100) {
                val label =
                    "You are using a fake version of the app. For your own safety download the original one from www.simplemobiletools.com. Thanks"
                ConfirmationDialog(this, label, positive = R.string.ok, negative = 0) {
                    launchViewIntent(DEVELOPER_PLAY_STORE_URL)
                }
                return
            }
        }

        Intent(applicationContext, CustomizationActivity::class.java).apply {
            putExtra(APP_ICON_IDS, getAppIconIDs())
            putExtra(APP_LAUNCHER_NAME, getAppLauncherName())
            startActivity(this)
        }
    }

    fun handleCustomizeColorsClick() {
        if (isOrWasThankYouInstalled()) {
            startCustomizationActivity()
        } else {
            FeatureLockedDialog(this) {}
        }
    }

    fun launchCustomizeNotificationsIntent() {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(this)
        }
    }

    fun launchChangeAppLanguageIntent() {
        try {
            Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                startActivity(this)
            }
        } catch (e: Exception) {
            openDeviceSettings()
        }
    }

    // synchronous return value determines only if we are showing the SAF dialog, callback result tells if the SD or OTG permission has been granted
    fun handleSAFDialog(path: String, callback: (success: Boolean) -> Unit): Boolean {
        hideKeyboard()
        return if (!packageName.startsWith("com.simplemobiletools")) {
            callback(true)
            false
        } else if (isShowingSAFDialog(path) || isShowingOTGDialog(path)) {
            funAfterSAFPermission = callback
            true
        } else {
            callback(true)
            false
        }
    }

    fun handleSAFDialogSdk30(path: String, callback: (success: Boolean) -> Unit): Boolean {
        hideKeyboard()
        return if (!packageName.startsWith("com.simplemobiletools")) {
            callback(true)
            false
        } else if (isShowingSAFDialogSdk30(path)) {
            funAfterSdk30Action = callback
            true
        } else {
            callback(true)
            false
        }
    }

    private fun checkManageMediaOrHandleSAFDialogSdk30(
        path: String,
        callback: (success: Boolean) -> Unit
    ): Boolean {
        hideKeyboard()
        return if (canManageMedia()) {
            callback(true)
            false
        } else {
            handleSAFDialogSdk30(path, callback)
        }
    }

    fun handleSAFCreateDocumentDialogSdk30(
        path: String,
        callback: (success: Boolean) -> Unit
    ): Boolean {
        hideKeyboard()
        return if (!packageName.startsWith("com.simplemobiletools")) {
            callback(true)
            false
        } else if (isShowingSAFCreateDocumentDialogSdk30(path)) {
            funAfterSdk30Action = callback
            true
        } else {
            callback(true)
            false
        }
    }

    fun handleAndroidSAFDialog(path: String, callback: (success: Boolean) -> Unit): Boolean {
        hideKeyboard()
        return if (!packageName.startsWith("com.simplemobiletools")) {
            callback(true)
            false
        } else if (isShowingAndroidSAFDialog(path)) {
            funAfterSAFPermission = callback
            true
        } else {
            callback(true)
            false
        }
    }

    fun handleOTGPermission(callback: (success: Boolean) -> Unit) {
        hideKeyboard()
        if (baseConfig.otgTreeUri.isNotEmpty()) {
            callback(true)
            return
        }

        funAfterSAFPermission = callback
        WritePermissionDialog(this, WritePermissionDialogMode.Otg) {
            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                try {
                    startActivityForResult(this, OPEN_DOCUMENT_TREE_OTG)
                    return@apply
                } catch (e: Exception) {
                    type = "*/*"
                }

                try {
                    startActivityForResult(this, OPEN_DOCUMENT_TREE_OTG)
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.system_service_disabled, Toast.LENGTH_LONG)
                } catch (e: Exception) {
                    toast(R.string.unknown_error_occurred)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    fun deleteSDK30Uris(uris: List<Uri>, callback: (success: Boolean) -> Unit) {
        hideKeyboard()
        if (isRPlus()) {
            funAfterSdk30Action = callback
            try {
                val deleteRequest =
                    MediaStore.createDeleteRequest(contentResolver, uris).intentSender
                startIntentSenderForResult(deleteRequest, DELETE_FILE_SDK_30_HANDLER, null, 0, 0, 0)
            } catch (e: Exception) {
                showErrorToast(e)
            }
        } else {
            callback(false)
        }
    }

    @SuppressLint("NewApi")
    fun trashSDK30Uris(uris: List<Uri>, toTrash: Boolean, callback: (success: Boolean) -> Unit) {
        hideKeyboard()
        if (isRPlus()) {
            funAfterTrash30File = callback
            try {
                val trashRequest =
                    MediaStore.createTrashRequest(contentResolver, uris, toTrash).intentSender
                startIntentSenderForResult(trashRequest, TRASH_FILE_SDK_30_HANDLER, null, 0, 0, 0)
            } catch (e: Exception) {
                showErrorToast(e)
            }
        } else {
            callback(false)
        }
    }

    @SuppressLint("NewApi")
    fun updateSDK30Uris(uris: List<Uri>, callback: (success: Boolean) -> Unit) {
        hideKeyboard()
        if (isRPlus()) {
            funAfterUpdate30File = callback
            try {
                val writeRequest = MediaStore.createWriteRequest(contentResolver, uris).intentSender
                startIntentSenderForResult(writeRequest, UPDATE_FILE_SDK_30_HANDLER, null, 0, 0, 0)
            } catch (e: Exception) {
                showErrorToast(e)
            }
        } else {
            callback(false)
        }
    }

    @SuppressLint("NewApi")
    fun handleRecoverableSecurityException(callback: (success: Boolean) -> Unit) {
        try {
            callback.invoke(true)
        } catch (securityException: SecurityException) {
            if (isQPlus()) {
                funRecoverableSecurity = callback
                val recoverableSecurityException =
                    securityException as? RecoverableSecurityException ?: throw securityException
                val intentSender = recoverableSecurityException.userAction.actionIntent.intentSender
                startIntentSenderForResult(
                    intentSender,
                    RECOVERABLE_SECURITY_HANDLER,
                    null,
                    0,
                    0,
                    0
                )
            } else {
                callback(false)
            }
        }
    }

    fun launchMediaManagementIntent(callback: () -> Unit) {
        Intent(Settings.ACTION_REQUEST_MANAGE_MEDIA).apply {
            data = Uri.parse("package:$packageName")
            try {
                startActivityForResult(this, MANAGE_MEDIA_RC)
            } catch (e: Exception) {
                showErrorToast(e)
            }
        }
        funAfterManageMediaPermission = callback
    }

    fun copyMoveFilesTo(
        fileDirItems: ArrayList<FileDirItem>,
        source: String,
        destination: String,
        isCopyOperation: Boolean,
        copyPhotoVideoOnly: Boolean,
        copyHidden: Boolean,
        callback: (destinationPath: String) -> Unit
    ) {
        if (source == destination) {
            toast(R.string.source_and_destination_same)
            return
        }

        if (!getDoesFilePathExist(destination)) {
            toast(R.string.invalid_destination)
            return
        }

        handleSAFDialog(destination) {
            if (!it) {
                copyMoveListener.copyFailed()
                return@handleSAFDialog
            }

            handleSAFDialogSdk30(destination) {
                if (!it) {
                    copyMoveListener.copyFailed()
                    return@handleSAFDialogSdk30
                }

                copyMoveCallback = callback
                var fileCountToCopy = fileDirItems.size
                if (isCopyOperation) {
                    val recycleBinPath = fileDirItems.first().isRecycleBinPath(this)
                    if (canManageMedia() && !recycleBinPath) {
                        val fileUris = getFileUrisFromFileDirItems(fileDirItems)
                        updateSDK30Uris(fileUris) { sdk30UriSuccess ->
                            if (sdk30UriSuccess) {
                                startCopyMove(
                                    fileDirItems,
                                    destination,
                                    isCopyOperation,
                                    copyPhotoVideoOnly,
                                    copyHidden
                                )
                            }
                        }
                    } else {
                        startCopyMove(
                            fileDirItems,
                            destination,
                            isCopyOperation,
                            copyPhotoVideoOnly,
                            copyHidden
                        )
                    }
                } else {
                    if (isPathOnOTG(source) || isPathOnOTG(destination) || isPathOnSD(source) || isPathOnSD(
                            destination
                        ) ||
                        isRestrictedSAFOnlyRoot(source) || isRestrictedSAFOnlyRoot(destination) ||
                        isAccessibleWithSAFSdk30(source) || isAccessibleWithSAFSdk30(destination) ||
                        fileDirItems.first().isDirectory
                    ) {
                        handleSAFDialog(source) { safSuccess ->
                            if (safSuccess) {
                                val recycleBinPath = fileDirItems.first().isRecycleBinPath(this)
                                if (canManageMedia() && !recycleBinPath) {
                                    val fileUris = getFileUrisFromFileDirItems(fileDirItems)
                                    updateSDK30Uris(fileUris) { sdk30UriSuccess ->
                                        if (sdk30UriSuccess) {
                                            startCopyMove(
                                                fileDirItems,
                                                destination,
                                                isCopyOperation,
                                                copyPhotoVideoOnly,
                                                copyHidden
                                            )
                                        }
                                    }
                                } else {
                                    startCopyMove(
                                        fileDirItems,
                                        destination,
                                        isCopyOperation,
                                        copyPhotoVideoOnly,
                                        copyHidden
                                    )
                                }
                            }
                        }
                    } else {
                        try {
                            checkConflicts(fileDirItems, destination, 0, LinkedHashMap()) {
                                toast(R.string.moving)
                                ensureBackgroundThread {
                                    val updatedPaths = ArrayList<String>(fileDirItems.size)
                                    val destinationFolder = File(destination)
                                    for (oldFileDirItem in fileDirItems) {
                                        var newFile = File(destinationFolder, oldFileDirItem.name)
                                        if (newFile.exists()) {
                                            when {
                                                getConflictResolution(
                                                    it,
                                                    newFile.absolutePath
                                                ) == CONFLICT_SKIP -> fileCountToCopy--

                                                getConflictResolution(
                                                    it,
                                                    newFile.absolutePath
                                                ) == CONFLICT_KEEP_BOTH -> newFile =
                                                    getAlternativeFile(newFile)

                                                else ->
                                                    // this file is guaranteed to be on the internal storage, so just delete it this way
                                                    newFile.delete()
                                            }
                                        }

                                        if (!newFile.exists() && File(oldFileDirItem.path).renameTo(
                                                newFile
                                            )
                                        ) {
                                            if (!baseConfig.keepLastModified) {
                                                newFile.setLastModified(System.currentTimeMillis())
                                            }
                                            updatedPaths.add(newFile.absolutePath)
                                            deleteFromMediaStore(oldFileDirItem.path)
                                        }
                                    }

                                    runOnUiThread {
                                        if (updatedPaths.isEmpty()) {
                                            copyMoveListener.copySucceeded(
                                                false,
                                                fileCountToCopy == 0,
                                                destination,
                                                false
                                            )
                                        } else {
                                            copyMoveListener.copySucceeded(
                                                false,
                                                fileCountToCopy <= updatedPaths.size,
                                                destination,
                                                updatedPaths.size == 1
                                            )
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            showErrorToast(e)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun getAlternativeFile(file: File): File {
        var fileIndex = 1
        var newFile: File?
        do {
            val newName =
                String.format("%s(%d).%s", file.nameWithoutExtension, fileIndex, file.extension)
            newFile = File(file.parent, newName)
            fileIndex++
        } while (getDoesFilePathExist(newFile!!.absolutePath))
        return newFile
    }

    private fun startCopyMove(
        files: ArrayList<FileDirItem>,
        destinationPath: String,
        isCopyOperation: Boolean,
        copyPhotoVideoOnly: Boolean,
        copyHidden: Boolean
    ) {
        val availableSpace = destinationPath.getAvailableStorageB()
        val sumToCopy = files.sumByLong { it.getProperSize(applicationContext, copyHidden) }
        if (availableSpace == -1L || sumToCopy < availableSpace) {
            checkConflicts(files, destinationPath, 0, LinkedHashMap()) {
                toast(if (isCopyOperation) R.string.copying else R.string.moving)
                val pair = Pair(files, destinationPath)
                handleNotificationPermission { granted ->
                    if (granted) {
                        CopyMoveTask(
                            this,
                            isCopyOperation,
                            copyPhotoVideoOnly,
                            it,
                            copyMoveListener,
                            copyHidden
                        ).execute(pair)
                    } else {
                        PermissionRequiredDialog(
                            this,
                            R.string.allow_notifications_files,
                            { openNotificationSettings() })
                    }
                }
            }
        } else {
            val text = String.format(
                getString(R.string.no_space),
                sumToCopy.formatSize(),
                availableSpace.formatSize()
            )
            toast(text, Toast.LENGTH_LONG)
        }
    }

    private fun checkConflicts(
        files: ArrayList<FileDirItem>,
        destinationPath: String,
        index: Int,
        conflictResolutions: LinkedHashMap<String, Int>,
        callback: (resolutions: LinkedHashMap<String, Int>) -> Unit
    ) {
        if (index == files.size) {
            callback(conflictResolutions)
            return
        }

        val file = files[index]
        val newFileDirItem =
            FileDirItem("$destinationPath/${file.name}", file.name, file.isDirectory)
        ensureBackgroundThread {
            if (getDoesFilePathExist(newFileDirItem.path)) {
                runOnUiThread {
                    FileConflictDialog(
                        this,
                        newFileDirItem,
                        files.size > 1
                    ) { resolution, applyForAll ->
                        if (applyForAll) {
                            conflictResolutions.clear()
                            conflictResolutions[""] = resolution
                            checkConflicts(
                                files,
                                destinationPath,
                                files.size,
                                conflictResolutions,
                                callback
                            )
                        } else {
                            conflictResolutions[newFileDirItem.path] = resolution
                            checkConflicts(
                                files,
                                destinationPath,
                                index + 1,
                                conflictResolutions,
                                callback
                            )
                        }
                    }
                }
            } else {
                runOnUiThread {
                    checkConflicts(files, destinationPath, index + 1, conflictResolutions, callback)
                }
            }
        }
    }

    fun handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
        actionOnPermission = null
        if (hasPermission(permissionId)) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(
                this,
                arrayOf(getPermissionString(permissionId)),
                GENERIC_PERM_HANDLER
            )
        }
    }

    fun handlePartialMediaPermissions(
        permissionIds: Collection<Int>,
        force: Boolean = false,
        callback: (granted: Boolean) -> Unit
    ) {
        actionOnPermission = null
        if (isUpsideDownCakePlus()) {
            if (hasPermission(PERMISSION_READ_MEDIA_VISUAL_USER_SELECTED) && !force) {
                callback(true)
            } else {
                isAskingPermissions = true
                actionOnPermission = callback
                ActivityCompat.requestPermissions(
                    this,
                    permissionIds.map { getPermissionString(it) }.toTypedArray(),
                    GENERIC_PERM_HANDLER
                )
            }
        } else {
            if (hasAllPermissions(permissionIds)) {
                callback(true)
            } else {
                isAskingPermissions = true
                actionOnPermission = callback
                ActivityCompat.requestPermissions(
                    this,
                    permissionIds.map { getPermissionString(it) }.toTypedArray(),
                    GENERIC_PERM_HANDLER
                )
            }
        }
    }

    private fun handleNotificationPermission(callback: (granted: Boolean) -> Unit) {
        if (!isTiramisuPlus()) {
            callback(true)
        } else {
            handlePermission(PERMISSION_POST_NOTIFICATIONS) { granted ->
                callback(granted)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isAskingPermissions = false
        if (requestCode == GENERIC_PERM_HANDLER && grantResults.isNotEmpty()) {
            actionOnPermission?.invoke(grantResults[0] == 0)
        }
    }

    private val copyMoveListener = object : CopyMoveListener {
        override fun copySucceeded(
            copyOnly: Boolean,
            copiedAll: Boolean,
            destinationPath: String,
            wasCopyingOneFileOnly: Boolean
        ) {
            if (copyOnly) {
                toast(
                    if (copiedAll) {
                        if (wasCopyingOneFileOnly) {
                            R.string.copying_success_one
                        } else {
                            R.string.copying_success
                        }
                    } else {
                        R.string.copying_success_partial
                    }
                )
            } else {
                toast(
                    if (copiedAll) {
                        if (wasCopyingOneFileOnly) {
                            R.string.moving_success_one
                        } else {
                            R.string.moving_success
                        }
                    } else {
                        R.string.moving_success_partial
                    }
                )
            }

            copyMoveCallback?.invoke(destinationPath)
            copyMoveCallback = null
        }

        override fun copyFailed() {
            toast(R.string.copy_move_failed)
            copyMoveCallback = null
        }
    }

    fun checkAppOnSDCard() {
        if (!baseConfig.wasAppOnSDShown && isAppInstalledOnSDCard()) {
            baseConfig.wasAppOnSDShown = true
            ConfirmationDialog(this, "", R.string.app_on_sd_card, R.string.ok, 0) {}
        }
    }


    fun exportSettings(configItems: LinkedHashMap<String, Any>) {
        if (isQPlus()) {
            configItemsToExport = configItems
            ExportSettingsDialog(this, getExportSettingsFilename(), true) { _, filename ->
                Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, filename)
                    addCategory(Intent.CATEGORY_OPENABLE)

                    try {
                        startActivityForResult(this, SELECT_EXPORT_SETTINGS_FILE_INTENT)
                    } catch (e: ActivityNotFoundException) {
                        toast(R.string.system_service_disabled, Toast.LENGTH_LONG)
                    } catch (e: Exception) {
                        showErrorToast(e)
                    }
                }
            }
        } else {
            handlePermission(PERMISSION_WRITE_STORAGE) {
                if (it) {
                    ExportSettingsDialog(
                        this,
                        getExportSettingsFilename(),
                        false
                    ) { path, _ ->
                        val file = File(path)
                        getFileOutputStream(file.toFileDirItem(this), true) {
                            exportSettingsTo(it, configItems)
                        }
                    }
                }
            }
        }
    }

    private fun exportSettingsTo(
        outputStream: OutputStream?,
        configItems: LinkedHashMap<String, Any>
    ) {
        if (outputStream == null) {
            toast(R.string.unknown_error_occurred)
            return
        }

        ensureBackgroundThread {
            outputStream.bufferedWriter().use { out ->
                for ((key, value) in configItems) {
                    out.writeLn("$key=$value")
                }
            }

            toast(R.string.settings_exported_successfully)
        }
    }

    private fun getExportSettingsFilename(): String {
        val appName = baseConfig.appId.removeSuffix(".debug").removeSuffix(".pro")
            .removePrefix("com.simplemobiletools.")
        return "$appName-settings_${getCurrentFormattedDateTime()}"
    }

    @SuppressLint("InlinedApi")
    protected fun launchSetDefaultDialerIntent() {
        if (isQPlus()) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager!!.isRoleAvailable(RoleManager.ROLE_DIALER) && !roleManager.isRoleHeld(
                    RoleManager.ROLE_DIALER
                )
            ) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
            }
        } else {
            Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(
                TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                packageName
            ).apply {
                try {
                    startActivityForResult(this, REQUEST_CODE_SET_DEFAULT_DIALER)
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.no_app_found)
                } catch (e: Exception) {
                    showErrorToast(e)
                }
            }
        }
    }

    fun setDefaultCallerIdApp() {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) && !roleManager.isRoleHeld(
                RoleManager.ROLE_CALL_SCREENING
            )
        ) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_CALLER_ID)
        }
    }


    private fun isShowingSAFDialog(path: String): Boolean {
        return if ((!isRPlus() && isPathOnSD(path) && !isSDCardSetAsDefaultStorage() && (baseConfig.sdTreeUri.isEmpty() || !hasProperStoredTreeUri(
                false
            )))
        ) {
            runOnUiThread {
                if (!isDestroyed && !isFinishing) {
                    WritePermissionDialog(
                        this,
                        WritePermissionDialogMode.SdCard
                    ) {
                        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                            putExtra(EXTRA_SHOW_ADVANCED, true)
                            try {
                                startActivityForResult(this, OPEN_DOCUMENT_TREE_SD)
                                checkedDocumentPath = path
                                return@apply
                            } catch (e: Exception) {
                                type = "*/*"
                            }

                            try {
                                startActivityForResult(this, OPEN_DOCUMENT_TREE_SD)
                                checkedDocumentPath = path
                            } catch (e: ActivityNotFoundException) {
                                toast(R.string.system_service_disabled, Toast.LENGTH_LONG)
                            } catch (e: Exception) {
                                toast(R.string.unknown_error_occurred)
                            }
                        }
                    }
                }
            }
            true
        } else {
            false
        }
    }

    @SuppressLint("InlinedApi")
    fun isShowingSAFDialogSdk30(path: String): Boolean {
        return if (isAccessibleWithSAFSdk30(path) && !hasProperStoredFirstParentUri(path)) {
            runOnUiThread {
                if (!isDestroyed && !isFinishing) {
                    val level = getFirstParentLevel(path)
                    WritePermissionDialog(
                        this,
                        WritePermissionDialogMode.OpenDocumentTreeSDK30(
                            path.getFirstParentPath(
                                this,
                                level
                            )
                        )
                    ) {
                        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                            putExtra(EXTRA_SHOW_ADVANCED, true)
                            putExtra(
                                DocumentsContract.EXTRA_INITIAL_URI,
                                createFirstParentTreeUriUsingRootTree(path)
                            )
                            try {
                                startActivityForResult(this, OPEN_DOCUMENT_TREE_FOR_SDK_30)
                                checkedDocumentPath = path
                                return@apply
                            } catch (e: Exception) {
                                type = "*/*"
                            }

                            try {
                                startActivityForResult(this, OPEN_DOCUMENT_TREE_FOR_SDK_30)
                                checkedDocumentPath = path
                            } catch (e: ActivityNotFoundException) {
                                toast(R.string.system_service_disabled, Toast.LENGTH_LONG)
                            } catch (e: Exception) {
                                toast(R.string.unknown_error_occurred)
                            }
                        }
                    }
                }
            }
            true
        } else {
            false
        }
    }

    @SuppressLint("InlinedApi")
    fun isShowingSAFCreateDocumentDialogSdk30(path: String): Boolean {
        return if (!hasProperStoredDocumentUriSdk30(path)) {
            runOnUiThread {
                if (!isDestroyed && !isFinishing) {
                    WritePermissionDialog(
                        this,
                        WritePermissionDialogMode.CreateDocumentSDK30
                    ) {
                        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            type = DocumentsContract.Document.MIME_TYPE_DIR
                            putExtra(EXTRA_SHOW_ADVANCED, true)
                            addCategory(Intent.CATEGORY_OPENABLE)
                            putExtra(
                                DocumentsContract.EXTRA_INITIAL_URI,
                                buildDocumentUriSdk30(path.getParentPath())
                            )
                            putExtra(Intent.EXTRA_TITLE, path.getFilenameFromPath())
                            try {
                                startActivityForResult(this, CREATE_DOCUMENT_SDK_30)
                                checkedDocumentPath = path
                                return@apply
                            } catch (e: Exception) {
                                type = "*/*"
                            }

                            try {
                                startActivityForResult(this, CREATE_DOCUMENT_SDK_30)
                                checkedDocumentPath = path
                            } catch (e: ActivityNotFoundException) {
                                toast(R.string.system_service_disabled, Toast.LENGTH_LONG)
                            } catch (e: Exception) {
                                toast(R.string.unknown_error_occurred)
                            }
                        }
                    }
                }
            }
            true
        } else {
            false
        }
    }

    private fun isShowingAndroidSAFDialog(path: String): Boolean {
        return if (isRestrictedSAFOnlyRoot(path) && (getAndroidTreeUri(path).isEmpty() || !hasProperStoredAndroidTreeUri(
                path
            ))
        ) {
            runOnUiThread {
                if (!isDestroyed && !isFinishing) {
                    ConfirmationAdvancedDialog(
                        this,
                        "",
                        R.string.confirm_storage_access_android_text,
                        R.string.ok,
                        R.string.cancel
                    ) { success ->
                        if (success) {
                            Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                                putExtra(EXTRA_SHOW_ADVANCED, true)
                                putExtra(
                                    DocumentsContract.EXTRA_INITIAL_URI,
                                    createAndroidDataOrObbUri(path)
                                )
                                try {
                                    startActivityForResult(
                                        this,
                                        OPEN_DOCUMENT_TREE_FOR_ANDROID_DATA_OR_OBB
                                    )
                                    checkedDocumentPath = path
                                    return@apply
                                } catch (e: Exception) {
                                    type = "*/*"
                                }

                                try {
                                    startActivityForResult(
                                        this,
                                        OPEN_DOCUMENT_TREE_FOR_ANDROID_DATA_OR_OBB
                                    )
                                    checkedDocumentPath = path
                                } catch (e: ActivityNotFoundException) {
                                    toast(R.string.system_service_disabled, Toast.LENGTH_LONG)
                                } catch (e: Exception) {
                                    toast(R.string.unknown_error_occurred)
                                }
                            }
                        }
                    }
                }
            }
            true
        } else {
            false
        }
    }


    private fun isShowingOTGDialog(path: String): Boolean {
        return if (!isRPlus() && isPathOnOTG(path) && (baseConfig.otgTreeUri.isEmpty() || !hasProperStoredTreeUri(
                true
            ))
        ) {
            showOTGPermissionDialog(path)
            true
        } else {
            false
        }
    }


    private fun showOTGPermissionDialog(path: String) {
        runOnUiThread {
            if (!isDestroyed && !isFinishing) {
                WritePermissionDialog(this, WritePermissionDialogMode.Otg) {
                    Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                        try {
                            startActivityForResult(this, OPEN_DOCUMENT_TREE_OTG)
                            checkedDocumentPath = path
                            return@apply
                        } catch (e: Exception) {
                            type = "*/*"
                        }

                        try {
                            startActivityForResult(this, OPEN_DOCUMENT_TREE_OTG)
                            checkedDocumentPath = path
                        } catch (e: ActivityNotFoundException) {
                            toast(R.string.system_service_disabled, Toast.LENGTH_LONG)
                        } catch (e: Exception) {
                            toast(R.string.unknown_error_occurred)
                        }
                    }
                }
            }
        }
    }


    fun deleteFile(
        file: FileDirItem,
        allowDeleteFolder: Boolean = false,
        callback: ((wasSuccess: Boolean) -> Unit)? = null
    ) {
        deleteFiles(arrayListOf(file), allowDeleteFolder, callback)
    }

    private fun copySingleFileSdk30(source: FileDirItem, destination: FileDirItem): Boolean {
        val directory = destination.getParentPath()
        if (!createDirectorySync(directory)) {
            val error = String.format(getString(R.string.could_not_create_folder), directory)
            showErrorToast(error)
            return false
        }

        var inputStream: InputStream? = null
        var out: OutputStream? = null
        try {

            out = getFileOutputStreamSync(destination.path, source.path.getMimeType())
            inputStream = getFileInputStreamSync(source.path)!!

            var copiedSize = 0L
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = inputStream.read(buffer)
            while (bytes >= 0) {
                out!!.write(buffer, 0, bytes)
                copiedSize += bytes
                bytes = inputStream.read(buffer)
            }

            out?.flush()

            return if (source.size == copiedSize && getDoesFilePathExist(destination.path)) {
                if (baseConfig.keepLastModified) {
                    copyOldLastModified(source.path, destination.path)
                    val lastModified = File(source.path).lastModified()
                    if (lastModified != 0L) {
                        File(destination.path).setLastModified(lastModified)
                    }
                }
                true
            } else {
                false
            }
        } finally {
            inputStream?.close()
            out?.close()
        }
    }

    private fun copyOldLastModified(sourcePath: String, destinationPath: String) {
        val projection =
            arrayOf(MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.DATE_MODIFIED)
        val uri = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.MediaColumns.DATA} = ?"
        var selectionArgs = arrayOf(sourcePath)
        val cursor =
            applicationContext.contentResolver.query(
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
                applicationContext.contentResolver.update(uri, values, selection, selectionArgs)
            }
        }
    }


    private fun deleteFiles(
        files: List<FileDirItem>,
        allowDeleteFolder: Boolean = false,
        callback: ((wasSuccess: Boolean) -> Unit)? = null
    ) {
        ensureBackgroundThread {
            deleteFilesBg(files, allowDeleteFolder, callback)
        }
    }


    private fun deleteFilesBg(
        files: List<FileDirItem>,
        allowDeleteFolder: Boolean = false,
        callback: ((wasSuccess: Boolean) -> Unit)? = null
    ) {
        if (files.isEmpty()) {
            runOnUiThread {
                callback?.invoke(true)
            }
            return
        }

        val firstFile = files.first()
        val firstFilePath = firstFile.path
        handleSAFDialog(firstFilePath) {
            if (!it) {
                return@handleSAFDialog
            }

            checkManageMediaOrHandleSAFDialogSdk30(firstFilePath) {
                if (!it) {
                    return@checkManageMediaOrHandleSAFDialogSdk30
                }

                val recycleBinPath = firstFile.isRecycleBinPath(this)
                if (canManageMedia() && !recycleBinPath && !firstFilePath.doesThisOrParentHaveNoMedia(
                        HashMap(), null
                    )
                ) {
                    val fileUris = getFileUrisFromFileDirItems(files)

                    deleteSDK30Uris(fileUris) { success ->
                        runOnUiThread {
                            callback?.invoke(success)
                        }
                    }
                } else {
                    deleteFilesCasual(files, allowDeleteFolder, callback)
                }
            }
        }
    }

    private fun deleteFilesCasual(
        files: List<FileDirItem>,
        allowDeleteFolder: Boolean = false,
        callback: ((wasSuccess: Boolean) -> Unit)? = null
    ) {
        var wasSuccess = false
        val failedFileDirItems = ArrayList<FileDirItem>()
        files.forEachIndexed { index, file ->
            deleteFileBg(file, allowDeleteFolder, true) {
                if (it) {
                    wasSuccess = true
                } else {
                    failedFileDirItems.add(file)
                }

                if (index == files.lastIndex) {
                    if (isRPlus() && failedFileDirItems.isNotEmpty()) {
                        val fileUris = getFileUrisFromFileDirItems(failedFileDirItems)
                        deleteSDK30Uris(fileUris) { success ->
                            runOnUiThread {
                                callback?.invoke(success)
                            }
                        }
                    } else {
                        runOnUiThread {
                            callback?.invoke(wasSuccess)
                        }
                    }
                }
            }
        }
    }

    fun deleteFileBg(
        fileDirItem: FileDirItem,
        allowDeleteFolder: Boolean = false,
        isDeletingMultipleFiles: Boolean,
        callback: ((wasSuccess: Boolean) -> Unit)? = null,
    ) {
        val path = fileDirItem.path
        if (isRestrictedSAFOnlyRoot(path)) {
            deleteAndroidSAFDirectory(path, allowDeleteFolder, callback)
        } else {
            val file = File(path)
            if (!isRPlus() && file.absolutePath.startsWith(internalStoragePath) && !file.canWrite()) {
                callback?.invoke(false)
                return
            }

            var fileDeleted =
                !isPathOnOTG(path) && ((!file.exists() && file.length() == 0L) || file.delete())
            if (fileDeleted) {
                deleteFromMediaStore(path) { needsRescan ->
                    if (needsRescan) {
                        rescanAndDeletePath(path) {
                            runOnUiThread {
                                callback?.invoke(true)
                            }
                        }
                    } else {
                        runOnUiThread {
                            callback?.invoke(true)
                        }
                    }
                }
            } else {
                if (getIsPathDirectory(file.absolutePath) && allowDeleteFolder) {
                    fileDeleted = deleteRecursively(file, this)
                }

                if (!fileDeleted) {
                    if (needsStupidWritePermissions(path)) {
                        handleSAFDialog(path) {
                            if (it) {
                                trySAFFileDelete(fileDirItem, allowDeleteFolder, callback)
                            }
                        }
                    } else if (isAccessibleWithSAFSdk30(path)) {
                        if (canManageMedia()) {
                            deleteSdk30(fileDirItem, callback)
                        } else {
                            handleSAFDialogSdk30(path) {
                                if (it) {
                                    deleteDocumentWithSAFSdk30(
                                        fileDirItem,
                                        allowDeleteFolder,
                                        callback
                                    )
                                }
                            }
                        }
                    } else if (isRPlus() && !isDeletingMultipleFiles) {
                        deleteSdk30(fileDirItem, callback)
                    } else {
                        callback?.invoke(false)
                    }
                }
            }
        }
    }

    private fun deleteSdk30(
        fileDirItem: FileDirItem,
        callback: ((wasSuccess: Boolean) -> Unit)?
    ) {
        val fileUris = getFileUrisFromFileDirItems(arrayListOf(fileDirItem))
        deleteSDK30Uris(fileUris) { success ->
            runOnUiThread {
                callback?.invoke(success)
            }
        }
    }

    fun checkWhatsNew(releases: List<Release>, currVersion: Int) {
        if (baseConfig.lastVersion == 0) {
            baseConfig.lastVersion = currVersion
            return
        }

        val newReleases = arrayListOf<Release>()
        releases.filterTo(newReleases) { it.id > baseConfig.lastVersion }

        if (newReleases.isNotEmpty()) {
            WhatsNewDialog(this, newReleases)
        }

        baseConfig.lastVersion = currVersion
    }


    fun renameFile(
        oldPath: String,
        newPath: String,
        isRenamingMultipleFiles: Boolean,
        callback: ((success: Boolean, android30RenameFormat: Android30RenameFormat) -> Unit)? = null
    ) {
        if (isRestrictedSAFOnlyRoot(oldPath)) {
            handleAndroidSAFDialog(oldPath) {
                if (!it) {
                    runOnUiThread {
                        callback?.invoke(false, Android30RenameFormat.NONE)
                    }
                    return@handleAndroidSAFDialog
                }

                try {
                    ensureBackgroundThread {
                        val success = renameAndroidSAFDocument(oldPath, newPath)
                        runOnUiThread {
                            callback?.invoke(success, Android30RenameFormat.NONE)
                        }
                    }
                } catch (e: Exception) {
                    showErrorToast(e)
                    runOnUiThread {
                        callback?.invoke(false, Android30RenameFormat.NONE)
                    }
                }
            }
        } else if (isAccessibleWithSAFSdk30(oldPath)) {
            if (canManageMedia() && !File(oldPath).isDirectory && isPathOnInternalStorage(oldPath)) {
                renameCasually(oldPath, newPath, isRenamingMultipleFiles, callback)
            } else {
                handleSAFDialogSdk30(oldPath) {
                    if (!it) {
                        return@handleSAFDialogSdk30
                    }

                    try {
                        ensureBackgroundThread {
                            val success = renameDocumentSdk30(oldPath, newPath)
                            if (success) {
                                updateInMediaStore(oldPath, newPath)
                                rescanPath(newPath) {
                                    runOnUiThread {
                                        callback?.invoke(true, Android30RenameFormat.NONE)
                                    }
                                    if (!oldPath.equals(newPath, true)) {
                                        deleteFromMediaStore(oldPath)
                                    }
                                    scanPathRecursively(newPath)
                                }
                            } else {
                                runOnUiThread {
                                    callback?.invoke(false, Android30RenameFormat.NONE)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        showErrorToast(e)
                        runOnUiThread {
                            callback?.invoke(false, Android30RenameFormat.NONE)
                        }
                    }
                }
            }
        } else if (needsStupidWritePermissions(newPath)) {
            handleSAFDialog(newPath) {
                if (!it) {
                    return@handleSAFDialog
                }

                val document = getSomeDocumentFile(oldPath)
                if (document == null || (File(oldPath).isDirectory != document.isDirectory)) {
                    runOnUiThread {
                        toast(R.string.unknown_error_occurred)
                        callback?.invoke(false, Android30RenameFormat.NONE)
                    }
                    return@handleSAFDialog
                }

                try {
                    ensureBackgroundThread {
                        try {
                            DocumentsContract.renameDocument(
                                applicationContext.contentResolver,
                                document.uri,
                                newPath.getFilenameFromPath()
                            )
                        } catch (ignored: FileNotFoundException) {
                            // FileNotFoundException is thrown in some weird cases, but renaming works just fine
                        } catch (e: Exception) {
                            showErrorToast(e)
                            callback?.invoke(false, Android30RenameFormat.NONE)
                            return@ensureBackgroundThread
                        }

                        updateInMediaStore(oldPath, newPath)
                        rescanPaths(arrayListOf(oldPath, newPath)) {
                            if (!baseConfig.keepLastModified) {
                                updateLastModified(newPath, System.currentTimeMillis())
                            }
                            deleteFromMediaStore(oldPath)
                            runOnUiThread {
                                callback?.invoke(true, Android30RenameFormat.NONE)
                            }
                        }
                    }
                } catch (e: Exception) {
                    showErrorToast(e)
                    runOnUiThread {
                        callback?.invoke(false, Android30RenameFormat.NONE)
                    }
                }
            }
        } else renameCasually(oldPath, newPath, isRenamingMultipleFiles, callback)
    }

    private fun renameCasually(
        oldPath: String,
        newPath: String,
        isRenamingMultipleFiles: Boolean,
        callback: ((success: Boolean, android30RenameFormat: Android30RenameFormat) -> Unit)?
    ) {
        val oldFile = File(oldPath)
        val newFile = File(newPath)
        val tempFile = try {
            com.simplemobiletools.notes.pro.extensions.createTempFile(oldFile) ?: return
        } catch (exception: Exception) {
            if (isRPlus() && exception is java.nio.file.FileSystemException) {
                // if we are renaming multiple files at once, we should give the Android 30+ permission dialog all uris together, not one by one
                if (isRenamingMultipleFiles) {
                    callback?.invoke(false, Android30RenameFormat.CONTENT_RESOLVER)
                } else {
                    val fileUris =
                        getFileUrisFromFileDirItems(arrayListOf(File(oldPath).toFileDirItem(this)))
                    updateSDK30Uris(fileUris) { success ->
                        if (success) {
                            val values = ContentValues().apply {
                                put(
                                    MediaStore.Images.Media.DISPLAY_NAME,
                                    newPath.getFilenameFromPath()
                                )
                            }

                            try {
                                contentResolver.update(fileUris.first(), values, null, null)
                                callback?.invoke(true, Android30RenameFormat.NONE)
                            } catch (e: Exception) {
                                showErrorToast(e)
                                callback?.invoke(false, Android30RenameFormat.NONE)
                            }
                        } else {
                            callback?.invoke(false, Android30RenameFormat.NONE)
                        }
                    }
                }
            } else {
                if (exception is IOException && File(oldPath).isDirectory && isRestrictedWithSAFSdk30(
                        oldPath
                    )
                ) {
                    toast(R.string.cannot_rename_folder)
                } else {
                    showErrorToast(exception)
                }
                callback?.invoke(false, Android30RenameFormat.NONE)
            }
            return
        }

        val oldToTempSucceeds = oldFile.renameTo(tempFile)
        val tempToNewSucceeds = tempFile.renameTo(newFile)
        if (oldToTempSucceeds && tempToNewSucceeds) {
            if (newFile.isDirectory) {
                updateInMediaStore(oldPath, newPath)
                rescanPath(newPath) {
                    runOnUiThread {
                        callback?.invoke(true, Android30RenameFormat.NONE)
                    }
                    if (!oldPath.equals(newPath, true)) {
                        deleteFromMediaStore(oldPath)
                    }
                    scanPathRecursively(newPath)
                }
            } else {
                if (!baseConfig.keepLastModified) {
                    newFile.setLastModified(System.currentTimeMillis())
                }
                updateInMediaStore(oldPath, newPath)
                scanPathsRecursively(arrayListOf(newPath)) {
                    if (!oldPath.equals(newPath, true)) {
                        deleteFromMediaStore(oldPath)
                    }
                    runOnUiThread {
                        callback?.invoke(true, Android30RenameFormat.NONE)
                    }
                }
            }
        } else {
            tempFile.delete()
            newFile.delete()
            if (isRPlus()) {
                // if we are renaming multiple files at once, we should give the Android 30+ permission dialog all uris together, not one by one
                if (isRenamingMultipleFiles) {
                    callback?.invoke(false, Android30RenameFormat.SAF)
                } else {
                    val fileUris =
                        getFileUrisFromFileDirItems(arrayListOf(File(oldPath).toFileDirItem(this)))
                    updateSDK30Uris(fileUris) { success ->
                        if (!success) {
                            return@updateSDK30Uris
                        }
                        try {
                            val sourceUri = fileUris.first()
                            val sourceFile = File(oldPath).toFileDirItem(this)

                            if (oldPath.equals(newPath, true)) {
                                val tempDestination = try {
                                    com.simplemobiletools.notes.pro.extensions.createTempFile(
                                        File(
                                            sourceFile.path
                                        )
                                    ) ?: return@updateSDK30Uris
                                } catch (exception: Exception) {
                                    showErrorToast(exception)
                                    callback?.invoke(false, Android30RenameFormat.NONE)
                                    return@updateSDK30Uris
                                }

                                val copyTempSuccess =
                                    copySingleFileSdk30(
                                        sourceFile,
                                        tempDestination.toFileDirItem(this)
                                    )
                                if (copyTempSuccess) {
                                    contentResolver.delete(sourceUri, null)
                                    tempDestination.renameTo(File(newPath))
                                    if (!baseConfig.keepLastModified) {
                                        newFile.setLastModified(System.currentTimeMillis())
                                    }
                                    updateInMediaStore(oldPath, newPath)
                                    scanPathsRecursively(arrayListOf(newPath)) {
                                        runOnUiThread {
                                            callback?.invoke(true, Android30RenameFormat.NONE)
                                        }
                                    }
                                } else {
                                    callback?.invoke(false, Android30RenameFormat.NONE)
                                }
                            } else {
                                val destinationFile = FileDirItem(
                                    newPath,
                                    newPath.getFilenameFromPath(),
                                    sourceFile.isDirectory,
                                    sourceFile.children,
                                    sourceFile.size,
                                    sourceFile.modified
                                )
                                val copySuccessful =
                                    copySingleFileSdk30(sourceFile, destinationFile)
                                if (copySuccessful) {
                                    if (!baseConfig.keepLastModified) {
                                        newFile.setLastModified(System.currentTimeMillis())
                                    }
                                    contentResolver.delete(sourceUri, null)
                                    updateInMediaStore(oldPath, newPath)
                                    scanPathsRecursively(arrayListOf(newPath)) {
                                        runOnUiThread {
                                            callback?.invoke(true, Android30RenameFormat.NONE)
                                        }
                                    }
                                } else {
                                    toast(R.string.unknown_error_occurred)
                                    callback?.invoke(false, Android30RenameFormat.NONE)
                                }
                            }

                        } catch (e: Exception) {
                            showErrorToast(e)
                            callback?.invoke(false, Android30RenameFormat.NONE)
                        }
                    }
                }
            } else {
                toast(R.string.unknown_error_occurred)
                callback?.invoke(false, Android30RenameFormat.NONE)
            }
        }
    }

    private fun getFileOutputStream(
        fileDirItem: FileDirItem,
        allowCreatingNewFile: Boolean = false,
        callback: (outputStream: OutputStream?) -> Unit
    ) {
        val targetFile = File(fileDirItem.path)
        when {
            isRestrictedSAFOnlyRoot(fileDirItem.path) -> {
                handleAndroidSAFDialog(fileDirItem.path) {
                    if (!it) {
                        return@handleAndroidSAFDialog
                    }

                    val uri = getAndroidSAFUri(fileDirItem.path)
                    if (!getDoesFilePathExist(fileDirItem.path)) {
                        createAndroidSAFFile(fileDirItem.path)
                    }
                    callback.invoke(applicationContext.contentResolver.openOutputStream(uri, "wt"))
                }
            }

            needsStupidWritePermissions(fileDirItem.path) -> {
                handleSAFDialog(fileDirItem.path) {
                    if (!it) {
                        return@handleSAFDialog
                    }

                    var document = getDocumentFile(fileDirItem.path)
                    if (document == null && allowCreatingNewFile) {
                        document = getDocumentFile(fileDirItem.getParentPath())
                    }

                    if (document == null) {
                        showFileCreateError(fileDirItem.path)
                        callback(null)
                        return@handleSAFDialog
                    }

                    if (!getDoesFilePathExist(fileDirItem.path)) {
                        document = getDocumentFile(fileDirItem.path) ?: document.createFile(
                            "",
                            fileDirItem.name
                        )
                    }

                    if (document?.exists() == true) {
                        try {
                            callback(
                                applicationContext.contentResolver.openOutputStream(
                                    document.uri,
                                    "wt"
                                )
                            )
                        } catch (e: FileNotFoundException) {
                            showErrorToast(e)
                            callback(null)
                        }
                    } else {
                        showFileCreateError(fileDirItem.path)
                        callback(null)
                    }
                }
            }

            isAccessibleWithSAFSdk30(fileDirItem.path) -> {
                handleSAFDialogSdk30(fileDirItem.path) {
                    if (!it) {
                        return@handleSAFDialogSdk30
                    }

                    callback.invoke(
                        try {
                            val uri = createDocumentUriUsingFirstParentTreeUri(fileDirItem.path)
                            if (!getDoesFilePathExist(fileDirItem.path)) {
                                createSAFFileSdk30(fileDirItem.path)
                            }
                            applicationContext.contentResolver.openOutputStream(uri, "wt")
                        } catch (e: Exception) {
                            null
                        } ?: createCasualFileOutputStream(this, targetFile)
                    )
                }
            }

            isRestrictedWithSAFSdk30(fileDirItem.path) -> {
                callback.invoke(
                    try {
                        val fileUri = getFileUrisFromFileDirItems(arrayListOf(fileDirItem))
                        applicationContext.contentResolver.openOutputStream(fileUri.first(), "wt")
                    } catch (e: Exception) {
                        null
                    } ?: createCasualFileOutputStream(this, targetFile)
                )
            }

            else -> {
                callback.invoke(createCasualFileOutputStream(this, targetFile))
            }
        }
    }

    private fun deleteRecursively(file: File, context: Context): Boolean {
        if (file.isDirectory) {
            val files = file.listFiles() ?: return file.delete()
            for (child in files) {
                deleteRecursively(child, context)
            }
        }

        val deleted = file.delete()
        if (deleted) {
            context.deleteFromMediaStore(file.absolutePath)
        }
        return deleted
    }

    private fun createCasualFileOutputStream(
        activity: BaseSimpleActivity,
        targetFile: File
    ): OutputStream? {
        if (targetFile.parentFile?.exists() == false) {
            targetFile.parentFile?.mkdirs()
        }

        return try {
            FileOutputStream(targetFile)
        } catch (e: Exception) {
            activity.showErrorToast(e)
            null
        }
    }
}
