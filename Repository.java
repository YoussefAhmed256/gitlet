package gitlet;

import java.io.File;
import java.util.*;

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
    public static final File ADDITION_DIR = join(STAGING_DIR,"StagingForAddition");
    public static final File REMOVAL_DIR = join(STAGING_DIR,"StagingForRemoval");
    public static final File BRANCH_DIR = join(GITLET_DIR,"Branch");

    BlobStore blobStore = new BlobStore(BLOBS_DIR);
    CommitStore commitStore = new CommitStore(COMMIT_DIR);
    Head head = new Head(HEAD_DIR);
    StagingArea stagingArea = new StagingArea(ADDITION_DIR, REMOVAL_DIR);
    WorkingArea workingArea = new WorkingArea(WORKING_DIR);
    BranchStore branchStore = new BranchStore(BRANCH_DIR);
    CommitTree commitTree = new CommitTree();
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
        ADDITION_DIR.mkdirs();
        BRANCH_DIR.mkdirs();

        Commit initialCommit =new Commit("initial commit",null,null,new Date(0),null);
        commitStore.saveCommit(initialCommit);

        Branch masterBranch =new Branch("master",initialCommit.getHash());
        branchStore.saveBranch(masterBranch);
    }

    public void add(String fileName){
        checkInitializedGitletDirectory();
        File workingFile = workingArea.GetFile(fileName);
        if (workingFile==null){
            exitWithMessage("File does not exist.");
        }
        String commitedFileHash = getCurrentCommit().getTrackedFiles().get(fileName);
        String hash = sha1(readContents(workingFile));

        if (!commitedFileHash.equals(hash)){
            stagingArea.StageFileForAddition(workingFile);
        }
        else {
            stagingArea.UnstageFileForAddition(workingFile);
        }
        stagingArea.UnstageFileForRemoval(workingFile);
    }

    public void commit (String message){
        checkInitializedGitletDirectory();
        commit(message,null);
    }

    public void commit (String message , String secondParent){
        if (message.isEmpty()) {
            exitWithMessage("Please enter a commit message.");
        }

        if (stagingArea.IsEmpty()) {
            exitWithMessage("No changes added to the commit.");
        }

        TreeMap<String,String>Tracked = getCurrentCommit().getTrackedFiles();

        for (File file : ADDITION_DIR.listFiles()) {
            Tracked.put(file.getName(),sha1(readContents(file)));
            stagingArea.UnstageFileForAddition(file);
        }

        for (File file : REMOVAL_DIR.listFiles()) {
            Tracked.remove(file.getName());
            stagingArea.UnstageFileForRemoval(file);
        }

        Commit newcommit = new Commit(message,getCurrentCommit().getHash(),secondParent,null,Tracked) ;
        commitStore.saveCommit(newcommit);

        Branch newBranch = getCurrentBranch();
        newBranch.setCommitHash(sha1(newcommit.getHash()));
        branchStore.saveBranch(newBranch);

    }

    public void rm (String fileName){
        checkInitializedGitletDirectory();
        boolean isStaged = stagingArea.isStagedForAddition(fileName);
        boolean isInTheLastCommit = getCurrentCommit().containsFile(fileName);

        if(!isStaged && !isInTheLastCommit){
            exitWithMessage("No reason to remove this file.");
        }
        File fileToBeRemoved = workingArea.GetFile(fileName);
        if (isInTheLastCommit){
            stagingArea.StageFileForRemoval(fileToBeRemoved);
        }
        if (isStaged){
            stagingArea.UnstageFileForAddition(fileToBeRemoved);
        }
        workingArea.DeleteFile(fileName);
    }

    public void log(){
        checkInitializedGitletDirectory();
        String currentCommitHash = getCurrentCommit().getHash();
        while(currentCommitHash !=null){
            currentCommitHash = commitTree.getParent(currentCommitHash);
            System.out.println(currentCommitHash);
        }
    }

    public void globalLog(){
        checkInitializedGitletDirectory();
        commitStore.getAllCommitHashes()
                .forEach(System.out::println);
    }

    public void status (){
        checkInitializedGitletDirectory();
        String currentBranch = getCurrentBranch().getName();

        List<Branch>Branches = branchStore.getBranches();

        System.out.println("=== Branches ===");
        for (Branch branch : Branches) {
            String fileName = branch.getName();
            if (fileName.equals(currentBranch)) {
                System.out.print('*');
            }
            System.out.println(fileName);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        List<String>AdditionFiles = stagingArea.GetAllFilesForAddition();
        for (String name : AdditionFiles) {
            System.out.println(name);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        List<String>RemovalFiles = stagingArea.GetAllFilesForRemoval();
        for (String name : RemovalFiles) {
            System.out.println(name);
        }
        System.out.println();

    }

    public void find (String message){
        checkInitializedGitletDirectory();
        List<Commit> Commits = commitStore.getCommitByMessage(message);
        if (Commits.size()==0){
            exitWithMessage("No commits found with this message");
        }
        for (Commit commit : Commits) {
            System.out.println(commit.getHash());
        }
    }

    public void branch(String branchName) {
        checkInitializedGitletDirectory();
        if (branchStore.getBranch(branchName) != null) {
            exitWithMessage("A branch with that name already exists");
        }
        Branch branch = new Branch(branchName, getCurrentBranch().getCommitHash());
        branchStore.saveBranch(branch);
    }

    public void rmBranch(String branchName) {
        checkInitializedGitletDirectory();
        if (getCurrentBranch().getName().equals(branchName)) {
            exitWithMessage("Cannot remove the current branch.");
        }
        Branch branch = branchStore.getBranch(branchName);
        if (branch == null) {
            exitWithMessage("A branch with that name does not exist.");
        }
        branchStore.removeBranch(branch);
    }



    /*Utils */
    private void checkInitializedGitletDirectory() {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }
    }

    public Branch getCurrentBranch(){
        return branchStore.getBranch(head.getHead());
    }

    private Commit getCurrentCommit(){
        String hash = getCurrentBranch().getCommitHash();
        return commitStore.getCommitByHash(hash) ;
    }
}
