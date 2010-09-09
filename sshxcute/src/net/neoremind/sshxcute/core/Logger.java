package net.neoremind.sshxcute.core;

import java.io.*;
import java.util.*;

/**
 * This class provides a shared logger for an application that is distributed
 * between Java and Jython.<br>
 * The first time Logger.setInstance() is used determines where the Logger instance is created,
 * what it's name is, and where the log files are created.  After that, all other 
 * Java and Jython code can get the Logger instance and then call putMsg() to output 
 * to the same Log and Debug files.<br>
 * Subsequent setInstance() calls can change the Logger to a new name and location.<br>
 * Java methods should use the putMsg(level,msg) interface so that the caller can be found 
 * using Java reflection methods. Since Java cannot follow the call stack in and out of
 * the Jython application, the Jython methods must use the  putMsg(caller,level,msg) interface 
 * so that the call stack entry can be passed in from Jython.  The Jython class PyLogger helps 
 * do this. 
 * 
 * @author jbvoge@us.ibm.com 
 *
 */
public class Logger
{
   private  static Logger instance=null;       
   private  static String pathToFiles;
   private  static String instanceName;
   private  static String logFile;
   private  static String dbgFile;
   private  static Map<String,String> classNames;        // Keeps track of class knicknames
   private  static int    classCount;
   private  static int    logThreshold=Logger.INFO;       
   
   public static final int DIVIDER=0;
   public static final int DEEP_DBUG=1;
   public static final int MID_DBUG=2;
   public static final int LITE_DBUG=3;
   public static final int INFO=4;
   public static final int WARN=5;
   public static final int ERROR=6;
   public static final int FATAL=7;
   public static final int OFF=99;
   
    
   /**
    * Get the shared Logger instance.   
    * @return Logger instance, or null if one has not been set up
    */
   public static Logger getLogger() 
   {
	   try {
		   instance = new Logger(getCaller());
	   } catch (Exception e) {
		   return null;
	   }
	   instance.setThreshold(INFO);
	   return instance;
   }
   
//   /**
//    * Set the Logger instance to 'name' and set the path for log files.<br>
//    * If the Logger is currently open, it is closed and new log files are started.
//    * @param name
//    * @param path
//    * @return Logger instance
//    */
//   public static Logger setInstance( String name, String path, int threshold )
//   throws Exception
//   {
//	   return setInstance( getCaller(), name, path, threshold );
//   }
//   
//
//   /**
//    * Set the Logger instance to 'name' and set the path for log files.<br>
//    * If the Logger is currently open, it is closed and new log files are started.
//    * This method is generally for the PyLogger where the callstack must be passed in
//    * @param name
//    * @param path
//    * @return Logger instance
//    */
//   public static Logger setInstance( String caller, String name, String path, int threshold )
//   throws Exception
//   {
//	   instance = new Logger(caller,name,path);
//	   instance.setThreshold(threshold);
//	   return instance;
//   }

   
//   /**
//    * Set the logging threshold using the name of the level
//    * @param newlevel
//    */
//   public void setThreshold( String newlevel )
//   {
//	   int lvl = levelFmString(newlevel);
//       setThreshold( getCaller(), lvl );
//   }
   
   /**
    * Set the logging threshold using the enum value of the level
    * @param newlevel
    */
   public void setThreshold( int newThreshold ) 
   {
       setThreshold( getCaller(), newThreshold );
   }
   
//   /**
//    * Set the logging threshold using the name of the level
//    * This method is generally for the PyLogger class
//    * @param caller
//    * @param newlevel
//    */
//   public void setThreshold( String caller, String newThreshold ) 
//   {
//	   int lvl = levelFmString(newThreshold);
//       setThreshold( caller, lvl );
//   }
   
   /**
    * Set the logging threshold using the enum value of the level
    * This method is generally for the PyLogger class
    * @param caller
    * @param newThreshold
    */
   public void setThreshold( String caller, int newThreshold ) 
   { 
	   DateCalendar d = new DateCalendar();
	   //String outp = "\n--------------- ["+d.toTimeStamp()+", "+caller+"] --- LogThreshold changed from "+levelToString(logThreshold)+" to "+levelToString(newThreshold)+" ----------\n";
	   logThreshold = newThreshold; 
	   //printLog(outp,true);
	   //if (logThreshold < INFO  )
		   //printDbg(outp,true); 
   }
   
   /**
    * Get the path where the Logger files are being stored
    * @return logPath
    */
   public String getLogpath() { return pathToFiles; }
   
   /**
    * This optional method will terminate the *.dbg file with the shortName cross-reference list.<br>
    * To make the Logger debug files easier to read, each message line only contains the short Class
    * name, with the package name part replaced with #n.  Thus, each #n value is effectively a nickname
    * for the fully-qualified package and class name.  When the debug file is closed with this
    * method, a cross-reference listing is provided to give the full name for each #n nickname. 
    */
   public void close()
   {
	   if (logThreshold < INFO  )
	   {
		  printDbg("----Classes----------------------",true); 
		  Set<String> keys = classNames.keySet();
		  Iterator it=keys.iterator();
	      while(it.hasNext())
	      {
	    	  String k=(String)it.next();
              String v = classNames.get(k);
    		  printDbg(v+" = "+k,true); 
	      }
	   }
   }
   
   /**
    * Clears out the log and debug files
    */
   public void clearLogger( ) 
   {
       clearLogger( getCaller() );
   }
   
   /**
    * Clears out the log and debug files
    * This method is generally for the PyLogger class
    * @param caller
    */
   public void clearLogger( String caller )
   {
	  DateCalendar d = new DateCalendar();
 	  String outp = "\n--------------- ["+d.toTimeStamp()+", "+caller+"] --- CLEARED ----------\n";
      printLog(outp,false);
	  if (logThreshold < INFO  )
	      printDbg(outp,false);  
	  else
	  {
		  File f = new File(dbgFile);
		  f.delete();
	  }
   }
   
   /**
    * Put a message to the *.log or *.dbg files based on the LogLevel given
    * @param level
    * @param msg
    * @throws Exception
    */
   public void putMsg(int level, String msg) 
   {
	   if (level == FATAL)
	   {  
		  DateCalendar d = new DateCalendar();
	      String t = levelToString(level)+":       ";
	      String outp = "["+d.timeString()+"] "+t.substring(0,9)+msg;
	      printLog(outp,true);
		  if (logThreshold < INFO  )
			  printDbg(outp,true); 
	   }
	   else if (level >= logThreshold || level==DIVIDER)
	   {
	      putMsg(getCaller(),level,msg);
	   }
   }
   
   /**
    * Put a message to the *.log or *.dbg files based on the LogLevel given
    * Generally for the PyLogger class 
    * @param caller
    * @param level
    * @param msg
    * @throws Exception
    */
   public void putMsg(String caller, int level, String msg)
   {
	   if (level >= logThreshold || level == DIVIDER)
	   {
	      DateCalendar d = new DateCalendar();
	      if (level==DIVIDER)
	      {
	    	 String outp = "\n=============== ["+d.toTimeStamp()+", "+caller+"] ====================\n";
	         printLog(outp,true);
	         if (logThreshold < INFO)
	            printDbg(outp,true);         
	      }
	      else if (level<INFO)
	      {
	    	 caller = shortHandCaller(caller); 
	         String outp = "["+d.timeString()+" - "+caller+"] "+msg;
	         printDbg(outp,true);
	      }
	      else 
	      {
	         String t = levelToString(level)+":       ";
	         String outp = "["+d.timeString()+"] "+t.substring(0,9)+msg;
	         printLog(outp,true);
	         if (level != FATAL)
	         {
	            System.out.println(msg);
	            System.out.flush();
	         }
	      }     
	   }
   }
   
   
   
   /**
    * Scan the callstack to find the entry which called this method
    * @return callStackEntry
    */
   public static synchronized String getCaller()
   {
       String[] callStack = getCallStackAsStringArray();
       for (int i=0; i<callStack.length; i++)
       	if (callStack[i].contains("getCaller:") )  
              return callStack[i+2]; 
       return callStack[callStack.length-1];
   }
   
   
   /**
    * Return a Logger enum level from the level's name
    * @param level
    * @return int
    */
   public static int levelFmString( String level )
   {
	   int lvl;
	   if (level.equals("DEEP_DBUG"))      lvl=DEEP_DBUG;
	   else if (level.equals("MID_DBUG"))  lvl=MID_DBUG;
	   else if (level.equals("LITE_DBUG")) lvl=LITE_DBUG;
	   else if (level.equals("INFO"))      lvl=INFO;
	   else if (level.equals("WARN"))      lvl=WARN;
	   else if (level.equals("FATAL"))     lvl=FATAL;
	   else if (level.equals("OFF"))       lvl=OFF;
	   else  lvl=ERROR;
	   return lvl;
   }
   
   /**
    * Return a Logger enum logging level as a String
    * @param level
    * @return string
    */
   public static String levelToString( int level )
   {
       String result;
       switch (level)
       {  
          case DEEP_DBUG:   result="DEEP_DBUG";  break;
          case MID_DBUG:    result="MID_DBUG";  break;
          case LITE_DBUG:   result="LITE_DBUG";  break;
          case INFO:        result="INFO";  break;
          case WARN:        result="WARN";  break;
          case FATAL:       result="FATAL";  break;
          case OFF:         result="OFF";  break;
          case ERROR: 
          default:    result="ERROR:   ";  break;
       }
       return result;
   }
   
   
   
   //=====  P R I V A T E  =====================================================
   


   /**
    * Creates a new  Logger instance.  use getInstance() instead
    * The caller parm provided is recorded in the log file.  This is generally for the PyLogger class
    * @param name
    */
   private Logger(String caller) throws Exception
   {
	   if (instance!=null)
	   {
		   instance.close();	
		   instance=null;	   
	   }
	   classNames = new HashMap<String,String>();
	   classCount=0;
	   instanceName = "sshxcute"; 

       pathToFiles = System.getProperty("user.dir") ;

       logFile=pathToFiles +"/"+"sshxcute"+".log";
       dbgFile=pathToFiles +"/"+"sshxcute"+".dbg";
       this.putMsg(caller,DIVIDER,"");
   }

   
   /**
    * Strips the leading part of a classname from 'inp' and replaces it with
    * the shorthand name (ie: #1) that has been saved in the classNames hash.
    * If the shorthand name has not been seen yet, it is created and added to the hash 
    * This technique makes the *.dbg file easier to read
    */
   private String shortHandCaller( String inp )
   {	   
	   String caller;
	   String shortName;
	   String prefix;
	   int pos = inp.lastIndexOf("/");
	   if (pos==-1)
	   {
		   pos = inp.lastIndexOf(".");
		   prefix = inp.substring(0,pos);
		   pos = prefix.lastIndexOf(".");
	   } 
	   prefix = inp.substring(0,pos);	
	   shortName = classNames.get(prefix);
	   if (shortName == null)
	   {
		   classCount++;
		   shortName = "#"+classCount;
		   classNames.put(prefix,shortName);
	   }
	   caller = shortName+"/"+inp.substring(pos+1);
	   return caller;
   }
   
   
   
   /**
    * Low-level interface to write the *.log file
    * @param msg
    * @param append
    */
    private synchronized void printLog( String msg, boolean append )
    {
	   try {
           FileWriter w = new FileWriter(logFile,append);
           w.write(msg+"\n");
           w.flush();
           w.close();
           } catch (IOException e)
        {
           System.out.println("IOException in Logger, message: "+e.getMessage());
           System.out.println(msg);
        }  
    }
    
    /**
     * Low-level interface to write the *.dbg file
     * @param msg
     * @param append
     */
    private synchronized void printDbg(String msg, boolean append ) 
    {
	   try {          
           FileWriter w = new FileWriter(dbgFile,append);
           w.write(msg+"\n");
           w.flush();
           w.close();
        } catch (IOException e)
        {
           System.out.println("IOException on debug message: "+e.getMessage());
           System.out.println("DEBUG: "+msg);
        } 
    }

   /** 
    * low-level interface to get the callstack as an array of strings 
    * @return
    */
   private synchronized static String[] getCallStackAsStringArray() {
     ArrayList<String> list = new ArrayList<String>();
     String[] array = new String[1];
     StackTraceElement[] stackTraceElements = 
         Thread.currentThread().getStackTrace();
     for (int i = 0; i < stackTraceElements.length; i++) {
       StackTraceElement element = stackTraceElements[i];
       String classname = element.getClassName();
       String methodName = element.getMethodName();
       int lineNumber = element.getLineNumber();
       String entry = classname + "." + methodName + ":" + lineNumber;
       list.add(entry);
     }
     return list.toArray(array);
   }
   /**
    * This class extends GregorianCalendar and provides some useful methods.
    */
   private class DateCalendar extends GregorianCalendar
   {
      private static final long serialVersionUID = -98734585L;
      
      /**
       * Constructs a default DateCalendar using the current time in the default time zone with the default locale.
       *
       */
      public DateCalendar()
      {
         super();
      }

      /**
       * Gets the value for a given time field.
       * 
       * @param field the given time field.
       * @return the value for the given time field.
       */
      public int get(int field)
      {
         int result = (field == MONTH) ? (super.get(field) + 1) : (super.get(field));
         return result;
      }

      /**
       * Get a String representation of a GregorianCalendar (yyyy/mm/dd hh:mm:ss)
       * @return TimeString A string formatted as yyyy.mm.dd.hh.mm.ss
       */
      public String toString()
      {
         int year = this.get(DateCalendar.YEAR);
         int month = this.get(DateCalendar.MONTH);
         int day = this.get(DateCalendar.DAY_OF_MONTH);
         int hour24 = this.get(DateCalendar.HOUR_OF_DAY);
         int min = this.get(DateCalendar.MINUTE);
         int sec = this.get(DateCalendar.SECOND);

         String datetimeString = String.valueOf(year) + "." + String.valueOf(month) + "." + String.valueOf(day)
            + "." + String.valueOf(hour24) + "." + String.valueOf(min) + "." + String.valueOf(sec);

         return datetimeString;
      }

      public String timeString()
      {
         return toTimeStamp().substring(11);
      }
      
      
      /**
       * Get an ISO String representation of a GregorianCalendar
       * @return TimeString A string formatted as yyyy/mm/dd hh:mm:ss
       */
      public String toTimeStamp()
      {
         String year = String.valueOf(this.get(DateCalendar.YEAR));
         String month = String.valueOf(this.get(DateCalendar.MONTH));
         String day = String.valueOf(this.get(DateCalendar.DAY_OF_MONTH));
         String hour24 = String.valueOf(this.get(DateCalendar.HOUR_OF_DAY));
         String min = String.valueOf(this.get(DateCalendar.MINUTE));
         String sec = String.valueOf(this.get(DateCalendar.SECOND));

         if (month.length() == 1)
         {
            month = "0" + month;
         }
         if (day.length() == 1)
         {
            day = "0" + day;
         }
         if (hour24.length() == 1)
         {
            hour24 = "0" + hour24;
         }
         if (min.length() == 1)
         {
            min = "0" + min;
         }
         if (sec.length() == 1)
         {
            sec = "0" + sec;
         }
         String datetimeString = year + "/" + month + "/" + day + " " + hour24 + ":" + min + ":" + sec;

         return datetimeString;
      }
   }
}

