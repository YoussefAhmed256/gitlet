package gitlet;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static gitlet.Utils.*;

public class CommitStore {
    private File commitDir;
    public CommitStore(File commitDir) {
        this.commitDir = commitDir;
        if(!commitDir.exists()) {
            commitDir.mkdirs();
        }
    }

    public void saveCommit (Commit commit){
        File commitFile = join(commitDir,commit.getHash());
        writeObject(commitFile, commit);
    }


    public Commit getCommitByHash(String commitId) {
        if (commitDir == null) {
            return null;
        }
        File commitFile = join(commitDir, commitId);
        if(commitFile.exists()) {
            return readObject(commitFile, Commit.class);
        }
        // search by hash prefix
        return getAllCommits().stream()
                .filter(commit -> commit.getHash().startsWith(commitId))
                .findFirst()
                .orElse(null);
    }


    public List<Commit> getCommitByMessage(String message) {
        return getAllCommits().stream()
                .filter(commit -> commit.getMessage().equals(message))
                .collect(Collectors.toList());
    }


    public List<Commit> getAllCommits(){
        return Objects.requireNonNull(plainFilenamesIn(commitDir)).stream()
                .map(hash -> getCommitByHash(hash))
                .collect(Collectors.toList());
    }

    public List<String> getAllCommitHashes() {
        return Objects.requireNonNull(plainFilenamesIn(commitDir));
    }

}