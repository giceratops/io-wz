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

    public File getFile() throws IOException {
        if (file == null) {
            createFile();
        }
        return file;
    }

    @Override
    protected void before() throws Throwable {
        System.out.println("BEFOREEEE DFGHSDFGJKLDFGSJKL;SDFGJKL;SDFGJKL;SDJKL;SDJKL;SDSDFGJKSDFGJK");
        super.before();
        stream = getClass().getResourceAsStream(res);
    }

    @Override
    protected void after() {
        System.out.println("DFGHSDFGJKLDFGSJKL;SDFGJKL;SDFGJKL;SDJKL;SDJKL;SDSDFGJKSDFGJK");
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
        InputStream stream = getClass().getResourceAsStream(res);
        try {
            file.createNewFile();
            FileOutputStream ostream = null;
            try {
                ostream = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                while (true) {
                    int len = stream.read(buffer);
                    if (len < 0) {
                        break;
                    }
                    ostream.write(buffer, 0, len);
                }
            } finally {
                if (ostream != null) {
                    ostream.close();
                }
            }
        } finally {
            stream.close();
        }
    }

}
