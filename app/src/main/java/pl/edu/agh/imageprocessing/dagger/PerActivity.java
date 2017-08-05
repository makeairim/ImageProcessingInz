package pl.edu.agh.imageprocessing.dagger;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by bwolcerz on 31.07.2017.
 */

@Scope
@Retention(RUNTIME)
public @interface PerActivity {}