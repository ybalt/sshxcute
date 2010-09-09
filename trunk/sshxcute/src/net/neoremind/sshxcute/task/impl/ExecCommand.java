package net.neoremind.sshxcute.task.impl;


import java.util.Iterator;

import net.neoremind.sshxcute.task.CustomTask;

/**
 * The class extends from CustomCode class. <br>
 * <p>
 * This task is used to execute commands. You can wrap the command you want to execute into quote, and the constructor accepts multi-commands.<br>
 * Example:<p>
 * 1) CustomCode cc1 = new CommonCmd("ls");<p>
 * 2) CustomCode cc2 = new CommonCmd("CP * /opt/ibm","dir");<p> 			
 * 
 * @author zxucdl
 *
 */
public class ExecCommand extends CustomTask{

	protected String command = "";
	
	private ExecCommand(){
		
	}
	
	public ExecCommand(String...args){
		for(int i = 0; i < args.length; i++)
		{
			command = command + args[i] +DELIMETER;
		}
		command = (command.length()==0 ? "" : command.substring(0, command.length() - 1));
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
		return command;
	}
	
	public String getInfo(){
		return "Exec command " + getCommand();
	}
	
	
}


