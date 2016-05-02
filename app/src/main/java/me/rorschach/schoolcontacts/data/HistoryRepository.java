package me.rorschach.schoolcontacts.data;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.data.local.History;
import me.rorschach.schoolcontacts.data.local.History_Table;

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

    private static class RepositoryHolder {
        public static final HistoryRepository REPOSITORY = new HistoryRepository();
    }

    private HistoryRepository() {
    }

    @DebugLog
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
    public List<History> queryList(Class<History> clz, @Nullable Condition... conditions) {
        return SQLite.select()
                .from(clz)
                .where(conditions)
                .queryList();
    }

    public List<History> loadAll() {
        return SQLite.select()
                .from(History.class)
                .orderBy(OrderBy.fromProperty(History_Table.id).descending())
                .queryList();
    }

    public List<History> loadLast() {
        return SQLite.select()
                .from(History.class)
                .orderBy(OrderBy.fromProperty(History_Table.id).descending())
                .limit(5)
                .queryList();
    }

}
