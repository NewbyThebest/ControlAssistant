package com.geekb.controlassistant;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiInterface {


    @GET("/initDB")
    Observable<Void> initDB();

    @GET("/clearDB")
    Observable<Void> clearDB();

    @GET("/all")
    Observable<List<BindData>> getAll();

    @GET("/get")
    Observable<BindData> getOperation(@Query("gesture") int gesture);

    @GET("/put")
    Observable<Boolean> putOperation(@Query("gesture") int gesture,@Query("operation") String operation);

    @GET("/del")
    Observable<Boolean> delOperation(@Query("gesture") int gesture);

    @GET("/update")
    Observable<Boolean> updateOperation(@Query("gesture") int gesture,@Query("operation") String operation);



}
