package cn.muxiaozi.circle.game.link.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import cn.muxiaozi.circle.utils.LogUtil;

/**
 * Created by 慕宵子 on 2016/9/25 0025.
 */

public class PlayerActor extends Actor {
    private TextureRegion background;
    private TextureRegion meRegion;
    private TextureRegion otherRegion;

    private int currentIndex;

    private int myIndex;

    private int cellWidth;

    private int playerCount;

    public PlayerActor(Pixmap pixmap, int playerCount, int myIndex) {
        setSize(pixmap.getWidth(), pixmap.getHeight());
        cellWidth = pixmap.getWidth() / playerCount;
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
        forePixmap.setColor(Color.GREEN);
        forePixmap.fillRectangle(0, 0, 10, 10);
        forePixmap.setColor(Color.GRAY);
        forePixmap.fillRectangle(10, 0, 10, 10);
        Texture texture = new Texture(forePixmap);

        meRegion = new TextureRegion(texture, 0, 0, 10, 10);
        otherRegion = new TextureRegion(texture, 10, 0, 10, 10);

        currentIndex = 0;
        this.myIndex = myIndex;
        this.playerCount = playerCount;
    }

    public void takeTurn(int index) {
        currentIndex = index;
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

        for (int i = 0; i < playerCount; i++) {
            if (i == currentIndex) {
                batch.draw(i == myIndex ? meRegion : otherRegion,
                        getX() + cellWidth * i + 5, getY() + 5,
                        getOriginX(), getOriginY(),
                        cellWidth - 10, getHeight() - 10,
                        getScaleX(), getScaleY(),
                        getRotation());
            }
        }

        batch.setColor(tempBatchColor);
    }
}
