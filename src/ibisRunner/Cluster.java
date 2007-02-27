/*
 * Created on Mar 6, 2006
 */
package ibisRunner;


public class Cluster {
    private String friendlyName;
    private String hostname;

    private String accessType;

    private int machineCount;
    
    private int CPUsPerMachine;
    
    /**
     * @param accessType the resource manager to use
     * @param hostname the hostname to contact
     */
    public Cluster(String friendlyName, String hostname, String accessType,
            int machineCount, int CPUsPerMachine) {
        this.friendlyName = friendlyName;
        this.accessType = accessType;
        this.hostname = hostname;
        this.machineCount = machineCount;
        this.CPUsPerMachine = CPUsPerMachine;
    }

    public int getCPUsPerMachine() {
        return CPUsPerMachine;
    }

    public int getMachineCount() {
        return machineCount;
    }

    public String getAccessType() {
        return accessType;
    }

    public String getHostname() {
        return hostname;
    }

    public String toString() {
        return hostname + " with "
            + accessType;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
