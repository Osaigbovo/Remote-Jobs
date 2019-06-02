package io.github.osaigbovo.remotejobs.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.github.osaigbovo.remotejobs.ui.favorite.FavoriteActivity;
import io.github.osaigbovo.remotejobs.ui.jobs.JobActivity;

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract JobActivity contributeJobActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract FavoriteActivity contributeFavoriteActivity();

    /*@ContributesAndroidInjector
    abstract SearchActivity contributeSearchActivity();*/
}
