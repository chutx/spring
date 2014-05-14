package com.something.app.test;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;

//extends AbstractTransactionalJUnit4SpringContextTests 

public abstract class GizmoSpringDBUnitTest {

	private static DBUnitFacade DBUNIT_FACADE;

	static {
		String driver = "org.h2.Driver";
		String user = "cta_user";
		String pwd = "cta_u543";
		
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl("jdbc:h2:mem:cta;MODE=ORACLE");
		dataSource.setUsername(user);
		dataSource.setPassword(pwd);
		
		DBUNIT_FACADE = DBUnitFacade.getInstance(dataSource);
	}

	@BeforeClass
	public static void classSetup(){
			DBUNIT_FACADE.executeSql( "scripts/cta-create.sql", false );
			DBUNIT_FACADE.executeSql( "scripts/cdb-create.sql", false );
			
			DBUNIT_FACADE.insert( "datasets/cta-catalogs-dataset.xml" );
			DBUNIT_FACADE.insert( "datasets/cdb-catalogs-dataset.xml" );
			
			DBUNIT_FACADE.insert( "datasets/cdb-commons-dataset.xml" );
			DBUNIT_FACADE.insert( "datasets/cta-commons-dataset.xml" );
	}
	
	@AfterClass
	public static void classTearDown() {
			DBUNIT_FACADE.executeSql( "scripts/cta-drop.sql", false );
			DBUNIT_FACADE.executeSql( "scripts/cdb-drop.sql", false );
	}
	
	 
}
