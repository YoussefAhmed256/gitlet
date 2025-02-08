package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class Head {
    private final File HEAD_FILE;

    public Head(File headFile) {
        this.HEAD_FILE = headFile;
    }

    public String getHead() {
        return readContentsAsString(HEAD_FILE);
    }

    public void setHead(Branch branch) {
        writeContents(HEAD_FILE,branch.getName());
    }

}
