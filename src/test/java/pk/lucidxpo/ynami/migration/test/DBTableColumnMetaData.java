package pk.lucidxpo.ynami.migration.test;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode
public class DBTableColumnMetaData {
    private final String tableName;
    private final String columnName;
    private final String dataType;
    private final BigDecimal characterMaxLength;
    private final int numericPrecision;
    private final int datetimePrecision;
    private final boolean nullable;
    private final String dataDefault;

    DBTableColumnMetaData(final String tableName,
                          final String columnName,
                          final String dataType,
                          final BigDecimal characterMaxLength,
                          final int numericPrecision,
                          final int datetimePrecision,
                          final boolean nullable,
                          final String dataDefault) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.dataType = dataType;
        this.characterMaxLength = characterMaxLength;
        this.numericPrecision = numericPrecision;
        this.datetimePrecision = datetimePrecision;
        this.nullable = nullable;
        this.dataDefault = dataDefault;
    }
}