package cn.muxiaozi.circle;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void testSize() {
        byte data[] = new byte[]{-3, -2, -1, 0, 1, 2, 3};
        for (byte aData : data) {
            System.out.println(aData + ":" + (aData & 1));
        }
    }
}