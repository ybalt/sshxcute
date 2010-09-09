package net.neoremind.sshxcute.junit;

import junit.framework.TestCase;
import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.IOptionName;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import net.neoremind.sshxcute.task.impl.ExecShellScript;

/**
 * 
 * Test case number: FVT_CMD_002
 * 
 * Objective: START RFIDIC
 *  
 * Procedure: 1) Connect to Linux server
 *   
 * Expected Results:
 *            2) Return correct response.
 * 
 * @author neo
 *
 */
public class FVT_SHELL_001 extends TestCase{

	public void setUp() throws Exception {
		super.setUp();
	}

	public void testFVT_SHELL_001()
	{
		SSHExec ssh = null;
		try {
			SSHExec.setOption(IOptionName.HALT_ON_FAILURE, false);
			ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin","u7i8o9p0");
			ssh = SSHExec.getInstance(cb);		
			CustomTask ct1 = new ExecCommand("chmod 755 /home/tsadmin/sshxcute_test.sh");
			CustomTask ct2 = new ExecShellScript("/home/tsadmin","./sshxcute_test.sh","hello world");
			ssh.connect();
			ssh.uploadSingleDataToServer("data/sshxcute_test.sh", "/home/tsadmin");
			ssh.exec(ct1);
			Result res = ssh.exec(ct2);
			assertEquals(res.rc, 0);
			assertTrue(res.isSuccess);
			if (res.isSuccess)
			{
				System.out.println("R1 Return code: " + res.rc);
				System.out.println("R1 sysout: " + res.sysout);
			}
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
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
}
