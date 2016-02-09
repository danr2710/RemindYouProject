package com.danram.www.eventsharer;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class CreateEventFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageView createButton, shareButton, addToCalButton;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private EditText eventName, eventInfo, location;
    private TextView beginDateField, beginTimeField, endDateField, endTimeField;
    private TextView titleErrorMessage, startDateErrorMessage, startTimeErrorMessage;

    private Spinner TZone;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        beginDateField = (TextView) view.findViewById(R.id.beginDateValue);
        beginTimeField = (TextView) view.findViewById(R.id.beginTimeValue);
        endDateField = (TextView) view.findViewById(R.id.endDateValue);
        endTimeField = (TextView) view.findViewById(R.id.endTimeValue);
        createButton = (ImageView) view.findViewById(R.id.createButton);
        shareButton = (ImageView) view.findViewById(R.id.share_button);
        addToCalButton = (ImageView) view.findViewById(R.id.addToCalendarButton);
        eventName = (EditText) view.findViewById(R.id.eventNameInput);
        eventInfo = (EditText) view.findViewById(R.id.eventInfoInput);
        location = (EditText) view.findViewById(R.id.locationValue);
        //titleErrorMessage = (TextView) view.findViewById(R.id.titleErrorMessage);
        startDateErrorMessage = (TextView) view.findViewById(R.id.startDateError);
        startTimeErrorMessage = (TextView) view.findViewById(R.id.startTimeError);
        TZone = (Spinner) view.findViewById(R.id.timeZoneSpinner);
        eventName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //validateEventName();
            }
        });

        beginDateField.setOnClickListener(this);
        beginTimeField.setOnClickListener(this);
        endDateField.setOnClickListener(this);
        endTimeField.setOnClickListener(this);
        createButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        addToCalButton.setOnClickListener(this);

        ArrayList<String> TZ1 = new ArrayList<>();
        TZone.setAdapter(CalendarUtilsClass.getTimeZoneValues(TZ1, getActivity()));
        for (int i = 0; i < TZ1.size(); i++) {
            if (TZ1.get(i).equals(TimeZone.getDefault().getID())) {
                TZone.setSelection(i);
            }
        }

        return view;
    }

    private boolean validateEntry() {
        //validateEventName();

        String dateStringStart = beginDateField.getText().toString() + "T" + beginTimeField.getText().toString();
        String dateStringEnd = endDateField.getText().toString() + "T" + endTimeField.getText().toString();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm");

        int startYear, startMonth, startDate, startHour, startMin;
        int endYear, endMonth, endDate, endHour, endMin;
        startDate = startHour = startMin = startYear = startMonth = -1;
        endDate = endHour = endMin = endMonth = endYear = -1;
        try {
            Date date = sdf.parse(dateStringStart);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            startYear = cal.get(Calendar.YEAR);
            startMonth = cal.get(Calendar.MONTH);
            startDate = cal.get(Calendar.DAY_OF_MONTH);
            startHour = cal.get(Calendar.HOUR_OF_DAY);
            startMin = cal.get(Calendar.MINUTE);
            Date date2 = sdf.parse(dateStringEnd);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            endYear = cal2.get(Calendar.YEAR);
            endMonth = cal2.get(Calendar.MONTH);
            endDate = cal2.get(Calendar.DAY_OF_MONTH);
            endHour = cal2.get(Calendar.HOUR_OF_DAY);
            endMin = cal2.get(Calendar.MINUTE);
            int[] startDay = {startYear, startMonth, startDate, startHour, startMin};
            int[] endDay = {endYear, endMonth, endDate, endHour, endMin};
            if (eventName.getText().toString().length() != 0) {
                if (CalendarUtilsClass.createCalendar(getActivity(), eventName.getText().toString(), startDay, endDay,
                        location.getText().toString(), eventInfo.getText().toString())) {

                    Toast.makeText(getActivity(), eventName.getText().toString() + " event successfully created", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Please enter an Event Name", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            //e.printStackTrace();
            Toast.makeText(getActivity(), "ERROR! Please check the details again", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }


   /* private void validateEventName() {
        if (eventName.getText().toString().trim().equalsIgnoreCase(""))
            titleErrorMessage.setVisibility(View.VISIBLE);
        else
            titleErrorMessage.setVisibility(View.INVISIBLE);
    }*/


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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.beginDateValue: {
                final DialogFragment newFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        int actualMonth = month + 1;
                        //if (CalendarUtilsClass.isSelectedDateGreaterThanToday(year, actualMonth, day) == CalendarUtilsClass.BEFORE_TODAY)
                        //   startDateErrorMessage.setVisibility(View.VISIBLE);

                        //else {
                        startDateErrorMessage.setVisibility(View.INVISIBLE);

                        beginDateField.setText(String.format("%02d/%02d/%04d", day, actualMonth, year));
                        endDateField.setText(String.format("%02d/%02d/%04d", day, actualMonth, year));
                    }
                    // }


                };

                newFragment.show(getFragmentManager(), "datePicker");
                break;
            }
            case R.id.beginTimeValue: {
                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                        startTimeErrorMessage.setVisibility(View.INVISIBLE);
                        beginTimeField.setText(String.format("%02d:%02d", hourOfDay, minute));
                        endTimeField.setText(String.format("%02d:%02d", hourOfDay, minute + 15));

                    }
                };
                newFragment.show(getFragmentManager(), "timePicker");
                break;
            }
            case R.id.endDateValue: {
                DialogFragment newFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthIndex, int day) {
                        int month = monthIndex + 1;
                        endDateField.setText(String.format("%02d/%02d/%04d", day, month, year));

                    }
                };
                newFragment.show(getFragmentManager(), "datePicker");
                break;
            }
            case R.id.endTimeValue: {
                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTimeField.setText(String.format("%02d:%02d", hourOfDay, minute));

                    }
                };
                newFragment.show(getFragmentManager(), "timePicker");
                break;
            }
            case R.id.createButton: {
                validateEntry();
                break;
            }
            case R.id.share_button: {
                if (validateEntry()) {
                    Log.d("tag", "filename: " + Environment.getExternalStorageDirectory().getAbsoluteFile().getAbsolutePath() + "/Events");

                    File file = new File(String.format("%s/Events", Environment.getExternalStorageDirectory().getAbsoluteFile()), eventName.getText().toString() + ".ics");
                    Uri uri = FileProvider.getUriForFile(getActivity(), "com.mydomain.fileprovider", file);

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/calendar");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    // sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    //getActivity().grantUriPermission();
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
                break;
            }
            case R.id.addToCalendarButton: {
                String start, end;
                start = end = " ";

                String dateStringStart = beginDateField.getText().toString() + "T" + beginTimeField.getText().toString();
                String dateStringEnd = endDateField.getText().toString() + "T" + endTimeField.getText().toString();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

                try {
                    Date date = sdf.parse(dateStringStart);
                    start = sdf2.format(date);
                    Date date2 = sdf.parse(dateStringEnd);
                    end = sdf2.format(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                final TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
                final TimeZone timezone = registry.getTimeZone(TimeZone.getTimeZone(TZone.getSelectedItem().toString()).getID());

                if (CalendarUtilsClass.saveEventToCalendar(getActivity(), eventName.getText().toString(), eventInfo.getText().toString()
                        , start, end, timezone)) {
                    Toast.makeText(getActivity(), "Successfully added to your Calendar", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "ERROR! Could not add event to Calendar", Toast.LENGTH_SHORT).show();
            }
        }
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
