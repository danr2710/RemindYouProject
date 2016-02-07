package com.danram.www.eventsharer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class CalendarUtilsClass {

    public static int BEFORE_TODAY = -1;
    public static int AFTER_TODAY = 1;
    public static int SAME_TODAY = 0;
    public static int AFTER_NOW = 1;
    public static int BEFORE_NOW = -1;

    public static int isSelectedDateGreaterThanToday(int year, int month, int day) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date dateSelected = sdf.parse("" + day + "-" + month + "-" + year);
            final Calendar c = Calendar.getInstance();
            Date today = sdf.parse("" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR));
            if (dateSelected.before(today))
                return BEFORE_TODAY;
            else if (dateSelected.after(today))
                return AFTER_TODAY;
            else if (dateSelected.equals(today))
                return SAME_TODAY;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return BEFORE_TODAY;
    }

    public static int isSelectedTimeGreaterThanNow(int hour, int min) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date timeSelected = sdf.parse("" + hour + ":" + min);
            final Calendar c = Calendar.getInstance();
            Date today = sdf.parse("" + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
            Log.d("tag", "time selected: " + timeSelected.toString());
            Log.d("tag", "now time: " + today.toString());
            if (timeSelected.before(today) || timeSelected.equals(today))
                return BEFORE_NOW;
            else if (timeSelected.after(today))
                return AFTER_NOW;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return BEFORE_TODAY;
    }

    public static ArrayAdapter getTimeZoneValues(ArrayList<String> TZ1, Context context) {
        ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] TZ = TimeZone.getAvailableIDs();
        //ArrayList<String> TZ1 = new ArrayList<String>();
        for (String aTZ : TZ) {
            if (!(TZ1.contains(TimeZone.getTimeZone(aTZ).getDisplayName()))) {
                TZ1.add(TimeZone.getTimeZone(aTZ).getDisplayName());
            }
        }

        for (int i = 0; i < TZ1.size(); i++) {
            adapter.add(TZ1.get(i));
        }
        return adapter;
    }

    public static boolean saveCalendar(Activity context, String eventName, int[] startDay, int[] endDay, String location, String description) {
        PermissionClass.verifyStoragePermissions(context);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Create a TimeZone
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        net.fortuna.ical4j.model.TimeZone timezone = registry.getTimeZone("Asia/Kolkata");
        VTimeZone tz = timezone.getVTimeZone();

        java.util.Calendar startDate = new GregorianCalendar();
        startDate.setTimeZone(timezone);
        startDate.set(java.util.Calendar.MONTH, startDay[1]);
        startDate.set(java.util.Calendar.DAY_OF_MONTH, startDay[2]);
        startDate.set(java.util.Calendar.YEAR, startDay[0]);
        startDate.set(java.util.Calendar.HOUR_OF_DAY, startDay[3]);
        startDate.set(java.util.Calendar.MINUTE, startDay[4]);
        startDate.set(java.util.Calendar.SECOND, 0);

        // End Date is on: April 1, 2008, 13:00
        java.util.Calendar endDate = new GregorianCalendar();
        endDate.setTimeZone(timezone);
        endDate.set(java.util.Calendar.MONTH, endDay[1]);
        endDate.set(java.util.Calendar.DAY_OF_MONTH, endDay[2]);
        endDate.set(java.util.Calendar.YEAR, endDay[0]);
        endDate.set(java.util.Calendar.HOUR_OF_DAY, endDay[3]);
        endDate.set(java.util.Calendar.MINUTE, endDay[4]);
        endDate.set(java.util.Calendar.SECOND, 0);

        // Create the event

        DateTime start = new DateTime(startDate.getTime());
        DateTime end = new DateTime(endDate.getTime());
        VEvent meeting = new VEvent(start, end, eventName);


        if (location != null && location.length() > 0) {
            meeting.getProperties().add(new Location(location));
        }

        if (description != null && description.length() > 0) {
            meeting.getProperties().add(new Description(description));
        }

        // add timezone info..
        meeting.getProperties().add(tz.getTimeZoneId());



        /* generate unique identifier.. */
        UidGenerator ug;

        try {
            ug = new UidGenerator("uidGen");
            Uid uid = ug.generateUid();
            meeting.getProperties().add(uid);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Create a calendar
        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
        icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        icsCalendar.getProperties().add(Version.VERSION_2_0);
        icsCalendar.getProperties().add(CalScale.GREGORIAN);

        // Add the event and print
        icsCalendar.getComponents().add(meeting);
        System.out.println(icsCalendar);
        return FileUtilsClass.saveEventAsICS(context, icsCalendar, eventName);

    }
}
