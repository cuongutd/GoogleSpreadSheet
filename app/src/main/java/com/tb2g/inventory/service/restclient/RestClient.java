package com.tb2g.inventory.service.restclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * Created by Cuong on 11/17/2015.
 */
public class RestClient {

    private static UPCSearchService searchService;
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

    public static UPCSearchService getUPCSearchService(){

        if (searchService == null) {

            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(searchService.BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.BASIC)
                    .setConverter(new GsonConverter(gson));

            RestAdapter restAdapter = builder.build();

            searchService = restAdapter.create(UPCSearchService.class);
        }

        return searchService;
    }

}
