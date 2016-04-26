package me.rorschach.schoolcontacts.data;

import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import me.rorschach.schoolcontacts.data.local.History;

/**
 * Created by lei on 16-4-10.
 */
public class HistoryRepository implements Repository<History> {

    private static HistoryRepository INSTANCE;

    public static HistoryRepository getInstance() {
        INSTANCE = RepositoryHolder.REPOSITORY;
        return INSTANCE;
    }

    public static void destoryInstance() {
        INSTANCE = null;
    }

    private static class RepositoryHolder{
        public static final HistoryRepository REPOSITORY = new HistoryRepository();
    }

    private HistoryRepository(){}

    @Override
    public void add(History item) {
        if (item.exists()) {
            item.update();
        } else {
            item.save();
        }
    }

    @Override
    public void add(Iterable<History> items) {
        for (History item : items) {
            item.save();
        }
    }

    @Override
    public void update(History item) {
        if (item.exists()) {
            item.update();
        }
    }

    @Override
    public void remove(History item) {
        if (item.exists()) {
            item.delete();
        }
    }

    @Override
    public History querySingle(Class<History> clz, Condition... conditions) {
        return SQLite.select().from(clz).where(conditions).querySingle();
    }

    @Override
    public List<History> queryList(Class<History> clz, Condition... conditions) {
        return SQLite.select().from(clz).where(conditions).queryList();
    }

    public List<History> loadAll() {
        return SQLite.select().from(History.class).queryList();
    }
}
