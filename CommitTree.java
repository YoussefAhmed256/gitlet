package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

public class CommitTree implements Serializable {
    private TreeMap<String , String> parentOf;

    public CommitTree() {
        this.parentOf = new TreeMap<>();
    }
    public void addCommit(String commit , String parent) {
        parentOf.put(commit, parent);
    }
    public String getParent(String commit) {
        return parentOf.get(commit);
    }
    public boolean containsCommit(String commit) {
        return parentOf.containsKey(commit);
    }

}
