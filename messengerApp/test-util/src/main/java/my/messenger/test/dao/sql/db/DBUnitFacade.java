package my.messenger.test.dao.sql.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import my.messenger.test.util.StreamUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

public final class DBUnitFacade {

	private static Log log = LogFactory.getLog(DBUnitFacade.class);
	private static final String statementSeparator = ";";

	private static DataSource dataSource;

	private static IDatabaseConnection connection;

	private Connection jdbcConnection = null;

	private static DBUnitFacade dbUnit;

	private DBUnitFacade(DataSource ds) {
		dataSource = ds;
	}

	public static synchronized DBUnitFacade getInstance(DataSource ds) {
		if (dbUnit == null) {
			dbUnit = new DBUnitFacade(ds);
			dbUnit.initializeConnection();
		}

		return dbUnit;
	}

	protected void initializeConnection() {
		if (connection == null) {
			try {
				jdbcConnection = dataSource.getConnection();
				connection = new DatabaseConnection(jdbcConnection);
				DatabaseConfig config = connection.getConfig();
				config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
						new H2DataTypeFactory());
			} catch (SQLException e) {
				throw new IllegalStateException(
						"No fue posible inicializar la conexión", e);
			} catch (DatabaseUnitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected Connection getJDBCConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new IllegalStateException(
					"No fue posible obtener la conexión del datasource", e);
		}
	}

	public IDatabaseConnection getConnection() {
		initializeConnection();
		return connection;
	}

	public IDataSet getDataSet(String nomArch) {
		IDataSet iDataSet;
		try {
			InputStream stream = StreamUtils.getResourceAsInputStream(nomArch);
			FlatXmlDataSetBuilder dsBuilder = new FlatXmlDataSetBuilder();
			iDataSet = dsBuilder.build(stream);
		} catch (Exception e) {
			throw new IllegalStateException(
					"No se pudo inicializar el dataset", e);
		}

		return iDataSet;
	}

	public void executeSql(String path) {
		executeSql(path, true);
	}

	/**
	 * Ejecuta el script que se le envia.
	 * 
	 * @param esquema
	 *            El script a ejecutar.
	 * @throws Exception
	 */
	public void executeSql(String path, boolean exceptionOnError) {

		Connection conn = getJDBCConnection();
		Statement stm = null;
		try {
			stm = conn.createStatement();
			List<String> list = readScheme(path);
			Iterator<String> iter = list.iterator();

			while (iter.hasNext()) {
				String cmd = (String) iter.next();
				executeUpdate(stm, cmd, exceptionOnError);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(conn, stm);
		}
	}

	public void insert(String nomArch) {
		log.debug("Inicia insert(" + nomArch + ")");
		initializeConnection();

		IDataSet dataSet = getDataSet(nomArch);
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
		insertExecution(replacementDataSet);
	}
	
	private void insertExecution(ReplacementDataSet replacementDataSet){
		try {
			DatabaseOperation.INSERT.execute(connection, replacementDataSet);
		} catch (Exception e) {
			String msg = "Excepcion al ejecutar clean all and insert";
			handleException(msg, e);
		}
	}

	private int executeUpdate(Statement statement, String command,
			boolean exceptionOnError) throws SQLException {
		int ret = -1;
		try {
			log.debug("Executing: " + command);
			ret = statement.executeUpdate(command);
			log.debug("result executeUpdate()=" + ret);
		} catch (SQLException exc) {
			if (exceptionOnError) {
				throw exc;
			}
		}

		return ret;
	}

	private List<String> readScheme(String path) {
		BufferedReader reader = null;
		List<String> list = new ArrayList<String>();

		try {

			InputStream stream = StreamUtils.getResourceAsInputStream(path);
			reader = new BufferedReader(new InputStreamReader(stream));
			StringBuffer command = new StringBuffer();
			String line;

			while ((line = reader.readLine()) != null) {
				if (!isSQLComment(line)) {
					if (line.endsWith(statementSeparator)) {
						line = line.substring(0, line.length() - 1); // chop
						command.append(line);
						list.add(command.toString());
						command = new StringBuffer();
					} else {
						command.append(line).append("\n");
					}
				}
			}
		} catch (Exception e) {
			String msg = "Excepcion al leer el esquema de BD";
			handleException(msg, e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error(e, e);
				}
			}
		}

		return list;
	}

	private static Boolean isSQLComment(String line) {
		return line.startsWith("--");
	}

	private void closeStatement(Connection conn, Statement stm) {
		if (stm != null) {
			try {
				stm.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void handleException(String msg, Throwable e) {
		log.error(msg, e);
		throw new IllegalStateException(msg, e);
	}

}
