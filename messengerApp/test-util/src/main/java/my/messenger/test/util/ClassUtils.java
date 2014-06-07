package my.messenger.test.util;

import java.io.InputStream;

public class ClassUtils {

	public static InputStream getResourceAsStream(String url) {
		InputStream stream = getDefaultClassLoader().getResourceAsStream(url);
		return stream;
	}
	
	private static ClassLoader getDefaultClassLoader(){
		ClassLoader cl = null;
		
		cl = Thread.currentThread().getContextClassLoader();
		if(cl == null){
			cl = ClassUtils.class.getClassLoader();
		}
		
		return cl;
	}
}
