package migration.pk.lucidxpo.ynami.helper;

import java.util.List;

import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.SCHEMA_NAME;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.getConvertedDataType;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.junit.jupiter.api.Assertions.fail;

public class DBTableColumnMetaDataUtil {

    @SuppressWarnings("ConstantConditions")
    public static List<DBTableColumnMetaData> getColumnMetaData(final MultiSqlExecutor executor, final String tableName) {
        final String querySQL = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, "
                + "NUMERIC_PRECISION, DATETIME_PRECISION, IS_NULLABLE, COLUMN_DEFAULT "
                + "FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' ";
        return executor.getTemplate().query(querySQL, null, null, (rs, rowNum) -> new DBTableColumnMetaData(
                rs.getString("TABLE_NAME"),
                rs.getString("COLUMN_NAME"),
                rs.getString("DATA_TYPE"),
                rs.getBigDecimal("CHARACTER_MAXIMUM_LENGTH"),
                rs.getInt("NUMERIC_PRECISION"),
                rs.getInt("DATETIME_PRECISION"),
                toBoolean(rs.getString("IS_NULLABLE")),
                rs.getString("COLUMN_DEFAULT")
        ));
    }

    public static void assertColumnExists(final List<DBTableColumnMetaData> tableColumnMetaData,
                                          final String columnName,
                                          final String dataType) {
        for (final DBTableColumnMetaData columnMetaData : tableColumnMetaData) {
            if (columnName.equalsIgnoreCase(columnMetaData.getColumnName())
                    && getConvertedDataType(dataType).equalsIgnoreCase(columnMetaData.getDataType())) {
                return;
            }
        }
        fail("The table [" + tableColumnMetaData.get(0).getTableName() + "] should have column ["
                + columnName + "] with dataType [" + dataType + "]"
        );
    }
}
