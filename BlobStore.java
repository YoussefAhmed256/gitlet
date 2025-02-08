package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class BlobStore {
    private final File BLOB_DIR;

    public BlobStore(File blobDir) {
        this.BLOB_DIR = blobDir;
    }

    public File SaveBlob(File Blob) {
        String content = readContentsAsString(Blob);
        String hash = sha1(content);
        File blobFile = join(BLOB_DIR, hash);
        writeContents(blobFile, content);
        return blobFile;
    }
    public File GetBlob(String hash) {
        File blobFile = join(BLOB_DIR, hash);
        return blobFile;
    }
    public boolean Contains(String hash) {
        return GetBlob(hash)!=null;
    }

}
