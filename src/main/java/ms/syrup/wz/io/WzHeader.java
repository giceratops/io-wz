package ms.syrup.wz.io;

import java.io.IOException;

public class WzHeader {

    private short encVersion = 1;
    private int fileStart = 60;
    private long fileSize = 0;
    private String ident = "PKG1";
    private String copyright = "Package file v1.0 Copyright 2002 Wizet, ZMS";

    private short realVersion = 0;
    private long hash = 0;

    public WzHeader() {
    }

    public WzHeader read(final WzFile reader) throws IOException {
        this.ident = reader.seek(0).readString(4);
        this.fileSize = reader.readLong();
        this.fileStart = reader.readInt();
        this.copyright = reader.readNullTerminatedString();
        this.encVersion = reader.seek(this.fileStart).readShort();
        this.nextVersion();
        return this;
    }

    public short encVersion() {
        return this.encVersion;
    }

    public int fileStart() {
        return this.fileStart;
    }

    public long fileSize() {
        return this.fileSize;
    }

    public String ident() {
        return this.ident;
    }

    public String copyright() {
        return this.copyright;
    }

    public short realVersion() {
        return this.realVersion;
    }

    public long hash() {
        return this.hash;
    }

    public boolean nextVersion() {
        while (this.realVersion < Short.MAX_VALUE) {
            this.realVersion++;
            if (this.encVersion == WzUtils.encVersion(this.realVersion)) {
                this.hash = WzUtils.encHash(this.realVersion);
                return true;
            }
        }
        return false;
    }

    public int indexStart() {
        return this.fileStart + Short.BYTES;
    }
}
