package pk.lucidxpo.ynami.migration.helper;

import org.springframework.jdbc.core.JdbcOperations;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static pk.lucidxpo.ynami.migration.test.Insert.strVal;

public class MultiSqlExecutor {
    private static final String DELIMITER = ";";
    private final JdbcOperations template;

    public MultiSqlExecutor(final JdbcOperations template) {
        this.template = template;
    }

    public void execute(final String multiSql) {
        final String[] sqlStatements = multiSql.split(DELIMITER);
        for (final String sql : sqlStatements) {
            if (sql.contains("--//@UNDO")) break;
            if (isNotBlank(sql)) {
                template.execute(sql);
            }
        }
    }

    public JdbcOperations getTemplate() {
        return template;
    }

    public RowExtractor getRowExtractor(final String tableName) {
        return new RowExtractor(tableName);
    }

    public class RowExtractor {
        private final String tableName;

        RowExtractor(final String tableName) {
            this.tableName = tableName;
        }

        public Map<String, Object> getSingleRowWithId(final String id) {
            final List<Map<String, Object>> results = getRowsWithSpecifiedField("id", id);
            if (results.size() > 1) {
                throw new IllegalArgumentException("Found more than 1 row in " + tableName + " that satisfies " + id);
            }

            return results.get(0);
        }

        public List<Map<String, Object>> getRowsWithSpecifiedField(final String fieldName, final String fieldValue) {
            final String sqlQuery = "SELECT * FROM " + tableName + " WHERE " + fieldName + " = " + strVal(fieldValue);
            return template.queryForList(sqlQuery);
        }
    }
}