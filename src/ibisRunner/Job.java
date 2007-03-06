package ibisRunner;

import java.util.ArrayList;

public class Job {
    ArrayList<SubJob> subJobs = new ArrayList<SubJob>();

    public void addSubJob(SubJob j) {
        subJobs.add(j);
    }

    public int size() {
        return subJobs.size();
    }

    public SubJob get(int index) {
        return subJobs.get(index);
    }

    public String toString() {
        String res = "Job: ";
        for (int j = 0; j < subJobs.size(); j++) {
            SubJob subJob = subJobs.get(j);
            res += subJob + "\n";
        }

        return res;
    }
}
