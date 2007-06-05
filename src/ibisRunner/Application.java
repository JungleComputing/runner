/*
 * Created on Mar 8, 2006 by rob
 */
package ibisRunner;

import java.util.ArrayList;

public class Application {
    private String executable;

    private String realAppName;
    
    private String[] arguments;

    private String friendlyName;

    private String[] preStaged;
    
    public Application(String command, String[] parameters, String name, String realAppName, String[] preStaged) {
        this.executable = command;
        this.arguments = parameters;
        friendlyName = name;
        this.realAppName = realAppName;
        this.preStaged = preStaged;
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String exe) {
        this.executable = exe;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String[] getArguments() {
        return (String[]) arguments.clone();
    }

    public void setArguments(String[] parameters) {
        this.arguments = parameters;
    }

    public static Application loadApplication(String filename) {
        System.err.print("loading application: " + filename);
        Input in = new Input(filename);

        String name = in.readString();
        in.readln();

        String jarFile = in.readString();
        in.readln();

        String command = in.readString();
        in.readln();

        ArrayList<String> params = new ArrayList<String>();
        while (!in.eoln()) {
            String p = in.readWord();
            in.skipWhiteSpace();
            params.add(p);
        }
        in.readln();
        
        String[] parameters = new String[params.size()];
        for(int i=0; i<parameters.length; i++) {
            parameters[i] = (String) params.get(i);
        }
        
        ArrayList<String> pre = new ArrayList<String>();
        while (!in.eoln()) {
            String p = in.readWord();
            in.skipWhiteSpace();
            pre.add(p);
        }
        
        String[] preStaged = new String[pre.size()];
        for(int i=0; i<preStaged.length; i++) {
            preStaged[i] = (String) pre.get(i);
        }
        in.readln();
        
        System.err.println(" DONE");
        return new Application(command, parameters, name, jarFile, preStaged);
        
    }
    
    public String toString() {
        String res = "Application " + friendlyName + "\n";
        res += "   executable: " + executable + "\n";
        res += "   realAppName: " + realAppName + "\n";
        res += "   parameters:";
        
        for(int i=0; i<arguments.length; i++) {
            res += " " + arguments[i];
        }
        
        return res;
    }

    /**
     * @return the real name
     */
    public String getRealAppName() {
        return realAppName;
    }

    /**
     * @param realAppName the jarFile to set
     */
    public void setRealAppName(String realAppName) {
        this.realAppName = realAppName;
    }

    public String[] getPreStaged() {
        return preStaged;
    }

    public void setPreStaged(String[] preStaged) {
        this.preStaged = preStaged;
    }
}
