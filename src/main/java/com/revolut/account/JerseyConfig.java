package com.revolut.account;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

class JerseyConfig extends ResourceConfig {

    JerseyConfig() {
        // scan for resources
        packages("com.revolut.account.resource");
        // enable jackson JSON mapping feature
        register(JacksonFeature.class);
        register(CustomExceptionMapper.class);
    }
}
