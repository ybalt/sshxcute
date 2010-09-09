package net.neoremind.sshxcute.core;

import com.jcraft.jsch.UserInfo;

/**
 * This is an implementation of UserInfo interface that provided by Jsch. It is used to process password for SSH connection. 
 * 
 * @author zxucdl
 *
 */
public class ConnCredential implements UserInfo {
	
	String passwd ;
	
	public ConnCredential(String passwd){
		this.passwd = passwd;
	}
	
	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getPassword() {
		return passwd;
	}

	public boolean promptYesNo(String str) {
		return true;
	}



	public String getPassphrase() {
		return null;
	}

	public boolean promptPassphrase(String message) {
		return true;
	}

	public boolean promptPassword(String message) {

		return true;
	}

	public void showMessage(String message) {
		return;
	}
}

