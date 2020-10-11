package com.example.forestinventorysurverytools.sever;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NetService {
    // @FormUrlEncoded
    // @POST("/search/")
    // Call<TreeInfo> post_treeInfo(@Field("treeid") String id, @Field("diameter") float diameter,
    //                                 @Field("height") float height);
    // @FormUrlEncoded
    // @POST("/search/")
    // Call<TreeInfo> post_treeInfo2(@Body TreeInfo treeinfo);

    @POST("save")
    Call<String> post_trees(@Body JSONObject tree);

    @POST("save")
    Call<String> post_tree_dto(@Body treeDTO tree);


}
