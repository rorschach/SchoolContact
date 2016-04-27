package me.rorschach.schoolcontacts.search;

import android.support.annotation.NonNull;

import java.util.List;

import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.data.local.Contact_Table;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lei on 16-4-27.
 */
public class SearchPresenter implements SearchContract.Presenter {


    private ContactRepository mRepository;
    private SearchContract.View mView;

    public SearchPresenter(@NonNull ContactRepository repository,
                           @NonNull SearchContract.View view) {
        mRepository = repository;
        mView = view;

        mView.setPresenter(this);
    }

    @Override
    public void search(final String keyword) {
        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {

                        List<Contact> result = mRepository.queryList(Contact.class,
                                Contact_Table.name.like(keyword), Contact_Table.phone.like(keyword));

                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contact>>() {
                    @Override
                    public void onCompleted() {
                        if (mView.isActive()) {
                            mView.setLoadingIndicator(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isActive()) {
                            mView.setLoadingIndicator(false);
                        }
                    }

                    @Override
                    public void onNext(List<Contact> result) {
                        if (mView.isActive()) {

                            if (result.isEmpty()) {
                                mView.showNoResult();
                            }else{
                                mView.showSearchResult(result);

                            }
                        }
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
