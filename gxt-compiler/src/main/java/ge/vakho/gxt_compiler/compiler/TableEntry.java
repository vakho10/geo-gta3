package ge.vakho.gxt_compiler.compiler;

public class TableEntry implements Comparable<TableEntry> {

    private String key;
    private long offset;

    public TableEntry(String key, long offset) {
        this.key = key;
        this.offset = offset;
    }

    public String getKey() {
        return key;
    }

    public long getOffset() {
        return offset;
    }

    @Override
    public int compareTo(TableEntry te2) {
        return key.compareTo(te2.key);
    }
}
