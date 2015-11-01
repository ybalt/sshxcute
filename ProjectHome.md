# sshxcute Guideline #

## 1. Overview ##
As its name indicates, SSHXCUTE is a framework. It was designed to let engineers to use Java call to execute command/script on remote Linux/UNIX system through SSH connection way, which make software testing or system deployment easier and specifically to make it easier to automate software testing and system environment deployment.

SSHXCUTE was designed with the following points in mind:
  * Minimum machine requirements – Only use SSH protocol to connect.
  * Easily useable - Engineers use Java code to execute command/script.
  * Build-in executing command/script task type
  * Easily extendable - This means that it should be easy to create other task type to plug into sshxcute.

## 2. Limitation and scope ##
### 2.1 Limitation ###
  * Remote system should open SSH connection with credential enabled.
  * You can only plug sshxcute into Java based project.
  * JDK version newer or equal to 5.0
### 2.2 Scope ###
**Scenario 1.** If you have a batch of commands/scripts that are to be run on remote system (maybe deploying development or production system environment), and you think developing a script to invoke every command/script is too complex. And you have one Java IDE (like Eclipse) on your windows/Linux, why not try to execute through your client side?

**Scenario 2.** Your automation tool is implemented by Java, and you have requirements to run some configuration commands/scripts on remote Linux/UNIX system, sshxcute is just the ideal tool to help you achieve your goal! Just import the jar and you can invoke sshxcute API in your project.

## 3. How to use ##
First, you must import sshxcute.jar into your $CLASSPATH, so that you can use it. The section below indicates the build path settings for a Java project in Eclipse IDE. You can reach this through the project properties (Project > Properties > Java Build Path). More detail please search online.

![http://neoremind.net/wp-content/uploads/2010/09/howtoimport.jpg](http://neoremind.net/wp-content/uploads/2010/09/howtoimport.jpg)

### 3.1 Preparation ###
Usually when we want to run commands or scripts on remote Linux/UNIX system, the common steps are:
  * 1) Open SSH client tool (e.g. Putty console).
  * 2) Enter ip address.
  * 3) Enter username and password to login.
  * 4) When prompted login successful, enter command to execute.
  * 5) Log out.

The first three steps can be stimulated and finished by sshxcute Java API.

```
// Initialize a ConnBean object, parameter list is ip, username, password
ConnBean cb = new ConnBean("ip ", "username","password");
// Put the ConnBean instance as parameter for SSHExec static method getInstance(ConnBean) to retrieve a singleton SSHExec instance
ssh = SSHExec.getInstance(cb);		
// Connect to server
ssh.connect();
```

The 4th step is the core jobs that we want to do – executing commands/scripts. Please see below section for more information.

The 5th step is used to disconnect from server:
```
ssh.disconnect();
```

### 3.2 Execute command on remote system ###
Let’s jump into sshxcute Java API code directly, then later we will explain that. Because it is so obvious that if you have OO programming experience, you will fell it is so easy.
```
CustomTask sampleTask = new ExecCommand("echo 123");
ssh.exec(sampleTask);
```

ExecCommand class extends CustomTask class, we create ExecCommand object that has a CustomTask class type reference. Below picture shows the class diagram for ExecCommand, ExecShellScript and CustomTask.

![http://neoremind.net/wp-content/uploads/2010/09/sshxcute_classdiagram.jpg](http://neoremind.net/wp-content/uploads/2010/09/sshxcute_classdiagram.jpg)

The only parameter for ExecCommand constructor is the command string. Note to execute multiple commands, you can separate them by delimiter “,”. For example:
```
CustomTask sampleTask = new ExecCommand("echo 123", "echo 456,"echo 789");
```

ExecCommand constructor is
  * `public ExecCommand(String...args)`

Put the ExecCommand instance as argument into SSHExec.exec(CustomTask) method, then it begins to run.

### 3.3 Execute shell script on remote system ###
It is almost the same way as 3.2 Execute command on remote system section. For example, if we want to execute sshxcute\_test.sh on remote system at /home/tsadmin with two arguments “hello world”, we should invoke sshxcute Java API like below:
```
CustomTask ct1 = new ExecShellScript("/home/tsadmin","./sshxcute_test.sh","hello world");
ssh.exec(ct1);
```

ExecShellScript constructor is
  * `public ExecShellScript(String workingDir, String shellPath, String args)`

  * `public ExecShellScript(String shellPath, String args)`

  * `public ExecShellScript(String shellPath)`

### 3.4 Upload files to remote system ###
Here comes one problem, what if the shell script saved at our local machine and we want to execute it on remote system, of course, we should first upload that script to remote system. That can be done by sshxcute Java API as well. For example, we want to upload all files under c:/data2/data on local machine to /home/tsadmin on remote system, we can
```
ssh.uploadAllDataToServer("c:/data2/data", "/home/tsadmin");
```

Or if we want to upload single file on local machine to /home/tsadmin on remote system, we can
```
ssh.uploadSingleDataToServer("data/sshxcute_test.sh","/home/tsadmin");
```

Note that we should put upload work before execution and after connection. For example,
```
CustomTask ct1 = new ExecShellScript("/home/tsadmin","./sshxcute_test.sh","hello world");
ssh.connect();  // After connection
ssh.uploadSingleDataToServer("data/sshxcute_test.sh", "/home/tsadmin");
ssh.exec(ct1);  // Before execution
```

Uploading does not limit to help executing shell scripts, you can use uploading function based on the simple requirement – just upload files to remote sytem.

### 3.5 Result handle ###
All task including ExecCommand and ExecShellScript or even what we will discuss later about customized task, when executing them, a result handle can be returned. The handle is a Result object with return code, system printout, error message printout. What’s more, you can get a Boolean variable – isSuccess to indicate whether tasks run successfully or not.

In [section 4.1](http://code.google.com/p/sshxcute/#4.1_Sysout_keywords_to_determine_whether_task_is_successful_or_n), we will see more on how SSHXCUTE determine a task’s status (OK or fail), that is configurable too.

For example, you can get a Result object that returned from a SSHExec.exec(CustomTask) method. And you can use logical algorithm to print out message information.
```
Result res = ssh.exec(task);
if (res.isSuccess)
{
    System.out.println("Return code: " + res.rc);
    System.out.println("sysout: " + res.sysout);
}
else
{
    System.out.println("Return code: " + res.rc);
    System.out.println("error message: " + res.error_msg);
}
```

### 3.6 Whole story ###
Assume we want to run a shell script a Linux server (e.g. ip is 9.125.71.115). About sshxcute\_test.sh, please refer to [Appendix A](http://code.google.com/p/sshxcute/#Appendix_A.).

Below is the Java code to finish that job.
```
// Initialize a SSHExec instance without referring any object. 
SSHExec ssh = null;
// Wrap the whole execution jobs into try-catch block	
try {
    // Initialize a ConnBean object, parameter list is ip, username, password
    ConnBean cb = new ConnBean("9.125.71.115", "username","password");
    // Put the ConnBean instance as parameter for SSHExec static method getInstance(ConnBean) to retrieve a real SSHExec instance
    ssh = SSHExec.getInstance(cb);		
    // Create a ExecCommand, the reference class must be CustomTask
    CustomTask ct1 = new ExecCommand("chmod 755 /home/tsadmin/sshxcute_test.sh");
    // Create a ExecShellScript, the reference class must be CustomTask
    CustomTask ct2 = new ExecShellScript("/home/tsadmin","./sshxcute_test.sh","hello world");
    // Connect to server
    ssh.connect();
    // Upload sshxcute_test.sh to /home/tsadmin on remote system
    ssh.uploadSingleDataToServer("data/sshxcute_test.sh", "/home/tsadmin");
    // Execute task
    ssh.exec(ct1);
    // Execute task and get the returned Result object
    Result res = ssh.exec(ct2);
    // Check result and print out messages.
    if (res.isSuccess)
    {
        System.out.println("Return code: " + res.rc);
	System.out.println("sysout: " + res.sysout);
    }
    else
    {
	System.out.println("Return code: " + res.rc);
        System.out.println("error message: " + res.error_msg);
    }
} 
catch (TaskExecFailException e) 
{
    System.out.println(e.getMessage());
    e.printStackTrace();
} 
catch (Exception e) 
{
    System.out.println(e.getMessage());
    e.printStackTrace();
} 
finally 
{
    ssh.disconnect();	
}
```

**System out message:**
```
SSHExec initializing ...
Session initialized and associated with user credential tsadmin123
SSHExec initialized successfully
SSHExec trying to connect username@9.125.71.115
SSH connection established
Ready to transfer local file 'data/sshxcute_test.sh' to server directory '/home/tsadmin'
Connection channel established succesfully
Start to upload
Upload success
channel disconnect
Command is chmod 755 /home/tsadmin/sshxcute_test.sh
Connection channel established succesfully
Start to run command
Connection channel closed
Check if exec success or not ... 
Execute successfully for command: chmod 755 /home/tsadmin/sshxcute_test.sh
Now wait 5 seconds to begin next task ...
Connection channel disconnect
Command is cd /home/tsadmin ; ./sshxcute_test.sh hello world 
Connection channel established succesfully
Start to run command
Login success

Connection channel closed
Check if exec success or not ... 
Execute successfully for command: cd /home/tsadmin ; ./sshxcute_test.sh hello world 
Now wait 5 seconds to begin next task ...
Connection channel disconnect
R1 Return code: 0
R1 sysout: Login success

SSH connection shutdown
```


### 3.7 Logging ###

All the output and error message can be found at the path you execute SSHXCUTE. That log file name is sshxcute.log. If you import sshxcute.jar into your project, it will be stored at the project root directory.

## 4. Configuration ##
There are some configurable parameters that users can specify according to their needs. The API is like the following format:

  * `SSHExec.setOption(String optionName, String/int/long value);`

The clause should put at the very beginning, so that when executing tasks, these configurations will be applied.

The below section will describe how to configure all the parameters.


### 4.1 Sysout keywords to determine whether task is successful or not ###
Let’s take a look back to [section3.5](http://code.google.com/p/sshxcute/#3.5_Result_handle), whenever SSHExec.exec(CustomTask) is invoked, it will return a Result object containing all the result information. There is one Boolean variable – isSuccess to indicate whether a task’s execution status is successful or not. Here comes the question, how SSHXCUTE determine that? It depends on what?

By default, there are two conditions to determine a task’s status. SSHXCUTE checks command or script return code, if return code is 0 and system out message does not contain the following keywords then task execute successfully.
  * "Usage"
  * "usage"
  * "not found"
  * "fail"
  * "Fail"
  * "error"
  * "Error"
  * "exception"
  * "Exception"
  * "not a valid"

You can not change the behavior to check return code, it must be 0 if task execute successfully, and that is one common sense.

You can change the checking filter keywords to determine the task’s status by the following code. Assume you want task to be fail if sysout contains "error",”fail”,”exception” keywords.
```
String[] reset_keyword = { "error",”fail”,”exception” };
CustomTask ct1 = new ExecCommand("exit 0");
ct1.resetErrSysoutKeyword(reset_keyword);
```
Note that the array does not accept regular expression.


### 4.2 Halt task if failed ###

When you execute multiple tasks, sometimes not all the tasks will be successful. If you do not want to proceed if one of the tasks execute failed, you can switch off  HALT\_ON\_FAILURE by setting:
```
SSHExec.setOption(IOptionName.HALT_ON_FAILURE, false);
```

If you want to continue even if some of the tasks failed to execute, you can switch on HALT\_ON\_FAILURE by setting:
```
SSHExec.setOption(IOptionName.HALT_ON_FAILURE, true);
```

By default, HALT\_ON\_FAILURE is set to false.

For example, you want to execute “pwd” > “ABCD” >”echo $HOME”, the second command “ABCD” will fail and you want to continue execution just neglect the failure. You can switch off HALT\_ON\_FAILURE by setting or just keep the default value. See code below
```
SSHExec.setOption(IOptionName.HALT_ON_FAILURE, false);  
ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin","u7i8o9p0");
ssh = SSHExec.getInstance(cb);
CustomTask ct1 = new ExecCommand("pwd");
CustomTask ct2 = new ExecCommand("ABCD");
CustomTask ct3 = new ExecCommand("echo $HOME");
ssh.connect();
Result r1 = ssh.exec(ct1);
Result r2 = ssh.exec(ct2);
Result r3 = ssh.exec(ct3);  // Continue to execute
System.out.println("Return code: " + r1.rc);
System.out.println("sysout: " + r1.sysout);
System.out.println("error msg: " + r1.error_msg);
System.out.println("Return code: " + r2.rc);
System.out.println("sysout: " + r3.sysout);
System.out.println("error msg: " + r3.error_msg);
```


### 4.3 SSH port number ###

By default, SSH port number is 22. If the port number of remote system you want to use is not the default value but 18. You can set by the following way:
```
SSHExec.setOption(IOptionName.SSH_PORT_NUMBER, 18);
```

### 4.4 Error output temporally file store path ###

SSHXCUTE will use temp file to store error message from the output of commands or scripts. This is just for internal use, users will not bother to touch this file. By default, the temp file store path is $USERHOME/sshxcute\_err.msg.

e.g.
  * C:\Documents and Settings\Administrator\sshxcute\_err.msg for Windows
  * /home/user/sshxcute\_err.msg for Linux/UNIX.

**It is strongly recommend that user better not change the parameter.** If you want to change the path, see below code:
```
SSHExec.setOption(IOptionName.ERROR_MSG_BUFFER_TEMP_FILE_PATH, "c:\\123.err");
```

### 4.5 Interval time between tasks ###
When you execute multiple tasks, you can set interval time between tasks. That means when one task executes to the end, SSHXCUTE will wait some time to launch next task. That may be useful when previous task takes some time to execute and the below task must wait until the previous one finishes. Refer to below code:
```
SSHExec.setOption(IOptionName.INTEVAL_TIME_BETWEEN_TASKS, 5000l);
```

Note the parameter must be long type, not int. Do not forget the “l”.

### 4.6 Timeout ###
Every task will be execute within a time range, if it exceeds that time, program will be exit. The time rage is called TIMEOUT. You can specify timout parameter in this way:
```
SSHExec.setOption(IOptionName.TIMEOUT, 36000l);
```

Note the parameter must be long type, not int. Do not forget the “l”.


### 4.7 Print out all system configuration parameters’ value ###
If you want to print out all system configuration parameters, you can try the following code:
```
SSHExec.showEnvConfig();
```

Output:
```
******************************************************
The list below shows sshxcute configuration parameter
******************************************************
TIMEOUT => 36000
HALT_ON_FAILURE => true
INTEVAL_TIME_BETWEEN_TASKS => 5000
SSH_PORT_NUMBER => 22
ERROR_MSG_BUFFER_TEMP_FILE_PATH => c:\123.err
```


## 5. API Extension ##

This part is for developers who want to extend SSHXCUTE’s default task type. Developers can create their own customized task for the specific use of themselves.

All task class extends CustomTask class. The class contains the following abstract method:
```
       /**
	 * Check the sysout that returns from a command or script.
	 * 
	 * @param stdout
	 * @return If it executes successfully, returns true. Or else returns false.
	 */
	protected abstract Boolean checkStdOut(String stdout);
	
	/**
	 * Check the exit code that after running a command or script. 
	 * 
	 * @param exitCode 
	 * @return If it executes successfully, returns true. Or else returns false.
	 */
	protected abstract Boolean checkExitCode(int exitCode);

	/**
	 * Get the command 
	 * 
	 * @return command that used to finish the task
	 */
	public abstract String getCommand();
	
	/**
	 * Get task description
	 * 
	 * @return description of the task 
	 */
	public abstract String getInfo();
```

If you want to build up a new task type, just override these methods. Below is a real scenario illustrating the whole process to implement customized task.

**Requirement:**
Product A has its own shell script called: deployMetadata.sh. The team does not want to use ExecShellScript task class, and want to use a customized task to finish that job. The usage of deployMetadata.sh is
```
/opt/ProductA/bin/deployMetadata.sh –dba_user=system –dba_password=pw4dba
```

**Implementation:**
```
public class DeployMetadata extends CustomTask{

	protected String dba_user = "";
	
	protected String dba_password = "";
	
	public DeployMetadata(String dba_user, String dba_password){
		this.dba_user = dba_user;
		this.dba_password = dba_password;
	}
	
	
	public Boolean checkStdOut(String stdout){
		Iterator<String> iter = err_sysout_keyword_list.iterator();
		while(iter.hasNext()){
			if (stdout.contains(iter.next()))
			{
				return false;
			}
		}
		return true;
	}
	
	public Boolean checkExitCode(int exitCode){
		if (exitCode == 0)
			return true;
		else
			return false;
	}
	
	public String getCommand(){
		return "/opt/ProductA/bin/deployMetadata.sh" + " -dba_user=" + dba_user + " -dba_password=" + dba_password;
	}
	
	
	public String getInfo(){
		return "Deploy metadata ";
	}
}

```


**Usage:**
```
CustomTask task1 = new DeployMetadata ("system", “pw4dba”);
ssh.connect();
ssh.exec(task1);
```

## 6. Samples ##

### Sample 1 ###
```
                SSHExec ssh = null;
		try {
			ConnBean cb = new ConnBean("grnvm164.svl.ibm.com", "vmadmin","vmadmin123");
			ssh = SSHExec.getInstance(cb);		
			CustomTask echo = new ExecCommand("pwd");
			ssh.connect();
			Result res = ssh.exec(echo);
			if (res.isSuccess)
			{
				System.out.println("Return code: " + res.rc);
				System.out.println("sysout: " + res.sysout);
			}
			else
			{
				System.out.println("Return code: " + res.rc);
				System.out.println("error message: " + res.error_msg);
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
```

### Sample 2 ###
```
                SSHExec ssh = null;
		try {
			SSHExec.setOption(IOptionName.HALT_ON_FAILURE, true);
			SSHExec.setOption(IOptionName.SSH_PORT_NUMBER, 22);
			SSHExec.setOption(IOptionName.ERROR_MSG_BUFFER_TEMP_FILE_PATH, "c:\\123.err");
			SSHExec.setOption(IOptionName.INTEVAL_TIME_BETWEEN_TASKS, 100000l);
			SSHExec.setOption(IOptionName.TIMEOUT, 36000l);
			SSHExec.showEnvConfig();
			
			ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin","u7i8o9p0");
			ssh = SSHExec.getInstance(cb);		
			CustomTask task1 = new ExecCommand("echo 123");
			CustomTask task2 = new ExecCommand("abcd");
			CustomTask task3 = new ExecCommand("pwd");
			ssh.connect();
			ssh.exec(task1);
			ssh.exec(task2);
			System.out.println("This should not print!");
			ssh.exec(task3);
			System.out.println("Task3 does not execute");
		} catch (TaskExecFailException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			ssh.disconnect();	
		}
```

### Sample 3 ###
```
                SSHExec ssh = null;
		try {
			ConnBean cb = new ConnBean("grnvm164.svl.ibm.com", "tsadmin","tsadmin123");
			ssh = SSHExec.getInstance(cb);		
			CustomTask ct1 = new ExecCommand("chmod 755 /home/tsadmin/sshxcute_test.sh");
			CustomTask ct2 = new ExecShellScript("/home/tsadmin","./sshxcute_test.sh","hello world");
			ssh.connect();
			ssh.uploadSingleDataToServer("data/sshxcute_test.sh", "/home/tsadmin");
			ssh.exec(ct1);
			Result res = ssh.exec(ct2);
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
```

### Sample 4 ###
```
                String uploadFileDirPath = "/home/tsadmin/data";
		String RFIDIC_HOME = "/opt/ibm/InfoSphere/TraceabilityServer";
		
		SSHExec ssh = null;
		try {
			SSHExec.setOption(IOptionName.HALT_ON_FAILURE, false);
			ConnBean cb = new ConnBean("rfidic-1.svl.ibm.com", "tsadmin","u7i8o9p0");
			ssh = SSHExec.getInstance(cb);
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
		} catch (TaskExecFailException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			ssh.disconnect();	
		}
```


## Appendix A. ##
```
#!/bin/bash
if [ $# -ne 2 ];then
        echo "usage: sshxcute_test.sh username password"
        exit 1
fi
export USERNAME=$1
export PASSWORD=$2

if [ "$USERNAME" = "hello" -a "$PASSWORD" = "world" ];then
	echo "Login success"
	exit 0
fi
echo "Login falied"
exit 2
```