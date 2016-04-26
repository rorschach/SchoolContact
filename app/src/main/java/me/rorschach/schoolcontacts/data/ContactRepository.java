package me.rorschach.schoolcontacts.data;

import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.data.local.Contact_Table;

/**
 * Created by lei on 16-4-10.
 */
public class ContactRepository implements Repository<Contact> {

    private static ContactRepository INSTANCE;

    public static ContactRepository getInstance() {
        INSTANCE =  ContactRepositoryHolder.REPOSITORY;
        return INSTANCE;
    }

    public static void destoryInstance() {
        INSTANCE = null;
    }

    private ContactRepository(){}

    private static class ContactRepositoryHolder{
        public static final ContactRepository REPOSITORY = new ContactRepository();
    }

    @Override
    public void add(Contact item) {
        item.save();
    }

    @Override
    public void add(Iterable<Contact> items) {
        for (Contact item : items) {
            item.save();
        }
    }

    @Override
    public void update(Contact item) {
        if (item.exists()) {
            item.update();
        }
    }

    @Override
    public void remove(Contact item) {
        if (item.exists()) {
            item.delete();
        }
    }

    @Override
    public Contact querySingle(Class<Contact> clz,Condition... conditions) {
        return SQLite.select().from(clz).where(conditions).querySingle();
    }

    @Override
    public List<Contact> queryList(Class<Contact> clz, Condition... conditions) {
        return SQLite.select().from(clz).where(conditions).queryList();
    }

    public List<Contact> loadAllColleges() {
        return new Select(Contact_Table.college).distinct().from(Contact.class).queryList();
//        return null;
    }

    public List<Contact> loadAllStared() {
        return SQLite
                .select(Contact_Table.name)
                .from(Contact.class)
                .where(Contact_Table.stared.eq(true))
                .queryList();
//        return null;
    }
}
