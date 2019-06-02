package io.github.osaigbovo.remotejobs.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.github.osaigbovo.remotejobs.data.provider.JobsProvider;

@Module
public abstract class ContentProviderBuilderModule {

    @ContributesAndroidInjector
    abstract JobsProvider contributeJobsProvider();
}
