package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {
    public static void main(String[] args) {
        Repository repo = new Repository();
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                repo.init();
                break;
            case "add":
                repo.add(args[1]);
                break;
            case "commit":
                repo.commit(args[1]);
                break;
            case "merge":

                break;
            case "branch":
                repo.branch(args[1]);
                break;
            case "rm":
                repo.rm(args[1]);
                break;
            case "rm-branch":
                repo.rmBranch(args[1]);
                break;
            case "checkout":

                break;
            case "log":
                repo.log();
                break;
            case "global-log" :
                repo.globalLog();
                break;
            case "find":
                repo.find(args[1]);
                break;
            case "status":
                repo.status();
                break;
        }
    }
}