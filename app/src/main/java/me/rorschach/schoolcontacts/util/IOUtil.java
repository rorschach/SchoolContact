package me.rorschach.schoolcontacts.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.rorschach.schoolcontacts.data.local.Contact;

/**
 * Created by lei on 16-4-27.
 */
public class IOUtil {

    public static List<Contact> parseXml(XmlPullParser xmlPullParser) {
        Contact contact;
        ArrayList<Contact> contactsList = new ArrayList<>();

        try {
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("channel")) {
                            contact = new Contact();
                            contact.setName(xmlPullParser.getAttributeValue(0));
                            contact.setPhone(xmlPullParser.getAttributeValue(1));
                            contact.setCollege(xmlPullParser.getAttributeValue(2));
                            contact.setStared(false);
                            contactsList.add(contact);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contactsList;
    }

}
