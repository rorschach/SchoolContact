package me.rorschach.schoolcontacts.detail;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.HistoryRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.data.local.ContactType;
import me.rorschach.schoolcontacts.data.local.History;
import me.rorschach.schoolcontacts.data.local.History_Table;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lei on 16-5-1.
 */
public class DetailPresenter implements DetailContract.Presenter {

    private ContactRepository mContactRepository;
    private HistoryRepository mHistoryRepository;
    private DetailContract.View mView;

    private static final String TAG = "DetailPresenter";

    private DateTime startTime;

    public DetailPresenter(@NonNull ContactRepository contactRepository,
                           @NonNull HistoryRepository historyRepository,
                           @NonNull DetailContract.View view) {
        mContactRepository = contactRepository;
        mHistoryRepository = historyRepository;
        mView = view;

        mView.setPresenter(this);
    }

    @Override
    public void addRecord(@NonNull final Contact contact) {
        Single
                .create(new Single.OnSubscribe<History>() {
                    @Override
                    public void call(SingleSubscriber<? super History> singleSubscriber) {

                        History history = new History();
                        history.setContactsId(contact.getId());
                        history.setContactType(ContactType.PHONE);
                        history.setBeginTime(startTime);
                        history.setEndTime(new DateTime());

                        mHistoryRepository.add(history);

                        singleSubscriber.onSuccess(history);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<History>() {
                    @Override
                    public void onSuccess(History history) {
                        if (mView.isActive()) {
                            if (history != null) {
                                mView.showAddRecord(history);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "onError: " + error.getMessage());
                    }
                });
    }

    @Override
    public void loadRecord(@NonNull final Contact contact) {
        Single
                .create(new Single.OnSubscribe<List<History>>() {
                    @Override
                    public void call(SingleSubscriber<? super List<History>> singleSubscriber) {

                        List<History> histories = mHistoryRepository
                                .queryList(History.class, History_Table.contactsId.eq(contact.getId()));

                        singleSubscriber.onSuccess(histories);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<List<History>>() {
                    @Override
                    public void onSuccess(List<History> histories) {
                        if (mView.isActive()) {
                            if (histories == null || histories.isEmpty()) {
                                mView.showNoRecord();
                            } else {
                                mView.showRecord(histories);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "onError: " + error.getMessage());
                    }
                });
    }


    @Override
    public void call(@NonNull String phone) {

        Uri uri = Uri.parse("tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        try {
            ((Activity) mView.getContext()).startActivityForResult(intent, 0x00);
            startTime = new DateTime();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendSms(@NonNull String phone) {

        Uri uri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("exit_on_sent", true);
        try {
            mView.getContext().startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @DebugLog
    @Override
    public void update(final Contact contact) {
        Single
                .create(new Single.OnSubscribe<Contact>() {
                    @Override
                    public void call(SingleSubscriber<? super Contact> singleSubscriber) {
                        mContactRepository.update(contact);

                        singleSubscriber.onSuccess(contact);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Contact>() {
                    @Override
                    public void onSuccess(Contact contact1) {
                        if (mView.isActive()) {
                            mView.onUpdate();
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "onError: " + error.getMessage());
                    }
                });
    }

    @Override
    public void delete(final Contact contact) {
        Single
                .create(new Single.OnSubscribe<Contact>() {
                    @Override
                    public void call(SingleSubscriber<? super Contact> singleSubscriber) {
                        mContactRepository.remove(contact);

                        singleSubscriber.onSuccess(contact);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Contact>() {
                    @Override
                    public void onSuccess(Contact contact1) {
                        if (mView.isActive()) {
                            mView.onDelete();
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
