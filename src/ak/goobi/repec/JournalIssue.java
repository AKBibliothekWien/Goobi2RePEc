package ak.goobi.repec;

public class JournalIssue {

	private String journalTitle = new String();
	private String year = new String();
	private String volumeNo = new String();
	private String issueNo = new String();
	
	
	
	public JournalIssue(
			String journalTitle,
			String year,
			String volumeNo,
			String issueNo) {
		
		this.setJournalTitle(journalTitle);
		this.setYear(year);
		this.setVolumeNo(volumeNo);
		this.setIssueNo(issueNo);
	}
	
	public String getJournalTitle() {
		return journalTitle;
	}
	
	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}
	
	public String getYear() {
		return year;
	}
	
	public void setYear(String year) {
		this.year = year;
	}
	
	public String getVolumeNo() {
		return volumeNo;
	}
	
	public void setVolumeNo(String volumeNo) {
		this.volumeNo = volumeNo;
	}
	
	public String getIssueNo() {
		return issueNo;
	}
	
	public void setIssueNo(String issueNo) {
		this.issueNo = issueNo;
	}
	
}
