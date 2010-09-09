package net.neoremind.sshxcute.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class SSHExecUtil {

	public static String getErrorMsg(String filename) {
		StringBuilder sb = new StringBuilder("");
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {				// Process the data, here we just print it out
				sb.append(line);
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}	
		}
		return sb.toString();
	}
	
	/**
	 * Check file for upload
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}
	
	/**
	 * Get files under a directory
	 * 
	 * @param dir
	 *            - Path of directory
	 * @return String[] - files under the dir directory
	 */
	protected static String[] getFiles(File dir) {
		return dir.list();
	}
	
	
}
