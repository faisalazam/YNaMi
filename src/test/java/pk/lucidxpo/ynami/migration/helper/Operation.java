package pk.lucidxpo.ynami.migration.helper;

public interface Operation {
    void execute(MultiSqlExecutor executor);
}