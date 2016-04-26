package me.rorschach.schoolcontacts.base;

/**
 * Created by lei on 16-4-24.
 */
public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

}
