package net.lingin.max.android.net.exception;

import net.lingin.max.android.utils.ToastUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;
import retrofit2.HttpException;

/**
 * @author Administrator
 * @date 2019/12/28 11:21
 */
public class ExceptionAction implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) throws Exception {
        if (throwable instanceof ConnectException || throwable instanceof UnknownHostException) {
            ToastUtils.show("网络错误");
        } else if (throwable instanceof SocketTimeoutException) {
            ToastUtils.show("连接超时，请重试");
        } else if (throwable instanceof HttpException) {
            ToastUtils.show("服务器错误(" + ((HttpException) throwable).code());
        } else {
            //未知错误，最好将其上报给服务端，供异常排查
        }
    }
}
