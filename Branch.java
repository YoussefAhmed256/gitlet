package gitlet;

import java.io.Serializable;

public class Branch implements Serializable {
    private String name;
    private String commitHash;

    public Branch(String name, String commitHash) {
        this.name = name;
        this.commitHash = commitHash;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getCommitHash() {
        return commitHash;
    }
    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    @Override
    public String toString() {
        return "Branch [name=" + name + ", commitHash=" + commitHash + "]";
    }
}
