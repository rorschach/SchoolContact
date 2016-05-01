package me.rorschach.schoolcontacts.contacts;

import android.support.annotation.NonNull;
import android.util.Log;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.data.local.Contact_Table;
import me.rorschach.schoolcontacts.util.HanziToPinyin;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lei on 16-5-1.
 */
public class ContactsPresenter implements ContactsContract.Presenter {

    private ContactRepository mRepository;
    private ContactsContract.View mView;

    private static final String TAG = "ContactsPresenter";

    public ContactsPresenter(@NonNull ContactRepository repository,
                             @NonNull ContactsContract.View view) {
        mRepository = repository;
        mView = view;

        mView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadContacts(final String college) {
        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {

                        List<Contact> contacts = mRepository.queryList(
                                Contact.class, Contact_Table.college.eq(college));

                        Collections.sort(contacts, new Comparator<Contact>() {
                            @Override
                            public int compare(Contact lhs, Contact rhs) {
                                return Collator.getInstance(Locale.CHINESE)
                                        .compare(HanziToPinyin.getPinYin(
                                                    ((lhs.getName().charAt(0) + "").charAt(0) + "")),
                                                HanziToPinyin.getPinYin(
                                                        (rhs.getName().charAt(0) + "").charAt(0) + ""));
                            }
                        });

                        subscriber.onNext(contacts);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contact>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        if (mView.isActive()) {
                            mView.showNoContact();
                        }
                    }

                    @Override
                    public void onNext(List<Contact> contacts) {
                        if (mView.isActive()) {
                            if (contacts == null || contacts.isEmpty()) {
                                mView.showNoContact();
                            } else {
                                mView.showContacts(contacts);
                            }
                        }
                    }
                });
    }

    @Override
    public void addContact(@NonNull final Contact contact) {
        Single
                .create(new Single.OnSubscribe<String>() {
                    @Override
                    public void call(SingleSubscriber<? super String> singleSubscriber) {

                        mRepository.add(contact);

                        singleSubscriber.onSuccess("");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<String>() {
                    @Override
                    public void onSuccess(String value) {
                        Log.d(TAG, "onSuccess: " + value);
                        if (mView.isActive()) {
                            mView.showAddContact(contact);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "onError: " + error.getMessage());
                    }
                });
    }

    @Override
    public void deleteContact(@NonNull final Contact contact) {
        Single
                .create(new Single.OnSubscribe<String>() {
                    @Override
                    public void call(SingleSubscriber<? super String> singleSubscriber) {

                        mRepository.remove(contact);

                        singleSubscriber.onSuccess("");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<String>() {
                    @Override
                    public void onSuccess(String value) {
                        Log.d(TAG, "onSuccess: " + value);
                        if (mView.isActive()) {
                            mView.showDeleteContact(contact);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "onError: " + error.getMessage());
                    }
                });
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {
        mView = null;
    }
}
