package me.rorschach.schoolcontacts.data.local;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by lei on 16-4-10.
 */
@Database(name = ContactDatabase.NAME, version = ContactDatabase.VERSION)
public class ContactDatabase {

    public static final String NAME = "ContactsDb";

    public static final int VERSION = 1;
}
