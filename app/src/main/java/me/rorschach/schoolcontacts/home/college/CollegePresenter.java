package me.rorschach.schoolcontacts.home.college;

import android.support.annotation.NonNull;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
public class CollegePresenter implements CollegeContract.Presenter {

    private ContactRepository mRepository;

    private CollegeContract.View mView;

    public CollegePresenter(@NonNull ContactRepository repository,
                            @NonNull CollegeContract.View view) {
        mRepository = repository;
        mView = view;

        mView.setPresenter(this);
    }

    @DebugLog
    @Override
    public void start() {
        loadColleges();
    }

    @Override
    public void destroy() {
        mView = null;
    }

    @DebugLog
    @Override
    public void loadColleges() {

        Observable
                .create(new Observable.OnSubscribe<List<String>>() {
                    @Override
                    public void call(Subscriber<? super List<String>> subscriber) {

                        List<Contact> contacts = mRepository.loadAllColleges();
                        List<String> colleges = new ArrayList<>();

                        for (Contact contact : contacts) {
                            colleges.add(contact.getCollege());
                        }

                        Collections.sort(colleges, Collator.getInstance(Locale.CHINESE));

                        subscriber.onNext(colleges);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(List<String> strings) {

                                   if (mView.isActive()) {
                                       mView.showColleges(strings);
                                   }
                               }
                           }
                );
    }
}
