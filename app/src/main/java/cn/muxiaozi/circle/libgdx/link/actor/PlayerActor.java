package cn.muxiaozi.circle.libgdx.link.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by 慕宵子 on 2016/9/25 0025.
 */

public class PlayerActor extends Actor implements Disposable {
    private TextureRegion background;
    private TextureRegion meRegion;
    private TextureRegion otherRegion;

    //记录玩家分数
    private int[] grades;

    private BitmapFont gradeFont;

    //每个小方框的宽度
    private int cellWidth;

    //当前玩家的index
    private int currentIndex;

    //我的index
    private int myIndex;

    //玩家的数量
    private int playerCount;

    public PlayerActor(Pixmap pixmap, int playerCount, int myIndex) {
        super();
        setSize(pixmap.getWidth(), pixmap.getHeight());
        //初始化每个小格的宽度
        cellWidth = pixmap.getWidth() / playerCount;

        gradeFont = new BitmapFont();
        gradeFont.getData().setScale(1.5F);
        gradeFont.setColor(Color.BLACK);

        grades = new int[playerCount];

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

    public void addGrade(int index) {
        grades[index]++;
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
            gradeFont.draw(batch, String.valueOf(grades[i]),
                    (float) ((i + 0.5) * cellWidth), (getHeight() - 10));
        }

        batch.setColor(tempBatchColor);
    }

    @Override
    public void dispose() {
        gradeFont.dispose();
    }
}
