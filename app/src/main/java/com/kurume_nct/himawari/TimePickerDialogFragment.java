package com.kurume_nct.himawari;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TimePickerDialogFragment extends BaseDialogFragment {

    private TimePicker timePicker;

    public static TimePickerDialogFragment newInstance(Fragment fragment, int requestCode, Date time) {
        TimePickerDialogFragment dialog = new TimePickerDialogFragment();
        dialog.setTargetFragment(fragment, requestCode);
        Bundle args = new Bundle();
        args.putSerializable("TIME", time);
        dialog.setArguments(args);
        return dialog;
    }

    private View createPickerView(int hourValue, int minuteValue) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.time_picker, null);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            timePicker.setHour(hourValue);
            timePicker.setMinute(minuteValue);
        } else {
            timePicker.setCurrentHour(hourValue);
            timePicker.setCurrentMinute(minuteValue);
        }
        timePicker.setIs24HourView(true);
        return view;
    }

    private int getHour(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            return timePicker.getHour();
        } else {
            return timePicker.getCurrentHour();
        }
    }

    private int getMinute(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            return timePicker.getMinute();
        } else {
            return timePicker.getCurrentMinute();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        final Date time = (Date) getArguments().getSerializable("TIME");
        int hour = time.getHours();
        int minute = time.getMinutes();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(createPickerView(hour, minute));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                time.setHours(getHour(timePicker));
                time.setMinutes(getMinute(timePicker));
                OnValueSetListener listener = getListener();
                if (listener != null) {
                    listener.onValueSet(getTargetRequestCode(), time);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setCancelable(true);

        return builder.create();
    }
}
