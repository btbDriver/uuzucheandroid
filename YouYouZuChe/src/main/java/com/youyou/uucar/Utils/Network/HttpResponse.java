package com.youyou.uucar.Utils.Network;

import com.android.volley.VolleyError;

/**
 * Created by taurusxi on 14-8-6.
 */
public class HttpResponse {


    public interface NetWorkResponse<T> {

        public void onSuccessResponse(T responseData);

        public void onError(VolleyError errorResponse);

        public void networkFinish();

    }

}
