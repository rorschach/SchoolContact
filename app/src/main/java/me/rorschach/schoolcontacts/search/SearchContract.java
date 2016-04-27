package me.rorschach.schoolcontacts.search;

import java.util.List;

import me.rorschach.schoolcontacts.base.BasePresenter;
import me.rorschach.schoolcontacts.base.BaseView;
import me.rorschach.schoolcontacts.data.local.Contact;

/**
 * Created by lei on 16-4-24.
 */
public interface SearchContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showSearchResult(List<Contact> contacts);

        void showNoResult();

        boolean isActive();
    }

    interface Presenter extends BasePresenter{

        void search(String keyword);
    }
}
