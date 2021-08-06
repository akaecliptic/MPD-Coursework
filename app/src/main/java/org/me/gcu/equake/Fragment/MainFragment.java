package org.me.gcu.equake.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.me.gcu.equake.Fragment.Tabs.EQUAKEListView;
import org.me.gcu.equake.Fragment.Tabs.EQUAKEMapView;
import org.me.gcu.equake.R;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import static org.me.gcu.equake.Fragment.FilterFragment.FILTER_TAG_FROM;
import static org.me.gcu.equake.Fragment.FilterFragment.FILTER_TAG_TO;

/**
 * Developed by: Michael A. F.
 */
public class MainFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private ImageView imgSearch;
    private TextView textLabel;
    private TextView textSearch;

    private MaterialDatePicker.Builder<Pair<Long, Long>> builder;
    private LocalDate from;
    private LocalDate to;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initListeners();
        assignViews();
    }

    private void initViews() {
        textLabel = requireView().findViewById(R.id.main_label);
        viewPager = requireView().findViewById(R.id.main_pager);
        tabLayout = requireView().findViewById(R.id.main_tab_layout);

        textSearch = requireView().findViewById(R.id.sdr_search);
        imgSearch = requireView().findViewById(R.id.sdr_icon);

        builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Dates")
                .setTheme(R.style.EQuakeDatePicker)
                .setCalendarConstraints(getCalendarConstraints());
    }

    private CalendarConstraints getCalendarConstraints() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        long today = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -4);
        long earliest = calendar.getTimeInMillis();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder().setStart(earliest).setEnd(today);
        return constraintsBuilder.build();
    }

    private void assignViews() {
        String label = "Showing all earthquakes within the last 100 days";
        textLabel.setText(label);
        viewPager.setAdapter(new EUQAKViewAdapter(this));
        viewPager.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(EUQAKViewAdapter.headings[position])
        ).attach();
    }

    private void initListeners(){
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        datePicker.addOnNegativeButtonClickListener(view -> {
            textSearch.setText(R.string.placeholder_date_hint, TextView.BufferType.NORMAL);
            from = to = null;
        });

        datePicker.addOnPositiveButtonClickListener(selection -> {
            from = Instant
                    .ofEpochMilli(selection.first)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            to = Instant
                    .ofEpochMilli(selection.second)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            String text = "From: '" + from.toString() + "' To: '" + to.toString() + "'";
            textSearch.setText(text);
        });

        imgSearch.setOnClickListener(view -> {
            if (from == null || to == null){
                Toast.makeText(requireContext(), "Date Range Must Be Valid", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable(FILTER_TAG_FROM, from);
            bundle.putSerializable(FILTER_TAG_TO, to);

            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_main_to_filter, bundle);
        });

        textSearch.setOnClickListener(view -> {
            datePicker.show(getParentFragmentManager(), "tag");
        });
    }

    public static class EUQAKViewAdapter extends FragmentStateAdapter {
        public static final String[] headings = { "List", "Map" };

        public EUQAKViewAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;

            if (position == 0) {
                fragment = new EQUAKEListView();
            } else {
                fragment = new EQUAKEMapView();
            }

            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}