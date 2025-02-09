package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class WorkingArea {
    private final File WORKING_DIR;

    public WorkingArea(File workingDir) {
        this.WORKING_DIR = workingDir;
    }

    public void SaveFile(String content , String fileName) {
        File workingFile = join(WORKING_DIR, fileName);
        writeContents(workingFile,content);
    }
    public boolean DeleteFile(String fileName) {
        File file=GetFile(fileName);
        return file.delete();
    }

    public File GetFile(String fileName) {
        File file=join(WORKING_DIR, fileName);
        return file;
    }
    public void Clear() {
        for (String name : WORKING_DIR.list()) GetFile(name).delete();
    }

}
