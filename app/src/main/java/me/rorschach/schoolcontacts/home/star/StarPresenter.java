package me.rorschach.schoolcontacts.home.star;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.data.local.Contact_Table;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lei on 16-4-25.
 */
public class StarPresenter implements StarContract.Presenter {

    private static final String TAG = "StarPresenter";

    private ContactRepository mRepository;
    private StarContract.View mView;

    public StarPresenter(@NonNull ContactRepository repository,
                         @NonNull StarContract.View view) {
        mRepository = repository;
        mView = view;

        mView.setPresenter(this);
    }

    @DebugLog
    @Override
    public void start() {
        loadStars();
    }

    @Override
    public void destroy() {
        mView = null;
    }

    @Override
    public void loadStars() {

        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {
                        List<Contact> stared = mRepository
                                .queryList(Contact.class, Contact_Table.stared.eq(true));

                        subscriber.onNext(stared);
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

                    }

                    @Override
                    public void onNext(List<Contact> stared) {
                        if (mView.isActive()) {
                            if (stared == null || stared.isEmpty()) {
                                mView.showNoStar();
                            } else {
                                mView.showStars(stared);
                            }
                        }
                    }
                });

    }

    @Override
    public void insertStar(@NonNull Contact contact) {
        mRepository.add(contact);

        mView.showAddStar();
    }

    @Override
    public void deleteStar(@NonNull Contact contact) {
        mRepository.remove(contact);

        mView.showDeleteStar();
    }

    public void deleteAll() {
        Single
                .create(new Single.OnSubscribe<Void>() {
                    @Override
                    public void call(SingleSubscriber<? super Void> singleSubscriber) {
                        mRepository.deleteAll();

                        singleSubscriber.onSuccess(null);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Void>() {
                    @Override
                    public void onSuccess(Void value) {
                        if (mView.isActive()) {
                            mView.showNoStar();
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "onError: " + error.getMessage());
                    }
                });
    }

}
