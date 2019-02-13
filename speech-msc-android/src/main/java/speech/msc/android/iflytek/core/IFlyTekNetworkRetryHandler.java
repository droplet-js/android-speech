package speech.msc.android.iflytek.core;

import android.content.Context;
import speech.msc.android.core.RetryHandler;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;

public final class IFlyTekNetworkRetryHandler implements RetryHandler<SpeechError> {

    private final int retryCount;

    public IFlyTekNetworkRetryHandler(int retryCount) {
        super();
        this.retryCount = retryCount;
    }

    @Override
    public boolean retry(Context context, SpeechError error, int executionCount) {
        boolean retry = false;
        if (error != null) {
            final int errorCode = error.getErrorCode();
            retry = errorCode == ErrorCode.ERROR_NETWORK_TIMEOUT/** 网络连接超时 **/
                    || errorCode == ErrorCode.ERROR_NET_EXCEPTION/** 网络连接发生异常 **/;
        }
        return retry && executionCount < retryCount;
    }
}
