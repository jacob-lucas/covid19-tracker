package com.jacoblucas.covid19tracker.iot;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacoblucas.covid19tracker.utils.ObjectMapperFactory;

abstract class RequestHandler {
    protected ObjectMapper objectMapper;

    RequestHandler() {
        objectMapper = ObjectMapperFactory.getInstance();
    }

    protected void log(final Context context, final String message, final Object... args) {
        context.getLogger().log(String.format(message, args));
    }
}
