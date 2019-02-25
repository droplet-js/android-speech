package speech.msc.android.iflytek;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import speech.msc.android.MscPlayer;
import speech.msc.android.MscSettings;
import speech.msc.android.MscSpeaker;
import speech.msc.android.core.CheckEnvHandler;
import speech.msc.android.core.RetryHandler;
import speech.msc.android.core.TransferHandler;
import speech.msc.android.error.InitException;
import speech.msc.android.error.NotInstalledException;
import speech.msc.android.error.SpeechException;
import speech.msc.android.error.UnknownException;
import speech.msc.android.iflytek.core.IFlyTekInnerRetryHandler;
import speech.msc.android.iflytek.core.IFlyTekNetworkRetryHandler;
import speech.msc.android.iflytek.core.IFlyTekNoNetworkCheckEnvHandler;
import speech.msc.android.iflytek.core.IFlyTekTransferHandler;

public final class IFlyTekMscPlayer extends MscPlayer {

    private static final String TAG = "IFlyTekMscPlayer";

    public static final String TYPE_CLOUD = SpeechConstant.TYPE_CLOUD;
    public static final String TYPE_LOCAL = SpeechConstant.TYPE_LOCAL;

    @StringDef({TYPE_CLOUD, TYPE_LOCAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EngineType {
    }

    static final RetryHandler<SpeechError> INNER_RETRY_HANDLER = new IFlyTekInnerRetryHandler(1);
    static final CheckEnvHandler<SpeechError> NO_NETWORK_CHECK_ENV_HANDLER = new IFlyTekNoNetworkCheckEnvHandler();
    static final RetryHandler<SpeechError> NETWORK_RETRY_HANDLER = new IFlyTekNetworkRetryHandler(2);
    static final TransferHandler<SpeechError> TRANSFER_HANDLER = new IFlyTekTransferHandler();

    final String appId;
    final String engineType;

    SpeechSynthesizer speechSynthesizer;

    public IFlyTekMscPlayer(Context context, MscSettings settings, @NonNull String appId, @NonNull @EngineType String engineType) {
        super(context, settings);
        this.appId = appId;
        this.engineType = engineType;
    }

    private void createSpeechUtility() {
        if (SpeechUtility.getUtility() != null) {
            SpeechUtility.getUtility().destroy();
        }
        // 请勿在“=”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility(context.getApplicationContext(), SpeechConstant.APPID + "=" + appId);
    }

    /**
     * 适配小米手机默认禁用语记后台联网功能
     * 与讯飞语音开放平台研发沟通，强制使用 SDK 在线语音合成，不走语记在线语音合成
     */
    void createSpeechUtilityForNoNetwork() {
        if (SpeechUtility.getUtility() != null) {
            SpeechUtility.getUtility().destroy();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(SpeechConstant.APPID + "=" + appId);
        builder.append(",");
        // 设置使用v5+
        builder.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(context.getApplicationContext(), builder.toString());
    }

    @Override
    public void beginPlay(String text) {
        super.beginPlay(text);
        if (SpeechUtility.getUtility() == null) {
            createSpeechUtility();
        }
        if (speechSynthesizer == null) {
            setPlayState(PLAY_STATE_PENDING);
            speechSynthesizer = SpeechSynthesizer.createSynthesizer(context.getApplicationContext(), new IFlyTekInitListener(text));
        } else {
            setPlayState(PLAY_STATE_PLAYING);
            beginPlayReact(text);
        }
    }

    private final class IFlyTekInitListener implements InitListener {

        private String text;

        IFlyTekInitListener(String text) {
            super();
            this.text = text;
        }

        @Override
        public void onInit(int code) {
            if (code == ErrorCode.SUCCESS) {
                setPlayState(PLAY_STATE_PLAYING);
                beginPlayReact(text);
            } else {
                Log.e(TAG, "init speak error: " + code);
                postDelayed(300, new InitFailAction());
            }
        }
    }

    void beginPlayReact(String text) {
        if (speechSynthesizer != null) {
            /** 清空参数 **/
            speechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
            /** 设置引擎模式 */
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_MODE, TextUtils.equals(engineType, SpeechConstant.TYPE_CLOUD) ? SpeechConstant.MODE_MSC : SpeechConstant.MODE_PLUS);
            /** 设置引擎类型 **/
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, engineType);
            /** 设置发音人 **/
            MscSpeaker speaker = settings.getSpeaker();
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, speaker.alias);
            /** 设置语速 **/
            speechSynthesizer.setParameter(SpeechConstant.SPEED, String.valueOf(settings.getSpeed()));
            /** 设置音调 **/
            speechSynthesizer.setParameter(SpeechConstant.PITCH, String.valueOf(50));
            /** 设置音量，范围 0 - 100 **/
            speechSynthesizer.setParameter(SpeechConstant.VOLUME, String.valueOf(80));
            /** 设置播放器音频流类型 **/
//            speechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");
            /** 设置播放合成音频打断音乐播放，默认为true **/
            speechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
            /**
             * 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
             * 注：AUDIO_FORMAT参数语记需要更新版本才能生效
             */
//            speechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//            speechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.wav");
            try {
                int code = speechSynthesizer.startSpeaking(text, synthesizerListener);
                if (code != ErrorCode.SUCCESS) {
                    Log.e(TAG, "start speak error: " + code);
                    // 初始化失败
                    speechSynthesizer.destroy();
                    speechSynthesizer = null;
                    // 朗读时，把语记卸载了
                    if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                        onPlayTransferred(new NotInstalledException("未安装讯飞语音插件"));
                    } else {
                        setPlayState(PLAY_STATE_QUIT);
                        onPlayEnd(new InitException("讯飞语音初始化失败"));
                    }
                }
            } catch (Exception ignored) {
                // 华硕 Tooj 使用的是内置的灵犀语音进行朗读，会导致崩溃
                speechSynthesizer.destroy();
                speechSynthesizer = null;
                setPlayState(PLAY_STATE_QUIT);
                onPlayEnd(new UnknownException(ignored.getMessage(), ignored.getCause()));
            }
        }
    }

    private SynthesizerListener synthesizerListener = new SynthesizerListener() {
        private final AtomicInteger innerStat = new AtomicInteger(0);
        private final AtomicInteger networkStat = new AtomicInteger(0);

        @Override
        public void onSpeakBegin() {
            // 开始播放
            innerStat.set(0);
            networkStat.set(0);
        }

        @Override
        public void onSpeakPaused() {
            // 暂停播放
        }

        @Override
        public void onSpeakResumed() {
            // 继续播放
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度 - progress 最大只会跑到 99
            onPlayProgress(percent, beginPos, endPos);
        }

        @Override
        public void onCompleted(SpeechError error) {
            // 播放结束
            if (error == null) {
                // 播放下一段内容
                onPlayEnd(null);
            } else {
                final SpeechException transformError = new SpeechException("code: " + error.getErrorCode() + ", description: " + error.getErrorDescription());
                if (TextUtils.equals(engineType, TYPE_CLOUD)) {
                    if (INNER_RETRY_HANDLER.retry(context, error, innerStat.getAndIncrement())) {
                        postDelayed(300, new Runnable() {
                            @Override
                            public void run() {
                                onPlayRetry(true, transformError);
                            }
                        });
                    } else if (NO_NETWORK_CHECK_ENV_HANDLER.checked(context, error)) {
                        postDelayed(300, new Runnable() {
                            @Override
                            public void run() {
                                createSpeechUtilityForNoNetwork();
                                onPlayRetry(true, transformError);
                            }
                        });
                    } else if (NETWORK_RETRY_HANDLER.retry(context, error, networkStat.getAndIncrement())) {
                        onPlayRetry(false, transformError);
                    } else {
                        innerStat.set(0);
                        networkStat.set(0);
                        if (TRANSFER_HANDLER.transferred(context, error)) {
                            onPlayTransferred(transformError);
                        } else {
                            setPlayState(PLAY_STATE_QUIT);
                            onPlayEnd(error);
                        }
                    }
                } else {
                    setPlayState(PLAY_STATE_QUIT);
                    onPlayEnd(error);
                }
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
//            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
//                String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
//                Log.d(TAG, "session id =" + sid);
//            }
        }
    };

    /**
     * 有些初始化回调是同步回调，这时候可能 speechSynthesizer 还是 null 值，故而无法在回调中销毁。
     * 所以在这里将销毁任务交个另一个消息去执行，错开 mSpeechSynthesizer 初始化消息。
     */
    class InitFailAction implements Runnable {

        @Override
        public void run() {
            // 初始化失败
            if (speechSynthesizer != null) {
                speechSynthesizer.destroy();
                speechSynthesizer = null;
            }
            setPlayState(PLAY_STATE_QUIT);
            onPlayEnd(new InitException("讯飞语音初始化失败"));
        }
    }

    @Override
    public void pausePlay() {
        super.pausePlay();
        if (speechSynthesizer != null) {
            speechSynthesizer.pauseSpeaking();
        }
    }

    @Override
    public void resumePlay() {
        super.resumePlay();
        if (speechSynthesizer != null) {
            speechSynthesizer.resumeSpeaking();
        }
    }

    @Override
    public void quitPlay() {
        super.quitPlay();
        if (speechSynthesizer != null) {
            speechSynthesizer.stopSpeaking();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (speechSynthesizer != null) {
            speechSynthesizer.destroy();
            speechSynthesizer = null;
        }
        if (SpeechUtility.getUtility() != null) {
            SpeechUtility.getUtility().destroy();
        }
    }
}
