package ibisRunner;

public class SatinRunner {
    public static void main(String[] args) {
        String runFile = args[0];
        
        Run run = Run.loadRun(runFile);
        
        System.err.println(run);
    }
}
