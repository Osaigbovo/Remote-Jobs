package io.github.osaigbovo.remotejobs.ui.jobs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import io.github.osaigbovo.remotejobs.data.local.entity.FavoriteJob;
import io.github.osaigbovo.remotejobs.data.model.Job;
import io.github.osaigbovo.remotejobs.data.repository.JobRepository;
import io.github.osaigbovo.remotejobs.utils.DbUtil;
import io.github.osaigbovo.remotejobs.utils.Resource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class JobViewModel extends ViewModel {

    private final JobRepository jobRepository;
    LiveData<Resource<List<Job>>> jobsLiveData;

    @Inject
    JobViewModel(JobRepository jobRepository) {
        this.jobRepository = jobRepository;

        this.jobsLiveData = LiveDataReactiveStreams
                .fromPublisher(jobRepository.retrieveJobs()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                );
    }

    void setJobFavored(Job job, boolean favorite) {
        FavoriteJob favoriteJob = DbUtil.getFavorite(job, favorite);
        if (favorite) {
            jobRepository.addFavorite(favoriteJob);
        } else {
            jobRepository.removeFavorite(favoriteJob);
        }
    }

    void refresh(){
        //jobRepository.getFreshJobs();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        jobRepository.clear();
    }

}
