package cn.muxiaozi.circle.game.link.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Align;

import cn.muxiaozi.circle.game.link.Res;

/**
 * Created by 慕宵子 on 2016/9/21 0021.
 * <p>
 * 方块
 */

public class DiamondActor extends Actor {

    //纹理集
    private TextureAtlas atlas;

    //宝石纹理
    private TextureAtlas.AtlasRegion texture;

    //是否聚焦
    private boolean isFocused;

    //方块类型
    private byte type;

    //获取焦点后特效，透明度一直变化
    private RepeatAction focusAction = new RepeatAction();

    public DiamondActor(TextureAtlas atlas, byte type) {
        this.atlas = atlas;
        this.type = type;
        if (type == -1) {
            texture = null;
        } else {
            texture = atlas.findRegion(Res.Atlas.DIAMOND, type);
        }
        isFocused = false;
        focusAction.setAction(new SequenceAction(Actions.fadeOut(0.5F), Actions.fadeIn(0.2F)));
        focusAction.setCount(1000);
    }

    public void setFocus(boolean isFocused) {
        if (isFocused != this.isFocused) {
            this.isFocused = isFocused;
            if (isFocused) {
                addAction(focusAction);
                focusAction.restart();
            } else {
                removeAction(focusAction);
                setAlpha(1.0F);
            }
        }
    }

    private void setAlpha(float alpha) {
        Color oldColor = getColor();
        oldColor.a = alpha;
        setColor(oldColor);
    }

    public boolean isFocused() {
        return isFocused;
    }

    private byte getType() {
        return type;
    }

    public void setType(byte type) {
        if (this.type != type) {
            isFocused = false;
            this.type = type;

            clearActions();

            if (type != -1) {
                setAlpha(1.0F);
                setScale(1.0F);
                texture = atlas.findRegion(Res.Atlas.DIAMOND, type);
            } else {
                setOrigin(Align.center);
                addAction(Actions.scaleTo(0f, 0f, 0.2F));
                addAction(Actions.after(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if (getType() == -1) {    //可能在执行动画期间图标被刷新
                            texture = null;
                        }
                    }
                })));
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (texture == null)
            return;

        Color tempBatchColor = batch.getColor();
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(texture,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation());
        batch.setColor(tempBatchColor);
    }
}
