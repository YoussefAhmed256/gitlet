package gitlet;

import java.io.File;

public class Head {
    File headDir;

    public Head(File headDir) {
        this.headDir = headDir;
    }

    public File getHeadDir() {
        return headDir;
    }

    public void setHeadDir(File headDir) {
        this.headDir = headDir;
    }
}
