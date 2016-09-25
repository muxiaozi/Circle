package cn.muxiaozi.circle;

import org.junit.Test;


import cn.muxiaozi.circle.game.flappy_bird.DataFactory;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testSize(){
        byte[] data = DataFactory.packStartGame(new String[]{"123123123123","123123123123","123123123123","123123123123",
                "123123123123","123123123123","123123123123","123123123123"});
        System.out.print(data.length);

    }
}