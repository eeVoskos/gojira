package com.gojira.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;
import com.gojira.R;
import com.gojira.app.GojiraApp;
import com.gojira.data.api.JiraService;
import com.gojira.data.io.DashboardsResponse;
import com.gojira.data.model.Dashboard;
import com.gojira.util.DataRecyclerAdapter;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 15/05/2015
 */
public class DashboardListFragment extends ProgressFragment implements Callback<DashboardsResponse> {

    @Inject
    JiraService mService;

    @InjectView(R.id.recycler)
    RecyclerView mRecycler;

    DashboardRecyclerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        GojiraApp.get(activity).getGraph().inject(this);
        mAdapter = new DashboardRecyclerAdapter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContentView(R.layout.fragment_dashboard_list);
        ButterKnife.inject(this, getContentView());
        setEmptyText(getString(R.string.list_dashboards_empty));

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mAdapter);
        setContentShown(!mAdapter.isEmpty());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mService.dashboards(this);
    }

    @Override
    public void success(DashboardsResponse data, Response response) {
        Timber.d("Received %s dashboards.", data.total);
        mAdapter.setData(data.dashboards);
        setContentShown(true);
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.w(error, "Could not get dashboards.");
        Snackbar.make(getContentView(), R.string.list_dashboards_error, Snackbar.LENGTH_LONG).show();
        setContentShown(true);
    }

    static class DashboardRecyclerAdapter extends DataRecyclerAdapter<Dashboard, DashboardRecyclerAdapter.ViewHolder> {

        @Override
        public DashboardRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new DashboardRecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DashboardRecyclerAdapter.ViewHolder holder, int position) {
            Dashboard dashboard = getItem(position);
            holder.text.setText(dashboard.name);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @InjectView(android.R.id.text1)
            TextView text;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.inject(this, view);
            }
        }

    }

}
