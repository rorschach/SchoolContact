package me.rorschach.schoolcontacts.detail;

import android.support.annotation.NonNull;

import me.rorschach.schoolcontacts.base.BasePresenter;
import me.rorschach.schoolcontacts.base.BaseView;
import me.rorschach.schoolcontacts.data.local.Contact;

/**
 * Created by lei on 16-4-24.
 */
public interface DetailContract {

    interface View extends BaseView<Presenter> {

        void showDetail(@NonNull Contact contact);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void loadContact();

        void call();

        void sendSms();

        void update(Contact contact);

        void delete(Contact contact);

    }
}
