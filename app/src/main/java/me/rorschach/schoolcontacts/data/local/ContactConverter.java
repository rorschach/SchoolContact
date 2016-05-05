package me.rorschach.schoolcontacts.data.local;

import com.raizlabs.android.dbflow.converter.TypeConverter;

/**
 * Created by lei on 16-5-5.
 */
public class ContactConverter extends TypeConverter<String, Contact> {

    @Override
    public String getDBValue(Contact model) {
        return model.toString();
    }

    //Contact{id=544, name='曾祥志', phone='13707978420', college='物理与电子信息学院', stared=true}
    @Override
    public Contact getModelValue(String data) {

        int start = data.charAt('{') + 1;
        int end = data.charAt('}');
        String sub = data.substring(start, end);

        String[] s = sub.split(",");

        String[] idStr = s[0].split("=");
        long id = Long.valueOf(idStr[1]);

        String[] nameStr = s[1].split("=");
        String name = nameStr[1].substring(1, nameStr.length - 1);

        String[] phoneStr = s[2].split("=");
        String phone = phoneStr[1].substring(1, phoneStr.length - 1);

        String[] collegeStr = s[3].split("=");
        String college = collegeStr[1].substring(1, collegeStr.length - 1);

        Contact contact = new Contact();
        contact.setId(id);
        contact.setName(name);
        contact.setPhone(phone);
        contact.setCollege(college);

        return contact;
    }
}
