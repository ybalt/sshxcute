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
 * Test case number: FVT_CMD_001
 * 
 * Objective: Test basic function for sshxcute
 *  
 * Procedure: 1) Connect to Linux server
 * 			  2) Exec command "pwd"
 *   
 * Expected Results:
 *            2) Return user home path and return code is 0, no error message.
 * 
 * @author neo
 *
 */
public class FVT_CMD_001 extends TestCase {
	
	public void setUp() throws Exception {
		super.setUp();
	}

	public void testFVT_CMD_001() throws Exception
	{
		SSHExec ssh = null;
		try {
			ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin","u7i8o9p0");
			ssh = SSHExec.getInstance(cb);
			CustomTask pwd = new ExecCommand("pwd");
			ssh.connect();
			Result r1 = ssh.exec(pwd);
			System.out.println("Return code: " + r1.rc);
			System.out.println("sysout: " + r1.sysout);
			System.out.println("error msg: " + r1.error_msg);
			boolean _sysout_ = r1.sysout.contains("/home");
			assertEquals(r1.rc, 0);
			assertTrue(_sysout_);
			assertEquals(r1.error_msg, "");
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
