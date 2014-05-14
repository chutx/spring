package com.something.app.test;
import java.sql.Types;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;

public class H2DataTypeFactory extends DefaultDataTypeFactory {
	@Override
	public DataType createDataType(int sqlType, String sqlTypeName)
			throws DataTypeException {
		
		if (sqlType == Types.BOOLEAN) {
			return DataType.BOOLEAN;
		} else if (sqlType == 1111) {
			// en Oracle este es un NCLOB
			return DataType.VARCHAR;
		}

		return super.createDataType(sqlType, sqlTypeName);
	}
}