package com.simplemobiletools.notes.pro.new_architecture.shared.helpers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.new_architecture.shared.activities.SplashActivity
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.applyColorFilter
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getLaunchIntent
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.notesDB
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.setText
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.setVisibleIf
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.widgetsDB
import com.simplemobiletools.notes.pro.new_architecture.shared.data.models.Widget
import com.simplemobiletools.notes.pro.services.WidgetService

class MyWidgetProvider : AppWidgetProvider() {
    private fun setupAppOpenIntent(context: Context, views: RemoteViews, id: Int, widget: Widget) {
        val intent = context.getLaunchIntent() ?: Intent(context, SplashActivity::class.java)
        intent.putExtra(OPEN_NOTE_ID, widget.noteId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            widget.widgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(id, pendingIntent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        ensureBackgroundThread {
            for (widgetId in appWidgetIds) {
                val widget = context.widgetsDB.getWidgetWithWidgetId(widgetId) ?: continue
                val views = RemoteViews(context.packageName, R.layout.widget)
                val note = context.notesDB.getNoteWithId(widget.noteId)
                views.applyColorFilter(R.id.notes_widget_background, widget.widgetBgColor)
                views.setTextColor(R.id.widget_note_title, widget.widgetTextColor)
                views.setText(R.id.widget_note_title, note?.title ?: "")
                views.setVisibleIf(R.id.widget_note_title, widget.widgetShowTitle)
                setupAppOpenIntent(context, views, R.id.notes_widget_holder, widget)

                Intent(context, WidgetService::class.java).apply {
                    putExtra(NOTE_ID, widget.noteId)
                    putExtra(WIDGET_TEXT_COLOR, widget.widgetTextColor)
                    data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
                    views.setRemoteAdapter(R.id.notes_widget_listview, this)
                }

                val startActivityIntent =
                    context.getLaunchIntent() ?: Intent(context, SplashActivity::class.java)
                startActivityIntent.putExtra(OPEN_NOTE_ID, widget.noteId)
                val startActivityPendingIntent =
                    PendingIntent.getActivity(
                        context,
                        widgetId,
                        startActivityIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                views.setPendingIntentTemplate(
                    R.id.notes_widget_listview,
                    startActivityPendingIntent
                )

                appWidgetManager.updateAppWidget(widgetId, views)
                appWidgetManager.notifyAppWidgetViewDataChanged(
                    widgetId,
                    R.id.notes_widget_listview
                )
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        ensureBackgroundThread {
            appWidgetIds.forEach {
                context.widgetsDB.deleteWidgetId(it)
            }
        }
    }
}
