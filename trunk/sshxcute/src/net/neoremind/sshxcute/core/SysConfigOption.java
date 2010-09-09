package net.neoremind.sshxcute.core;

import java.io.File;

public class SysConfigOption {

	public static long TIMEOUT = 100000;
	
	public static boolean HALT_ON_FAILURE = false;
	
	public static long INTEVAL_TIME_BETWEEN_TASKS = 5000;
	
	public static int SSH_PORT_NUMBER = 22;
	
	public static String ERROR_MSG_BUFFER_TEMP_FILE_PATH = System.getProperty("user.home") + File.separator + "sshxcute_err.msg";
	

}
