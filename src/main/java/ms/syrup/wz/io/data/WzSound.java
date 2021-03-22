package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import ms.syrup.wz.io.WzFile;

import java.io.IOException;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzSound extends WzAbstractExtendedData {

    public WzSound(final String label) {
        super(WzDataType.SOUND, label);
    }

    @Override
    protected WzSound read(final WzFile reader) throws IOException {
        reader.readByte();
        final int dataLength = reader.readCompressedInt();

//            //note - soundDataLen does NOT include the length of the header.
//            soundDataLen = preparedReader.ReadCompressedInt();
//            len_ms = preparedReader.ReadCompressedInt();
//
//            long headerOff = preparedReader.BaseStream.Position;
//            preparedReader.BaseStream.Position += soundHeader.Length; //skip GUIDs
//            int wavFormatLen = preparedReader.ReadByte();
//            preparedReader.BaseStream.Position = headerOff;
//
//            header = preparedReader.ReadBytes(soundHeader.Length + 1 + wavFormatLen);
//            ParseHeader();
//
//            //sound fileAtOffset offs
//            offs = preparedReader.BaseStream.Position;
//            if (parseNow)
//                mp3bytes = preparedReader.ReadBytes(soundDataLen);
//            else
//                preparedReader.BaseStream.Position += soundDataLen;
//
        //throw new UnsupportedOperationException("read is not supported yet.");
        return this;
    }
}
