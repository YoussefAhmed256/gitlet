package gitlet;

import gitlet.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Utils.*;

public class StagingArea {
    private File additionDir;
    private File removalDir;

    public StagingArea(File additionDir, File removalDir) {
        this.additionDir = additionDir;
        this.removalDir = removalDir;
    }

    public void StageFileForAddition(File file){
        String name=file.getName();
        File stagedFile = join(additionDir, name);
        writeContents(stagedFile, readContentsAsString(file));
    }

    public void StageFileForRemoval(File file){
        String name=file.getName();
        File stagedFile = join(removalDir, name);
        writeContents(stagedFile, readContentsAsString(file));
    }

    public File GetFileForAddition(File file){
        String name=file.getName();
        File stagedFile = Utils.join(additionDir, name);
        return stagedFile;
    }
    public File GetFileForRemoval(File file){
        String name=file.getName();
        File stagedFile = Utils.join(removalDir, name);
        return stagedFile;
    }
    public boolean IsStagedForAddition(File file){
        String name=file.getName();
        File stagedFile = Utils.join(additionDir, name);
        return stagedFile.exists();
    }
    public boolean IsStagedForRemoval(File file){
        String name=file.getName();
        File stagedFile = Utils.join(removalDir, name);
        return stagedFile.exists();
    }

    public boolean UnstageFileForAddition(File file){
        String name=file.getName();
        File stagedFile = Utils.join(additionDir, name);
        return stagedFile.delete();
    }
    public boolean UnstageFileForRemoval(File file){
        String name=file.getName();
        File stagedFile = Utils.join(removalDir, name);
        return stagedFile.delete();
    }

    public List<String> GetAllFilesForAddition(){
        List<String> files = new ArrayList<String>();
        for (String name : plainFilenamesIn(additionDir))
            files.add(name);
        return files;
    }
    public List<String> GetAllFilesForRemoval(){
        List<String> files = new ArrayList<String>();
        for (String name : plainFilenamesIn(removalDir))
            files.add(name);
        return files;
    }

    public boolean IsEmpty(){
        return plainFilenamesIn(additionDir).isEmpty()&& plainFilenamesIn(removalDir).isEmpty() ;
    }
    
}
