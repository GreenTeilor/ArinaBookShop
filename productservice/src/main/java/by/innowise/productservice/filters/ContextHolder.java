package by.innowise.productservice.filters;

import lombok.NonNull;

public class ContextHolder {
    private static final ThreadLocal<Context> userContext = new ThreadLocal<>();

    public static Context getContext(){
        Context context = userContext.get();
        if (context == null) {
            context = createEmptyContext();
            setContext(context);
        }
        return userContext.get();
    }

    public static void setContext(@NonNull Context context) {
        userContext.set(context);
    }

    public static Context createEmptyContext(){
        return new Context();
    }
}
