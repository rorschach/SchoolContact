package me.rorschach.schoolcontacts.home.history;

import android.support.annotation.NonNull;

import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.data.HistoryRepository;
import me.rorschach.schoolcontacts.data.local.History;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lei on 16-4-25.
 */
public class HistoryPresenter implements HistoryContract.Presenter {

    private HistoryRepository mRepository;
    private HistoryContract.View mView;

    public HistoryPresenter(@NonNull HistoryRepository repository,
                            @NonNull HistoryContract.View view) {
        mView = view;
        mRepository = repository;

        mView.setPresenter(this);
    }

    @DebugLog
    @Override
    public void start() {
        loadHistories();
    }

    @Override
    public void destroy() {
        mView = null;
    }

    @Override
    public void loadHistories() {
        Observable
                .create(new Observable.OnSubscribe<List<History>>() {
                    @Override
                    public void call(Subscriber<? super List<History>> subscriber) {

                        List<History> histories = mRepository.loadAll();

                        subscriber.onNext(histories);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<History>>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onNext(List<History> histories) {
                                   if (mView.isActive()) {
                                       if (histories.isEmpty()) {
                                           mView.showNoHistory();
                                       } else {
                                           mView.showHistories(histories);
                                       }
                                   }
                               }
                           }
                );
    }

    @Override
    public void addToHistory(@NonNull History history) {

        mRepository.add(history);

        if (mView.isActive()) {
            mView.showAddHistory();
        }
    }

    @Override
    public void deleteHistory(@NonNull History history) {

        mRepository.remove(history);

        if (mView.isActive()) {
            mView.showDeleteHistory();
        }
    }

}
