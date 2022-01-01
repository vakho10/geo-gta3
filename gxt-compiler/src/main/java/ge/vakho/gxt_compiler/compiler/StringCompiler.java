package ge.vakho.gxt_compiler.compiler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ge.vakho.gxt_compiler.App;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StringCompiler {

    private Map<String, Short> converterJsonMap;
    public TableData mainTable = new TableData();

    public StringCompiler() throws IOException {
        // Load key mappings (this is very important)
        try (InputStream is = App.class.getResourceAsStream("/key-mapping.json");
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr)) {
            converterJsonMap = new Gson().fromJson(br, new TypeToken<Map<String, Short>>() {
            }.getType());
        }
    }

    public void addTextLine(String key, String text) {
        long offset = mainTable.getData().size() * Character.BYTES;
        mainTable.getKeys().add(new TableEntry(key, offset));
        convertString(text, mainTable.getData());
    }

    public void convertString(String text, List<Short> buf) {
        for (int i = 0; i < text.toCharArray().length; i++) {
            buf.add(convertChar(text.charAt(i)));
        }
        buf.add((short) 0);
    }

    public short convertChar(char chr) {
        // TODO Default value must not return!
        return converterJsonMap.getOrDefault("" + chr, (short) chr);
    }

    public void outputIntoStream(OutputStream s) throws IOException {
        if (isMainTableNeedSorting()) {
            Collections.sort(mainTable.getKeys());
        }
        System.out.printf("Table MAIN has %d keys\n", mainTable.getKeys().size());
        mainTable.writeToStream(s);
    }

    public boolean isMainTableNeedSorting() {
        return true;
    }
}
