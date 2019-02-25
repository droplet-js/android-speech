package speech.msc.android.iflytek;

import android.text.TextUtils;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import speech.msc.android.MscSpeaker;

public final class IFlyTekMscHelper {

    public static final int DEFAULT_SPEED = 50;

    private static final String ACCENT_ZH_CH = "mandarin";
    private static final String ACCENT_ENGLISH = "english";
    private static final String ACCENT_CANTONESE = "cantonese";
    private static final String ACCENT_SICHUAN = "sichuan";
    private static final String ACCENT_TAIWAN = "taiwan";
    private static final String ACCENT_HUNAN = "hunan";
    private static final String ACCENT_DONGBEI = "dongbei";
    private static final String ACCENT_HENAN = "henan";
    private static final String ACCENT_SHANXI = "shanxi";

    private static final Map<String, String> DEFAULT_PLUS_ACCENT_MAP = new HashMap<>();

    static {
        DEFAULT_PLUS_ACCENT_MAP.put(ACCENT_ZH_CH, "普通话");
        DEFAULT_PLUS_ACCENT_MAP.put(ACCENT_ENGLISH, "英语");
        DEFAULT_PLUS_ACCENT_MAP.put(ACCENT_CANTONESE, "粤语");
        DEFAULT_PLUS_ACCENT_MAP.put(ACCENT_SICHUAN, "四川话");
        DEFAULT_PLUS_ACCENT_MAP.put(ACCENT_TAIWAN, "台湾话");
        DEFAULT_PLUS_ACCENT_MAP.put(ACCENT_HUNAN, "湖南话");
        DEFAULT_PLUS_ACCENT_MAP.put(ACCENT_DONGBEI, "东北话");
        DEFAULT_PLUS_ACCENT_MAP.put(ACCENT_HENAN, "河南话");
        DEFAULT_PLUS_ACCENT_MAP.put(ACCENT_SHANXI, "陕西话");
    }

    // 默认内置音色
    public static final MscSpeaker DEFAULT_SPEAKER = new MscSpeaker("晓燕", "xiaoyan", MscSpeaker.SEX_WOMAN, ACCENT_ZH_CH, MscSpeaker.LANGUAGE_ZH_CN); // 晓燕

    private static final List<MscSpeaker> DEFAULT_MSC_SPEAKERS = new ArrayList<MscSpeaker>();

    static {
        DEFAULT_MSC_SPEAKERS.add(DEFAULT_SPEAKER); // 晓燕
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小宇", "xiaoyu", MscSpeaker.SEX_MAN, ACCENT_ZH_CH, MscSpeaker.LANGUAGE_ZH_CN)); // 小宇
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小研", "vixy", MscSpeaker.SEX_WOMAN, ACCENT_ZH_CH, MscSpeaker.LANGUAGE_ZH_CN)); // 小研
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小琪", "xiaoqi", MscSpeaker.SEX_WOMAN, ACCENT_ZH_CH, MscSpeaker.LANGUAGE_ZH_CN)); // 小琪
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小峰", "vixf", MscSpeaker.SEX_MAN, ACCENT_ZH_CH, MscSpeaker.LANGUAGE_ZH_CN)); // 小峰
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小梅", "xiaomei", MscSpeaker.SEX_WOMAN, ACCENT_CANTONESE, MscSpeaker.LANGUAGE_ZH_CN)); // 小梅
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小莉", "vixl", MscSpeaker.SEX_WOMAN, ACCENT_TAIWAN, MscSpeaker.LANGUAGE_ZH_CN)); // 小莉
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("晓琳", "xiaolin", MscSpeaker.SEX_WOMAN, ACCENT_TAIWAN, MscSpeaker.LANGUAGE_ZH_CN)); // 晓琳
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小蓉", "xiaorong", MscSpeaker.SEX_WOMAN, ACCENT_SICHUAN, MscSpeaker.LANGUAGE_ZH_CN)); // 小蓉
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小芸", "vixyun", MscSpeaker.SEX_WOMAN, ACCENT_DONGBEI, MscSpeaker.LANGUAGE_ZH_CN)); // 小芸
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小倩", "xiaoqian", MscSpeaker.SEX_WOMAN, ACCENT_DONGBEI, MscSpeaker.LANGUAGE_ZH_CN)); // 小倩
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小坤", "xiaokun", MscSpeaker.SEX_MAN, ACCENT_HENAN, MscSpeaker.LANGUAGE_ZH_CN)); // 小坤
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小强", "xiaoqiang", MscSpeaker.SEX_MAN, ACCENT_HUNAN, MscSpeaker.LANGUAGE_ZH_CN)); // 小强
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小莹", "vixying", MscSpeaker.SEX_WOMAN, ACCENT_SHANXI, MscSpeaker.LANGUAGE_ZH_CN)); // 小莹
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("小新", "xiaoxin", MscSpeaker.SEX_MAN, ACCENT_ZH_CH, MscSpeaker.LANGUAGE_ZH_CN)); // 小新
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("楠楠", "nannan", MscSpeaker.SEX_WOMAN, ACCENT_ZH_CH, MscSpeaker.LANGUAGE_ZH_CN)); // 楠楠
        DEFAULT_MSC_SPEAKERS.add(new MscSpeaker("老孙", "vils", MscSpeaker.SEX_MAN, ACCENT_ZH_CH, MscSpeaker.LANGUAGE_ZH_CN)); // 老孙
    }

    // ---

    public static String[] queryAvailableEngines() {
        return SpeechUtility.getUtility().queryAvailableEngines();
    }

    public static boolean checkServiceInstalled() {
        return SpeechUtility.getUtility().checkServiceInstalled();
    }

    public static String getComponentUrl() {
        return SpeechUtility.getUtility().getComponentUrl();
    }

    public static void openEngineSettings() {
        SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_TTS);
    }

    public static List<MscSpeaker> getIFlyTekMSC() {
        return DEFAULT_MSC_SPEAKERS;
    }

    public static List<MscSpeaker> getIFlyTekPlus(boolean onlyZh) {
        ArrayList<MscSpeaker> speakers = new ArrayList<>();
        String jsonStr = SpeechUtility.getUtility().getParameter(SpeechConstant.PLUS_LOCAL_TTS);
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject object = new JSONObject(jsonStr);
                String ret = !object.isNull("ret") ? object.getString("ret") : null;
                if (TextUtils.equals(ret, "0")) {
                    JSONObject result = !object.isNull("result") ? object.getJSONObject("result") : null;
                    if (result != null) {
                        JSONArray tts = !result.isNull("tts") ? result.getJSONArray("tts") : null;
                        if (tts != null && tts.length() > 0) {
                            final int len = tts.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject info = tts.getJSONObject(i);
                                String name = !info.isNull("nickname") ? info.getString("nickname") : null;
                                String alias = !info.isNull("name") ? info.getString("name") : null;
                                String sex = !info.isNull("sex") ? info.getString("sex") : null;
                                String language = !info.isNull("language") ? info.getString("language") : null;
                                String accent = !info.isNull("accent") ? info.getString("accent") : null;
//                                int selected = !info.isNull("selected") ? info.getInt("selected") : 0;
//                                int age = !info.isNull("age") ? info.getInt("age") : -1;

                                if (!TextUtils.isEmpty(name)) {
                                    MscSpeaker speaker = new MscSpeaker(name, alias, sex, language, accent);
                                    if (!onlyZh || speaker.isZhCN() || speaker.isZhTW()) {
                                        speakers.add(speaker);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException ignored) {
            }
        }
        if (speakers.isEmpty()) {
            speakers.add(DEFAULT_SPEAKER);
        }
        return speakers;
    }
}
