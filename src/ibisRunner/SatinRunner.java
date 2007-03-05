package ibisRunner;

import java.net.URISyntaxException;
import java.util.ArrayList;
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
import org.gridlab.gat.resources.Job;
import org.gridlab.gat.resources.JobDescription;
import org.gridlab.gat.resources.ResourceBroker;
import org.gridlab.gat.resources.ResourceDescription;
import org.gridlab.gat.resources.SoftwareDescription;

public class SatinRunner implements MetricListener {
    public static void main(String[] args) {
        new SatinRunner().start(args[0]);
    }

    public void start(String runFile) {
        String gatLocation = System.getenv("GAT_LOCATION");
        if (gatLocation == null) {
            System.err.println("please set your GAT_LOCATION");
            System.exit(1);
        }
        System.err.println("using GAT at: " + gatLocation);

        String ibisHome = System.getenv("IBIS_HOME");
        if (ibisHome == null) {
            System.err.println("please set your IBIS_HOME");
            System.exit(1);
        }
        System.err.println("using Ibis at: " + ibisHome);

        String ibisAppsHome = System.getenv("IBIS_APPS_HOME");
        if (ibisAppsHome == null) {
            System.err.println("please set your IBIS_APPS_HOME");
            System.exit(1);
        }
        System.err.println("using Ibis applications at: " + ibisAppsHome);

        Run run = Run.loadRun(runFile);

        System.err.println(run);

        GATContext context = new GATContext();

        ArrayList<RequestedResource> requested = run.getRequestedResources();
        Job[] jobs = new Job[requested.size()];
        for (int i = 0; i < requested.size(); i++) {
            try {
                jobs[i] = submitJob(run, ibisHome, ibisAppsHome, context,
                    requested.get(i));
            } catch (Exception e) {
                System.err.println("Job submission to " + requested.get(i)
                    + " failed: " + e);
                e.printStackTrace();
                GAT.end();
                System.exit(1);
            }
        }

        for (int i = 0; i < requested.size(); i++) {
            // wait until jobs are done
            synchronized (this) {
                while ((jobs[i].getState() != Job.STOPPED)
                    && (jobs[i].getState() != Job.SUBMISSION_ERROR)) {
                    try {
                        wait();
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        }
    }

    public Job submitJob(Run run, String ibisHome, String ibisAppsHome,
        GATContext context, RequestedResource req)
        throws GATInvocationException, GATObjectCreationException,
        URISyntaxException {
        Application app = run.getApp();
        Grid grid = run.getGrid();
        Cluster cluster = grid.getCluster(req.getClusterName());

        Preferences prefs = new Preferences();
        File outFile = GAT.createFile(context, prefs, new URI("any:///"
            + app.getFriendlyName() + "." + req.getClusterName() + ".stdout"));
        File errFile = GAT.createFile(context, prefs, new URI("any:///"
            + app.getFriendlyName() + "." + req.getClusterName() + ".stderr"));

        File ibisLib = GAT.createFile(context, prefs,
            new URI(ibisHome + "/lib"));

        File applicationJar = GAT.createFile(context, prefs, new URI(
            ibisAppsHome + "/satin/" + app.getFriendlyName() + "/"
                + app.getFriendlyName() + ".jar"));

        SoftwareDescription sd = new SoftwareDescription();
        sd.setLocation(new URI(app.getExecutable()));
        sd.setStdout(outFile);
        sd.setStderr(errFile);
        sd.addPreStagedFile(ibisLib);
        sd.addPreStagedFile(applicationJar);
        sd.addAttribute("count", req.getMachineCount()
            * req.getCPUsPerMachine());
        sd.addAttribute("hostCount", req.getMachineCount());
        sd.addAttribute("java.home", new URI(cluster.getJavaHome()));
     
        String classpath = app.getFriendlyName() + ".jar:.";
        java.io.File tmp = new java.io.File(ibisHome + "/lib");
        String[] jars = tmp.list();
        for(int i=0; i<jars.length; i++) {
            classpath += ":" + jars[i];
        }
        sd.addAttribute("java.classpath", classpath);
        
        prefs.put("ResourceBroker.adaptor.name", cluster.getAccessType());
        Hashtable<String, String> hardwareAttributes = new Hashtable<String, String>();
        hardwareAttributes.put("machine.node", cluster.getHostname());

        ResourceDescription rd = new HardwareResourceDescription(
            hardwareAttributes);

        JobDescription jd = new JobDescription(sd, rd);

        System.err.println("constructed job description: " + jd);

        ResourceBroker broker = GAT.createResourceBroker(context, prefs);

        Job job = broker.submitJob(jd);
        MetricDefinition md = job.getMetricDefinitionByName("job.status");
        Metric m = md.createMetric(null);
        job.addMetricListener(this, m);

        return job;
    }

    public void processMetricEvent(MetricValue val) {
        String state = (String) val.getValue();

        System.err.println("SubmitJobCallback: Processing metric: "
            + val.getMetric() + ", value is " + state);

        if (state.equals("STOPPED") || state.equals("SUBMISSION_ERROR")) {
            notifyAll();
        }
    }
}
