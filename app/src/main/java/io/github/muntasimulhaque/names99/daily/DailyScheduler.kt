package io.github.muntasimulhaque.names99.daily

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.github.muntasimulhaque.names99.MainActivity
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.NamesRepository
import io.github.muntasimulhaque.names99.data.Prefs
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

object DailyScheduler {

    private const val WIDGET_WORK = "daily_widget_update"
    private const val NOTIFY_WORK = "daily_notification"
    const val CHANNEL_ID = "name_of_the_day"

    /** Called on app start: keeps the widget fresh and re-applies the notification schedule. */
    fun ensureScheduled(context: Context) {
        val widgetRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(minutesUntil(0, 5), TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WIDGET_WORK, ExistingPeriodicWorkPolicy.KEEP, widgetRequest
        )
    }

    suspend fun rescheduleNotification(context: Context) {
        val prefs = Prefs(context.applicationContext)
        if (!prefs.dailyEnabled.first()) return
        val (hour, minute) = prefs.dailyTime.first()
        val request = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(minutesUntil(hour, minute), TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NOTIFY_WORK, ExistingPeriodicWorkPolicy.REPLACE, request
        )
    }

    fun cancelNotification(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(NOTIFY_WORK)
    }

    /** Minutes from now until the next occurrence of hour:minute (local time). */
    private fun minutesUntil(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val next = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        return (next.timeInMillis - now.timeInMillis) / 60_000L + 1
    }
}

class WidgetUpdateWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        DailyNameWidget().updateAll(applicationContext)
        return Result.success()
    }
}

class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        DailyNameWidget().updateAll(context)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val name = NamesRepository.dailyName(context)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(
                NotificationChannel(
                    DailyScheduler.CHANNEL_ID,
                    context.getString(R.string.daily_notification_channel),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.EXTRA_NAME_NUMBER, name.number)
        }
        val pending = PendingIntent.getActivity(
            context, name.number, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, DailyScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("${name.arabic}  ${name.transliteration}")
            .setContentText(name.title)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${name.title}. Tap to read the full meaning."))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
        return Result.success()
    }
}
