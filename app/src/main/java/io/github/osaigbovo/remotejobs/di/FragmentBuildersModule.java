package io.github.osaigbovo.remotejobs.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.github.osaigbovo.remotejobs.ui.favorite.FavoriteFragment;

/*
 * @author Osaigbovo Odiase.
 * */
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract FavoriteFragment contributeFavoriteFragment();

}
