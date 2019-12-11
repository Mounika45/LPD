package in.acs.lpd.Services;


import android.util.Log;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;


public class RetroHelper {

    public static RestAdapter getAdapter(String serverUrl, String header) {

        String url = serverUrl;
        Log.e("KAR","getAdapter url :: "+url);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .setRequestInterceptor(getRequestInterceptor(header))
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String msg) {
                        Log.e("Retro Helper", msg);
                    }
                })
                .build();

        return restAdapter;
    }

    private static RequestInterceptor getRequestInterceptor(final String header) {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if (header != null) {
                    Log.e("KAR","header :: "+header);
                    request.addHeader("authkey", header);
                }
            }
        };

        return requestInterceptor;
    }

}
