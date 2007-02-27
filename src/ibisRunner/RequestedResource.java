package ibisRunner;

public class RequestedResource {
        private String clusterName;
        private int machineCount = 0;
        private int CPUsPerMachine = 0;
        
        public RequestedResource(String clusterName, int machineCount, int CPUsPerMachine) {
            this.clusterName = clusterName;
            this.machineCount = machineCount;
            this.CPUsPerMachine = CPUsPerMachine;
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
        
}
