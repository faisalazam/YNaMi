package pk.lucidxpo.ynami.migration.test;

import pk.lucidxpo.ynami.migration.helper.MultiSqlExecutor;

import java.util.LinkedHashMap;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static com.google.common.collect.Lists.newArrayList;
import static pk.lucidxpo.ynami.utils.Identity.randomInt;

class InsertSample extends Insert {

    private final LinkedHashMap<String, Object> sampleColumnValues = new LinkedHashMap<>();

    InsertSample() {
        final Integer identity = randomInt();

        sampleColumnValues.put("id", identity);
        sampleColumnValues.put("active", 0);
        sampleColumnValues.put("address", randomAlphabetic(5, 50));
        sampleColumnValues.put("firstName", randomAlphabetic(5, 50));
        sampleColumnValues.put("lastName", randomAlphabetic(5, 50));
    }


    @Override
    public void to(final MultiSqlExecutor executor) {
        List<String> columnNames = newArrayList(sampleColumnValues.keySet());
        List<Object> columnValues = newArrayList(sampleColumnValues.values());

        insert(executor, "Sample", columnNames, columnValues);
    }

    InsertSample withId(final Integer id) {
        this.sampleColumnValues.put("id", id);
        return this;
    }

    InsertSample withActive(final int active) {
        this.sampleColumnValues.put("active", active);
        return this;
    }
}
