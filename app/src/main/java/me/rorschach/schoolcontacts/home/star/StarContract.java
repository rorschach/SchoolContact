package me.rorschach.schoolcontacts.home.star;

import android.support.annotation.NonNull;

import java.util.List;

import me.rorschach.schoolcontacts.base.BasePresenter;
import me.rorschach.schoolcontacts.base.BaseView;
import me.rorschach.schoolcontacts.data.local.Contact;

/**
 * Created by lei on 16-4-24.
 */
public interface StarContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showStars(List<Contact> contacts);

        void showNoStar();

        void showAddStar();

        void showDeleteStar();

        boolean isActive();

    }

    interface Presenter extends BasePresenter {

        void loadStars();

        void insertStar(@NonNull Contact contact);

        void deleteStar(@NonNull Contact contact);
    }
}
