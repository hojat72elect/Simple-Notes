package com.simplemobiletools.notes.pro.services

import android.content.Intent
import android.widget.RemoteViewsService
import com.simplemobiletools.notes.pro.new_architecture.shared.ui.adapters.WidgetAdapter

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent) = WidgetAdapter(applicationContext, intent)
}
