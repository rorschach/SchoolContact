package me.rorschach.schoolcontacts.home.history;

import android.support.annotation.NonNull;

import java.util.List;

import me.rorschach.schoolcontacts.base.BasePresenter;
import me.rorschach.schoolcontacts.base.BaseView;
import me.rorschach.schoolcontacts.data.local.History;

/**
 * Created by lei on 16-4-24.
 */
public interface HistoryContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showHistories(List<History> histories);

        void showNoHistory();

        void showAddHistory(History history);

        void showDeleteHistory(History history);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void loadHistories();

        void addToHistory(@NonNull History history);

        void deleteHistory(@NonNull History history);
    }

}
