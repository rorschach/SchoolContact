package me.rorschach.schoolcontacts.data.local;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by lei on 16-4-10.
 */
@Database(name = HistoryDatabase.NAME, version = HistoryDatabase.VERSION)
public class HistoryDatabase {

    public static final String NAME = "HistoryDb";

    public static final int VERSION = 1;
}
