package ibisRunner;

public class SubJob {
    private String clusterName;

    private int machineCount = 0;

    private int CPUsPerMachine = 0;

    public SubJob(String clusterName, int machineCount, int usPerMachine) {
        this.clusterName = clusterName;
        this.machineCount = machineCount;
        CPUsPerMachine = usPerMachine;
        System.err.println("new subjob: " + this );
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
        return "SubJob " + clusterName + " " + machineCount + " machines, with "
                + CPUsPerMachine + " CPUs/machine, for a total of "
                + (machineCount * CPUsPerMachine) + " CPUs";
    }
}
