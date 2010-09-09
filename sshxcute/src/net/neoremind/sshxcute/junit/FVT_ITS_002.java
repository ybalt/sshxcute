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
public class FVT_ITS_002 extends TestCase{

	public void setUp() throws Exception {
		super.setUp();
	}

	public void testFVT_ITS_002()
	{
		SSHExec ssh = null;
		try {
			SSHExec.setOption(IOptionName.HALT_ON_FAILURE, false);
			ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin","u7i8o9p0");
			ssh = SSHExec.getInstance(cb);
			//CustomTask ct2 = new ExecCommand("/home/tsadmin/WASAlertsMQRegister.sh");
			CustomTask ct2 = new ExecShellScript("/home/tsadmin","./WASAlertsMQRegister.sh","");
			CustomTask ct1 = new ExecCommand("chmod 755 /home/tsadmin/WASAlertsMQRegister.sh");
			ssh.connect();
			ssh.uploadSingleDataToServer("d:/data2/data/shellscript/WASAlertsMQRegister.sh", "/home/tsadmin");
			//ssh.uploadAllDataToServer("C:\\data2\\data", "/home/tsadmin");	
			Result r1 = ssh.exec(ct1);
			assertEquals(r1.rc, 0);
			assertTrue(r1.isSuccess);
			System.out.println("Return code: " + r1.rc);
			System.out.println("sysout: " + r1.sysout);
			System.out.println("error msg: " + r1.error_msg);
			Result r2 = ssh.exec(ct2);
			System.out.println("Return code: " + r2.rc);
			System.out.println("sysout: " + r2.sysout);
			System.out.println("error msg: " + r2.error_msg);
			assertEquals(r2.rc, 1);
			assertFalse(r2.isSuccess);
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
