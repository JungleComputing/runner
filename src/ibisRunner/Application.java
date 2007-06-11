/*
 * Created on Mar 8, 2006 by rob
 */
package ibisRunner;

import java.util.ArrayList;

public class Application {
    private String executable;

    private String[] javaFlags;
    
    private String[] arguments;

    private String friendlyName;

    private String[] preStaged;
    private String[] postStaged;
    
    public Application(String command, String[] javaFlags, String[] parameters, String name, String[] preStaged, String[] postStaged) {
        this.executable = command;
        this.javaFlags = javaFlags;
        this.arguments = parameters;
        friendlyName = name;
        this.preStaged = preStaged;
        this.postStaged = postStaged;
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

    private static String[] readStringArray(Input in) {
        ArrayList<String> res = new ArrayList<String>();
        while (!in.eoln()) {
            String p = in.readWord();
            in.skipWhiteSpace();
            res.add(p);
        }
        in.readln();
        
        String[] result = new String[res.size()];
        for(int i=0; i<result.length; i++) {
            result[i] = (String) res.get(i);
        }

        return result;
    }
    
    public static Application loadApplication(String filename) {
        System.err.print("loading application: " + filename);
        Input in = new Input(filename);

        String name = in.readString();
        in.readln();

        String command = in.readString();
        in.readln();

        String[] javaFlags = readStringArray(in); 
        String[] parameters = readStringArray(in); 
        String[] preStaged = readStringArray(in);
        String[] postStaged = readStringArray(in);
        
        System.err.println(" DONE");
        return new Application(command, javaFlags, parameters, name, preStaged, postStaged);
        
    }
    
    public String toString() {
        String res = "Application " + friendlyName + "\n";
        res += "   executable: " + executable + "\n";
        res += "   parameters:";
        for(int i=0; i<arguments.length; i++) {
            res += " " + arguments[i];
        }
        res += "\n";
        
        res += "   java flags:";
        for(int i=0; i<javaFlags.length; i++) {
            res += " " + javaFlags[i];
        }
        res += "\n";
        
        res += "   pre staged:";
        for(int i=0; i<preStaged.length; i++) {
            res += " " + preStaged[i];
        }
        res += "\n";
        
        res += "   post staged:";
        for(int i=0; i<postStaged.length; i++) {
            res += " " + postStaged[i];
        }
        res += "\n";
        
        return res;
    }

    public String[] getPreStaged() {
        return preStaged;
    }

    public void setPreStaged(String[] preStaged) {
        this.preStaged = preStaged;
    }

    public String[] getPostStaged() {
        return postStaged;
    }

    public void setPostStaged(String[] postStaged) {
        this.postStaged = postStaged;
    }

    public String[] getJavaFlags() {
        return javaFlags;
    }

    public String getJavaFlagsAsString() {
        String res = "";
        for(int i=0; i<javaFlags.length; i++) {
            res += javaFlags[i];
            if(i != javaFlags.length-1) {
                res += " ";
            }
        }
        return res;
    }

    public void setJavaFlags(String[] javaFlags) {
        this.javaFlags = javaFlags;
    }
}
