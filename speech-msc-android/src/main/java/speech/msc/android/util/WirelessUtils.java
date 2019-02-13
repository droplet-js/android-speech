package speech.msc.android.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

/**
 * 所需权限
 * android.permission.WRITE_SETTINGS - Manifest.permission.WRITE_SETTINGS
 */
public final class WirelessUtils {

    @IntDef({
            Settings.System.WIFI_SLEEP_POLICY_DEFAULT, // 休眠后不久就关闭 wifi 链接
            Settings.System.WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED, // 仅限充电时
            Settings.System.WIFI_SLEEP_POLICY_NEVER // 始终
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface WifiSleepPolicy {
    }

    private WirelessUtils() {
        super();
    }

    public static boolean canWriteSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(context);
        } else {
            return true;
        }
    }

    public static int getSleepPolicy(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
    }

    /**
     * 设置休眠状态下保持 WiFi
     */
    public static void setSleepPolicy(Context context, @WifiSleepPolicy int policy) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY, policy);
    }

    public static void showSystemSettingUI(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent();
                ComponentName component = new ComponentName(
                        "com.android.settings",
                        "com.android.settings.WirelessSettings");
                intent.setComponent(component);
                intent.setAction(Intent.ACTION_VIEW);
                context.startActivity(intent);
            }
        } catch (Exception ignored) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }
}
