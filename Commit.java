package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;


public class Commit implements Serializable {
    private String message;
    private String firstParent;
    private String secondParent;
    private String hash;
    private Date timeStamp;
    private TreeMap<String,String>trackedFiles;

    public Commit(String message, String firstParent, String secondParent, Date timeStamp, TreeMap<String, String> trackedFiles) {
        this.message = message;
        this.firstParent = firstParent;
        this.secondParent = secondParent;
        this.hash = Utils.sha1(message, firstParent, secondParent, timeStamp) ;
        this.timeStamp = timeStamp;
        this.trackedFiles = trackedFiles;
    }

    public Map<String, String> getTrackedFiles() {
        return trackedFiles;
    }

    public String getHash() {
        return hash;
    }

    public void saveCommit(File commitsDirectory) {
        File commitFile = join(commitsDirectory, hash);
        writeObject(commitFile, this);
    }

    @Override
    public String toString() {
        return "Commit{" +
                "timestamp=" + timeStamp +
                ", message='" + message + '\'' +
                ", parent='" + firstParent + '\'' +
                ", secondaryParent='" + secondParent + '\'' +
                ", trackedFiles=" + trackedFiles +
                ", hash='" + hash + '\'' +
                '}';
    }
}
