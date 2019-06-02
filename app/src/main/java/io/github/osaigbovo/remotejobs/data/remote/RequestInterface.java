package io.github.osaigbovo.remotejobs.data.remote;

import java.util.List;

import io.github.osaigbovo.remotejobs.data.model.Job;
import io.reactivex.Observable;
import retrofit2.http.GET;

public interface RequestInterface {

    @GET("/api")
    Observable<List<Job>> getAllJobs();

}
