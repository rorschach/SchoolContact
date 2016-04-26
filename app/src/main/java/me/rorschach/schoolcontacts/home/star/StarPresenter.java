package me.rorschach.schoolcontacts.home.star;

import android.support.annotation.NonNull;

import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lei on 16-4-25.
 */
public class StarPresenter implements StarContract.Presenter {

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
                        List<Contact> stared = mRepository.loadAllStared();

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
                            mView.showStars(stared);
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

}
