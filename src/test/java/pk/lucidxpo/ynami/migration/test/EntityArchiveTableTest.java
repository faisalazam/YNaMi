package pk.lucidxpo.ynami.migration.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import pk.lucidxpo.ynami.migration.helper.DBCleaner;
import pk.lucidxpo.ynami.migration.helper.MigrationScriptFetcher;
import pk.lucidxpo.ynami.migration.helper.MultiSqlExecutor;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.data.util.Pair.of;
import static org.springframework.util.CollectionUtils.isEmpty;
import static pk.lucidxpo.ynami.PackageVerifierTest.BASE_PACKAGE;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.SCHEMA_NAME;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.SCRIPT_DIRECTORY_PATH;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.evolveDatabase;
import static pk.lucidxpo.ynami.migration.helper.MigrationTestHelper.executorForLocalMySql;
import static pk.lucidxpo.ynami.utils.ReflectionHelper.getTypesAnnotatedWith;

/**
 * If the Java archive entity name follows the pattern "EntityName" + "Archive", you don't need to write testcase manually
 */
@ExtendWith(MockitoExtension.class)
class EntityArchiveTableTest {

    private static final String ARCHIVE_SUFFIX = "Archive";
    /*
     * add if required, i.e. if the working and archive tables need to have different structure for some reason,
     * then they need to be ignored in this test.
     */
    private static final Set<Class<?>> IGNORE_ENTITY_CLASSES = newHashSet();

    private final MultiSqlExecutor executor = executorForLocalMySql();

    @BeforeAll
    static void setup() throws IOException {
        final MultiSqlExecutor multiSqlExecutor = executorForLocalMySql();
        final DBCleaner dbCleaner = new DBCleaner(multiSqlExecutor);
        dbCleaner.cleanDB();
        evolveDatabase(new MigrationScriptFetcher(SCRIPT_DIRECTORY_PATH), multiSqlExecutor);
    }

    @Test
    void shouldHaveSameDBStructureBetweenEntityTableAndRelatedArchiveTable() {
        final Collection<Pair<Class, Class>> archivableEntityClasses = getArchivableEntityPairs();

        for (final Pair<Class, Class> entry : archivableEntityClasses) {
            System.out.println("Checking the table structure between [" + entry.getFirst().getSimpleName() + "] and [" + entry.getSecond().getSimpleName() + "]");

            checkEntityTableAndArchiveTableDBStructure(entry.getFirst().getSimpleName(), entry.getSecond().getSimpleName(), true, true);
        }
    }

    @Test
    void shouldGetExceptionWhenCheckStructureForTableInIgnoredList() {
        for (final Pair<Class, Class> entry : getPairsFromEntityCollection(IGNORE_ENTITY_CLASSES)) {
            System.out.println("Checking the table structure between [" + entry.getFirst().getSimpleName() + "] and [" + entry.getSecond().getSimpleName() + "]");
            Exception expectedException = null;
            try {
                checkEntityTableAndArchiveTableDBStructure(entry.getFirst().getSimpleName(), entry.getSecond().getSimpleName(), true, true);
            } catch (Exception e) {
                expectedException = e;
            }
            assertNotNull(expectedException);
        }
    }

    private void checkEntityTableAndArchiveTableDBStructure(final String entityTable, final String secondaryTable, final String archiveTable, final boolean ignoreNullable, final boolean ignoreDataDefault) {
        final List<DBTableColumnMetaData> tableColumnMetaData = getColumnMetaData(executor, entityTable);
        final List<DBTableColumnMetaData> archiveTableColumnMetaData = getColumnMetaData(executor, archiveTable);
        assertMetaDataNotEmpty(tableColumnMetaData, entityTable);
        assertMetaDataNotEmpty(archiveTableColumnMetaData, archiveTable);

        checkTableStructure(tableColumnMetaData, archiveTableColumnMetaData, ignoreNullable, ignoreDataDefault);

        if (isNotBlank(secondaryTable)) {
            final List<DBTableColumnMetaData> secondaryTableColumnMetaData = getColumnMetaData(executor, secondaryTable);
            assertMetaDataNotEmpty(secondaryTableColumnMetaData, secondaryTable);
            checkTableStructure(secondaryTableColumnMetaData, archiveTableColumnMetaData, ignoreNullable, ignoreDataDefault);
        }
    }

    private void assertMetaDataNotEmpty(final List<DBTableColumnMetaData> columnMetaData, final String tableName) {
        if (isEmpty(columnMetaData)) {
            fail("could not find any column on entity table[" + tableName + "], make sure all the delta scripts are applied.");
        }
    }

    private void checkEntityTableAndArchiveTableDBStructure(final String entityTable, final String archiveTable, final boolean ignoreNullable, final boolean ignoreDataDefault) {
        checkEntityTableAndArchiveTableDBStructure(entityTable, null, archiveTable, ignoreNullable, ignoreDataDefault);
    }

    private void checkTableStructure(final List<DBTableColumnMetaData> tableColumnMetaData, final List<DBTableColumnMetaData> archiveTableColumnMetaData, final boolean ignoreNullable, final boolean ignoreDataDefault) {
        assertColumnExists(tableColumnMetaData, "ID", "VARCHAR");

        DBTableColumnMetaData columnMetaData, archiveColumnMetaData;
        for (DBTableColumnMetaData aTableColumnMetaData : tableColumnMetaData) {
            columnMetaData = aTableColumnMetaData;
            archiveColumnMetaData = findColumnMetaData(archiveTableColumnMetaData, columnMetaData.getColumnName());

            if (archiveColumnMetaData == null) {
                fail("Could not find the archive column for [" + columnMetaData.getColumnName() + "] on table [" + archiveTableColumnMetaData.get(0).getTableName() + "]");
                return;
            }

            assertEquals("The column[" + columnMetaData.getColumnName() + "] on table [" + columnMetaData.getTableName() + "] should have corresponding column on archive table [" + archiveColumnMetaData.getTableName() + "]",
                    columnMetaData.getColumnName(), archiveColumnMetaData.getColumnName());
            assertEquals("The data type of column[" + columnMetaData.getColumnName() + "] on table [" + columnMetaData.getTableName() + "] should be the same on archive table [" + archiveColumnMetaData.getTableName() + "]",
                    columnMetaData.getDataType(), archiveColumnMetaData.getDataType());
            assertEquals("The data length of column[" + columnMetaData.getColumnName() + "] on table [" + columnMetaData.getTableName() + "] is [" + columnMetaData.getCharacterMaxLength()
                            + "] and it should not be greater than the data length [" + archiveColumnMetaData.getCharacterMaxLength() + "] on archive table [" + archiveColumnMetaData.getTableName() + "]",
                    columnMetaData.getCharacterMaxLength(), archiveColumnMetaData.getCharacterMaxLength());

            if (columnMetaData.getNumericPrecision() > archiveColumnMetaData.getNumericPrecision()) {
                fail("The numeric precision of column[" + columnMetaData.getColumnName() + "] on table [" + columnMetaData.getTableName() + "] is [" + columnMetaData.getNumericPrecision()
                        + "] and it should not be greater than the numeric precision [" + archiveColumnMetaData.getNumericPrecision() + "] on archive table [" + archiveColumnMetaData.getTableName() + "]");
            }

            if (columnMetaData.getDatetimePrecision() > archiveColumnMetaData.getDatetimePrecision()) {
                fail("The datetime precision of column[" + columnMetaData.getColumnName() + "] on table [" + columnMetaData.getTableName() + "] is [" + columnMetaData.getDatetimePrecision()
                        + "] and it should not be greater than the datetime precision [" + archiveColumnMetaData.getDatetimePrecision() + "] on archive table [" + archiveColumnMetaData.getTableName() + "]");
            }

            if (!ignoreNullable) {
                assertEquals("The nullable of column[" + columnMetaData.getColumnName() + "] on table [" + columnMetaData.getTableName() + "] should be the same on archive table [" + archiveColumnMetaData.getTableName() + "]",
                        columnMetaData.isNullable(), archiveColumnMetaData.isNullable());
            }
            if (!ignoreDataDefault) {
                assertEquals("The data default value of column[" + columnMetaData.getColumnName() + "] on table [" + columnMetaData.getTableName() + "] should be the same on archive table [" + archiveColumnMetaData.getTableName() + "]",
                        columnMetaData.getDataDefault(), archiveColumnMetaData.getDataDefault());
            }
        }
    }

    private void assertColumnExists(final List<DBTableColumnMetaData> tableColumnMetaData, final String columnName, final String dataType) {
        for (final DBTableColumnMetaData columnMetaData : tableColumnMetaData) {
            if (columnName.equalsIgnoreCase(columnMetaData.getColumnName()) && dataType.equalsIgnoreCase(columnMetaData.getDataType())) {
                return;
            }
        }
        fail("The table [" + tableColumnMetaData.get(0).getTableName() + "] should have column [" + columnName + "] with dataType [" + dataType + "]");
    }

    private DBTableColumnMetaData findColumnMetaData(final List<DBTableColumnMetaData> archiveTableColumnMetaData, final String columnName) {
        for (final DBTableColumnMetaData columnMetaData : archiveTableColumnMetaData) {
            if (columnName.equals(columnMetaData.getColumnName())) {
                return columnMetaData;
            }
        }
        return null;
    }

    private Collection<Pair<Class, Class>> getArchivableEntityPairs() {
        final Collection<Class<?>> entityClasses = getEntityClasses();
        return getPairsFromEntityCollection(entityClasses);
    }

    private Collection<Pair<Class, Class>> getPairsFromEntityCollection(Collection<Class<?>> entityClasses) {
        final Set<Pair<Class, Class>> archivableEntityClasses = newHashSet();
        for (final Class entityClass : entityClasses) {
            final String relatedClassName = getRelatedClassSimpleName(entityClass.getSimpleName());
            final Class relatedClass = getEntityClass(relatedClassName, entityClasses);
            if (relatedClass != null) {
                addToCollection(archivableEntityClasses, entityClass, relatedClass);
            }
        }

        return archivableEntityClasses;
    }

    private void addToCollection(final Collection<Pair<Class, Class>> archivableEntityClasses, final Class entityClass, final Class relatedClass) {
        final Class entity = entityClass.getSimpleName().endsWith(ARCHIVE_SUFFIX) ? relatedClass : entityClass;
        final Class archiveEntity = entityClass.getSimpleName().endsWith(ARCHIVE_SUFFIX) ? entity : relatedClass;

        if (!containEntry(archivableEntityClasses, entity)) {
            archivableEntityClasses.add(of(entity, archiveEntity));
        }
    }

    private boolean containEntry(final Collection<Pair<Class, Class>> archivableEntityClasses, final Class entity) {
        for (final Pair<Class, Class> entry : archivableEntityClasses) {
            if (entry.getFirst().equals(entity)) {
                return true;
            }
        }
        return false;
    }

    private Class getEntityClass(final String relatedClassName, final Collection<Class<?>> entityClasses) {
        for (final Class entity : entityClasses) {
            if (entity.getSimpleName().equals(relatedClassName)) {
                return entity;
            }
        }
        System.out.println("Could not find related class for entity class [" + relatedClassName + "]");
        return null;
    }

    private String getRelatedClassSimpleName(final String entityClassSimpleName) {
        if (entityClassSimpleName.endsWith(ARCHIVE_SUFFIX)) {
            return entityClassSimpleName.substring(0, entityClassSimpleName.indexOf(ARCHIVE_SUFFIX));
        } else {
            return entityClassSimpleName + ARCHIVE_SUFFIX;
        }
    }

    private Collection<Class<?>> getEntityClasses() {
        return getTypesAnnotatedWith(BASE_PACKAGE, Entity.class)
                .stream()
                .filter(entityClass -> !IGNORE_ENTITY_CLASSES.contains(entityClass))
                .collect(toSet());
    }

    @SuppressWarnings("ConstantConditions")
    private List<DBTableColumnMetaData> getColumnMetaData(final MultiSqlExecutor executor, final String tableName) {
        final String querySQL = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, DATETIME_PRECISION, IS_NULLABLE, COLUMN_DEFAULT "
                + "FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE upper(TABLE_SCHEMA) = '" + SCHEMA_NAME.toUpperCase() + "' "
                + "AND upper(TABLE_NAME) = '" + tableName.toUpperCase() + "' ";
        return executor.getTemplate().query(querySQL, (Object[]) null, (rs, rowNum) -> new DBTableColumnMetaData(
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
}