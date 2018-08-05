package pk.lucidxpo.ynami.migration.helper;

public class MigrationScript {
    private final String fileName;
    private final String content;

    public MigrationScript(final String fileName, final String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getFileName() {
        return fileName;
    }
}