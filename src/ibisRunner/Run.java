package ibisRunner;

import java.util.ArrayList;

public class Run {
    private Grid grid;
    private Application app;
    private ArrayList<RequestedResource> requestedResources = new ArrayList<RequestedResource>();
    
    public static Run loadRun(String filename) {
        System.err.print("loading run: " + filename);
        Input in = new Input(filename);

        Run run = new Run();
        
        String gridFile = in.readWord();
        in.readln();

        String appFile = in.readWord();
        in.readln();

        while (!in.eof()) {
            int machineCount = 0;
            int CPUsPerMachine = 0;
            String cluster = in.readWord();
            in.skipWhiteSpace();

            if(!in.eoln()) {
                machineCount = in.readInt();
                in.skipWhiteSpace();
            }

            if(!in.eoln()) {
                CPUsPerMachine = in.readInt();
            }

            in.readln();
            
            RequestedResource res = new RequestedResource(cluster, machineCount, CPUsPerMachine);
            run.requestedResources.add(res);
        }

        System.err.println(" DONE");
        
        run.grid = Grid.loadGrid(gridFile);
        run.app = Application.loadApplication(appFile);
        
        return run;
    }

    public Application getApp() {
        return app;
    }

    public Grid getGrid() {
        return grid;
    }

    public ArrayList<RequestedResource> getRequestedResources() {
        return requestedResources;
    }
    
    public String toString() {
        String res = "Run: " + app + "\n" + grid + "\n";
        
        res += "requested: ";
        for(int i=0; i<requestedResources.size(); i++) {
            RequestedResource r = requestedResources.get(i);
            res += "    " + r.getClusterName()
            + " " + r.getMachineCount() + " machines, with "
            + r.getCPUsPerMachine() + " CPUs/machine, for a total of " + (r.getMachineCount() * r.getCPUsPerMachine()) + "CPUs\n";
        }
        
        return res;
    }
}
