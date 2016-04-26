package me.rorschach.schoolcontacts.contacts;

import android.support.annotation.NonNull;

import java.util.List;

import me.rorschach.schoolcontacts.base.BasePresenter;
import me.rorschach.schoolcontacts.base.BaseView;
import me.rorschach.schoolcontacts.data.local.Contact;

/**
 * Created by lei on 16-4-24.
 */
public interface ContactsContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showContacts(List<Contact> contacts);

        void showNoContact();

        void showAddContact();

        void showDeleContact();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadContacts(String college);

        void addContact(@NonNull Contact contact);

        void deleteContact(@NonNull Contact contact);
    }
}
