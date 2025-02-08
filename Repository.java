package gitlet;

import java.io.File;
import java.util.Date;

import static gitlet.Utils.*;

public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File WORKING_DIR =join(GITLET_DIR,"WorkingArea");
    public static final File COMMIT_DIR = join(GITLET_DIR,"Commits");
    public static final File BLOBS_DIR = join(GITLET_DIR,"Blobs");
    public static final File HEAD_DIR = join(GITLET_DIR,"Head");
    public static final File STAGING_DIR = join(GITLET_DIR,"Staging");
    public static final File ADDITION_Dir = join(STAGING_DIR,"StagingForAddition");
    public static final File REMOVAL_DIR = join(STAGING_DIR,"StagingForRemoval");
    public static final File BRANCH_DIR = join(GITLET_DIR,"Branch");

    BlobStore blobStore = new BlobStore(BLOBS_DIR);
    CommitStore commitStore = new CommitStore(COMMIT_DIR);
    Head head = new Head(HEAD_DIR);
    StagingArea stagingArea = new StagingArea(ADDITION_Dir, REMOVAL_DIR);
    WorkingArea workingArea = new WorkingArea(WORKING_DIR);
    BranchStore branchStore = new BranchStore(BRANCH_DIR);

    /* TODO: fill in the rest of this class. */

    public void init (){
        if (GITLET_DIR.exists()){
            exitWithMessage("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdirs();
        WORKING_DIR.mkdirs();
        COMMIT_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        HEAD_DIR.mkdirs();
        STAGING_DIR.mkdirs();
        ADDITION_Dir.mkdirs();
        BRANCH_DIR.mkdirs();

        Commit initialCommit =new Commit("initial commit",null,null,new Date(0),null);
        commitStore.saveCommit(initialCommit);

        Branch masterBranch =new Branch("master",initialCommit.getHash());
        branchStore.saveBranch(masterBranch);
    }



}
