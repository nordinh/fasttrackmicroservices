package com.github.nordinh.fasttrackmicroservices.exercise1;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class Exercise1Application extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new Exercise1Application().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {

    }
}
