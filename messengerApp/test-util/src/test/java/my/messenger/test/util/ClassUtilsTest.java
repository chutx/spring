package my.messenger.test.util;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

public class ClassUtilsTest {

	@Test
	public void getResourceAsStreamTest(){
		String url = "scripts/create-test.sql";
		InputStream stream = ClassUtils.getResourceAsStream(url);
		Assert.assertNotNull(stream);
	}
}
