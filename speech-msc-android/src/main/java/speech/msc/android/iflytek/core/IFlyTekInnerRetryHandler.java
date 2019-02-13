package speech.msc.android.iflytek.core;

import android.content.Context;
import speech.msc.android.core.RetryHandler;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;

public final class IFlyTekInnerRetryHandler implements RetryHandler<SpeechError> {

    private final int retryCount;

    public IFlyTekInnerRetryHandler(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public boolean retry(Context context, SpeechError error, int executionCount) {
        boolean retry = false;
        if (error != null) {
            final int errorCode = error.getErrorCode();
            retry = errorCode == ErrorCode.ERROR_ENGINE_INIT_FAIL/** 引擎初始化失败 */
                    || errorCode == ErrorCode.ERROR_ENGINE_CALL_FAIL/** 调用失败 */
                    || errorCode == ErrorCode.ERROR_ENGINE_BUSY/** 引擎繁忙 */;
        }
        return retry && executionCount < retryCount;
    }
}
