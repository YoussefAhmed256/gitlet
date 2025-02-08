package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class WorkingArea {
    private final File WORKING_DIR;

    public WorkingArea(File workingDir) {
        this.WORKING_DIR = workingDir;
    }

    public void SaveFile(File file) {
        String content=readContentsAsString(file);
        String hash =sha1(content);
        File workingFile = join(WORKING_DIR, hash);
        writeContents(workingFile,content);
    }
    public boolean DeleteFile(String fileName) {
        File file=GetFile(fileName);
        return file.delete();
    }

    private File GetFile(String fileName) {
        File file=join(WORKING_DIR, fileName);
        return file;
    }
    public void Clear() {
        for (String name : WORKING_DIR.list()) GetFile(name).delete();
    }

}
