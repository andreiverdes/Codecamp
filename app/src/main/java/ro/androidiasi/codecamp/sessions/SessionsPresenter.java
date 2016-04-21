package ro.androidiasi.codecamp.sessions;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.List;

import ro.androidiasi.codecamp.data.model.DataSession;
import ro.androidiasi.codecamp.data.source.IAgendaDataSource;
import ro.androidiasi.codecamp.data.source.ILoadCallback;
import ro.androidiasi.codecamp.internal.model.Session;

/**
 * Created by andrei on 08/04/16.
 */
@EBean
public class SessionsPresenter implements SessionsContract.Presenter, SwipeRefreshLayout.OnRefreshListener {

    @Bean SessionsAdapter mSessionsAdapter;

    private IAgendaDataSource<Long> mRepository;
    private SessionsContract.View mView;
    private Boolean mShowOnlyFavoriteSessions;

    @Override public void afterViews() {
        if(mView == null){
            throw new NullPointerException("View is NULL! Please set the view first!");
        }
        if(mRepository == null){
            throw new NullPointerException("Repository is NULL! Please set the Repository first!");
        }
        this.mView.getListView().setAdapter(this.mSessionsAdapter);
        this.mView.getSwipeRefreshLayout().setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        this.mView.getSwipeRefreshLayout().setOnRefreshListener(this);
        this.updateAdapter();
    }

    @Override public void onEventSessionUpdated() {
        this.updateAdapter();
    }

    private void updateAdapter() {
        if(mShowOnlyFavoriteSessions){
            this.mRepository.getFavoriteSessionsList(new ILoadCallback<List<DataSession>>() {
                @Override public void onSuccess(List<DataSession> pObject) {
                    mView.getEmptyListTextView().setVisibility(pObject.size() == 0 ? View.VISIBLE : View.GONE);
                    mSessionsAdapter.update(Session.fromDataSessionList(pObject));
                }

                @Override public void onFailure(Exception pE) {
                    mView.getEmptyListTextView().setVisibility(View.VISIBLE);
                }
            });
        } else {
            this.mRepository.getSessionsList(new ILoadCallback<List<DataSession>>() {
                @Override public void onSuccess(List<DataSession> pObject) {
                    mSessionsAdapter.update(Session.fromDataSessionList(pObject));
                }

                @Override public void onFailure(Exception pE) {

                }
            });
        }
    }

    public void setView(SessionsContract.View pView) {
        mView = pView;
    }

    public void setShowOnlyFavoriteSessions(Boolean pShowOnlyFavoriteSessions) {
        mShowOnlyFavoriteSessions = pShowOnlyFavoriteSessions;
    }

    public void setRepository(IAgendaDataSource<Long> pRepository) {
        mRepository = pRepository;
    }

    @Override public void onRefresh() {
        if(mShowOnlyFavoriteSessions){
            this.mRepository.getFavoriteSessionsList(true, new ILoadCallback<List<DataSession>>() {
                @Override public void onSuccess(List<DataSession> pObject) {
                    mView.getEmptyListTextView().setVisibility(pObject.size() == 0 ? View.VISIBLE : View.GONE);
                    mSessionsAdapter.update(Session.fromDataSessionList(pObject));
                    mView.getSwipeRefreshLayout().setRefreshing(false);
                }

                @Override public void onFailure(Exception pException) {
                    mView.getEmptyListTextView().setVisibility(View.VISIBLE);
                    mView.getSwipeRefreshLayout().setRefreshing(false);
                }
            });
        } else {
            this.mRepository.getSessionsList(true, new ILoadCallback<List<DataSession>>() {
                @Override public void onSuccess(List<DataSession> pObject) {
                    mSessionsAdapter.update(Session.fromDataSessionList(pObject));
                    mView.getSwipeRefreshLayout().setRefreshing(false);
                }

                @Override public void onFailure(Exception pException) {
                    mView.getSwipeRefreshLayout().setRefreshing(false);
                }
            });
        }
    }
}
