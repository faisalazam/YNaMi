package pk.lucidxpo.ynami.migration.test;

import org.joda.time.DateTime;
import pk.lucidxpo.ynami.migration.helper.MultiSqlExecutor;

import java.util.List;

import static org.assertj.core.util.Lists.newArrayList;

public abstract class Insert {
    void insert(final MultiSqlExecutor executor,
                final String tableName,
                final List<String> columnNames,
                final List<Object> columnValues) {
        final StringBuilder stringBuilder = new StringBuilder();

        final List<Object> values = newArrayList();
        for (final Object columnValue : columnValues) {
            if (columnValue instanceof DateTime) {
                values.add("to_date('" + ((DateTime) columnValue).toString("dd/MM/yyyy") + "','dd/MM/yyyy')");
            } else if ("current_date".equals(columnValue) || columnValue instanceof Integer) {
                values.add(columnValue);
            } else {
                values.add(strVal((String) columnValue));
            }
        }

        final String sqlString = stringBuilder.append("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(String.join(",", columnValues.toArray(new String[]{})))
                .append(") ")
                .append("VALUES (")
                .append(String.join(",", values.toArray(new String[]{})))
                .append(")")
                .toString();

        executor.getTemplate().execute(sqlString);
    }

    public static String strVal(final String columnValue) {
        if (columnValue == null) {
            return "null";
        } else {
            return "'" + columnValue + "'";
        }
    }

    public abstract void to(MultiSqlExecutor executor);
}
