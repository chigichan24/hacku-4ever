package com.kurume_nct.himawari;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class InputFormFragment extends Fragment implements ListView.OnItemClickListener, BaseDialogFragment.OnValueSetListener {
    public static  final String IS_SUBMIT = "submit_";
    public static final String PRICE_KEY = "price_";
    public static final String DURATION_HOUR = "duration_hour";
    public static final String DURATION_MINUTE = "duration_minute";
    public static final String POS_KEY = "marker_position";

    private int price;
    private int hour;
    private int minute;
    private LatLng markerPos;

    private ArrayList<InputFormItem> items;
    private ListView listView;


    public InputFormFragment() {
        // Required empty public constructor
    }

    public static InputFormFragment newInstance(Fragment fragment, int requestCode, LatLng markerPos) {
        InputFormFragment self = new InputFormFragment();
        self.setTargetFragment(fragment, requestCode);

        Bundle args = new Bundle();
        args.putInt(PRICE_KEY, 500);
        args.putInt(DURATION_HOUR, 0);
        args.putInt(DURATION_MINUTE, 30);
        args.putParcelable(POS_KEY, markerPos);
        self.setArguments(args);
        return self;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        importFromBundle(savedInstanceState);
    }

    private void importFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PRICE_KEY)) {
                price = savedInstanceState.getInt(PRICE_KEY);
            }
            if (savedInstanceState.containsKey(DURATION_HOUR)) {
                hour = savedInstanceState.getInt(DURATION_HOUR);
            }
            if (savedInstanceState.containsKey(DURATION_MINUTE)) {
                minute = savedInstanceState.getInt(DURATION_MINUTE);
            }
            if (savedInstanceState.containsKey(POS_KEY)) {
                markerPos = savedInstanceState.getParcelable(POS_KEY);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PRICE_KEY, price);
        outState.putInt(DURATION_HOUR, hour);
        outState.putInt(DURATION_MINUTE, minute);
        outState.putParcelable(POS_KEY, markerPos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_input_form, container, false);

        Bundle args = getArguments();
        importFromBundle(args);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.form_title));
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result = new Intent();
                result.putExtra(IS_SUBMIT, 0);
                returnValue(result);
            }
        });
        toolbar.inflateMenu(R.menu.submit);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent result = new Intent();
                result.putExtra(IS_SUBMIT, 1);
                result.putExtra(PRICE_KEY, price);
                result.putExtra(DURATION_HOUR, hour);
                result.putExtra(DURATION_MINUTE, minute);
                returnValue(result);
                return true;
            }
        });

        listView = (ListView) v.findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        items = new ArrayList<InputFormItem>();
        items.add(new InputFormItem(getString(R.string.price_label), Integer.toString(price), PriceDialogFragment.newInstance(this, 0, getString(R.string.price_label))));
        items.add(new InputFormItem(getString(R.string.duration_label), Integer.toString(hour) + ":" + Integer.toString(minute), TimePickerDialogFragment.newInstance(this, 1)));
        listView.setAdapter(new InputListViewAdapter(getContext(), R.layout.fragment_input_item, items));
        return v;
    }

    private void returnValue(Intent result) {
        Fragment fragment = getTargetFragment();
        if (fragment != null) {
            fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ListView listView = (ListView) adapterView;
        InputFormItem item = (InputFormItem) listView.getItemAtPosition(i);
        item.getDialog().show(getFragmentManager(), item.getLabel());
    }

    @Override
    public void onValueSet(int requstCode, String val) {
        if (requstCode == 0) {
            items.get(0).setValue(val);
            price = Integer.parseInt(val);
        }
        if (requstCode == 1) {
            String[] tmp = val.split(":");
            hour = Integer.parseInt(tmp[0]);
            minute = Integer.parseInt(tmp[1]);
            items.get(1).setValue(val);
        }
        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    public class InputFormItem {
        private String label;
        private String val;

        private DialogFragment dialog;

        public InputFormItem(String label, String val, DialogFragment dialogFragment) {
            this.label = label;
            this.val = val;
            this.dialog = dialogFragment;
        }

        public String getLabel() {
            return this.label;
        }

        public String getValue() {
            return this.val;
        }

        public void setValue(String val) {
            this.val = val;
        }

        public DialogFragment getDialog() {
            return this.dialog;
        }
    }
}
