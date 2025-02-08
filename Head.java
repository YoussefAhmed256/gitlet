package gitlet;

import java.io.File;

import static gitlet.Utils.writeContents;

public class Head {
    private final File HEAD_DIR;

    public Head(File headDir) {
        this.HEAD_DIR = headDir;
    }

    public File getHeadDir() {
        return HEAD_DIR;
    }

    public void setHead(Branch branch) {
        writeContents(HEAD_DIR,branch.getName());
    }
}
