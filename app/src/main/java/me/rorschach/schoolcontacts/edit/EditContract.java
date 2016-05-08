package me.rorschach.schoolcontacts.edit;

import android.support.annotation.NonNull;

import java.util.List;

import me.rorschach.schoolcontacts.base.BasePresenter;
import me.rorschach.schoolcontacts.base.BaseView;
import me.rorschach.schoolcontacts.data.local.Contact;

/**
 * Created by lei on 16-5-8.
 */
public interface EditContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void onCollegesLoad(@NonNull List<String>colleges);

        void onAdd(@NonNull Contact contact);

        void onEdit(@NonNull Contact contact);

        void onCancel();

    }


    interface Presenter extends BasePresenter {

        void loadColleges();

        void addContact(@NonNull Contact contact);

        void editContact(@NonNull Contact contact);

    }

}
