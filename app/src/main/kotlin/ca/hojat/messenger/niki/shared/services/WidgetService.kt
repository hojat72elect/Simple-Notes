package ca.hojat.messenger.niki.shared.services

import android.content.Intent
import android.widget.RemoteViewsService
import ca.hojat.messenger.niki.shared.ui.adapters.WidgetAdapter

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent) = WidgetAdapter(applicationContext, intent)
}
