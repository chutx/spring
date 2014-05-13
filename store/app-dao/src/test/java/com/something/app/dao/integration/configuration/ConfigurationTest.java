package com.something.app.dao.integration.configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:META-INF/spring/tx-context.xml"})
public class ConfigurationTest {

	@Autowired
	private DataSource dataSource;
	
	@Value(value="${jdbc.h2.username}")
	private String username;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	public void validateDataSource(){
		Assert.assertNotNull(dataSource);
	}
	
	@Test
	public void validateEntityManager(){
		Assert.assertNotNull(entityManager);
	}
}
