package ibisRunner;

public class SubJob {
    private String clusterName;

    private int machineCount = 0;

    private int CPUsPerMachine = 0;

    private static int subJobNrCounter = 0;
    
    private static int subJobNr = 0;
    
    public SubJob(String clusterName, int machineCount, int usPerMachine) {
        this.clusterName = clusterName;
        this.machineCount = machineCount;
        CPUsPerMachine = usPerMachine;
        subJobNr = ++subJobNrCounter;
    }

    public String getClusterName() {
        return clusterName;
    }

    public int getCPUsPerMachine() {
        return CPUsPerMachine;
    }

    public int getMachineCount() {
        return machineCount;
    }

    public String toString() {
        return "SubJob " + subJobNr + ": " + clusterName + " " + machineCount + " machines, with "
                + CPUsPerMachine + " CPUs/machine, for a total of "
                + (machineCount * CPUsPerMachine) + " CPUs";
    }
}
