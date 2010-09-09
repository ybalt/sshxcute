package net.neoremind.sshxcute.junit;

import com.jcraft.jsch.Logger;

import junit.framework.TestCase;
import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;

/**
 * 
 * Test case number: FVT_CMD_002
 * 
 * Objective: [Negative] Test basic function for sshxcute
 *  
 * Procedure: 1) Connect to Linux server
 * 			  2) Exec command "abcd"
 *   
 * Expected Results:
 *            2) Return user home path and return code is 0, no error message.
 * 
 * @author neo
 *
 */
public class FVT_CMD_002 extends TestCase {
	
	public void setUp() throws Exception {
		super.setUp();
	}

	public void testFVT_CMD_002()
	{
		SSHExec ssh = null;
		try {
			ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin","u7i8o9p0");
			ssh = SSHExec.getInstance(cb);
			CustomTask ls = new ExecCommand("abcd");
			ssh.connect();
			Result r = ssh.exec(ls);
			System.out.println("Return code: " + r.rc);
			System.out.println("sysout: " + r.sysout);
			System.out.println("error msg: " + r.error_msg);
			String _err_msg_ = r.error_msg.split("sh: ")[1];
			assertEquals(r.rc, 127);
			assertEquals(r.sysout, "");
			assertEquals(_err_msg_, "abcd:  not found.");
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
