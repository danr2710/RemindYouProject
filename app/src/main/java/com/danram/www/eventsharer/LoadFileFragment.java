package com.danram.www.eventsharer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import java.io.FileInputStream;
import java.io.IOException;


public class LoadFileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "LoadFileFragment";

    private static final int FILE_SELECT_CODE = 0;

    LinearLayout detailsLayout;
    TextView beginDateValue, beginTimeValue, eventTitle, eventInfo, eventLocation, endDateValue, endTimeValue;
    private OnFragmentInteractionListener mListener;
    private Button button;

    public LoadFileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_load_file, container, false);
        button = (Button) view.findViewById(R.id.load_button);
        beginDateValue = (TextView) view.findViewById(R.id.beginDateValue);
        beginTimeValue = (TextView) view.findViewById(R.id.beginTimeValue);
        endDateValue = (TextView) view.findViewById(R.id.endDateValue);
        endTimeValue = (TextView) view.findViewById(R.id.endTimeValue);
        detailsLayout = (LinearLayout) view.findViewById(R.id.detailsLayout);
        eventTitle = (TextView) view.findViewById(R.id.eventName);
        eventInfo = (TextView) view.findViewById(R.id.eventInfo);
        eventLocation = (TextView) view.findViewById(R.id.locationValue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        return view;
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

    private void readICSFile(String path) {
        FileInputStream fin;
        net.fortuna.ical4j.model.Calendar calendar = null;
        //Parsing calendar
        try {

            fin = new FileInputStream(path);
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
                    if (property.getName().equalsIgnoreCase("SUMMARY")) {
                        eventTitle.setText(property.getValue());
                    }
                    if (property.getName().equalsIgnoreCase("DESCRIPTION")) {
                        eventInfo.setText(property.getValue());
                    }
                    if (property.getName().equalsIgnoreCase("LOCATION")) {
                        eventLocation.setText(property.getValue());
                    }
                    if (property.getName().equalsIgnoreCase("DTSTART")) {
                        String startdate = property.getValue();
                        String[] startDt = startdate.split("T");
                        String year = startDt[0].substring(0, 4);
                        String month = startDt[0].substring(4, 6);
                        String day = startDt[0].substring(7);
                        String hour = startDt[1].substring(0, 2);
                        String min = startDt[1].substring(2, 4);
                        beginDateValue.setText(day + "/" + month + "/" + year);
                        beginTimeValue.setText(hour + ":" + min);
                    }
                    if (property.getName().equalsIgnoreCase("DTEND")) {
                        String endDate = property.getValue();
                        String[] endDt = endDate.split("T");
                        String year = endDt[0].substring(0, 4);
                        String month = endDt[0].substring(4, 6);
                        String day = endDt[0].substring(7);
                        String hour = endDt[1].substring(0, 2);
                        String min = endDt[1].substring(2, 4);
                        endDateValue.setText(day + "/" + month + "/" + year);
                        endTimeValue.setText(hour + ":" + min);
                    }
                }
            }
            button.setVisibility(View.INVISIBLE);
            detailsLayout.setVisibility(View.VISIBLE);
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

                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    path = FileUtilsClass.getPath(getActivity(), uri);
                    Log.d(TAG, "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                    readICSFile(path);
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
