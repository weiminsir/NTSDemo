package com.ulang.nts;

import com.ulang.nts.model.NLP;
import com.ulang.nts.model.STT;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import rx.Observable;

public interface APIClient {


    //上传材料
    @Multipart
    @POST("api/stt")
    Observable<STT> stt(@PartMap Map<String, RequestBody> params,
                        @Part MultipartBody.Part file);

    @GET("nlp/parser")
    Observable<NLP> nlp(@Query("line") String line,
                        @Query("telephoneNumber") String telephoneNumber,//请求方
                        @Query("userid") String userid//处理批次
    );

}
