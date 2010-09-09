package net.neoremind.sshxcute.junit;

import junit.framework.TestCase;
import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.IOptionName;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;

/**
 * 
 * Test case number: FVT_CMD_002
 * 
 * Objective: Launch multi-tasks with HALT_ON_FAILURE = TRUE
 *  
 * Procedure: 1) Connect to Linux server
 * 			  2) Exec command "pwd", "ls -al", "echo $HOME"
 *   
 * Expected Results:
 *            2) Return correct response.
 * 
 * @author neo
 *
 */
public class FVT_CMD_004 extends TestCase{

	public void setUp() throws Exception {
		super.setUp();
	}

	public void testFVT_CMD_004()
	{
		SSHExec ssh = null;
		try {
			SSHExec.setOption(IOptionName.HALT_ON_FAILURE, false);
			ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin","u7i8o9p0");
			ssh = SSHExec.getInstance(cb);
			CustomTask ct1 = new ExecCommand("pwd");
			CustomTask ct2 = new ExecCommand("ABCD");
			CustomTask ct3 = new ExecCommand("echo $HOME");
			ssh.connect();
			Result r1 = ssh.exec(ct1);
			Result r2 = ssh.exec(ct2);
			Result r3 = ssh.exec(ct3);
			assertEquals(r1.rc, 0);
			System.out.println("Return code: " + r1.rc);
			System.out.println("sysout: " + r1.sysout);
			System.out.println("error msg: " + r1.error_msg);
			System.out.println("Return code: " + r2.rc);
			System.out.println("sysout: " + r2.sysout);
			System.out.println("error msg: " + r2.error_msg);
		} catch (TaskExecFailException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			ssh.disconnect();	
		}
		
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
