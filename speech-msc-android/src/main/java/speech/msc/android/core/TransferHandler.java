package speech.msc.android.core;

import android.content.Context;

/**
 * 模式切换机制
 */
public interface TransferHandler<E extends Throwable> {

    public boolean transferred(Context context, E error);
}
