package com.liu.util.context;

public class ContextUtils {
	private static ThreadLocal<Context> threadContext = new ThreadLocal<Context>();

	public static void setContext(Context ctx) {
		threadContext.set(ctx);
	}

	public static Context getContext() {
		Context ctx = threadContext.get();
		if (ctx == null) {
			ctx = new Context();
			setContext(ctx);
		}
		return ctx;
	}

	public static boolean hasKey(String key) {
		Context ctx = threadContext.get();
		if (ctx == null) {
			return false;
		}
		return ctx.containsKey(key);
	}

	public static Object get(String key) {
		Context ctx = threadContext.get();
		if (ctx == null) {
			ctx = new Context();
			setContext(ctx);
		}
		return ctx.get(key);
	}

	public static <T> T get(String key, Class<T> type) {
		Context ctx = threadContext.get();
		if (ctx == null) {
			ctx = new Context();
			setContext(ctx);
		}
		return ctx.get(key, type);
	}

	public static void remove(String key) {
		Context ctx = threadContext.get();
		if (ctx != null) {
			ctx.remove(key);
		}
	}

	public static void put(String key, Object v) {
		Context ctx = threadContext.get();
		if (ctx == null) {
			ctx = new Context();
			setContext(ctx);
		}
		ctx.put(key, v);
	}

	public static void clear() {
		threadContext.remove();
	}

}
