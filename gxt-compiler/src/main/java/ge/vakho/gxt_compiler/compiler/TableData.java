package ge.vakho.gxt_compiler.compiler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class TableData {

    private List<TableEntry> keys = new ArrayList<>();
    private List<Short> data = new ArrayList<>();

    public List<TableEntry> getKeys() {
        return keys;
    }

    public List<Short> getData() {
        return data;
    }

    public void writeToStream(OutputStream os) throws IOException {
        ByteBuffer bb2 = ByteBuffer.allocate(2); // 2 bytes buffer
        bb2.order(ByteOrder.LITTLE_ENDIAN);

        ByteBuffer bb4 = ByteBuffer.allocate(4); // 4 bytes buffer
        bb4.order(ByteOrder.LITTLE_ENDIAN);

        ByteBuffer bb8 = ByteBuffer.allocate(8); // 8 bytes buffer
        bb8.order(ByteOrder.LITTLE_ENDIAN);

        os.write(new byte[]{'T', 'K', 'E', 'Y'});

        // Write TKEY_BLOCK_SIZE (each TKEY entry is 0xC bytes)
        bb4.putInt(keys.size() * 12); // SIZE * 12 bytes
        os.write(bb4.array()); // Store in 4 bytes
        bb4.putInt(0, 0); // Clear buffer
        bb4.clear();

        // Each entry is 12 bytes
        for (TableEntry key : keys) {
            // Key offset (4 bytes)
            bb4.putInt((int) key.getOffset());
            os.write(bb4.array());
            bb4.putInt(0, 0); // Clear buffer
            bb4.clear();

            // Key (8 bytes)
            bb8.put(key.getKey().getBytes());
            os.write(bb8.array());
            bb8.putLong(0, 0); // Clear buffer
            bb8.clear();
        }

        os.write(new byte[]{'T', 'D', 'A', 'T'});
        bb4.putInt(data.size() * Character.BYTES); // SIZE * 2 bytes
        os.write(bb4.array()); // Store in 4 bytes

        for (short chr : data) {
            bb2.putShort(chr);
            os.write(bb2.array());
            bb2.putShort(0, (short) 0); // Clear buffer
            bb2.clear();
        }
    }

}