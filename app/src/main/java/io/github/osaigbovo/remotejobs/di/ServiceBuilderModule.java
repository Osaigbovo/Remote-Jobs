package io.github.osaigbovo.remotejobs.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.github.osaigbovo.remotejobs.data.remote.FetchJobsIntentService;
import io.github.osaigbovo.remotejobs.widget.FavoriteJobWidgetService;

@Module
public abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract FavoriteJobWidgetService contributeFavoriteJobWidgetService();

    @ContributesAndroidInjector
    abstract FetchJobsIntentService contributeFetchJobsIntentService();
}
