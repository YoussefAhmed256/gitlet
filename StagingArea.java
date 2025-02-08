package gitlet;

import gitlet.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Utils.*;

public class StagingArea {
    private final File ADDITION_Dir;
    private final File REMOVAL_Dir;

    public StagingArea(File additionDir, File removalDir) {
        this.ADDITION_Dir = additionDir;
        this.REMOVAL_Dir = removalDir;
    }

    public File StageFileForAddition(File file){
        String name=file.getName();
        File stagedFile = join(ADDITION_Dir, name);
        writeContents(stagedFile, readContentsAsString(file));
        return stagedFile;
    }

    public File StageFileForRemoval(File file){
        String name=file.getName();
        File stagedFile = join(REMOVAL_Dir, name);
        writeContents(stagedFile, readContentsAsString(file));
        return stagedFile;
    }

    public File GetFileForAddition(File file){
        String name=file.getName();
        File stagedFile = Utils.join(ADDITION_Dir, name);
        return stagedFile;
    }
    public File GetFileForRemoval(File file){
        String name=file.getName();
        File stagedFile = Utils.join(REMOVAL_Dir, name);
        return stagedFile;
    }
    public boolean IsStagedForAddition(File file){
        String name=file.getName();
        File stagedFile = Utils.join(ADDITION_Dir, name);
        return stagedFile.exists();
    }
    public boolean IsStagedForRemoval(File file){
        String name=file.getName();
        File stagedFile = Utils.join(REMOVAL_Dir, name);
        return stagedFile.exists();
    }

    public boolean UnstageFileForAddition(File file){
        String name=file.getName();
        File stagedFile = Utils.join(ADDITION_Dir, name);
        return stagedFile.delete();
    }
    public boolean UnstageFileForRemoval(File file){
        String name=file.getName();
        File stagedFile = Utils.join(REMOVAL_Dir, name);
        return stagedFile.delete();
    }

    public List<String> GetAllFilesForAddition(){
        List<String> files = new ArrayList<String>();
        for (String name : plainFilenamesIn(ADDITION_Dir))
            files.add(name);
        return files;
    }
    public List<String> GetAllFilesForRemoval(){
        List<String> files = new ArrayList<String>();
        for (String name : plainFilenamesIn(REMOVAL_Dir))
            files.add(name);
        return files;
    }

    public boolean IsEmpty(){
        return plainFilenamesIn(ADDITION_Dir).isEmpty() || plainFilenamesIn(REMOVAL_Dir).isEmpty() ;
    }
    
}
