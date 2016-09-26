package cn.muxiaozi.circle.game.link.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import cn.muxiaozi.circle.game.framwork.BaseActor;

/**
 * Created by 慕宵子 on 2016/9/25 0025.
 */

public class PlayerActor extends Actor {
    private TextureRegion background;
    private TextureRegion foreground;
    private int playerCount;

    PlayerActor(Pixmap pixmap, int playerCount) {
        int cellWidth = pixmap.getWidth() / playerCount;
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixmap.setColor(Color.BLACK);
        for (int i = 0; i < playerCount; i++) {
            pixmap.drawRectangle(i * cellWidth + 4, 4, cellWidth - 8, (int) (getHeight() - 8));
        }
        background = new TextureRegion(new Texture(pixmap));

        Pixmap forePixmap = new Pixmap(20, 10, Pixmap.Format.RGB888);
        forePixmap.setColor(Color.WHITE);
        forePixmap.fill();
        forePixmap.setColor(Color.DARK_GRAY);
        forePixmap.fillRectangle(0, 0, 10, 10);
        forePixmap.setColor(Color.LIGHT_GRAY);
        forePixmap.fillRectangle(10, 0, 10, 10);

        this.playerCount = playerCount;
    }

    void takeTurn(int index) {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Color tempBatchColor = batch.getColor();

        Color color = getColor();

        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        batch.draw(background,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation());

        batch.draw(foreground,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation());

        batch.setColor(tempBatchColor);
    }
}
