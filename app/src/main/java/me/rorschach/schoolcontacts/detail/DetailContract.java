package me.rorschach.schoolcontacts.detail;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import me.rorschach.schoolcontacts.base.BasePresenter;
import me.rorschach.schoolcontacts.base.BaseView;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.data.local.History;

/**
 * Created by lei on 16-4-24.
 */
public interface DetailContract {

    interface View extends BaseView<Presenter> {

        void showDetail(@NonNull Contact contact);

        Context getContext();

        boolean isActive();

        void onCall();

        void onSms();

        void onUpdate();

        void onDelete();

        void showRecord(List<History> histories);

        void showAddRecord(History history);

        void showNoRecord();
    }

    interface Presenter extends BasePresenter {

        void addRecord(@NonNull Contact contact);

        void loadRecord(@NonNull Contact contact);

        void call(@NonNull String phone);

        void sendSms(@NonNull String phone);

        void update(@NonNull Contact contact);

        void delete(@NonNull Contact contact);

    }
}
