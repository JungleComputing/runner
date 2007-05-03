package ibisRunner;

import java.util.ArrayList;

public class Job {
    private ArrayList<SubJob> subJobs = new ArrayList<SubJob>();
    private int jobNr;
    
    public Job(int jobNr) {
        this.jobNr = jobNr;
    }
    
    public void addSubJob(SubJob j) {
        subJobs.add(j);
    }

    public int getJobNr() {
        return jobNr;
    }
    
    public int numberOfSubJobs() {
        return subJobs.size();
    }

    public SubJob get(int index) {
        return subJobs.get(index);
    }

    public String toString() {
        String res = "Job " + jobNr + ": ";
        for (int j = 0; j < subJobs.size(); j++) {
            SubJob subJob = subJobs.get(j);
            res += subJob + "\n";
        }

        return res;
    }
}
