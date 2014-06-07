package my.messenger.test.util;

import java.io.InputStream;

public class StreamUtils {

	public static InputStream getResourceAsInputStream(String url){
		return ClassUtils.getResourceAsStream(url);
	}
}
