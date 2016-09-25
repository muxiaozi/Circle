package cn.muxiaozi.circle.game.flappy_bird.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import cn.muxiaozi.circle.game.flappy_bird.MainGame;
import cn.muxiaozi.circle.game.flappy_bird.Res;
import cn.muxiaozi.circle.game.framwork.AnimationActor;
import cn.muxiaozi.circle.game.framwork.BaseActor;
import cn.muxiaozi.circle.game.framwork.BaseGroup;


/**
 * 分数结果显示组合, 显示 奖牌、分数、最好的分数
 *
 * @xietansheng
 */
public class ResultGroup extends BaseGroup<MainGame> {

    /**
     * 背景
     */
    private BaseActor gameResultBgActor;

    /**
     * 奖牌
     */
    private AnimationActor medalActor;

    /**
     * 当前分数
     */
    private NumGroup currScoreActor;

    /**
     * 最高分数
     */
    private NumGroup bestScoreActor;

    public ResultGroup(MainGame mainGame) {
        super(mainGame);
        init();
    }


    private void init() {
        // 创建背景
        gameResultBgActor = new BaseActor(getGame().getAtlas().findRegion(Res.Atlas.IMAGE_GAME_RESULT_BG));
        addActor(gameResultBgActor);
        // 设置 ResultGroup 的高度为背景高度
        setSize(gameResultBgActor.getWidth(), gameResultBgActor.getHeight());

        // 创建奖牌
        medalActor = new AnimationActor(0, getGame().getAtlas().findRegions(Res.Atlas.IMAGE_GAME_MEDAL_01_TO_04));
        medalActor.setPosition(44, 45);
        medalActor.setVisible(false);
        medalActor.setPlayAnimation(false);
        addActor(medalActor);

        // 创建当前分数的显示
        currScoreActor = new NumGroup(getGame(), Res.Atlas.IMAGE_NUM_SCORE_00_TO_09);
        currScoreActor.setRightX(290);
        currScoreActor.setY(95);
        addActor(currScoreActor);

        // 创建最佳分数的显示
        bestScoreActor = new NumGroup(getGame(), Res.Atlas.IMAGE_NUM_SCORE_00_TO_09);
        bestScoreActor.setRightX(290);
        bestScoreActor.setY(35);
        addActor(bestScoreActor);

        // 更新最佳分数
        updateBestScore();
    }

    /**
     * 更新当前分数
     */
    public void updateCurrScore(int currScore) {

        if (currScore < 0) {
            currScore = 0;
        }

        Preferences prefs = Gdx.app.getPreferences(Res.Prefs.PREFS_FILE_NAME);

        // 如果当前分数大于历史保存的最佳分数, 则更新最佳分数
        if (currScore > prefs.getInteger(Res.Prefs.KEY_BEST_SCORE, 0)) {
            // 持久化保存最佳分数到本地
            prefs.putInteger(Res.Prefs.KEY_BEST_SCORE, currScore).flush();

            // 更新最佳分数显示
            updateBestScore();
        }

        currScoreActor.setNum(currScore);
        currScoreActor.setRightX(290);

        // 根据分数设置不同等级的奖牌
        medalActor.setVisible(true);
        if (currScore >= 40) {
            medalActor.setFixedShowKeyFrameIndex(3);
        } else if (currScore >= 30) {
            medalActor.setFixedShowKeyFrameIndex(2);
        } else if (currScore >= 20) {
            medalActor.setFixedShowKeyFrameIndex(1);
        } else if (currScore >= 10) {
            medalActor.setFixedShowKeyFrameIndex(0);
        } else {
            medalActor.setVisible(false);
        }
    }

    /**
     * 更新最好的分数到界面
     */
    private void updateBestScore() {
        // 取出最佳分数
        Preferences prefs = Gdx.app.getPreferences(Res.Prefs.PREFS_FILE_NAME);
        int bestScore = prefs.getInteger(Res.Prefs.KEY_BEST_SCORE, 0);
        bestScoreActor.setNum(bestScore);
        bestScoreActor.setRightX(290);
    }

}



















