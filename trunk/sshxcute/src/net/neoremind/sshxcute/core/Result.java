package net.neoremind.sshxcute.core;

public class Result {
	
	public int rc;
	
	public String sysout;
	
	public String error_msg;
	
	public boolean isSuccess;
	
	void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

}
