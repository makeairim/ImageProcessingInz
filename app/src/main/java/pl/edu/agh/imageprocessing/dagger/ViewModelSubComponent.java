package pl.edu.agh.imageprocessing.dagger;


import dagger.Subcomponent;
import pl.edu.agh.imageprocessing.features.detail.viemodel.HomeViewModel;

/**
 * Created by Anil on 30/05/2017.
 */

@Subcomponent
public interface ViewModelSubComponent {

    @Subcomponent.Builder
    interface Builder{
        ViewModelSubComponent build();
    }

    HomeViewModel homeViewModel();
}
