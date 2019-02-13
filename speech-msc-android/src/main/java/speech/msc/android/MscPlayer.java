package speech.msc.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.IntDef;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class MscPlayer {
    public static final int PLAY_STATE_QUIT = 0;
    public static final int PLAY_STATE_PENDING = 1;
    public static final int PLAY_STATE_PLAYING = 2;
    public static final int PLAY_STATE_PAUSED = 3;

    @IntDef({PLAY_STATE_QUIT, PLAY_STATE_PENDING, PLAY_STATE_PLAYING, PLAY_STATE_PAUSED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayState {
    }

    protected final Context context;
    protected final MscSettings settings;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private int playState = PLAY_STATE_QUIT;
    private CallReceiver callReceiver;

    private OnPlayerListener onPlayerListener;

    public MscPlayer(Context context, MscSettings settings) {
        this.context = context;
        this.settings = settings;
    }

    public void setOnPlayerListener(OnPlayerListener onPlayerListener) {
        this.onPlayerListener = onPlayerListener;
    }

    @PlayState
    public final int getPlayState() {
        return playState;
    }

    /**
     * 播放状态设置内部维护，不向外暴露
     */
    protected final void setPlayState(@PlayState int playState) {
        this.playState = playState;
        if (onPlayerListener != null) {
            onPlayerListener.onPlayStateChanged(playState);
        }
    }

    protected final void post(Runnable action) {
        if (mainHandler != null) {
            mainHandler.post(action);
        }
    }

    protected final void postDelayed(Runnable action, long delayMillis) {
        if (mainHandler != null) {
            mainHandler.postDelayed(action, delayMillis);
        }
    }

    protected final void onPlayProgress(int percent, int beginPos, int endPos) {
        if (onPlayerListener != null) {
            onPlayerListener.onPlayProgress(percent, beginPos, endPos);
        }
    }

    protected final void onPlayEnd(Throwable error) {
        if (onPlayerListener != null) {
            onPlayerListener.onPlayEnd(error);
        }
    }

    protected final void onPlayRetry(boolean retryAuto, Throwable error) {
        if (onPlayerListener != null) {
            onPlayerListener.onPlayRetry(this, retryAuto, error);
        }
    }

    protected final void onPlayTransferred(Throwable error) {
        if (onPlayerListener != null) {
            onPlayerListener.onPlayTransferred(this, error);
        }
    }

    public void beginPlay(String text) {
        registerTelephonyReceiver();
//        setPlayState(PLAY_STATE_PENDING);
//        setPlayState(PLAY_STATE_PLAYING);
    }

    public void pausePlay() {
        unregisterTelephonyReceiver();
        setPlayState(PLAY_STATE_PAUSED);
    }

    public void resumePlay() {
        registerTelephonyReceiver();
        setPlayState(PLAY_STATE_PLAYING);

    }

    public void quitPlay() {
        unregisterTelephonyReceiver();
        setPlayState(PLAY_STATE_QUIT);
    }

    public void destroy() {
        unregisterTelephonyReceiver();
    }

    private void registerTelephonyReceiver() {
        if (callReceiver == null) {
            callReceiver = new CallReceiver();

            IntentFilter filter = new IntentFilter();
            filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
            context.registerReceiver(callReceiver, filter);
        }
    }

    private void unregisterTelephonyReceiver() {
        if (callReceiver != null) {
            context.unregisterReceiver(callReceiver);
            callReceiver = null;
        }
    }

    private final class CallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                    TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    int state = manager.getCallState();
                    switch (state) {
                        case TelephonyManager.CALL_STATE_RINGING: {
                            // 等待接电话
                            if (getPlayState() == PLAY_STATE_PENDING || getPlayState() == PLAY_STATE_PLAYING) {
                                pausePlay();
                            }
                            break;
                        }
                        case TelephonyManager.CALL_STATE_OFFHOOK: {
                            // 通话中
                            // do nothing
                            break;
                        }
                        case TelephonyManager.CALL_STATE_IDLE: {
                            // 挂断电话
                            // do nothing
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                } else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                    String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    if (!TextUtils.isEmpty(number)) {
                        if (getPlayState() == PLAY_STATE_PENDING || getPlayState() == PLAY_STATE_PLAYING) {
                            pausePlay();
                        }
                    }
                }
            }
        }
    }

    public interface OnPlayerListener {
        public void onPlayProgress(int percent, int beginPos, int endPos);
        public void onPlayEnd(Throwable error);

        // 播放状态
        public void onPlayStateChanged(@PlayState int playState);
        // 重试
        public void onPlayRetry(MscPlayer player, boolean retryAuto, Throwable error);
        // 模式切换
        public void onPlayTransferred(MscPlayer player, Throwable error);
    }
}
