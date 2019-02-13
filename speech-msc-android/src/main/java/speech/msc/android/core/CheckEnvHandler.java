package speech.msc.android.core;

import android.content.Context;

/**
 * 环境检查
 */
public interface CheckEnvHandler<E extends Throwable> {

    public boolean checked(Context context, E error);
}
