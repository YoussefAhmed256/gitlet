package gitlet;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static gitlet.Utils.*;

public class BranchStore {
    private final File BRANCH_DIR;

    public BranchStore(File branchDirectory) {
        this.BRANCH_DIR = branchDirectory;
    }

    public Branch getBranch(String branchName) {
        File branchFile = join(BRANCH_DIR , branchName);
        if (branchFile.exists()) {
            return readObject(branchFile , Branch.class);
        }
        return null;
    }

    public void saveBranch(Branch branch) {
        File branchFile = join(BRANCH_DIR , branch.getName());
        writeObject(branchFile , branch);
    }

    public void removeBranch(Branch branch) {
        File branchFile = join(BRANCH_DIR , branch.getName());
        if (branchFile.exists()) {
            branchFile.delete();
        }
    }
    public boolean branchExists(String branchName) {
        return (getBranch(branchName) != null);
    }

    public List<Branch> getBranches() {
        return Objects.requireNonNull(plainFilenamesIn(BRANCH_DIR)).stream()
                .map(this::getBranch)
                .collect(Collectors.toList());
    }
}
