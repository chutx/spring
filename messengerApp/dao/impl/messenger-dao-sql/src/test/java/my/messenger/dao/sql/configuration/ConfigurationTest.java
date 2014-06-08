package my.messenger.dao.sql.configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value={"classpath:META-INF/spring/orm-context.xml"})
public class ConfigurationTest {

	@Autowired
	private DataSource dataSource;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Value("${jdbc.h2.username}")
	private String userName;
	
	@Test
	public void dataSourceTest(){
		Assert.assertNotNull(dataSource);
	}
	
	@Test
	public void placeholderTest(){
		Assert.assertNotNull(userName);
	}
	
	@Test
	public void entityManagerTest(){
		Assert.assertNotNull(entityManager);
	}
	
}
