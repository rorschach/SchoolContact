package me.rorschach.schoolcontacts.data.local;

import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by lei on 16-4-10.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ContactTypeConverter extends TypeConverter<String, ContactType> {

    @Override
    public String getDBValue(ContactType model) {
        return model.toString();
    }

    @Override
    public ContactType getModelValue(String data) {
        if ("PHONE".equals(data)) {
            return ContactType.PHONE;
        } else if ("SMS".equals(data)) {
            return ContactType.SMS;
        } else if ("SMS_BULK".equals(data)) {
            return ContactType.SMS_BULK;
        } else {
            return null;
        }
    }
}
