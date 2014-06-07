package my.messenger.test.dao.sql.db;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.ITable;
import org.junit.Test;

public class DBUnitFacadeTest {

	private static DBUnitFacade dbUnit;
	
	static{
		String driver = "org.h2.Driver";
		String user = "msn_usr";
		String pwd = "msn_usr";
		String url = "jdbc:h2:mem:cta;MODE=ORACLE";
		
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(pwd);
		dbUnit = DBUnitFacade.getInstance(ds);
	}
	
	@Test
	public void assertCreateTable() throws SQLException, DatabaseUnitException{
		String dataSetPath = "datasets/dataset-test.xml";
		String path = "scripts/create-test.sql";
		dbUnit.executeSql(path);
		dbUnit.insert(dataSetPath);
		
		ITable actualTable = dbUnit.getConnection().createDataSet().getTable("user");
		
		ITable expectedTable = dbUnit.getDataSet(dataSetPath).getTable("user");
		
		Assertion.assertEquals(expectedTable, actualTable);
	}
}
