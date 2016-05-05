package me.rorschach.schoolcontacts.data.local;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.IndexGroup;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by lei on 16-4-10.
 */
@Table(database = ContactDatabase.class,
        indexGroups = {
                @IndexGroup(number = 1, name = "nameIndex"),
                @IndexGroup(number = 2, name = "phoneIndex"),
        })
public class Contact extends BaseModel implements Serializable {

    @Index
    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Index(indexGroups = 1)
    @Column
    String name;

    @Index(indexGroups = 2)
    @Column
    String phone;

    @Column
    String college;

    @Column
    Boolean stared;

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", college='" + college + '\'' +
                ", stared=" + stared +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (id != contact.id) return false;
        if (!name.equals(contact.name)) return false;
        if (!phone.equals(contact.phone)) return false;
        if (!college.equals(contact.college)) return false;
        return stared.equals(contact.stared);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + phone.hashCode();
        result = 31 * result + college.hashCode();
        result = 31 * result + stared.hashCode();
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public Boolean isStared() {
        return stared;
    }

    public void setStared(Boolean stared) {
        this.stared = stared;
    }
}
