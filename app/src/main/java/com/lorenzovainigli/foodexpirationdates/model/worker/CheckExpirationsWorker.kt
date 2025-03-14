package com.lorenzovainigli.foodexpirationdates.model.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lorenzovainigli.foodexpirationdates.R
import com.lorenzovainigli.foodexpirationdates.model.entity.computeExpirationDate
import com.lorenzovainigli.foodexpirationdates.model.repository.ExpirationDateRepository
import com.lorenzovainigli.foodexpirationdates.view.MainActivity
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

@HiltWorker
class CheckExpirationsWorker @Inject constructor(
    appContext: Context,
    params: WorkerParameters,
    private val repository: ExpirationDateRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val sb = StringBuilder()
        val today = Calendar.getInstance()
        val twoDaysAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -2)
        }
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -1)
        }
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }
        val msInADay = (1000 * 60 * 60 * 24)
        val filteredList = repository.getAll().first().filter {
            computeExpirationDate(it) < tomorrow.time.time
        }
        if (filteredList.isEmpty()) {
            return Result.success()
        }
        filteredList.map {
            val expDate = computeExpirationDate(it)
            sb.append(it.foodName).append(" (")
            if (expDate < twoDaysAgo.time.time) {
                val days = (today.time.time - expDate) / msInADay
                sb.append(applicationContext.getString(R.string.n_days_ago, days))
            } else if (expDate < yesterday.time.time)
                sb.append(applicationContext.getString(R.string.yesterday).lowercase())
            else if (expDate < today.time.time) {
                sb.append(applicationContext.getString(R.string.today).lowercase())
            } else {
                sb.append(applicationContext.getString(R.string.tomorrow).lowercase())
            }
            sb.append("), ")
        }
        var message = ""
        if (sb.toString().length > 2)
            message = sb.toString().substring(0, sb.toString().length - 2) + "."
        showNotification(
            title = applicationContext.getString(R.string.your_food_is_expiring),
            message = message
        )
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(applicationContext, "channel_reminders")
                .setContentText(message)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.fed_icon_notification)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
            notificationManager.notify(1, notification)
        }
    }

    companion object {
        const val workerID = "DailyExpirationCheck"
    }

}