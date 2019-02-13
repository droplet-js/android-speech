package speech.msc.android.iflytek.core;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import speech.msc.android.core.CheckEnvHandler;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;

/**
 * 小米手机默认禁用语记后台联网功能
 */
public final class IFlyTekNoNetworkCheckEnvHandler implements CheckEnvHandler<SpeechError> {

    @Override
    public boolean checked(Context context, SpeechError error) {
        boolean check = false;
        if (error != null) {
            final int errorCode = error.getErrorCode();
            /** 无有效的网络连接 **/
            if (errorCode == ErrorCode.ERROR_NO_NETWORK) {
                if (context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        check = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return check;
    }
}
