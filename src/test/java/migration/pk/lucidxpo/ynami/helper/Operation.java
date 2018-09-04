package migration.pk.lucidxpo.ynami.helper;

public interface Operation {
    void execute(MultiSqlExecutor executor);
}