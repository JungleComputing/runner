package ibisRunner;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.Preferences;
import org.gridlab.gat.URI;
import org.gridlab.gat.io.File;
import org.gridlab.gat.monitoring.Metric;
import org.gridlab.gat.monitoring.MetricDefinition;
import org.gridlab.gat.monitoring.MetricListener;
import org.gridlab.gat.monitoring.MetricValue;
import org.gridlab.gat.resources.HardwareResourceDescription;
import org.gridlab.gat.resources.JobDescription;
import org.gridlab.gat.resources.ResourceBroker;
import org.gridlab.gat.resources.ResourceDescription;
import org.gridlab.gat.resources.SoftwareDescription;

public class SatinRunner implements MetricListener {
    String gatLocation;

    String ibisHome;

    String ibisAppsHome;

    public static void main(String[] args) {
        new SatinRunner().start(args[0]);
    }

    public void start(String runFile) {
        gatLocation = System.getenv("GAT_LOCATION");
        if (gatLocation == null) {
            System.err.println("please set your GAT_LOCATION");
            System.exit(1);
        }
        System.err.println("using GAT at: " + gatLocation);

        ibisHome = System.getenv("IBIS_HOME");
        if (ibisHome == null) {
            System.err.println("please set your IBIS_HOME");
            System.exit(1);
        }
        System.err.println("using Ibis at: " + ibisHome);

        ibisAppsHome = System.getenv("IBIS_APPS_HOME");
        if (ibisAppsHome == null) {
            System.err.println("please set your IBIS_APPS_HOME");
            System.exit(1);
        }
        System.err.println("using Ibis applications at: " + ibisAppsHome);

        Run run = Run.loadRun(runFile);

        System.err.println(run);

        GATContext context = new GATContext();

        ArrayList<Job> requested = run.getRequestedResources();

        for (int i = 0; i < requested.size(); i++) {
            try {
                submitJob(run, context, requested.get(i));
            } catch (Exception e) {
                System.err.println("Job submission to " + requested.get(i)
                        + " failed: " + e);
                e.printStackTrace();
                GAT.end();
                System.exit(1);
            }
        }
    }

    public void submitJob(Run run, GATContext context, Job job)
            throws GATInvocationException, GATObjectCreationException,
            URISyntaxException {
        org.gridlab.gat.resources.Job[] jobs =
                new org.gridlab.gat.resources.Job[job.numberOfSubJobs()];
        for (int i = 0; i < job.numberOfSubJobs(); i++) {
            jobs[i] = submitSubJob(run, context, job, job.get(i));
        }

        System.err.println("job " + job.getJobNr() + " submitted, waiting for subjobs");

        synchronized (this) {
            for (int i = 0; i < job.numberOfSubJobs(); i++) {
                // wait until job is done
                while ((jobs[i].getState() != org.gridlab.gat.resources.Job.STOPPED)
                        && (jobs[i].getState() != org.gridlab.gat.resources.Job.SUBMISSION_ERROR)) {
                    try {
                        wait();
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        }
    }

    public org.gridlab.gat.resources.Job submitSubJob(Run run,
            GATContext context, Job job, SubJob subJob) throws GATInvocationException,
            GATObjectCreationException, URISyntaxException {

        System.err.println("submit of job " + job.getJobNr() + " subJob " + subJob.getSubJobNr());

        Application app = run.getApp();
        Grid grid = run.getGrid();
        Cluster cluster = grid.getCluster(subJob.getClusterName());

        Preferences prefs = new Preferences();
        File outFile =
                GAT.createFile(context, prefs, new URI("any:///"
                        + app.getFriendlyName() + "." + subJob.getClusterName()
                        + "." + job.getJobNr()
                        + "." + subJob.getSubJobNr()
                        + ".stdout"));
        File errFile =
                GAT.createFile(context, prefs, new URI("any:///"
                        + app.getFriendlyName() + "." + subJob.getClusterName()
                        + "." + job.getJobNr()
                        + "." + subJob.getSubJobNr()
                        + ".stderr"));

        File ibisLib =
                GAT.createFile(context, prefs, new URI(ibisHome + "/lib"));

        File applicationJar =
                GAT.createFile(context, prefs, new URI(ibisAppsHome + "/satin/"
                        + app.getFriendlyName() + "/" + app.getFriendlyName()
                        + ".jar"));

        SoftwareDescription sd = new SoftwareDescription();
        sd.setLocation(new URI(app.getExecutable()));
        sd.setStdout(outFile);
        sd.setStderr(errFile);
        sd.addPreStagedFile(ibisLib);
        sd.addPreStagedFile(applicationJar);
        sd.setArguments(app.getArguments());

        int machineCount = subJob.getMachineCount();
        if (machineCount == 0)
            machineCount = cluster.getMachineCount();
        int CPUsPerMachine = subJob.getCPUsPerMachine();
        if (CPUsPerMachine == 0)
            CPUsPerMachine = cluster.getCPUsPerMachine();
        sd.addAttribute("count", machineCount * CPUsPerMachine);
        sd.addAttribute("hostCount", machineCount);
        sd.addAttribute("java.home", new URI(cluster.getJavaHome()));

        String classpath = app.getFriendlyName() + ".jar:.";
        java.io.File tmp = new java.io.File(ibisHome + "/lib");
        String[] jars = tmp.list();
        for (int i = 0; i < jars.length; i++) {
            classpath += ":lib/" + jars[i];
        }
        sd.addAttribute("java.classpath", classpath);
        sd.addAttribute("java.flags", "-Xmx800M");

        HashMap<String, String> environment = new HashMap<String, String>();
        environment.put("ibis.server.address", "fs0.das3.cs.vu.nl");
        environment.put("ibis.pool.name", "satinRunner.job." + job.getJobNr());
        environment.put("ibis.pool.size", "" + job.getTotalCPUCount());
        environment.put("ibis.location", subJob.getClusterName());
        environment.put("satin.closed", "true");
        sd.setEnvironment(environment);

        prefs.put("ResourceBroker.adaptor.name", cluster.getAccessType());
        Hashtable<String, String> hardwareAttributes =
                new Hashtable<String, String>();
        hardwareAttributes.put("machine.node", cluster.getHostname());

        ResourceDescription rd =
                new HardwareResourceDescription(hardwareAttributes);

        JobDescription jd = new JobDescription(sd, rd);

        //       System.err.println("constructed job description: " + jd);

        ResourceBroker broker = GAT.createResourceBroker(context, prefs);

        org.gridlab.gat.resources.Job j = broker.submitJob(jd);
        MetricDefinition md = j.getMetricDefinitionByName("job.status");
        Metric m = md.createMetric(null);
        j.addMetricListener(this, m);

        return j;
    }

    public synchronized void processMetricEvent(MetricValue val) {
        String state = (String) val.getValue();

        System.err.println("Job status changed to : " + state);
        notifyAll();
    }
}
