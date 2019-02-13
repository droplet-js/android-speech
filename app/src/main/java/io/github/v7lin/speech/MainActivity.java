package io.github.v7lin.speech;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import speech.msc.android.MscPlayer;
import speech.msc.android.MscSettings;
import speech.msc.android.MscSpeaker;
import speech.msc.android.iflytek.IFlyTekMscHelper;
import speech.msc.android.iflytek.IFlyTekMscPlayer;

public class MainActivity extends Activity {

    private MscPlayer mscPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mscPlayer = new IFlyTekMscPlayer(this, mscSettings, "5c61057f", IFlyTekMscPlayer.TYPE_CLOUD);
        mscPlayer.setOnPlayerListener(new MscPlayer.OnPlayerListener() {

            @Override
            public void onPlayProgress(int percent, int beginPos, int endPos) {

            }

            @Override
            public void onPlayEnd(Throwable error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
                if (mscPlayer != null) {
                    mscPlayer.quitPlay();
                }
            }

            @Override
            public void onPlayStateChanged(int playState) {

            }

            @Override
            public void onPlayRetry(MscPlayer player, boolean retryAuto, Throwable error) {

            }

            @Override
            public void onPlayTransferred(MscPlayer player, Throwable error) {

            }
        });

        findViewById(R.id.text_to_speech).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mscPlayer != null) {
                    mscPlayer.beginPlay("科大讯飞语音合成开发 ...");
                }
            }
        });
    }

    private MscSettings mscSettings = new MscSettings() {
        @Override
        public MscSpeaker getSpeaker() {
            return IFlyTekMscHelper.DEFAULT_SPEAKER;
        }

        @Override
        public int getSpeed() {
            return IFlyTekMscHelper.DEFAULT_SPEED;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mscPlayer != null) {
            mscPlayer.destroy();
            mscPlayer = null;
        }
    }
}
