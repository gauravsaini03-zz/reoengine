package uRen;

public class PerfTimer {
	
	private long startTimeMilliSec;
	private long endTimeMilliSec;

	public PerfTimer() {
		startTimeMilliSec = 0;
		endTimeMilliSec = 0;
	}
	
	public void Start() {
		startTimeMilliSec = System.currentTimeMillis();
	}
	
	public void Stop() {
		endTimeMilliSec = System.currentTimeMillis();
	}
	
	public long GetExecTimeInMilliSeconds() {
		return endTimeMilliSec - startTimeMilliSec;
	}
	
}
