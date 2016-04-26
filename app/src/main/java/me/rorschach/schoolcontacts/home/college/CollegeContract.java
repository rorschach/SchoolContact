package me.rorschach.schoolcontacts.home.college;

import java.util.List;

import me.rorschach.schoolcontacts.base.BasePresenter;
import me.rorschach.schoolcontacts.base.BaseView;

/**
 * Created by lei on 16-4-24.
 */
public interface CollegeContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showColleges(List<String> colleges);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void loadColleges();
    }
}
