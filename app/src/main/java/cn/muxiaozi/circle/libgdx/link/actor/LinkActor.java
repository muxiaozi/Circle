package cn.muxiaozi.circle.libgdx.link.actor;

import android.graphics.Point;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.List;

import cn.muxiaozi.circle.libgdx.BaseActor;
import cn.muxiaozi.circle.libgdx.link.Res;
import cn.muxiaozi.circle.utils.LogUtil;


/**
 * Created by 慕宵子 on 2016/9/22 0022.
 * <p>
 * 地图Actor，负责显示地图
 */

public class LinkActor extends BaseActor {
    private Pixmap pixmap;

    public LinkActor(Pixmap pixmap) {
        this.pixmap = pixmap;
    }

    public void link(List<Point> pointList) {
        if (pointList != null) {
            pixmap.setColor(Color.WHITE);
            pixmap.fill();

            pixmap.setColor(Color.RED);
            for (int i = 0; i < pointList.size() - 1; i++) {
                LogUtil.d("start:" + pointList.get(i).x + "," + pointList.get(i).y +
                        " end:" + pointList.get(i + 1).x + "," + pointList.get(i + 1).y);
                pixmap.drawLine((int) ((pointList.get(i).x + 0.5) * Res.CELL_SIZE),
                        (int) (pixmap.getHeight() - (pointList.get(i).y + 0.5) * Res.CELL_SIZE),
                        (int) ((pointList.get(i + 1).x + 0.5) * Res.CELL_SIZE),
                        (int) (pixmap.getHeight() - (pointList.get(i + 1).y + 0.5) * Res.CELL_SIZE));
            }

            setRegion(new TextureRegion(new Texture(pixmap)));

            addAction(Actions.delay(0.3F, Actions.run(new Runnable() {
                @Override
                public void run() {
                    setRegion(null);
                }
            })));
        }
    }
}
