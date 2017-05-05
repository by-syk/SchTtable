package com.by_syk.schttable.util.impl;

import com.by_syk.schttable.bean.AppVerBean;
import com.by_syk.schttable.bean.CourseBean;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.bean.SchoolBean;
import com.by_syk.schttable.bean.SchoolTodoBean;
import com.by_syk.schttable.bean.StatusBean;
import com.by_syk.schttable.bean.TermBean;
import com.by_syk.schttable.bean.UserBean;
import com.by_syk.schttable.bean.WatchDogBean;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by By_syk on 2017-04-04.
 */

public interface ServerService {
    @POST("common/{schoolCode}/signin.do")
    @FormUrlEncoded
    Call<ResResBean<StatusBean>> signIn(@Path("schoolCode") String schoolCode,
                                        @Field("stuno") String enStudentNo,
                                        @Field("pwd") String enPwd);

    @POST("common/{schoolCode}/userinfo.do")
    @FormUrlEncoded
    Call<ResResBean<UserBean>> getUserInfo(@Path("schoolCode") String schoolCode,
                                           @Field("stuno") String enStudentNo,
                                           @Field("pwd") String enPwd);

    @POST("common/refresh.do")
    @FormUrlEncoded
    Call<ResResBean<StatusBean>> refresh(@Field("userkey") String userKey);

    @POST("common/daycourses.do")
    @FormUrlEncoded
    Call<ResResBean<List<CourseBean>>> getDayCourses(@Field("userkey") String userKey,
                                                     @Field("date") long targetDateMillis);

    @POST("common/coursepage.do")
    @FormUrlEncoded
    Call<ResponseBody> getCoursePage(@Field("userkey") String userKey);

    @POST("common/term.do")
    @FormUrlEncoded
    Call<ResResBean<TermBean>> getTermInfo(@Field("userkey") String userKey);

    @POST("common/schools.do")
    @FormUrlEncoded
    Call<ResResBean<ArrayList<SchoolBean>>> getSupportedSchools(@Field("userNum") boolean withUserNum);

    @POST("common/schoolstodo.do")
    Call<ResResBean<List<SchoolTodoBean>>> getTodoSchools();

    @POST("common/status.do")
    @FormUrlEncoded
    Call<ResResBean<StatusBean>> getStatus(@Field("id") String statusId);

    @POST("misc/appver.do")
    @FormUrlEncoded
    Call<ResResBean<AppVerBean>> getLatestAppInfo(@Field("userkey") String userKey,
                                                  @Field("devicebrand") String deviceBrand,
                                                  @Field("devicemodel") String deviceModel,
                                                  @Field("devicesdk") int de55viceSdk,
                                                  @Field("appvername") String appVerName,
                                                  @Field("appvercode") int appVerCode);

    @POST("misc/bugrep.do")
    @FormUrlEncoded
    Call<ResResBean> reportBug(@Field("schoolCode") String schoolCode,
                               @Field("stuNo") String stuNo,
                               @Field("courseDate") long courseDate,
                               @Field("courseOrder") int courseOrder,
                               @Field("desc") String desc);

    @GET("misc/watchdog.do")
    Call<ResResBean<WatchDogBean>> watchDog();
}
