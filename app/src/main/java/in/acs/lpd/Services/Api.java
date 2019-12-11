package in.acs.lpd.Services;

import android.database.Observable;

import in.acs.lpd.MyResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Abhishek on 12/3/2018.
 */

public interface Api {

    //the base URL for our API
    //make sure you are not using localhost
    //find the ip usinc ipconfig command
    //String BASE_URL = "http://192.168.0.26:8000/lec/rest/loanProcessingUploadController/uploadDocument";

    String BASE_URL = "http://103.211.39.43:8000/lec/rest/loanProcessingUploadController/uploadDocument/132734/";

    //H:\\LEC Services\\MAOUploadDocuments\\" + uploadedFileRef.getParameter("uploadFrom") + "\\"	+ applicationId+"\\"+fileName.contentType

    //this is our multipart request
    //we have two parameters on is name and other one is description
    /*@Multipart
    @POST("/upload")
    Call<MyResponse> updateProfile(@Part("files\"; filename=\"myfile.jpg\" ") RequestBody file, @Part("desc") RequestBody desc);*/

    @Multipart
    @POST("upload")
    Call<MyResponse> updateProfile(@Part MultipartBody.Part image, @Part("files") RequestBody file);

    /*@Multipart
    @POST("upload")
    Call<MyResponse> updateProfile(@Part MultipartBody.Part image);*/
}
