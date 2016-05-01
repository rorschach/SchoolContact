package me.rorschach.schoolcontacts;

import org.junit.Test;

/**
 * Created by lei on 16-5-1.
 */
public class HanziToPinyinTest {

    @Test
    public void testGetPinYin() throws Exception {

        String source = "哈哈哈";

        String result = HanziToPinyin.getPinYin(source);

        System.out.println(result);

    }
}