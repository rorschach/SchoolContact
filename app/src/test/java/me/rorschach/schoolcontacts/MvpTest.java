package me.rorschach.schoolcontacts;


import org.junit.Test;

import me.rorschach.schoolcontacts.mvp.TestPresenter;
import me.rorschach.schoolcontacts.mvp.TestView;

/**
 * Created by lei on 16-4-10.
 */
public class MvpTest {

    @Test
    public void testMvp() throws Exception {
        TestView testView = new TestView();
        TestPresenter testPresenter = new TestPresenter();
        testView.setPresenter(testPresenter);
        testPresenter.attachView(testView);
        testView.onDoSthBackground();
    }
}
