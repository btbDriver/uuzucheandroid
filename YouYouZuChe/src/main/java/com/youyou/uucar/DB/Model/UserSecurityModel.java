package com.youyou.uucar.DB.Model;

/**
 * Created by taurusxi on 14-8-29.
 */
public class UserSecurityModel {
    public String id;
    public String b3Key;
    public String b2;
    public String b3;
    public int startTime;//票据开始时间
    public int validSecs;//票据有效时间
    public int ticketFailureTime;//票据失效时间
    public int userId;
}
