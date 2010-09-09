package net.neoremind.sshxcute.task;

import java.util.LinkedList;
import java.util.List;


/**
 * 
 * This abstract class is the parent class of all custom tasks. In other words, if you want to 
 * develop your own task, the class should extend CustomCode class. <br>
 * <p>
 * Here, the task that extends CustomCode class can be whatever you want to execute on remote machine. It can be executing a shell script, executing an ordinary command, executing a specified script(like startRFIDIC.sh, deployMetadata.sh), or WAS jacl script. <br>
 * <p>
 * The sub class just need to override the following method:	<p>
 * 1) public abstract Boolean checkStdOut(String stdout);  // check the sysout that returns from a command or script. <p>
 * <p>
 * 2) public abstract Boolean checkExitCode(int stdout);   // check the exit code that after running a command or script. <p>
 * <p> 
 * 3) public abstract String getCommand();  // returns the real command you want to run <p>
 * <p>
 * 4) public abstract String getInfo();  // returns the description of the task <p>
 * <br>
 * 
 * The parent class has wrap some common method that can be used by sub class. <p>
 * 1) protected String cat(String... args)  // Use to concatenate strings with blank<p>
 * <p>
 * 2) protected String getITSHomeBin()  // return ITS home bin path<p>
 * <p>
 * 3) protected String getWASCredential()  // return WAS credential information: -was_admin_user=*** -was_admin_password=***<p>
 * <p>
 * @author zxucdl
 *
 */
public abstract class CustomTask {
	
	/**
	 * Command line delimiter 
	 */
	protected static String DELIMETER = ";";
	
	protected List err_sysout_keyword_list = new LinkedList();
	
	protected String[] err_sysout_keywords = {
			"Usage", 
			"usage",
			"not found",
			"fail",
			"Fail",
			"error",
			"Error",
			"exception",
			"Exception",
			"not a valid"
	};
	
	{
		resetErrSysoutKeyword(err_sysout_keywords);
	}

	
	public void resetErrSysoutKeyword (String[] str){
		err_sysout_keyword_list.clear();
		for (int i = 0; i < str.length; i++) 
		{
			err_sysout_keyword_list.add(str[i]);
		}
	}
	
	/**
	 * Check whether task executes successful or not.
	 *
	 * @param stdout 
	 * @param exitCode 
	 * @return If it executes successfully, returns true. Or else returns false.
	 */
	public Boolean isSuccess(String stdout, int exitCode){
		if (checkStdOut(stdout) && checkExitCode(exitCode))
			return true;
		else 
			return false;
	}
	
	/**
	 * Check the sysout that returns from a command or script.
	 * 
	 * @param stdout
	 * @return If it executes successfully, returns true. Or else returns false.
	 */
	protected abstract Boolean checkStdOut(String stdout);
	
	/**
	 * Check the exit code that after running a command or script. 
	 * 
	 * @param exitCode 
	 * @return If it executes successfully, returns true. Or else returns false.
	 */
	protected abstract Boolean checkExitCode(int exitCode);

	/**
	 * Get the command 
	 * 
	 * @return command that used to finish the task
	 */
	public abstract String getCommand();
	
	/**
	 * Get task description
	 * 
	 * @return description of the task 
	 */
	public abstract String getInfo();
	
	/**
	 * Use to concatenate strings with blank
	 * @param args - a list or string
	 * @return String that concatenate with blank
	 */
	protected String cat(String... args) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < args.length; i++)
		{
			sb.append(args[i]);
			sb.append(" ");
		}
		return sb.toString();
	}

}
