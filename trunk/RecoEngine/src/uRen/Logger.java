package uRen;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Logger {
	public static void Log(String message) {
		System.out.println(message);
	}
	
	public static void LogEvalResults(String message) {
		String fileName = SessionSettings.EvalResultsFile;
		if ( fileName != "" && fileName.length() > 0) {
			try {
				BufferedWriter fout = new BufferedWriter(new FileWriter(fileName, true));
				fout.write(message + System.getProperty("line.separator"));
				fout.close();
			}
			catch (Exception ex) {
				//not mission critical... ignore it.
			}
		}
	}
}
