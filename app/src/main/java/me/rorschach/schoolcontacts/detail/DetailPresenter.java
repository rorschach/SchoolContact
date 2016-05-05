package me.rorschach.schoolcontacts.detail;

import android.support.annotation.NonNull;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.HistoryRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.data.local.History;
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

    private DateTime startTime;

    private static final String TAG = "DetailPresenter";

    public DetailPresenter(@NonNull ContactRepository contactRepository,
                           @NonNull HistoryRepository historyRepository,
                           @NonNull DetailContract.View view) {
        mContactRepository = contactRepository;
        mHistoryRepository = historyRepository;
        mView = view;

        mView.setPresenter(this);
    }

    @DebugLog
    @Override
    public void addRecord(@NonNull final String name,
                          @NonNull final String phone,
                          @NonNull final DateTime startTime,
                          @NonNull final DateTime endTime) {
        Single
                .create(new Single.OnSubscribe<History>() {
                    @Override
                    public void call(SingleSubscriber<? super History> singleSubscriber) {

                        History history = new History();
                        history.setName(name);
                        history.setPhone(phone);
                        history.setBeginTime(startTime);
                        history.setEndTime(endTime);

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

    @DebugLog
    @Override
    public void loadRecord(@NonNull final String phone) {
        Single
                .create(new Single.OnSubscribe<List<History>>() {
                    @Override
                    public void call(SingleSubscriber<? super List<History>> singleSubscriber) {

                        List<History> histories = mHistoryRepository.loadLast(phone);

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
