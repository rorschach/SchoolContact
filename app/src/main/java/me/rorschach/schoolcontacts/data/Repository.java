package me.rorschach.schoolcontacts.data;

import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by lei on 16-4-10.
 */
public interface Repository<T extends BaseModel> {

    void add(T item);

    void add(Iterable<T> items);

    void update(T item);

    void remove(T item);

    T querySingle(Class<T> clz, Condition... conditions);

    List<T> queryList(Class<T> clz, Condition... conditions);
}
