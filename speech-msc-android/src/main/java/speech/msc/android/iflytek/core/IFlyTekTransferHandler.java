package speech.msc.android.iflytek.core;

import android.content.Context;
import speech.msc.android.core.TransferHandler;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechError;

public final class IFlyTekTransferHandler implements TransferHandler<SpeechError> {

    @Override
    public boolean transferred(Context context, SpeechError error) {
        boolean transferred = false;
        if (error != null) {
            final int errorCode = error.getErrorCode();
            transferred = errorCode == ErrorCode.ERROR_NETWORK_TIMEOUT/** 网络连接超时 **/
                    || errorCode == ErrorCode.ERROR_NET_EXCEPTION/** 网络连接发生异常 **/
                    || errorCode == ErrorCode.ERROR_NO_NETWORK/** 无有效的网络连接 **/;
        }
        return transferred;
    }
}
