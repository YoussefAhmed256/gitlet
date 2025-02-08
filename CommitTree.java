package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

public class CommitTree implements Serializable {
    private TreeMap<String , String> commitTree;

    public CommitTree() {
        this.commitTree = new TreeMap<>();
    }
    public void addCommit(String commit , String parent) {
        commitTree.put(commit, parent);
    }
    public TreeMap<String , String> getCommitTree() {
        return commitTree;
    }
    public boolean containsCommit(String commit) {
        return commitTree.containsKey(commit);
    }

}
