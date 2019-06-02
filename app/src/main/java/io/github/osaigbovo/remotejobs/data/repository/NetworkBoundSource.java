package io.github.osaigbovo.remotejobs.data.repository;

import android.annotation.SuppressLint;

import io.github.osaigbovo.remotejobs.utils.Resource;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@SuppressWarnings("ResultOfMethodCallIgnored")
abstract class NetworkBoundSource<LocalType, RemoteType> {

    @SuppressLint("CheckResult")
    NetworkBoundSource(FlowableEmitter<Resource<LocalType>> emitter) {

        Disposable firstDataDisposable = getLocal()
                .map(Resource::loading)
                .subscribe(emitter::onNext);

        getRemote().map(mapper())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(localTypeData -> {
                    firstDataDisposable.dispose();
                    NetworkBoundSource.this.saveCallResult(localTypeData);
                    NetworkBoundSource.this.getLocal()
                            .map(Resource::success)
                            .onErrorReturn(msg -> Resource.error(msg.getMessage()))
                            .subscribe(emitter::onNext);

                }, Timber::e);
    }

    protected abstract Observable<RemoteType> getRemote();

    protected abstract Flowable<LocalType> getLocal();

    protected abstract void saveCallResult(LocalType data);

    protected abstract Function<RemoteType, LocalType> mapper();

}