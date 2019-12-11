package in.acs.lpd.Services;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * Created by JoinUs4 on 17-06-2015.
 */
public interface ServiceHelper {

    @Headers("Accept:application/json")
    @POST("/login")
    public void getTokenForLogin(@Body JsonObject mainobj, Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void Login(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void getVillages(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void getLECDtls(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void getCOCDtls(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void getBOTHDtls(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void getCrops(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void getBanks(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void getBranches(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void getScaleOfFinance(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @POST("/")
    void uploadDocument(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody file, @retrofit2.http.Part("desc") RequestBody desc);

    @Headers("Accept:application/json")
    @GET("/")
    void getRejectionReasons(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @POST("/mobileuploadDocument")
    void mobileUploadDocument(@Body JsonObject mainobj, Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @POST("/processing")
    void saveSanctionData(@Body JsonObject loanProcessingUplaod, Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @POST("/processing")
    void saveRejectionData(@Body JsonObject processingDtls, Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void getGroupDetailsByMandal(Callback<JsonObject> callback);

    @Headers("Accept:application/json")
    @GET("/")
    void getLoanProcessingDetailsByGroupId(Callback<JsonObject> callback);
}
