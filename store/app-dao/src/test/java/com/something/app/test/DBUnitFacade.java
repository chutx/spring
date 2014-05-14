package com.something.app.test;
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

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.util.ClassUtils;

/**
 * Utilerías para el uso de DbUnit.
 * 
 * @author angel.rodriguez
 */
public final class DBUnitFacade {
	private static Log log = LogFactory.getLog(DBUnitFacade.class);
	private static DBUnitFacade dbunit;
	private static String statementSeparator = ";";

	private BasicDataSource dataSource;

	/**
	 * Atributo conexion
	 */
	private IDatabaseConnection connection = null;

	/**
	 * Atributo conexionJDBC
	 */
	private Connection jdbcConnection = null;

	/**
	 * Descripción: Regresa una única instancia de DBUnitFacade
	 * 
	 * @param configFile
	 * @return
	 */
	public static DBUnitFacade getInstance(BasicDataSource dataSource) {
		if (dbunit == null) {
			dbunit = new DBUnitFacade(dataSource);
			dbunit.initializeConnection();
			return dbunit;
		} else {
			return dbunit;
		}
	}

	private DBUnitFacade(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Descripción: Método que inicializa una conexion a la base de datos.
	 * 
	 * @throws TorqueDAOException
	 * @throws SQLException
	 */
	protected void initializeConnection() {
		if (connection == null) {
			try {
				jdbcConnection = dataSource.getConnection();
				connection = new DatabaseConnection(jdbcConnection);
				DatabaseConfig config = connection.getConfig();
				config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
			} catch (SQLException e) {
				throw new IllegalStateException(
						"No fue posible inicializar la conexión", e);
			} catch (DatabaseUnitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Descripción: Entrega una conexion jdbc.
	 * 
	 * @return
	 */
	protected Connection getJDBCConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new IllegalStateException("No fue posible obtener la conexión del datasource", e);
		}
	}

	/**
	 * Método que entrega una conexion a la base de datos.
	 * 
	 * @return una conexión de dbunit.
	 */
	public IDatabaseConnection getConnection() {
		initializeConnection();
		return connection;
	}

	/**
	 * Descripción: Método que carga el dataset.
	 * 
	 * @param nomArch
	 *            el nombre del archivo
	 * @return Un IDataSet
	 * @throws Exception
	 *             lanza una Excepcion
	 */
	protected IDataSet getDataSet(String nomArch) {
		IDataSet iDataSet;
		try {
			InputStream stream = getResourceAsStream(nomArch);
			iDataSet = new FlatXmlDataSet(stream);
		} catch (Exception e) {
			throw new IllegalStateException("No se pudo inicializar el dataset", e);
		}

		return iDataSet;
	}

	private static InputStream getResourceAsStream(String url) {
		InputStream stream = ClassUtils.getDefaultClassLoader().getResourceAsStream(url);
		return stream;
	}

	/**
	 * Descripción: Metodo Insert
	 * 
	 * @param nomArch
	 *            nombre del archivo
	 * @throws Exception
	 *             lanza una Excepcion
	 */
	public void insert(String nomArch) {
			log.debug("Inicia insert(" + nomArch + ")");
			initializeConnection();

			IDataSet dataSet = getDataSet(nomArch);
			ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
			insertExecution(replacementDataSet);
	}

	/**
	 * Descripción: Metodo que lee el esquema de un archivo .sql, lo filtra y
	 * regresa una coleccion de instrucciones SQL.
	 * 
	 * @param esquema
	 *            El esquema a ejecutar.
	 * @return Una lista con el esquema
	 * @throws Exception
	 *             lanza una Excepcion
	 */
	private static List<String> readScheme(String path) {
		BufferedReader reader = null;
		List<String> list = new ArrayList<String>();

		try {

			InputStream stream = getResourceAsStream(path);
			reader = new BufferedReader(new InputStreamReader(stream));
			StringBuffer command = new StringBuffer();
			String line;

			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("--")) {
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
			// GSE: Se fuerza a cerrar el stream
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

	/**
	 * Ejecuta el script que se le envia.
	 * 
	 * @param esquema
	 *            El script a ejecutar.
	 * @throws Exception
	 */
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

	private void insertExecution(ReplacementDataSet replacementDataSet){
		try {
			DatabaseOperation.INSERT.execute(connection, replacementDataSet);
		} catch (Exception e) {
			String msg = "Excepcion al ejecutar clean all and insert";
			handleException(msg, e);
		}
	}
	
	public void setDataSource(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}

	private static void handleException(String msg, Throwable e) {
		log.error(msg, e);
		throw new IllegalStateException(msg, e);
	}
}
