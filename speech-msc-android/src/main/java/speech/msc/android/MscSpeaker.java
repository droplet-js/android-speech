package speech.msc.android;

import android.text.TextUtils;

public final class MscSpeaker {

    public static final String SEX_MAN = "man";
    public static final String SEX_WOMAN = "woman";

    public static final String LANGUAGE_ZH_CN = "zh_cn";
    public static final String LANGUAGE_ZH_TW = "zh_tw";

    public final String name;
    public final String alias;
    public final String sex;
    public final String accent;
    public final String language;

    public MscSpeaker(String name, String alias, String sex, String accent, String language) {
        this.name = name;
        this.alias = alias;
        this.sex = sex;
        this.accent = accent;
        this.language = language;
    }

    public boolean isZhCN() {
        return TextUtils.equals(language, LANGUAGE_ZH_CN);
    }

    public boolean isZhTW() {
        return TextUtils.equals(language, LANGUAGE_ZH_TW);
    }
}
