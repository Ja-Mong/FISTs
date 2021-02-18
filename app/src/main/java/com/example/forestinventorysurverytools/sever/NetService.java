package com.example.forestinventorysurverytools.sever;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface NetService {
    @Multipart
    @POST("/imgsUpload")
    Call<String> imgFile(@Part ArrayList<MultipartBody.Part> imageFile);

    @POST("/jsonUpload")
    Call<String> jsonFile(@Body String jsonFile);
}
