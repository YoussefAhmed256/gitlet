package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class BlobStore {
    private File blobDir;

    public BlobStore(File blobDir) {
        this.blobDir = blobDir;
    }

    public void SaveBlob(File Blob) {
        String content = readContentsAsString(Blob);
        String hash = sha1(content);
        File blobFile = join(blobDir, hash);
        writeContents(blobFile, content);
    }
    public File GetBlob(String hash) {
        File blobFile = join(blobDir, hash);
        return blobFile;
    }
    public boolean Contains(String hash) {
        return GetBlob(hash)!=null;
    }

}
