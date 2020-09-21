package me.zfork.spartanoscraft.spartanosreport.utils;

import java.util.HashMap;

public class PlayerReports {

	private String p;
	private HashMap<String, Integer> reports;
	private int reportstotal;
	private String lastreporter;
	private String date;
	private long remove;

	public PlayerReports(String p, String report, String lastreporter, String date){
		this.p = p;
		this.reports = new HashMap<>();
		if(report != null) reports.put(report, 1);
		this.reportstotal = 1;
		this.lastreporter = lastreporter;
		this.date = date;
		this.remove = 0;
	}

	public String getPlayer() {
		return p;
	}
	
	public HashMap<String, Integer> getReports() {
		return reports;
	}
	
	public int getReportsTotal() {
		return reportstotal;
	}
	
	public void setReportsTotal(int reportstotal) {
		this.reportstotal = reportstotal;
	}
	
	public String getLastReporter() {
		return lastreporter;
	}
	
	public void setLastReporter(String lastreporter) {
		this.lastreporter = lastreporter;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public long getRemove() {
		return remove;
	}
	
	public void setRemove(long remove) {
		this.remove = remove;
	}

}
