package com.github.lulewiczg.controller.ui.component;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.Action;
import com.github.lulewiczg.controller.client.Client;
import com.github.lulewiczg.controller.common.ClientLimiter;
import com.github.lulewiczg.controller.model.Bind;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

/**
 * Data adapter for binds.
 */
@Getter
public class BindsDataAdapter extends RecyclerView.Adapter<BindsDataAdapter.BindHolder> {
    private final Activity activity;
    private final BindMenuBuilder menuBuilder;
    private final List<Bind> binds;
    @Setter
    private ClientLimiter limiter;
    @Setter
    private int longPressPos = -1;

    /**
     * View for bind.
     */
    public class BindHolder extends RecyclerView.ViewHolder {
        private final TextView bindName;
        private final TextView bindDetails;

        public BindHolder(ViewGroup v) {
            super(v);
            bindName = v.findViewById(R.id.bindName);
            bindDetails = v.findViewById(R.id.bindDetails);
            v.setOnCreateContextMenuListener(menuBuilder);
        }
    }

    /**
     * Adds new bind to list.
     *
     * @param bind bind
     */
    public void add(Bind bind) {
        binds.add(bind);
    }

    /**
     * Removes bind
     *
     * @param pos bind pos
     */
    public void remove(int pos) {
        binds.remove(pos);
    }

    /**
     * Executes bind.
     *
     * @param pos bind position
     */
    public synchronized void runBind(int pos) {
        List<Action> actions = binds.get(pos).getActions();
        for (Action a : actions) {
            while ((!limiter.checkIfDoBind())) {
                limiter.waitForBind();
            }
            Client.get().doActionFast(a, activity);
        }
    }

    public BindsDataAdapter(List<Bind> binds, Activity activity, BindMenuBuilder menuBuilder) {
        this.binds = binds;
        this.activity = activity;
        this.menuBuilder = menuBuilder;
        this.limiter = new ClientLimiter(activity);
    }

    @Override
    public BindsDataAdapter.BindHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bind_layout, parent, false);
        return new BindHolder(v);
    }

    @Override
    public void onBindViewHolder(BindHolder holder, int position) {
        Bind bind = binds.get(position);
        holder.bindName.setText(bind.getName());
        holder.bindDetails.setText(bind.buildDetails());
    }

    @Override
    public int getItemCount() {
        return Optional.ofNullable(binds).map(List::size).orElse(0);
    }

}