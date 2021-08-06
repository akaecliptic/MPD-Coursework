package org.me.gcu.equake.Fragment.Tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.me.gcu.equake.Adapter.AdapterMain;
import org.me.gcu.equake.Architecture.EQUAKEViewModel;
import org.me.gcu.equake.Interface.MyClickListener;
import org.me.gcu.equake.Model.EQUAKE;
import org.me.gcu.equake.R;
import org.me.gcu.equake.Utility.EQuakeUtility;

import java.util.ArrayList;

import static org.me.gcu.equake.Fragment.EQUAKEFragment.EQUAKE_TAG;
import static org.me.gcu.equake.Utility.EQuakeUtility.SortType;
import static org.me.gcu.equake.Utility.EQuakeUtility.SortType.DATE;

/**
 * Developed by: Michael Adebayo Fatoye
 * Student ID: S1718017
 */
public class EQUAKEListView extends Fragment {

    private RecyclerView recyclerView;
    private AdapterMain adapter;
    private MyClickListener clickListener;
    private EQUAKEViewModel model;
    private FloatingActionButton btnSort;
    private SortType sortType = DATE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(EQUAKEViewModel.class);
        return inflater.inflate(R.layout.main_equake_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews();
        initListeners();
        assignViews();
    }

    private void initViews() {
        recyclerView = requireView().findViewById(R.id.main_recycler);
        btnSort = requireView().findViewById(R.id.main_sort);
    }

    private void assignViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdapterMain(requireContext(), new ArrayList<>(model.getWorking()));
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(clickListener);

        btnSort.setImageResource(sortType.getIcon());
    }

    private void initListeners() {
        clickListener = (view, position) -> {
            EQUAKE equake = adapter.getItem(position);
            Toast.makeText(requireContext(), "Opening ' " + equake.getDisplayLocation() + "'", Toast.LENGTH_SHORT).show();
            navigate(equake);
        };

        btnSort.setOnClickListener(view -> {
            sortType = SortType.cycleSort(sortType);
            btnSort.setImageResource(sortType.getIcon());
            EQuakeUtility.sort(adapter.getList(), sortType);
            adapter.notifyDataSetChanged();
            Toast.makeText(requireContext(), "Sorting by: " + sortType.name(), Toast.LENGTH_SHORT).show();
        });
    }

    private void navigate(EQUAKE equake) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EQUAKE_TAG, equake);

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_main_to_equake, bundle);
    }
}
