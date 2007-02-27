package ibisRunner;

public class Run {
    private Grid grid;
    private Application app;
    
    
    public static Run loadRun(String filename) {
        Input in = new Input(filename);

        Run run = new Run();
        
        String gridFile = in.readString();
        in.readln();

        String appFile = in.readString();
        in.readln();
        
        run.grid = Grid.loadGrid(gridFile);
        run.app = Application.loadApplication(appFile);
        
        return run;
    }
}
