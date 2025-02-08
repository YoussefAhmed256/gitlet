package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class CommitTreeStore {
    private final File COMMIT_TREE_FILE ;

    public CommitTreeStore(File commitTreeFile) {
        this.COMMIT_TREE_FILE = commitTreeFile;
    }
    public void saveCommitTree (CommitTree commitTree){
        writeObject(COMMIT_TREE_FILE, commitTree);
    }

    public CommitTree loadCommitTree (){
        if(COMMIT_TREE_FILE.exists()) {
            return readObject(COMMIT_TREE_FILE, CommitTree.class);
        }
        return null;
    }
}
