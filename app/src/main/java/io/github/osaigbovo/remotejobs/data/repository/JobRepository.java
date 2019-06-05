package io.github.osaigbovo.remotejobs.data.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.osaigbovo.remotejobs.data.local.dao.FavoriteDao;
import io.github.osaigbovo.remotejobs.data.local.dao.JobDao;
import io.github.osaigbovo.remotejobs.data.local.entity.FavoriteJob;
import io.github.osaigbovo.remotejobs.data.model.Job;
import io.github.osaigbovo.remotejobs.data.provider.JobsProvider;
import io.github.osaigbovo.remotejobs.data.remote.FetchJobsIntentService;
import io.github.osaigbovo.remotejobs.data.remote.RequestInterface;
import io.github.osaigbovo.remotejobs.utils.Optional;
import io.github.osaigbovo.remotejobs.utils.Resource;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static io.github.osaigbovo.remotejobs.utils.DbUtil.FAVORITE_WIDGET_PROJECTION;
import static io.github.osaigbovo.remotejobs.utils.DbUtil.ID_PROJECTION;
import static io.github.osaigbovo.remotejobs.utils.DbUtil.ID_PROJECTION_MAP;
import static io.github.osaigbovo.remotejobs.utils.DbUtil.WIDGET_PROJECTION_MAP;

@Singleton
public class JobRepository {

    private final RequestInterface requestInterface;
    private final JobDao jobDao;
    private final FavoriteDao favoriteDao;
    private final ContentResolver contentResolver;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Context context;

    @Inject
    public JobRepository(JobDao jobDao, FavoriteDao favoriteDao, RequestInterface requestInterface,
                         ContentResolver contentResolver, Context context) {
        this.requestInterface = requestInterface;
        this.jobDao = jobDao;
        this.favoriteDao = favoriteDao;
        this.contentResolver = contentResolver;
        this.context = context;
    }

    public Flowable<Resource<List<Job>>> retrieveJobs() {
        return Flowable.create(emitter -> new NetworkBoundSource<List<Job>, List<Job>>(emitter) {
            @Override
            public Observable<List<Job>> getRemote() {
                Timber.e("Getting data from Remote.....");
                Observable<List<Job>> listObservable = requestInterface
                        .getAllJobs()
                        .flatMap(Observable::fromIterable)
                        .filter(job -> !TextUtils.isEmpty(job.getCompany()))
                        .toList()
                        .toObservable();

                return Observable
                        .combineLatest(listObservable, savedJobIds(),
                                (jobList, favoriteIds) -> {
                                    for (Job job : jobList) {
                                        job.setFavorite(favoriteIds.contains(job.getId()));
                                    }
                                    Timber.e(String.valueOf(favoriteIds.size()));
                                    Timber.e(String.valueOf(jobList.size()));
                                    return jobList;
                                })
                        .doOnError(Timber::e);
            }

            @Override
            public Flowable<List<Job>> getLocal() {
                Timber.e("Getting data from database.....");
                return jobDao.getJobs();
            }

            @Override
            public void saveCallResult(@NonNull List<Job> jobList) {
                jobDao.saveJobs(jobList);
                Timber.e("Save to database.....");
            }

            @Override
            public Function<List<Job>, List<Job>> mapper() {
                return jobList -> jobList;
            }
        }, BackpressureStrategy.BUFFER);
    }

    public void getFreshJobs() {
        FetchJobsIntentService.startActionGetRemoteJobs(context);
    }

    private Observable<Set<Integer>> savedJobIds() {
        return Observable
                .just(new Optional<>(contentResolver.query(JobsProvider.URI_JOB, ID_PROJECTION,
                        null, null, null)))
                .map(ID_PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<FavoriteJob>> getFavoriteListForWidget() {
        return Observable
                .just(new Optional<>(contentResolver.query(JobsProvider.URI_JOB, FAVORITE_WIDGET_PROJECTION,
                        null, null, "date")))
                .map(WIDGET_PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<FavoriteJob>> getFavorites() {
        return favoriteDao.getFavoriteJobs();
    }

    public void addFavorite(FavoriteJob favoriteJob) {
        compositeDisposable.add(Completable.fromAction(() -> {
            jobDao.update(favoriteJob.getId(), favoriteJob.isFavorite());
            favoriteDao.saveFavoriteJob(favoriteJob);
        })
                .subscribeOn(Schedulers.io())
                .subscribe(() -> Timber.e("Adding %s to database", favoriteJob.getPosition())));
    }

    public void removeFavorite(FavoriteJob favoriteJob) {
        compositeDisposable.add(Completable.fromAction(() -> {
            jobDao.update(favoriteJob.getId(), favoriteJob.isFavorite());
            favoriteDao.deleteFavoriteJob(favoriteJob);
        })
                .subscribeOn(Schedulers.io())
                .subscribe(() -> Timber.e("Removing %s to database", favoriteJob.getPosition())));
    }

    public void clear() {
        compositeDisposable.clear();
    }

}
