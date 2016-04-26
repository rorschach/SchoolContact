package me.rorschach.schoolcontacts.data.local;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.DateTime;

/**
 * Created by lei on 16-4-10.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class DateTimeConverter extends TypeConverter<String, DateTime> {


    @Override
    public String getDBValue(DateTime model) {
        if (model != null) {
            return model.toString();
        }
        return null;
    }

    @Override
    public DateTime getModelValue(String data) {
        if (data != null) {
            return DateTime.parse(data);
        }
        return null;
    }
}
