package com.danram.www.eventsharer;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TimeZone;


public class CreateEventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button createButton;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private EditText eventName, eventInfo, location;
    private TextView beginDateField, beginTimeField, endDateField, endTimeField;
    private TextView titleErrorMessage, startDateErrorMessage, startTimeErrorMessage;
    private int startYear, startMonth, startDate, startHour, startMin;
    private int endYear, endMonth, endDate, endHour, endMin;


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
        createButton = (Button) view.findViewById(R.id.createButton);
        eventName = (EditText) view.findViewById(R.id.eventNameInput);
        eventInfo = (EditText) view.findViewById(R.id.eventInfoInput);
        location = (EditText) view.findViewById(R.id.locationValue);
        titleErrorMessage = (TextView) view.findViewById(R.id.titleErrorMessage);
        startDateErrorMessage = (TextView) view.findViewById(R.id.startDateError);
        startTimeErrorMessage = (TextView) view.findViewById(R.id.startTimeError);
        eventName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateEventName();
            }
        });


        beginDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogFragment newFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        if (CalendarUtilsClass.isSelectedDateGreaterThanToday(year, month, day) == CalendarUtilsClass.BEFORE_TODAY)
                            startDateErrorMessage.setVisibility(View.VISIBLE);

                        else {
                            startDateErrorMessage.setVisibility(View.INVISIBLE);
                            startDate = day;
                            startMonth = month;
                            startYear = year;
                            beginDateField.setText(String.format("%d/%d/%d", day, month, year));
                            endDateField.setText(String.format("%d/%d/%d", day, month, year));
                        }
                    }


                };

                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        beginTimeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int dateCheck = CalendarUtilsClass.isSelectedDateGreaterThanToday(startYear, startMonth, startDate);
                        if (dateCheck == CalendarUtilsClass.SAME_TODAY) {
                            if (CalendarUtilsClass.isSelectedTimeGreaterThanNow(hourOfDay, minute) == CalendarUtilsClass.AFTER_NOW) {
                                startTimeErrorMessage.setVisibility(View.INVISIBLE);
                                startHour = hourOfDay;
                                startMin = minute;
                                beginTimeField.setText(String.format("%d:%d", hourOfDay, minute));
                                endTimeField.setText(String.format("%d:%d", hourOfDay, minute));
                            } else
                                startTimeErrorMessage.setVisibility(View.VISIBLE);
                        } else if (dateCheck == CalendarUtilsClass.AFTER_TODAY) {
                            startTimeErrorMessage.setVisibility(View.INVISIBLE);
                            beginTimeField.setText(String.format("%d:%d", hourOfDay, minute));
                            endTimeField.setText(String.format("%d:%d", hourOfDay, minute));
                        } else {
                            startTimeErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                };
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        endDateField.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                DialogFragment newFragment = new DatePickerFragment() {
                                                    @Override
                                                    public void onDateSet(DatePicker view, int year, int month, int day) {
                                                        endDateField.setText(String.format("%d/%d/%d", day, month, year));
                                                        endDate = day;
                                                        endMonth = month;
                                                        endYear = year;
                                                    }
                                                };
                                                newFragment.show(getFragmentManager(), "datePicker");
                                            }
                                        }
        );
        endTimeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTimeField.setText(String.format("%d:%d", hourOfDay, minute));
                        endHour = hourOfDay;
                        endMin = minute;
                    }
                };
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateEntry();
            }
        });
        final Spinner TZone = (Spinner) view.findViewById(R.id.timeZoneSpinner);
        ArrayList<String> TZ1 = new ArrayList<>();
        TZone.setAdapter(CalendarUtilsClass.getTimeZoneValues(TZ1, getActivity()));
        for (int i = 0; i < TZ1.size(); i++) {
            if (TZ1.get(i).equals(TimeZone.getDefault().getDisplayName())) {
                TZone.setSelection(i);
            }
        }
        return view;
    }

    private void validateEntry() {
        validateEventName();
        int[] startDay = {startYear, startMonth, startDate, startHour, startMin};
        int[] endDay = {endYear, endMonth, endDate, endHour, endMin};
        if (CalendarUtilsClass.saveCalendar(getActivity(), eventName.getText().toString(), startDay, endDay,
                location.getText().toString(), eventInfo.getText().toString())) {

            Toast.makeText(getActivity(), eventName.getText().toString() + " event successfully created", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateEventName() {
        if (eventName.getText().toString().trim().equalsIgnoreCase(""))
            titleErrorMessage.setVisibility(View.VISIBLE);
        else
            titleErrorMessage.setVisibility(View.INVISIBLE);
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
