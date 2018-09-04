package migration.pk.lucidxpo.ynami.test;

import org.joda.time.DateTime;
import migration.pk.lucidxpo.ynami.helper.MultiSqlExecutor;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.join;

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
                .append(join(columnNames, ","))
                .append(") ")
                .append("VALUES (")
                .append(join(values, ","))
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
