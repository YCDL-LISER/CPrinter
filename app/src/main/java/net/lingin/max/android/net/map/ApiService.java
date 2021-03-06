package net.lingin.max.android.net.map;

import net.lingin.max.android.BuildConfig;
import net.lingin.max.android.R;
import net.lingin.max.android.net.model.GoodsPrintResult;
import net.lingin.max.android.net.model.LoginRequest;
import net.lingin.max.android.net.model.LoginResponse;
import net.lingin.max.android.net.model.Result;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by: var_rain.
 * Created date: 2018/10/21.
 * Description: 服务器接口映射
 */
public interface ApiService {

    /* 服务器地址, 在 build.gradle 中配置 */
    String BASE_URL = BuildConfig.SERVER_BASE;

    /* 超时 */
    int TIME_OUT = 15;

    @POST("onefamily/operator/login")
    Observable<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("onefamily/items/{itemNo}/{branchNo}")
    Observable<Result<GoodsPrintResult>> goodsPrint(@Path("itemNo") String itemNo, @Path("branchNo") String branchNo);
}
