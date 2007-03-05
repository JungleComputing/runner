/*
 * Created on Mar 8, 2006 by rob
 */
package ibisRunner;

import java.util.ArrayList;

public class Application {
    private String executable;

    private String[] parameters;

    private String friendlyName;

    public Application(String command, String[] parameters, String name) {
        this.executable = command;
        this.parameters = parameters;
        friendlyName = name;
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

    public String[] getParameters() {
        return (String[]) parameters.clone();
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public static Application loadApplication(String filename) {
        System.err.print("loading application: " + filename);
        Input in = new Input(filename);

        String name = in.readString();
        in.readln();

        String command = in.readString();
        in.readln();

        ArrayList<String> params = new ArrayList<String>();
        while (!in.eof()) {
            String p = in.readWord();
            in.skipWhiteSpace();
            
            params.add(p);
            in.readln();
        }
        
        String[] parameters = new String[params.size()];
        for(int i=0; i<parameters.length; i++) {
            parameters[i] = (String) params.get(i);
        }
        
        System.err.println(" DONE");
        return new Application(command, parameters, name);
        
    }
    
    public String toString() {
        String res = "Application " + friendlyName + "\n";
        res += "   executable: " + executable + "\n";
        res += "   parameters: ";
        
        for(int i=0; i<parameters.length; i++) {
            res += "        " + parameters[i] + "\n";
        }
        
        return res;
    }
}