package org.me.gcu.equake.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.equake.Adapter.AdapterMain;
import org.me.gcu.equake.Architecture.EQUAKEViewModel;
import org.me.gcu.equake.Interface.MyClickListener;
import org.me.gcu.equake.Model.EQUAKE;
import org.me.gcu.equake.R;
import org.me.gcu.equake.Utility.EQuakeUtility;
import org.me.gcu.equake.Utility.EQuakeUtility.FilterType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.me.gcu.equake.Fragment.EQUAKEFragment.EQUAKE_TAG;
import static org.me.gcu.equake.Utility.EQuakeUtility.FilterType.DEFAULT;
import static org.me.gcu.equake.Utility.EQuakeUtility.FilterType.DEPTH;
import static org.me.gcu.equake.Utility.EQuakeUtility.FilterType.DIRECTIONS;
import static org.me.gcu.equake.Utility.EQuakeUtility.FilterType.MAGNITUDE;

/**
 * Developed by: Michael A. F.
 */
public class FilterFragment extends Fragment {

    public static final String FILTER_TAG_FROM = "from";
    public static final String FILTER_TAG_TO = "to";

    private EQUAKEViewModel model;
    private List<EQUAKE> working;
    private LocalDate from;
    private LocalDate to;

    private TextView textTitle;
    private TextView textLabelDirection;
    private TextView textLabelEmpty;
    private ImageView imgBack;

    private RecyclerView recyclerView;
    private AdapterMain adapter;
    private MyClickListener clickListener;

    private ImageView imgClear;
    private ImageView imgDirection;
    private ImageView imgMagnitude;
    private ImageView imgDepth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(EQUAKEViewModel.class);
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    private void incoming() {
        Bundle bundle = getArguments();
        Object obj1 = bundle.get(FILTER_TAG_FROM);
        Object obj2 = bundle.get(FILTER_TAG_TO);

        if(obj1 instanceof LocalDate && obj2 instanceof LocalDate){
            from = (LocalDate) obj1;
            to = (LocalDate) obj2;
        }else {
            Navigation.findNavController(requireView()).popBackStack();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        incoming();

        initViews();
        initListeners();
        assignViews();
    }

    private void initViews() {
        textTitle = requireView().findViewById(R.id.ff_top);
        textLabelDirection = requireView().findViewById(R.id.ff_label_direction);
        textLabelEmpty = requireView().findViewById(R.id.ff_label_empty);
        imgBack = requireView().findViewById(R.id.ff_back);

        recyclerView = requireView().findViewById(R.id.ff_recycler);

        imgClear = requireView().findViewById(R.id.ff_sort_clear);
        imgDirection = requireView().findViewById(R.id.ff_sort_direction);
        imgMagnitude = requireView().findViewById(R.id.ff_sort_magnitude);
        imgDepth = requireView().findViewById(R.id.ff_sort_depth);
    }

    private void initListeners() {
        clickListener = (view, position) -> {
            EQUAKE equake = adapter.getItem(position);
            Toast.makeText(requireContext(), "Opening ' " + equake.getDisplayLocation() + "'", Toast.LENGTH_SHORT).show();
            navigate(equake);
        };

        imgBack.setOnClickListener(view -> {
            Navigation.findNavController(requireView()).popBackStack();
        });

        imgDirection.setOnClickListener(onFilerSelect);
        imgMagnitude.setOnClickListener(onFilerSelect);
        imgDepth.setOnClickListener(onFilerSelect);
        imgClear.setOnClickListener(view -> {
            deselectFilters((ImageView) view);

            if(adapter.getList() == working)
                return;

            applyFilter(false, DEFAULT);
        });
    }

    private void assignViews() {
        String text = "Showing Earthquakes From: '" + from.toString() + "' To: '" + to.toString() + "'";
        textTitle.setText(text);

        working = new ArrayList<>(model.getWorking());
        working.removeIf(equake -> from.isAfter(LocalDate.parse(equake.getTime())) ||
                                to.isBefore(LocalDate.parse(equake.getTime())));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdapterMain(requireContext(), working);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(clickListener);

        if(working.isEmpty())
            textLabelEmpty.setVisibility(View.VISIBLE);
    }

    private void navigate(EQUAKE equake) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EQUAKE_TAG, equake);

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_filter_to_equake, bundle);
    }

    View.OnClickListener onFilerSelect = view -> {
        if (working.isEmpty())
            return;

        boolean state = false;
        FilterType filter = DEFAULT;
        int[] colours = {ContextCompat.getColor(requireContext(), R.color.color_text_black),
                            ContextCompat.getColor(requireContext(), R.color.color_text_white)};
        if(view == imgDirection){
            state = !imgDirection.isSelected();
            filter = DIRECTIONS;

            imgDirection.setSelected(state);
            imgDirection.setColorFilter(state ? colours[1] : colours[0]);
            textLabelDirection.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
        }
        else if(view == imgMagnitude){
            state = !imgMagnitude.isSelected();
            filter = MAGNITUDE;

            imgMagnitude.setSelected(state);
            imgMagnitude.setColorFilter(state ? colours[1] : colours[0]);
            textLabelDirection.setVisibility(View.INVISIBLE);
        }
        else if(view == imgDepth){
            state = !imgDepth.isSelected();
            filter = DEPTH;

            imgDepth.setSelected(state);
            imgDepth.setColorFilter(state ? colours[1] : colours[0]);
            textLabelDirection.setVisibility(View.INVISIBLE);
        }

        applyFilter(state, filter);
        deselectFilters((ImageView) view);
    };

    public void applyFilter(boolean state, FilterType filterType){
        if(state){
            adapter.setItems(EQuakeUtility.filter(working, filterType));
            Toast.makeText(requireContext(), filterType.getMessage(), Toast.LENGTH_SHORT).show();
        }else {
            if(adapter.getList() == working)
                return;

            Toast.makeText(requireContext(), DEFAULT.getMessage(), Toast.LENGTH_SHORT).show();
            textLabelDirection.setVisibility(View.INVISIBLE);
            adapter.setItems(working);
        }

        adapter.notifyDataSetChanged();
    }

    private void deselectFilters(@Nullable ImageView exc){
        for (ImageView v : new ImageView[]{imgDirection, imgMagnitude, imgDepth}){
            if(v == exc)
                continue;

            v.setSelected(false);
            v.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_text_black));
        }
    }
}
