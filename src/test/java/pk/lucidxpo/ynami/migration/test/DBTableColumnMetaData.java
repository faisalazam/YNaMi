package pk.lucidxpo.ynami.migration.test;

/*
 * immutable class, no getter/setter needed
 */
public class DBTableColumnMetaData {
    final String tableName;
    final String columnName;
    final String dataType;
    final int characterMaxLength;
    final int numericPrecision;
    final int datetimePrecision;
    final boolean nullable;
    final String dataDefault;

    DBTableColumnMetaData(final String tableName,
                          final String columnName,
                          final String dataType,
                          final int characterMaxLength,
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBTableColumnMetaData that = (DBTableColumnMetaData) o;

        if (characterMaxLength != that.characterMaxLength) return false;
        if (numericPrecision != that.numericPrecision) return false;
        if (datetimePrecision != that.datetimePrecision) return false;
        if (nullable != that.nullable) return false;
        if (!columnName.equals(that.columnName)) return false;
        if (dataDefault != null ? !dataDefault.equals(that.dataDefault) : that.dataDefault != null) return false;
        if (!dataType.equals(that.dataType)) return false;
        if (!tableName.equals(that.tableName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tableName.hashCode();
        result = 31 * result + columnName.hashCode();
        result = 31 * result + dataType.hashCode();
        result = 31 * result + characterMaxLength;
        result = 31 * result + numericPrecision;
        result = 31 * result + datetimePrecision;
        result = 31 * result + (nullable ? 1 : 0);
        result = 31 * result + (dataDefault != null ? dataDefault.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DBTableColumnMetaData{" + "tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", dataType='" + dataType + '\'' +
                ", characterMaxLength=" + characterMaxLength +
                ", numericPrecision=" + numericPrecision +
                ", datetimePrecision=" + datetimePrecision +
                ", nullable=" + nullable +
                ", dataDefault='" + dataDefault + '\'' +
                '}';
    }
}