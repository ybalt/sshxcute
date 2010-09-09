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
public class FVT_ITS_003 extends TestCase{

	public static String RFIDIC_HOME = "";
	
	public static String uploadFileDirPath = "";
	
	public void setUp() throws Exception {
		uploadFileDirPath = "/home/tsadmin/data";
		RFIDIC_HOME = "/opt/ibm/InfoSphere/TraceabilityServer";
		super.setUp();
	}

	public void testFVT_ITS_003()
	{
		SSHExec ssh = null;
		try {
			SSHExec.setOption(IOptionName.HALT_ON_FAILURE, false);
			ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin","u7i8o9p0");
			ssh = SSHExec.getInstance(cb);
			//CustomTask ct2 = new ExecCommand("/home/tsadmin/WASAlertsMQRegister.sh");

			CustomTask chmod = new ExecCommand("chmod -R 755 /home/tsadmin/data");
			CustomTask removePedigreeDefFromRFIDICServerXml = new ExecShellScript(uploadFileDirPath + "/shellscript","./RemovePedigreeMetaFromRFIDICServer.sh",RFIDIC_HOME);
			CustomTask copyMetadata = new ExecCommand("cp -rf " + uploadFileDirPath + "/metadata/*.xml " + RFIDIC_HOME + "/etc");
			CustomTask copyLib = new ExecCommand("cp -f " + uploadFileDirPath + "/jar/*.jar " + RFIDIC_HOME + "/lib");
			CustomTask registerLibToWAS = new ExecCommand("/usr/IBM/WebSphere/AppServer/bin/wsadmin.sh -f " + uploadFileDirPath + "/jacl/WASLibraryAddClassPath.jacl -user wsadmin -password was4me");
			CustomTask deployMetadata = new ExecShellScript(RFIDIC_HOME + "/bin/deployMetadata.sh -dba_user=db2inst1 -dba_password=passw0rd -force_drop_tables -force_stop_was");
			CustomTask ImportMasterdata = new ExecShellScript(RFIDIC_HOME + "/bin/import-masterdata.sh " + uploadFileDirPath + "/masterdata/*.xml");
			CustomTask startRFIDIC = new ExecCommand(RFIDIC_HOME + "/bin/startRFIDIC.sh -components=was,capture");
			CustomTask importEvents = new ExecShellScript(RFIDIC_HOME + "/bin/submitEvent.sh -queue myeventq " + uploadFileDirPath + "/event/group1_events.xml");
			ssh.connect();
			ssh.uploadAllDataToServer("c:/data2/data", "/home/tsadmin");
			Result r_chmod = ssh.exec(chmod);
			System.out.println("Return code: " + r_chmod.rc);
			System.out.println("sysout: " + r_chmod.sysout);
			System.out.println("error msg: " + r_chmod.error_msg);
			
			Result r_removePedigreeDefFromRFIDICServerXml = ssh.exec(removePedigreeDefFromRFIDICServerXml);
			System.out.println("Return code: " + r_removePedigreeDefFromRFIDICServerXml.rc);
			System.out.println("sysout: " + r_removePedigreeDefFromRFIDICServerXml.sysout);
			System.out.println("error msg: " + r_removePedigreeDefFromRFIDICServerXml.error_msg);
			
			Result r_cp = ssh.exec(copyMetadata);	
			System.out.println("Return code: " + r_cp.rc);
			System.out.println("sysout: " + r_cp.sysout);
			System.out.println("error msg: " + r_cp.error_msg);
			
			ssh.exec(copyLib);
			Result r_registerLibToWAS = ssh.exec(registerLibToWAS);
			System.out.println("Return code: " + r_registerLibToWAS.rc);
			System.out.println("sysout: " + r_registerLibToWAS.sysout);
			System.out.println("error msg: " + r_registerLibToWAS.error_msg);
			
			Result r_deployMetadata = ssh.exec(deployMetadata);
			System.out.println("Return code: " + r_deployMetadata.rc);
			System.out.println("sysout: " + r_deployMetadata.sysout);
			System.out.println("error msg: " + r_deployMetadata.error_msg);
			
			Result r_ImportMasterdata = ssh.exec(ImportMasterdata);
			System.out.println("Return code: " + r_ImportMasterdata.rc);
			System.out.println("sysout: " + r_ImportMasterdata.sysout);
			System.out.println("error msg: " + r_ImportMasterdata.error_msg);
			
			Result r_startRFIDIC = ssh.exec(startRFIDIC);
			System.out.println("Return code: " + r_startRFIDIC.rc);
			System.out.println("sysout: " + r_startRFIDIC.sysout);
			System.out.println("error msg: " + r_startRFIDIC.error_msg);
			
			Result r_importEvents = ssh.exec(importEvents);
			System.out.println("Return code: " + r_importEvents.rc);
			System.out.println("sysout: " + r_importEvents.sysout);
			System.out.println("error msg: " + r_importEvents.error_msg);
//			assertEquals(r1.rc, 0);
//			assertTrue(r1.isSuccess);
//			System.out.println("Return code: " + r1.rc);
//			System.out.println("sysout: " + r1.sysout);
//			System.out.println("error msg: " + r1.error_msg);
//			Result r2 = ssh.exec(ct2);
//			System.out.println("Return code: " + r2.rc);
//			System.out.println("sysout: " + r2.sysout);
//			System.out.println("error msg: " + r2.error_msg);
//			assertEquals(r2.rc, 1);
//			assertFalse(r2.isSuccess);
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
