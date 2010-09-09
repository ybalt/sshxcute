package net.neoremind.sshxcute.exception;


/**
 * 
 * When a task executes fail, program should throw this exception. <br>
 * Example: <p>
 *	CustomCode deployMetadata = new DeployMetadata(getProp("DBA_USER"),getProp("DBA_PASSWORD"));<p>
 *	CustomCode ImportMasterdata = new ImportMasterdata(getProp("REMOTE_DATA_PATH") + "/"+ getProp("LOCAL_DATA_PATH") + "/masterdata");<p>
 *	ssh.connect();<p>
 *	ssh.uploadAllDataToServer(getProp("LOCAL_DATA_PATH"),getProp("REMOTE_DATA_PATH"));<p>
 *	ssh.execCmd(deployMetadata);<p>
 *  ssh.execCmd(ImportMasterdata);	<p>
 *  <p>
 *  <p>
 *  when deploy metadata steps fails, program should exit at "ssh.execCmd(deployMetadata);" and throw CustomActionExecFailException.
 * 
 * @author zxucdl
 *
 */
public class UploadFileNotSuccessException extends Exception{
	
	private String reason;
	
	public UploadFileNotSuccessException (String message) { 
		super(message); 
		this.reason = "Upload failed for " + message + " | Make sure file exists. And you should specify the right upload type. If you want to upload all files under one dir, pls use method - uploadAllDataToServer. If you want to upload one single file, pls use method - uploadSingleDataToServer" ;
	}

	public String getMessage() {
		return reason;
	}


}
