package crypto.cryptocurrencies.cryptos.alarmManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cryptos.cryptocurrency.R;

import java.util.Calendar;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        /*10AM, 02PM, 06PM, 10PM, 12AM*/
        Glide.with(context)
                .asBitmap()
                .load(context.getResources().getDrawable(R.drawable.app_logo))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        String title = "";
                        if (intent.hasExtra("time")) {
                            if (intent.getStringExtra("time").equals("10AM")) {
                                title = getTitle(context);
                                setAlarm(context, 14, 0, "02PM");
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, 10);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 10);
                                if(calendar.getTimeInMillis() > System.currentTimeMillis()){
                                    showNotification(context, resource, title);
                                }
                            } else if (intent.getStringExtra("time").equals("02PM")) {
                                title = getTitle(context);
                                setAlarm(context, 18, 0, "06PM");
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, 14);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 10);
                                if(calendar.getTimeInMillis() > System.currentTimeMillis()){
                                    showNotification(context, resource, title);
                                }
                            } else if (intent.getStringExtra("time").equals("06PM")) {
                                title = getTitle(context);
                                setAlarm(context, 20, 0, "10PM");
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, 18);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 10);
                                if(calendar.getTimeInMillis() > System.currentTimeMillis()){
                                    showNotification(context, resource, title);
                                }
                            } else if (intent.getStringExtra("time").equals("10PM")) {
                                title = getTitle(context);
                                setAlarm(context, 24, 0, "12AM");
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, 20);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 10);
                                if(calendar.getTimeInMillis() > System.currentTimeMillis()){
                                    showNotification(context, resource, title);
                                }
                            } else if (intent.getStringExtra("time").equals("12AM")) {
                                title = getTitle(context);
                                setAlarm(context, 10, 0, "10AM");
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, 24);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 10);
                                if(calendar.getTimeInMillis() > System.currentTimeMillis()){
                                    showNotification(context, resource, title);
                                }
                            }
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void showNotification(Context context, Bitmap largeImage, String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel1")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.app_logo)
                .setLargeIcon(largeImage)
                .setContentTitle(title)
                .setContentText("Grab Free "+ context.getResources().getString(R.string.app_name) +" and Withdraw Directly in Wallet")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(largeImage));
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(200, builder.build());
    }

    private int generateRandomNumber() {
        return new Random().nextInt(9 - 1 + 1) + 1;
    }

    private String getTitle(Context context) {
        String title = "";
        switch (generateRandomNumber()) {
            case 1:
                title = "New Day New Collections";
                break;
            case 2:
                title = "Don't miss it";
                break;
            case 3:
                title = "Collect and Withdraw "+context.getResources().getString(R.string.app_name);
                break;
            case 4:
                title = "Don’t Missss";
                break;
            case 5:
                title = "FREE "+context.getResources().getString(R.string.app_name);
                break;
            case 6:
                title = "Collect "+context.getResources().getString(R.string.app_name);
                break;
            case 7:
                title = "Withdraw "+context.getResources().getString(R.string.app_name);
                break;
            case 8:
                title = "Get Unlimited Free "+context.getResources().getString(R.string.app_name);
                break;
            case 9:
                title = "Grab "+context.getResources().getString(R.string.app_name);
                break;
            default:
                title = "New Day New Collections";
                break;
        }
        return title;
    }

    private void setAlarm(Context context, int hour, int min, String time) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("time", time);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 4, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
