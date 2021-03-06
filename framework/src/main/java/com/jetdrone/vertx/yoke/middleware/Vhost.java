/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.middleware;

import com.jetdrone.vertx.yoke.Middleware;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

import java.util.regex.Pattern;

/**
 * # Vhost
 * <p/>
 * Setup vhost for the given *hostname* and *server*.
 */
public class Vhost extends Middleware {

    private final Handler<HttpServerRequest> handler;
    private final Pattern regex;

    /**
     * Create a new Vhost middleware. This middleware will verify the request hostname and if it matches it will send
     * the request to the registered handler, otherwise will continue inside the middleware chain.
     *
     * <pre>
     * new Yoke(...)
     *   .use(new Vhost("*.jetdrone.com", existingHttpServerObject))
     * </pre>
     *
     * @param hostname
     * @param handler
     */
    public Vhost(String hostname, Handler<HttpServerRequest> handler) {
        this.handler = handler;
        this.regex = Pattern.compile("^" + hostname.replaceAll("\\.", "\\\\.").replaceAll("[*]", "(.*?)") + "$", Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(final YokeRequest request, final Handler<Object> next) {
        String host = request.getHeader("host");
        if (host == null) {
            next.handle(null);
        } else {
            boolean match = false;
            for (String h : host.split(":")) {
                if (regex.matcher(h).find()) {
                    match = true;
                    break;
                }
            }

            if (match) {
                handler.handle(request);
            } else {
                next.handle(null);
            }
        }
    }
}
