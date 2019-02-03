package com.github.lulewiczg.controller.ui.component;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.client.Client;
import com.github.lulewiczg.controller.model.Bind;

import java.util.List;

/**
 * Data adapter for binds.
 */
public class BindsDataAdapter extends RecyclerView.Adapter<BindsDataAdapter.BindHolder> {
    private final Activity activity;
    private List<Bind> binds;

    /**
     * Adds new bind to list.
     *
     * @param bind bind
     */
    public void add(Bind bind) {
        binds.add(bind);
    }

    /**
     * Executes bind.
     *
     * @param pos bind position
     */
    public void runBind(int pos) {
        List<Action> actions = binds.get(pos).getActions();
        for (Action a : actions) {
            Client.get().doActionFast(a, activity);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * View for bind.
     */
    public class BindHolder extends RecyclerView.ViewHolder {
        private TextView bindName;
        private TextView bindDetails;

        public BindHolder(LinearLayout v) {
            super(v);
            bindName = v.findViewById(R.id.bindName);
            bindDetails = v.findViewById(R.id.bindDetails);
        }
    }

    public BindsDataAdapter(List<Bind> binds, Activity activity) {
        this.binds = binds;
        this.activity = activity;
    }

    @Override
    public BindsDataAdapter.BindHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bind_layout, parent, false);
        BindHolder holder = new BindHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(BindHolder holder, int position) {
        Bind bind = binds.get(position);
        holder.bindName.setText(bind.getName());
        holder.bindDetails.setText(bind.buildDetails());
    }

    @Override
    public int getItemCount() {
        return binds.size();
    }
}