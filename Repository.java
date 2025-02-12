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

        commitTree.addCommit(getCurrentCommit().getHash() , newcommit.getHash());

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

    public void checkoutFile(String fileName , String commitHash){
        checkInitializedGitletDirectory();
        Commit commit = commitStore.getCommitByHash(commitHash);
        if(commit == null){
            exitWithMessage("No commit with that id exists.");
        }
        if(!commit.containsFile(fileName)){
            exitWithMessage("File does not exist in the last commit");
        }
        String blobHash = commit.getTrackedFiles().get(fileName);
        File blob = blobStore.GetBlob(blobHash);
        String blobContent = readContentsAsString(blob);
        workingArea.SaveFile(blobContent , fileName);
    }
    public void checkoutFile(String fileName){
        checkInitializedGitletDirectory();
        checkoutFile(fileName , getCurrentCommit().getHash());
    }

    public void checkoutCommit(String commitHash) {
        checkInitializedGitletDirectory();
        Commit targetCommit = commitStore.getCommitByHash(commitHash);
        if (targetCommit == null) {
            exitWithMessage("No commit with that id exists.");
        }
        Commit currentCommit = getCurrentCommit();
        List<String> workingFileNames = workingArea.getAllFileNames();
        for (String fileName : workingFileNames) {
            if (!currentCommit.containsFile(fileName) && targetCommit.containsFile(fileName)) {
                exitWithMessage("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        workingArea.clear();
        stagingArea.clear();
        for (String fileName : targetCommit.getTrackedFiles().keySet()) {
            checkoutFile(fileName , targetCommit.getHash());
        }
    }

    public void checkoutBranch(String branchName) {
        checkInitializedGitletDirectory();
        Branch targetBranch = branchStore.getBranch(branchName);
        if (targetBranch == null) {
            exitWithMessage("No such branch exists.");
        }
        if(branchName.equals(getCurrentBranch().getName())){
            exitWithMessage("No need to checkout the current branch.");
        }
        if(targetBranch.getCommitHash() == null){
            exitWithMessage("error: branch points to a non-existent commit.");
        }
        checkoutCommit(targetBranch.getCommitHash());
        setCurrentBranch(targetBranch);
    }

    public void merge(String branchName){
        checkInitializedGitletDirectory();
        if(!stagingArea.IsEmpty()){
            exitWithMessage("You have uncommitted changes.");
        }
        if(branchStore.getBranch(branchName) == null){
            exitWithMessage("A branch with that name does not exist.");
        }
        if(branchName.equals(getCurrentBranch().getName())){
            exitWithMessage("Cannot merge a branch with itself.");
        }
        Branch headBranch = getCurrentBranch();
        Branch otherBranch = branchStore.getBranch(branchName);
        Commit splitPoint = findSplitPoint(headBranch , otherBranch);

        if(splitPoint == null){
            exitWithMessage("Internal error : Could not find split point.");
        }
        if(splitPoint.getHash().equals(otherBranch.getCommitHash())){
            exitWithMessage("Given branch is an ancestor of the current branch.");
        }

        if(splitPoint.getHash().equals(headBranch.getCommitHash())){
            checkoutBranch(otherBranch.getName());
            exitWithMessage("Current branch fast-forwarded.");
        }
        Commit headCommit = commitStore.getCommitByHash(headBranch.getCommitHash());
        Commit otherCommit = commitStore.getCommitByHash(otherBranch.getCommitHash());

        boolean isConflict = false;
        HashSet<String> processedFiles = new HashSet<>();
        for(String fileName : headCommit.getTrackedFiles().keySet()){
            processedFiles.add(fileName);
            String split = splitPoint.getTrackedFiles().get(fileName);
            String head = headCommit.getTrackedFiles().get(fileName);
            String other = otherCommit.getTrackedFiles().get(fileName);

            if(split == null && other == null){
                workingArea.SaveFile(readContentsAsString(blobStore.GetBlob(head)) , fileName);
            }
            if(Objects.equals(head,split) && !Objects.equals(other,split)){
                stagingArea.StageFileForAddition(readContentsAsString(blobStore.GetBlob(split)) , fileName);
                workingArea.SaveFile(readContentsAsString(blobStore.GetBlob(split)) , fileName);
            }

            if(!Objects.equals(head , split) && Objects.equals(other,split)){
                workingArea.SaveFile(readContentsAsString(blobStore.GetBlob(head)) , fileName);
            }

            if(!Objects.equals(head , split) && !Objects.equals(other,split) && Objects.equals(head , other)){
                workingArea.SaveFile(readContentsAsString(blobStore.GetBlob(head)) , fileName);
            }

            if(Objects.equals(head , split) && Objects.equals(other,split)){
                workingArea.SaveFile(readContentsAsString(blobStore.GetBlob(head)) , fileName);
            }

            if(other == null && Objects.equals(head,split)){
                stagingArea.StageFileForRemoval(workingArea.GetFile(fileName));
                workingArea.DeleteFile(fileName);
            }

            if(
                    split == null && !head.equals(other) ||
                    !Objects.equals(head,split) && !Objects.equals(other,split)
            ){
                String currentCommitFileContent = readContentsAsString(blobStore.GetBlob(headCommit.getTrackedFiles().get(fileName)));
                String givenCommitFileContent = readContentsAsString(blobStore.GetBlob(otherCommit.getTrackedFiles().get(fileName)));
                String concatContent = currentCommitFileContent + "\n=======\n"+(givenCommitFileContent);
                workingArea.SaveFile(concatContent, fileName);
                stagingArea.StageFileForAddition(workingArea.GetFile(fileName));
                isConflict = true;
            }
            if(other == null && !head.equals(split)){
                workingArea.SaveFile(readContentsAsString(blobStore.GetBlob(head)) , fileName);
            }
        }
        // Process Files that are not exists in map of current commit
        for(String fileName : otherCommit.getTrackedFiles().keySet()){
            if(processedFiles.contains(fileName)){
                continue;
            }
            if(
                    !splitPoint.containsFile(fileName) ||
                    !otherCommit.getTrackedFiles().get(fileName).equals(splitPoint.getTrackedFiles().get(fileName))
            ){
                workingArea.SaveFile(readContentsAsString(blobStore.GetBlob(otherCommit.getTrackedFiles().get(fileName))) , fileName);
                stagingArea.StageFileForAddition(readContentsAsString(blobStore.GetBlob(otherCommit.getTrackedFiles().get(fileName))) , fileName);
            }
        }
        String commitMessage = String.format(
                "Merged %s into %s.",
                otherBranch.getName(),
                headBranch.getName());
        commit(commitMessage, otherCommit.getHash());
        if (isConflict){
            System.out.println("Encountered a merge conflict.");
        }
    }

    /*Utils */
    private Commit findSplitPoint(Branch currentBranch , Branch givenBranch){
        HashSet<String> visitedHashes = new HashSet<>();
        String currentHash = currentBranch.getCommitHash();
        while (currentHash != null) {
            visitedHashes.add(currentHash);
            currentHash = commitTree.getParent(currentHash);
        }
        String givenHash = givenBranch.getCommitHash();
        while (givenHash != null) {
            if(visitedHashes.contains(givenHash)){
                return commitStore.getCommitByHash(givenHash);
            }
            givenHash = commitTree.getParent(givenHash);
        }
        return null;
    }


    private void checkInitializedGitletDirectory() {
        if (!GITLET_DIR.exists()) {
            exitWithMessage("Not in an initialized Gitlet directory.");
        }
    }

    private void setCurrentBranch(Branch branch) {
        head.setHead(branch);
    }

    private Branch getCurrentBranch(){
        return branchStore.getBranch(head.getHead());
    }

    private Commit getCurrentCommit(){
        String hash = getCurrentBranch().getCommitHash();
        return commitStore.getCommitByHash(hash) ;
    }
}
