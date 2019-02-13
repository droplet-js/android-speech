package speech.msc.android.core;

import android.content.Context;

/**
 * 重试机制
 */
public interface RetryHandler<E extends Throwable> {

    public boolean retry(Context context, E error, int executionCount);
}
