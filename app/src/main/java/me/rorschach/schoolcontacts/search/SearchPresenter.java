package me.rorschach.schoolcontacts.search;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.data.ContactRepository;
import me.rorschach.schoolcontacts.data.local.Contact;
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

    private static final String TAG = "SearchPresenter";

    public SearchPresenter(@NonNull ContactRepository repository,
                           @NonNull SearchContract.View view) {
        mRepository = repository;
        mView = view;

        mView.setPresenter(this);
    }

    @DebugLog
    @Override
    public void search(final String keyword) {

        Observable
                .create(new Observable.OnSubscribe<List<Contact>>() {
                    @Override
                    public void call(Subscriber<? super List<Contact>> subscriber) {

//                        if (keyword == null || "".equals(keyword)) {
//                            subscriber.onNext(null);
//                        }

                        List<Contact> result = mRepository.searchByKey(keyword);

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
                        Log.e(TAG, "onError: " + e.getMessage());
                        if (mView.isActive()) {
                            mView.showNoResult();
                            mView.setLoadingIndicator(false);
                        }
                    }

                    @Override
                    public void onNext(List<Contact> result) {
                        if (mView.isActive()) {

                            if (result == null || result.isEmpty()) {
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
        mView.showNoResult();
    }

    @Override
    public void destroy() {
        mView = null;
    }
}
