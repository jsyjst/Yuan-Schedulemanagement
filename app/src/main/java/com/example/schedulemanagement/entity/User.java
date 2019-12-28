package com.example.schedulemanagement.entity;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/12/27
 *     desc   : 单例类，用户
 * </pre>
 */

public class User {
    private static User sInstance = null;
    private int userId;
    private User(){}
    public static User getInstance(){
        if(sInstance == null){
            synchronized (User.class){
                if(sInstance == null){
                    sInstance = new User();
                }
            }
        }
        return sInstance;
    }
    public void setUserId(int userId){
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
