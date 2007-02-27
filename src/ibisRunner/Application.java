/*
 * Created on Mar 8, 2006 by rob
 */
package ibisRunner;

import java.util.ArrayList;

public class Application {
    private String command;

    private String[] parameters;

    private String friendlyName;

    public Application(String command, String[] parameters, String name) {
        this.command = command;
        this.parameters = parameters;
        friendlyName = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String toString() {
        return friendlyName;
    }

    public String[] getParameters() {
        return (String[]) parameters.clone();
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public static Application loadApplication(String filename) {
        Input in = new Input(filename);

        String name = in.readString();
        in.readln();

        String command = in.readString();
        in.readln();

        ArrayList params = new ArrayList();
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
        
        return new Application(command, parameters, name);
    }
}
