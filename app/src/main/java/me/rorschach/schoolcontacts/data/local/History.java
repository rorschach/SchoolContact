package me.rorschach.schoolcontacts.data.local;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

/**
 * Created by lei on 16-4-10.
 */
@Table(database = HistoryDatabase.class)
public class History extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    long contactsId;

    @Column(typeConverter = ContactTypeConverter.class)
    ContactType contactType;

    @Column(typeConverter = DateTimeConverter.class)
    DateTime beginTime;

    @Column(typeConverter = DateTimeConverter.class)
    DateTime endTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getContactsId() {
        return contactsId;
    }

    public void setContactsId(long contactsId) {
        this.contactsId = contactsId;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public DateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(DateTime beginTime) {
        this.beginTime = beginTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }
}

