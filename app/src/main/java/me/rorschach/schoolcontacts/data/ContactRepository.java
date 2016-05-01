package me.rorschach.schoolcontacts.data;

import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.data.local.Contact;
import me.rorschach.schoolcontacts.data.local.Contact_Table;
import me.rorschach.schoolcontacts.data.local.History;

/**
 * Created by lei on 16-4-10.
 */
public class ContactRepository implements Repository<Contact> {

    private static ContactRepository INSTANCE;

    public static ContactRepository getInstance() {
        INSTANCE = ContactRepositoryHolder.REPOSITORY;
        return INSTANCE;
    }

    public static void destoryInstance() {
        INSTANCE = null;
    }

    private ContactRepository() {
    }

    private static class ContactRepositoryHolder {
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
    public Contact querySingle(Class<Contact> clz, Condition... conditions) {
        return SQLite.select().from(clz).where(conditions).querySingle();
    }

    @Override
    public List<Contact> queryList(Class<Contact> clz, Condition... conditions) {
        return SQLite.select().from(clz).where(conditions).queryList();
    }

    @DebugLog
    public void saveAll(List<Contact> contacts) {
        TransactionManager.getInstance()
                .addTransaction(
                        new SaveModelTransaction<>(
                                ProcessModelInfo.withModels(contacts)));
    }

    public List<Contact> loadAllColleges() {
        return new Select(Contact_Table.college).distinct().from(Contact.class).queryList();
    }

    public List<Contact> loadAllStared() {
        return SQLite
                .select(Contact_Table.name)
                .from(Contact.class)
                .where(Contact_Table.stared.eq(true))
                .queryList();
    }

    public Contact getContactByHistory(History history) {
        return SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.id.eq(history.getContactsId()))
                .querySingle();
    }

    public List<Contact> loadContactsByCollege(String college) {

        return SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.college.eq(college))
                .queryList();
    }

    @DebugLog
    public List<Contact> searchByKey(String keyword) {

        return SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.name.like("%" + keyword + "%"))
                .or(Contact_Table.phone.like("%" + keyword + "%"))
                .queryList();
    }
}
