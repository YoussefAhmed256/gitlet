package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class WorkingArea {
    private File workingDir;

    public WorkingArea(File workingDir) {
        this.workingDir = workingDir;
    }

    public void SaveFile(File file) {
        String content=readContentsAsString(file);
        String hash =sha1(content);
        File workingFile = join(workingDir, hash);
        writeContents(workingFile,content);
    }
    public boolean DeleteFile(String fileName) {
        File file=GetFile(fileName);
        return file.delete();
    }

    private File GetFile(String fileName) {
        File file=join(workingDir, fileName);
        return file;
    }
    public void Clear() {
        for (String name : workingDir.list()) GetFile(name).delete();
    }

}
