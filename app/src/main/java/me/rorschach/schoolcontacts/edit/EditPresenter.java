package me.rorschach.schoolcontacts.edit;

import android.support.annotation.NonNull;
import android.util.Log;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lei on 16-5-8.
 */
public class EditPresenter implements EditContract.Presenter {

    private static final String TAG = "EditPresenter";

    private ContactRepository mRepository;
    private EditContract.View mView;

    public EditPresenter(@NonNull ContactRepository repository, @NonNull EditContract.View view) {
        mRepository = repository;
        mView = view;

        mView.setPresenter(this);
    }

    @Override
    public void addContact(@NonNull final Contact contact) {
        Single
                .create(new Single.OnSubscribe<Contact>() {
                    @Override
                    public void call(SingleSubscriber<? super Contact> singleSubscriber) {
                        mRepository.add(contact);

                        singleSubscriber.onSuccess(contact);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Contact>() {
                    @Override
                    public void onSuccess(Contact value) {
                        if (mView.isActive()) {
                            mView.onAdd(contact);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "onError: " + error.getMessage());
                    }
                });
    }

    @Override
    public void editContact(@NonNull final Contact contact) {
        Single
                .create(new Single.OnSubscribe<Contact>() {
                    @Override
                    public void call(SingleSubscriber<? super Contact> singleSubscriber) {
                        mRepository.update(contact);

                        singleSubscriber.onSuccess(contact);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Contact>() {
                    @Override
                    public void onSuccess(Contact value) {
                        if (mView.isActive()) {
                            mView.onEdit(contact);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "onError: " + error.getMessage());
                    }
                });
    }

    @Override
    public void loadColleges() {
        Single
                .create(new Single.OnSubscribe<List<String>>() {
                    @Override
                    public void call(SingleSubscriber<? super List<String>> singleSubscriber) {
                        List<Contact> contacts = mRepository.loadAllColleges();
                        List<String> colleges = new ArrayList<>();

                        for (Contact contact : contacts) {
                            colleges.add(contact.getCollege());
                        }

                        Collections.sort(colleges, Collator.getInstance(Locale.CHINESE));

                        singleSubscriber.onSuccess(colleges);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<List<String>>() {
                    @Override
                    public void onSuccess(List<String> value) {
                        if (mView.isActive()) {
                            mView.onCollegesLoad(value);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.d(TAG, "onError: " + error.getMessage());
                    }
                });
    }

    @Override
    public void start() {
        loadColleges();
    }

    @Override
    public void destroy() {
        mView = null;
    }
}
