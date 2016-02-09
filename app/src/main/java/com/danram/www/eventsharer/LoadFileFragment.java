package com.danram.www.eventsharer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.TimeZone;


public class LoadFileFragment extends Fragment {

    public static final String ARG_PARAM1 = "hasIntent";
    public static final String ARG_PARAM2 = "fileUri";
    private static final String TAG = "LoadFileFragment";

    private static final int FILE_SELECT_CODE = 0;

    LinearLayout detailsLayout;
    TextView beginDateValue, beginTimeValue, eventTitle, eventInfo, eventLocation, endDateValue, endTimeValue, timeZoneValue;
    String summary, description, location, start, end;
    TimeZone zone;
    boolean mParam1;
    Uri mParam2;
    ImageView addToCalendarButton;
    private OnFragmentInteractionListener mListener;
    private Button button;

    public LoadFileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getBoolean(ARG_PARAM1);
            mParam2 = getArguments().getParcelable(ARG_PARAM2);

        }
        summary = description = location = start = end = " ";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_load_file, container, false);
        initUI(view);

        if (mParam1) {
            readICSFile(mParam2);
        } else
            showFileChooser();
        addToCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CalendarUtilsClass.saveEventToCalendar(getActivity(), summary, description, start, end, zone)) {
                    Toast.makeText(getActivity(), "Successfully added to your Calendar", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "ERROR! Could not add event to Calendar", Toast.LENGTH_SHORT).show();
            }

        });
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });*/
        return view;
    }

    private void initUI(View view) {
        button = (Button) view.findViewById(R.id.load_button);
        beginDateValue = (TextView) view.findViewById(R.id.beginDateValue);
        beginTimeValue = (TextView) view.findViewById(R.id.beginTimeValue);
        endDateValue = (TextView) view.findViewById(R.id.endDateValue);
        endTimeValue = (TextView) view.findViewById(R.id.endTimeValue);
        detailsLayout = (LinearLayout) view.findViewById(R.id.detailsLayout);
        eventTitle = (TextView) view.findViewById(R.id.eventName);
        eventInfo = (TextView) view.findViewById(R.id.eventInfo);
        eventLocation = (TextView) view.findViewById(R.id.locationValue);
        timeZoneValue = (TextView) view.findViewById(R.id.timeZoneValue);
        addToCalendarButton = (ImageView) view.findViewById(R.id.addToCalendarButton);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void displayValues() {
        eventTitle.setText(summary);
        eventInfo.setText(description);
        eventLocation.setText(location);

        String[] parsedStartDate = parseDate(start);
        beginDateValue.setText(String.format("%s/%s/%s", parsedStartDate[2], parsedStartDate[1], parsedStartDate[0]));
        beginTimeValue.setText(String.format("%s:%s", parsedStartDate[3], parsedStartDate[4]));

        String[] parsedDate = parseDate(end);
        endDateValue.setText(String.format("%s/%s/%s", parsedDate[2], parsedDate[1], parsedDate[0]));
        endTimeValue.setText(String.format("%s:%s", parsedDate[3], parsedDate[4]));

        timeZoneValue.setText(zone.getDisplayName());
        button.setVisibility(View.INVISIBLE);
        detailsLayout.setVisibility(View.VISIBLE);

    }

    private String[] parseDate(String longDate) {
        String[] dateVal = new String[5];
        String[] endDt = longDate.split("T");
        dateVal[0] = endDt[0].substring(0, 4);
        dateVal[1] = endDt[0].substring(4, 6);
        dateVal[2] = endDt[0].substring(6);
        dateVal[3] = endDt[1].substring(0, 2);
        dateVal[4] = endDt[1].substring(2, 4);
        return dateVal;
    }

    private void readICSFile(final Uri uri) {

        net.fortuna.ical4j.model.Calendar calendar = null;
        final String path = uri.toString();
        //Parsing calendar
        String newPath;
        try {
            if (path.contains("content")) {
                String name = FileUtilsClass.getPath(getActivity(), uri);
                Log.d("tag", "name after parsing: " + name);
                InputStream fin2 = getActivity().getContentResolver().openInputStream(uri);
                FileOutputStream os;
                if (!FileUtilsClass.isExternalStorageDocument(uri)) {
                    os = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Events/" + name);

                    byte[] buffer = new byte[4096];
                    int count;
                    while ((count = fin2.read(buffer)) > 0) {
                        os.write(buffer, 0, count);
                    }
                    os.close();
                    fin2.close();
                    newPath = Environment.getExternalStorageDirectory() + "/Events/" + name;
                } else
                    newPath = name;
            } else {
                newPath = FileUtilsClass.getPath(getActivity(), uri);
            }
            InputStream fin = new FileInputStream(newPath);

            CalendarBuilder builder = new CalendarBuilder();
            calendar = builder.build(fin);
        } catch (ParserException | IOException e) {
            e.printStackTrace();
        }
        if (calendar != null) {
            for (Object o : calendar.getComponents()) {
                Component component = (Component) o;
                System.out.println("Component [" + component.getName() + "]");

                for (Object o1 : component.getProperties()) {
                    Property property = (Property) o1;
                    System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
                    if (property.getName().equalsIgnoreCase("SUMMARY"))
                        summary = property.getValue();
                    else if (property.getName().equalsIgnoreCase("DESCRIPTION"))
                        description = property.getValue();
                    else if (property.getName().equalsIgnoreCase("LOCATION"))
                        location = property.getValue();
                    else if (property.getName().equalsIgnoreCase("DTSTART"))
                        start = property.getValue();
                    else if (property.getName().equalsIgnoreCase("DTEND"))
                        end = property.getValue();
                    else if (property.getName().equalsIgnoreCase("TZID"))
                        zone = TimeZone.getTimeZone(property.getValue());

                }
            }
            displayValues();
        }

    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getActivity(), "Please install a File Manager",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    if (uri != null) {
                        Log.d(TAG, "File Uri: " + uri.toString());
                        // Get the path
                        String path;
                        path = FileUtilsClass.getPath(getActivity(), uri);
                        Log.d(TAG, "File Path: " + path);
                        // Get the file instance
                        // File file = new File(path);
                        // Initiate the upload
                        readICSFile(uri);
                    } else {

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

    }


}
