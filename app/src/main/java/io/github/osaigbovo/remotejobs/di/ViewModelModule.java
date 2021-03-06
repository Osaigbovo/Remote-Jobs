package io.github.osaigbovo.remotejobs.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import io.github.osaigbovo.remotejobs.ui.favorite.FavoriteViewModel;
import io.github.osaigbovo.remotejobs.ui.jobs.JobViewModel;
import io.github.osaigbovo.remotejobs.utils.JobsViewModelFactory;

/*
 * @author Osaigbovo Odiase.
 * */
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(JobViewModel.class)
    abstract ViewModel bindJobViewModel(JobViewModel jobViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(FavoriteViewModel.class)
    abstract ViewModel bindFavoriteViewModel(FavoriteViewModel favoriteViewModel);

        /*@Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    abstract ViewModel bindSearchViewModel(SearchViewModel searchViewModel);*/

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(JobsViewModelFactory factory);
}
