package org.me.gcu.equake.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.equake.Interface.MyClickListener;
import org.me.gcu.equake.Model.EQUAKE;
import org.me.gcu.equake.R;

import java.util.List;

/**
 * Developed by: Michael A. F.
 */
public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView magnitude;
        TextView depth;
        TextView location;
        TextView coordinates;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.magnitude = itemView.findViewById(R.id.mli_text_magnitude);
            this.depth = itemView.findViewById(R.id.mli_text_depth);
            this.location = itemView.findViewById(R.id.mli_text_location);
            this.coordinates = itemView.findViewById(R.id.mli_text_coordinates);
            this.time = itemView.findViewById(R.id.mli_text_time);

            itemView.setClickable(true);
            itemView.setOnClickListener(view -> clickListener.onClick(view, getAdapterPosition()));
        }

        public void setLevel(float level){
            itemView.findViewById(R.id.main_list_item).getBackground().setLevel((int)Math.ceil(level));
        }
    }

    private static MyClickListener clickListener;
    private final LayoutInflater inflater;
    private List<EQUAKE> list;

    public AdapterMain(Context context, List<EQUAKE> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.main_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EQUAKE current = list.get(position);

        if(current == null)
            return;

        holder.magnitude.setText(current.getDisplayMagnitude());
        holder.depth.setText(current.getDisplayDepth());
        holder.location.setText(current.getDisplayLocation());
        holder.coordinates.setText(current.getCoordinates());
        holder.time.setText(current.getTime());

        holder.setLevel(current.getMagnitude());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public EQUAKE getItem(int position){
        return list.get(position);
    }

    public void setItems(List<EQUAKE> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public List<EQUAKE> getList(){
        return list;
    }

    public void setClickListener(MyClickListener listener){
        clickListener = listener;
    }
}
