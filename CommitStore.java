package gitlet;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static gitlet.Utils.*;

public class CommitStore {
    private final File COMMIT_DIR;
    public CommitStore(File commitDir) {
        this.COMMIT_DIR = commitDir;
        if(!commitDir.exists()) {
            commitDir.mkdirs();
        }
    }

    public void saveCommit (Commit commit){
        File commitFile = join(COMMIT_DIR,commit.getHash());
        writeObject(commitFile, commit);
    }


    public Commit getCommitByHash(String commitId) {
        if (COMMIT_DIR == null) {
            return null;
        }
        File commitFile = join(COMMIT_DIR, commitId);
        if(commitFile.exists()) {
            return readObject(commitFile, Commit.class);
        }
        // search by hash prefix
        String targetCommitHash = getAllCommitHashes().stream()
                .filter(hash -> hash.startsWith(commitId))
                .findFirst()
                .orElse(null);
        if(targetCommitHash != null){
            return readObject(commitFile, Commit.class);
        }
        return null;
    }


    public List<Commit> getCommitByMessage(String message) {
        return getAllCommits().stream()
                .filter(commit -> commit.getMessage().equals(message))
                .collect(Collectors.toList());
    }


    public List<Commit> getAllCommits(){
        return Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR)).stream()
                .map(hash -> getCommitByHash(hash))
                .collect(Collectors.toList());
    }

    public List<String> getAllCommitHashes() {
        return Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR));
    }

}