package cn.muxiaozi.circle.game.flappy_bird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

import cn.muxiaozi.circle.base.Constants;

/**
 * Created by 慕宵子 on 2016/8/21 0021.
 * Email: 1002042998@qq.com
 */
public class SoundsManager {
    /**
     * 碰撞到水管的音效
     */
    private Sound hitSound;

    /**
     * 得分音效
     */
    private Sound scoreSound;

    /**
     * 点击屏幕时播放的音效
     */
    private Sound touchSound;

    /**
     * 撞到地板时的音效
     */
    private Sound dieSound;

    /**
     * 点击按钮恢复到初始状态时播放的音效
     */
    private Sound restartSound;

    private boolean allowSounds;

    SoundsManager(AssetManager manager) {
        hitSound = manager.get(Res.Audios.AUDIO_HIT, Sound.class);
        scoreSound = manager.get(Res.Audios.AUDIO_SCORE, Sound.class);
        touchSound = manager.get(Res.Audios.AUDIO_TOUCH, Sound.class);
        dieSound = manager.get(Res.Audios.AUDIO_DIE, Sound.class);
        restartSound = manager.get(Res.Audios.AUDIO_RESTART, Sound.class);

        allowSounds = Gdx.app.getPreferences(Constants.CIRCLE_CONFIG)
                .getBoolean(Constants.ConfigOption.SOUNDS, true);
    }

    public void hit() {
        if (allowSounds) {
            hitSound.play();
        }
    }

    public void score() {
        if (allowSounds) {
            scoreSound.play();
        }
    }

    public void touch() {
        if (allowSounds) {
            touchSound.play();
        }
    }

    public void die() {
        if (allowSounds) {
            dieSound.play();
        }
    }

    public void restart() {
        if (allowSounds) {
            restartSound.play();
        }
    }
}
