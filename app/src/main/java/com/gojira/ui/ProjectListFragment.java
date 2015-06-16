package com.gojira.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devspark.progressfragment.ProgressFragment;
import com.gojira.R;
import com.gojira.app.GojiraApp;
import com.gojira.data.api.JiraService;
import com.gojira.data.model.Project;
import com.gojira.util.DataRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

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
public class ProjectListFragment extends ProgressFragment implements Callback<List<Project>> {

    @Inject
    JiraService mService;

    @InjectView(R.id.recycler)
    RecyclerView mRecycler;

    ProjectRecyclerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        GojiraApp.get(activity).getGraph().inject(this);
        mAdapter = new ProjectRecyclerAdapter(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContentView(R.layout.fragment_project_list);
        ButterKnife.inject(this, getContentView());
        setEmptyText(getString(R.string.list_projects_empty));

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mAdapter);
        setContentShown(!mAdapter.isEmpty());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mService.projects(this);
    }

    @Override
    public void success(List<Project> projects, Response response) {
        Timber.d("Received %s projects.", projects.size());
        mAdapter.setData(projects);
        setContentShown(true);
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.w(error, "Could not get projects.");
        Snackbar.make(getContentView(), R.string.list_projects_error, Snackbar.LENGTH_LONG).show();
        setContentShown(true);
    }

    static class ProjectRecyclerAdapter extends DataRecyclerAdapter<Project, ProjectRecyclerAdapter.ViewHolder> {

        @Inject
        Picasso picasso;

        ProjectRecyclerAdapter(Context context) {
            GojiraApp.get(context).getGraph().inject(this);
        }

        @Override
        public ProjectRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_project, parent, false);
            return new ProjectRecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProjectRecyclerAdapter.ViewHolder holder, int position) {
            Project project = getItem(position);
            holder.text.setText(project.name);
            picasso.load(project.avatarUrls.large).into(holder.icon);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @InjectView(R.id.icon)
            ImageView icon;

            @InjectView(R.id.text)
            TextView text;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.inject(this, view);
            }
        }

    }

}
