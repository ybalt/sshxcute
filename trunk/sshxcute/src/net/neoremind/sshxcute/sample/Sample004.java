package net.neoremind.sshxcute.sample;

import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.IOptionName;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import net.neoremind.sshxcute.task.impl.ExecShellScript;

public class Sample004 {

	public static void main(String[] args) {
		SSHExec ssh = null;
		try {
			SSHExec.setOption(IOptionName.HALT_ON_FAILURE, true);
			SSHExec.setOption(IOptionName.SSH_PORT_NUMBER, 22);
			SSHExec.setOption(IOptionName.ERROR_MSG_BUFFER_TEMP_FILE_PATH, "c:\\123.err");
			SSHExec.setOption(IOptionName.INTEVAL_TIME_BETWEEN_TASKS, 100000l);
			SSHExec.setOption(IOptionName.TIMEOUT, 36000l);
			SSHExec.showEnvConfig();
			
			ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin","u7i8o9p0");
			ssh = SSHExec.getInstance(cb);		
			CustomTask task1 = new ExecCommand("echo 123");
			CustomTask task2 = new ExecCommand("abcd");
			CustomTask task3 = new ExecCommand("pwd");
			ssh.connect();
			ssh.exec(task1);
			ssh.exec(task2);
			System.out.println("This should not print!");
			ssh.exec(task3);
			System.out.println("Task3 does not execute");
		} catch (TaskExecFailException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			ssh.disconnect();	
		}
	}

}
