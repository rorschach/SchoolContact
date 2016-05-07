package me.rorschach.schoolcontacts.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import me.rorschach.schoolcontacts.R;
import me.rorschach.schoolcontacts.data.local.Contact;

/**
 * Created by lei on 16-4-27.
 */
public class IOUtil {

    public static final String BACKUP_DIR = "GnnuContact";
    public static final String BACKUP_VCF_FILE = "backup.vcf";
    public static final String BACKUP_XML_FILE = "backup.xml";
    public static final String RAW_XML_FILE = "gnnu.xml";

    private static final String TAG = "IOUtil";

    public static String getBackupDirPath(Context context) throws IOException {

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            throw new IOException("外部存储未挂载！");
        } else {
            SharedPreferences sp = context.getSharedPreferences("backup", Context.MODE_PRIVATE);
            String path = sp.getString("BACKUP_PATH",
                    Environment.getExternalStorageDirectory().getPath()
                            + File.separator + "GnnuContact" + File.separator);

            File dir = new File(path);

            if (!dir.exists() || !dir.isDirectory()) {

                if (!dir.mkdir()) {
                    throw new IOException("创建备份文件夹失败！");
                }
            }

            return path;
        }
    }

    public static String getAllVcfFilePath(Context context) throws IOException {
        String path = getBackupDirPath(context) + BACKUP_VCF_FILE;
        File file = new File(path);

        if (!file.exists() || !file.isFile()) {

            if (!file.createNewFile()) {
                throw new IOException("创建备份文件失败！");
            }
        }

        return path;
    }

    public static String getCollegeVcfFilePath(Context context, String college) throws IOException {
        String path = getBackupDirPath(context) + college + ".vcf";
        File file = new File(path);

        if (!file.exists() || !file.isFile()) {

            if (!file.createNewFile()) {
                throw new IOException("创建备份文件失败！");
            }
        }

        return path;
    }

    @DebugLog
    public static void export2VcfFile(Context context, List<Contact> contacts, int type) throws IOException {

        String vcfPath;

        if (type == ALL) {
            vcfPath = getAllVcfFilePath(context);
        } else if (type == COLLEGE) {
            vcfPath = getCollegeVcfFilePath(context, contacts.get(0).getCollege());
        }else {
            throw new IOException("导出类型错误！");
        }
        Log.d(TAG, "export2VcfFile: " + vcfPath);

        FileWriter fw = new FileWriter(vcfPath);

        fw.write("");

        for (Contact contact : contacts) {
            fw.write("BEGIN:VCARD\n");
            fw.write("VERSION:3.0\n");

            fw.write("FN:" + contact.getName() + "\n");
            fw.write("TEL;TYPE=WORK,PREF:" + contact.getPhone() + "\n");
            fw.write("ADR;TYPE=WORK:" + contact.getCollege() + "\n");

            fw.write("END:VCARD\n\n");
        }

        fw.close();
    }

    public static String getSingleVcfFile(Context context, Contact contact) throws IOException {

        String path = getBackupDirPath(context) + contact.getName() + ".vcf";
        File file = new File(path);

        if (!file.exists() || !file.isFile()) {

            if (!file.createNewFile()) {
                throw new IOException("创建联系人文件失败！");
            }
        }

        return file.getPath();
    }

    @DebugLog
    public static void export2VcfFile(Contact contact, String path) throws IOException {

        FileWriter fw = new FileWriter(path);

        fw.write("");

        fw.write("BEGIN:VCARD\n");
        fw.write("VERSION:3.0\n");

        fw.write("FN:" + contact.getName() + "\n");
        fw.write("TEL;TYPE=WORK,PREF:" + contact.getPhone() + "\n");
        fw.write("ADR;TYPE=WORK:" + contact.getCollege() + "\n");

        fw.write("END:VCARD\n\n");

        fw.close();
    }

    public static String getUpdateXmlFilePath(Context context) throws IOException {
        String path = getBackupDirPath(context) + RAW_XML_FILE;
        File file = new File(path);

        if (!file.exists() || !file.isFile()) {

            if (!file.createNewFile()) {
                throw new IOException("创建备份文件失败！");
            }
        }

        return path;
    }

    @DebugLog
    public static String getAllXmlBackupFilePath(Context context) throws IOException {
        String path = getBackupDirPath(context) + BACKUP_XML_FILE;
        File file = new File(path);

        if (!file.exists() || !file.isFile()) {

            if (!file.createNewFile()) {
                throw new IOException("创建备份文件失败！");
            }
        }

        return path;
    }

    @DebugLog
    public static String getCollegeXmlBackupFilePath(Context context, String college) throws IOException {
        String path = getBackupDirPath(context) + college + ".xml";
        File file = new File(path);

        if (!file.exists() || !file.isFile()) {

            if (!file.createNewFile()) {
                throw new IOException("创建备份文件失败！");
            }
        }

        return path;
    }

    public static final int ALL = 0;
    public static final int COLLEGE = 1;

    @DebugLog
    public static void export2XmlFile(Context context, List<Contact> contacts, int type) throws IOException {

        String xmlPath;

        if (type == ALL) {
            xmlPath = getAllXmlBackupFilePath(context);
        } else if (type == COLLEGE) {
            xmlPath = getCollegeXmlBackupFilePath(context, contacts.get(0).getCollege());
        }else {
            throw new IOException("导出类型错误！");
        }
        Log.d(TAG, "export2XmlFile: " + xmlPath);

        File file = new File(xmlPath);
        FileWriter fw = new FileWriter(file);
        fw.write("");
        fw.close();

        OutputStream os = new FileOutputStream(file);

        XmlSerializer xs = Xml.newSerializer();

        String enter = System.getProperty("line.separator");

        xs.setOutput(os, "utf-8");

        xs.startDocument("utf-8", true);
        xs.text(enter);

        xs.startTag(null, "resources");
        xs.text(enter);

        for (Contact contact : contacts) {

            xs.text("\t");
            xs.startTag(null, "channel");
            xs.attribute(null, "name", contact.getName());
            xs.attribute(null, "tel", contact.getPhone());
            xs.attribute(null, "type", contact.getCollege());
            xs.attribute(null, "id", contact.getId() + "");
            xs.endTag(null, "channel");
            xs.text(enter);
        }

        xs.endTag(null, "resources");
        xs.endDocument();

        os.close();
    }

    public static List<Contact> importFromResource(Context context) throws IOException {

        XmlPullParser xpp = context.getResources().getXml(R.xml.gnnu);

        return IOUtil.parseXml(xpp);
    }

    public static List<Contact> importFromXmlBackup(String path) throws IOException, XmlPullParserException {

        XmlPullParser xpp = Xml.newPullParser();

        FileInputStream is = new FileInputStream(path);

        xpp.setInput(is, "utf-8");

        List<Contact> contacts = IOUtil.parseXml(xpp);

        is.close();

        return contacts;
    }

    public static List<Contact> parseXml(XmlPullParser xpp) throws NullPointerException {

        if (xpp == null) {
            throw new NullPointerException("XmlPullParser is null!");
        }

        ArrayList<Contact> contactsList = new ArrayList<>();

        Contact contact = null;

        try {
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        String tagName = xpp.getName();
                        if (tagName.equals("channel")) {
                            contact = new Contact();
                            contact.setName(xpp.getAttributeValue(0));
                            contact.setPhone(xpp.getAttributeValue(1));
                            contact.setCollege(xpp.getAttributeValue(2));
                            contact.setStared(false);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("channel".equals(xpp.getName())) {
                            contactsList.add(contact);
                            contact = null;
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contactsList;
    }

}
