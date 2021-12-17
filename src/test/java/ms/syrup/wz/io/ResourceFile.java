package ms.syrup.wz.io;

import org.junit.rules.ExternalResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceFile extends ExternalResource {
    String res;
    File file = null;
    InputStream stream;

    public ResourceFile(String res) {
        this.res = res;
    }

    public File getFile()  {
        if (file == null) {
            try {
                createFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Override
    protected void before() {
        stream = getClass().getResourceAsStream(res);
    }

    @Override
    protected void after() {
        try {
            stream.close();
        } catch (IOException e) {
            // ignore
        }
        if (file != null) {
            System.out.println("AFTER TEST: " + file.delete());
        }
        super.after();
    }

    private void createFile() throws IOException {
        file = new File(".", res);
        var stream = getClass().getResourceAsStream(res);
        try {
            file.createNewFile();
            try (FileOutputStream ostream = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                while (true) {
                    assert stream != null;
                    int len = stream.read(buffer);
                    if (len < 0) {
                        break;
                    }
                    ostream.write(buffer, 0, len);
                }
            }
        } finally {
            assert stream != null;
            stream.close();
        }
    }

}
