package com.nuo.nuopaoserver.context;

public class UserContext {
    private static ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();
    public static void setCurrentUserId(Long userId) {
        userIdThreadLocal.set(userId);
    }
    public static Long getCurrentUserId() {
        return userIdThreadLocal.get();
    }
    public static void removeCurrentUserId() {
        userIdThreadLocal.remove();
    }
}
