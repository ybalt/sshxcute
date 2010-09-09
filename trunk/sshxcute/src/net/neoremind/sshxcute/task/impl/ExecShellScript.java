package net.neoremind.sshxcute.task.impl;


import java.util.Iterator;

import net.neoremind.sshxcute.task.CustomTask;

/**
 * The class extends from CustomCode class. <br>
 * <p>
 * This task is used to execute shell script. <br>
 * Example:<p>
 * CustomCode setWASSecurityOff = new ExecShellScript("/home/tsadmin/test.sh", "args1 args2");			
 * 
 * @author zxucdl
 *
 */
public class ExecShellScript extends CustomTask{

	protected String workingDir = "";
	
	protected String shellPath = "";
	
	protected String args = "";
	
	private ExecShellScript(){
		
	}
	
	public ExecShellScript(String workingDir, String shellPath, String args){
		this.workingDir = workingDir;
		this.shellPath = shellPath;
		this.args = args;
	}
	
	public ExecShellScript(String shellPath, String args){
		this.workingDir = "";
		this.shellPath = shellPath;
		this.args = args;
	}
	
	public ExecShellScript(String shellPath){
		this.workingDir = "";
		this.shellPath = shellPath;
		this.args = "";
	}
	
	public Boolean checkStdOut(String stdout){
		Iterator<String> iter = err_sysout_keyword_list.iterator();
		while(iter.hasNext()){
			if (stdout.contains(iter.next()))
			{
				return false;
			}
		}
		return true;
	}
	
	public Boolean checkExitCode(int exitCode){
		if (exitCode == 0)
			return true;
		else
			return false;
	}
	
	public String getCommand(){
		if (!workingDir.equals(""))
			return cat("cd",workingDir,DELIMETER,shellPath, getArgs());
		else 
			return cat(shellPath, getArgs());
	}
	
	protected String getArgs(){
		return args;
	}
	
	public String getInfo(){
		return "Exec shell script " + getCommand();
	}
}

