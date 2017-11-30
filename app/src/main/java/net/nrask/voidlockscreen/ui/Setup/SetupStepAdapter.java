package net.nrask.voidlockscreen.ui.Setup;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.nrask.voidlockscreen.R;

import java.util.List;

/**
 * Created by Sebastian on 30-11-2017.
 */

public class SetupStepAdapter extends RecyclerView.Adapter<SetupStepVH> {
    private int currentStep = 0;
    private List<SetupStep> steps;
    private Callback callback;

    public SetupStepAdapter(List<SetupStep> steps, Callback callback) {
        this.steps = steps;
        this.callback = callback;

        notifyDataSetChanged();
    }

    @Override
    public SetupStepVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.notification_cell_material, parent, false);


        final SetupStepVH vh = new SetupStepVH(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentStep != vh.getAdapterPosition()) {
                    return;
                }

                callback.onStepClicked(steps.get(vh.getAdapterPosition()));
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(SetupStepVH holder, int position) {
        SetupStep step = steps.get(position);
        holder.bindData(step);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    void nextStep() {
        if (currentStep >= getItemCount()) {
            return;
        }

        steps.get(currentStep).setDone(true);
        currentStep++;

        notifyDataSetChanged();
    }

    interface Callback {
        void onStepClicked(SetupStep step);
    }
}
