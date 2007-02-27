package ibisRunner;

import java.util.ArrayList;

public class Run {
    private Grid grid;
    private Application app;
    private ArrayList<RequestedResource> requestedResources = new ArrayList();
    
    public static Run loadRun(String filename) {
        Input in = new Input(filename);

        Run run = new Run();
        
        String gridFile = in.readString();
        in.readln();

        String appFile = in.readString();
        in.readln();
        
        while (!in.eof()) {
            int machineCount = 0;
            int CPUsPerMachine = 0;
            String cluster = in.readString();
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
        String res = app + "\n" + grid + "\n";
        
        res += "requested: ";
        for(int i=0; i<requestedResources.size(); i++) {
            RequestedResource r = requestedResources.get(i);
            res += "    " + r.getClusterName()
            + " " + r.getMachineCount() + " machines, with "
            + r.getCPUsPerMachine() + " CPUs/machine\n";
        }
        
        return res;
        
    }
}
