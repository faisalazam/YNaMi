package migration.pk.lucidxpo.ynami.test;

import migration.pk.lucidxpo.ynami.helper.DBCleaner;
import migration.pk.lucidxpo.ynami.helper.DBTableColumnMetaData;
import migration.pk.lucidxpo.ynami.helper.MigrationScriptFetcher;
import migration.pk.lucidxpo.ynami.helper.MultiSqlExecutor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static migration.pk.lucidxpo.ynami.helper.DBTableColumnMetaData.findColumnMetaData;
import static migration.pk.lucidxpo.ynami.helper.DBTableColumnMetaDataUtil.assertColumnExists;
import static migration.pk.lucidxpo.ynami.helper.DBTableColumnMetaDataUtil.getColumnMetaData;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.SCRIPT_DIRECTORY_PATH;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.evolveDatabase;
import static migration.pk.lucidxpo.ynami.helper.MigrationTestHelper.executorForLocalMySql;
import static migration.pk.lucidxpo.ynami.helper.ReflectionHelper.getEntityClasses;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.data.util.Pair.of;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * If the Java archive entity name follows the pattern "EntityName" + "Archive", you don't need to write testcase manually
 */
@SuppressWarnings({"rawtypes", "SameParameterValue"})
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
            System.out.println("Checking the table structure between [" + entry.getFirst().getSimpleName()
                    + "] and [" + entry.getSecond().getSimpleName() + "]");

            checkEntityTableAndArchiveTableDBStructure(entry.getFirst().getSimpleName(),
                    entry.getSecond().getSimpleName(), true, true);
        }
    }

    @Test
    void shouldGetExceptionWhenCheckStructureForTableInIgnoredList() {
        for (final Pair<Class, Class> entry : getPairsFromEntityCollection(IGNORE_ENTITY_CLASSES)) {
            System.out.println("Checking the table structure between [" + entry.getFirst().getSimpleName()
                    + "] and [" + entry.getSecond().getSimpleName() + "]");
            Exception expectedException = null;
            try {
                checkEntityTableAndArchiveTableDBStructure(entry.getFirst().getSimpleName(),
                        entry.getSecond().getSimpleName(), true, true);
            } catch (Exception e) {
                expectedException = e;
            }
            assertNotNull(expectedException);
        }
    }

    private void checkEntityTableAndArchiveTableDBStructure(final String entityTable,
                                                            final String secondaryTable,
                                                            final String archiveTable,
                                                            final boolean ignoreNullable,
                                                            final boolean ignoreDataDefault) {
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
            fail("could not find any column on entity table["
                    + tableName + "], make sure all the delta scripts are applied."
            );
        }
    }

    private void checkEntityTableAndArchiveTableDBStructure(final String entityTable,
                                                            final String archiveTable,
                                                            final boolean ignoreNullable,
                                                            final boolean ignoreDataDefault) {
        checkEntityTableAndArchiveTableDBStructure(entityTable,
                null,
                archiveTable,
                ignoreNullable,
                ignoreDataDefault
        );
    }

    private void checkTableStructure(final List<DBTableColumnMetaData> tableColumnMetaData,
                                     final List<DBTableColumnMetaData> archiveTableColumnMetaData,
                                     final boolean ignoreNullable,
                                     final boolean ignoreDataDefault) {
        assertColumnExists(tableColumnMetaData, "ID", "VARCHAR");

        DBTableColumnMetaData columnMetaData, archiveColumnMetaData;
        for (DBTableColumnMetaData aTableColumnMetaData : tableColumnMetaData) {
            columnMetaData = aTableColumnMetaData;
            archiveColumnMetaData = findColumnMetaData(archiveTableColumnMetaData, columnMetaData.getColumnName());

            if (archiveColumnMetaData == null) {
                fail("Could not find the archive column for [" + columnMetaData.getColumnName()
                        + "] on table [" + archiveTableColumnMetaData.get(0).getTableName() + "]"
                );
                return;
            }

            assertEquals(
                    columnMetaData.getColumnName(),
                    archiveColumnMetaData.getColumnName(),
                    "The column[" + columnMetaData.getColumnName() + "] on table ["
                            + columnMetaData.getTableName() + "] should have corresponding column on archive table ["
                            + archiveColumnMetaData.getTableName() + "]"
            );
            assertEquals(
                    columnMetaData.getDataType(),
                    archiveColumnMetaData.getDataType(),
                    "The data type of column[" + columnMetaData.getColumnName()
                            + "] on table [" + columnMetaData.getTableName() + "] should be the same on archive table ["
                            + archiveColumnMetaData.getTableName() + "]"
            );
            assertEquals(
                    columnMetaData.getCharacterMaxLength(),
                    archiveColumnMetaData.getCharacterMaxLength(),
                    "The data length of column[" + columnMetaData.getColumnName() + "] on table ["
                            + columnMetaData.getTableName() + "] is [" + columnMetaData.getCharacterMaxLength()
                            + "] and it should not be greater than the data length ["
                            + archiveColumnMetaData.getCharacterMaxLength() + "] on archive table ["
                            + archiveColumnMetaData.getTableName() + "]"
            );

            if (columnMetaData.getNumericPrecision() > archiveColumnMetaData.getNumericPrecision()) {
                fail("The numeric precision of column[" + columnMetaData.getColumnName()
                        + "] on table [" + columnMetaData.getTableName() + "] is ["
                        + columnMetaData.getNumericPrecision()
                        + "] and it should not be greater than the numeric precision ["
                        + archiveColumnMetaData.getNumericPrecision() + "] on archive table ["
                        + archiveColumnMetaData.getTableName() + "]"
                );
            }

            if (columnMetaData.getDatetimePrecision() > archiveColumnMetaData.getDatetimePrecision()) {
                fail("The datetime precision of column[" + columnMetaData.getColumnName()
                        + "] on table [" + columnMetaData.getTableName() + "] is [" + columnMetaData.getDatetimePrecision()
                        + "] and it should not be greater than the datetime precision ["
                        + archiveColumnMetaData.getDatetimePrecision() + "] on archive table ["
                        + archiveColumnMetaData.getTableName() + "]"
                );
            }

            if (!ignoreNullable) {
                assertEquals(
                        columnMetaData.isNullable(),
                        archiveColumnMetaData.isNullable(),
                        "The nullable of column[" + columnMetaData.getColumnName()
                                + "] on table [" + columnMetaData.getTableName()
                                + "] should be the same on archive table [" + archiveColumnMetaData.getTableName() + "]"
                );
            }
            if (!ignoreDataDefault) {
                assertEquals(
                        columnMetaData.getDataDefault(),
                        archiveColumnMetaData.getDataDefault(),
                        "The data default value of column[" + columnMetaData.getColumnName()
                                + "] on table [" + columnMetaData.getTableName()
                                + "] should be the same on archive table ["
                                + archiveColumnMetaData.getTableName() + "]"
                );
            }
        }
    }

    private Collection<Pair<Class, Class>> getArchivableEntityPairs() {
        final Collection<Class<?>> entityClasses = getEntityClasses(IGNORE_ENTITY_CLASSES);
        return getPairsFromEntityCollection(entityClasses);
    }

    private Collection<Pair<Class, Class>> getPairsFromEntityCollection(Collection<Class<?>> entityClasses) {
        final Set<Pair<Class, Class>> archivableEntityClasses = newHashSet();
        entityClasses.forEach(entityClass -> {
            final String relatedClassName = getRelatedClassSimpleName(entityClass.getSimpleName());
            final Class relatedClass = getEntityClass(relatedClassName, entityClasses);
            if (relatedClass != null) {
                addToCollection(archivableEntityClasses, entityClass, relatedClass);
            }
        });

        return archivableEntityClasses;
    }

    private void addToCollection(final Collection<Pair<Class, Class>> archivableEntityClasses,
                                 final Class entityClass,
                                 final Class relatedClass) {
        final Class entity = entityClass.getSimpleName().endsWith(ARCHIVE_SUFFIX) ? relatedClass : entityClass;
        final Class archiveEntity = entityClass.getSimpleName().endsWith(ARCHIVE_SUFFIX) ? entity : relatedClass;

        if (!containEntry(archivableEntityClasses, entity)) {
            archivableEntityClasses.add(of(entity, archiveEntity));
        }
    }

    private boolean containEntry(final Collection<Pair<Class, Class>> archivableEntityClasses, final Class entity) {
        return archivableEntityClasses.stream().anyMatch(entry -> entry.getFirst().equals(entity));
    }

    private Class<?> getEntityClass(final String relatedClassName, final Collection<Class<?>> entityClasses) {
        for (final Class<?> entity : entityClasses) {
            if (entity.getSimpleName().equals(relatedClassName)) {
                return entity;
            }
        }
        System.out.println("Could not find related class for entity class [" + relatedClassName + "]");
        return null;
    }

    private String getRelatedClassSimpleName(final String entityClassSimpleName) {
        return entityClassSimpleName.endsWith(ARCHIVE_SUFFIX)
                ? entityClassSimpleName.substring(0, entityClassSimpleName.indexOf(ARCHIVE_SUFFIX))
                : entityClassSimpleName + ARCHIVE_SUFFIX;
    }
}