package ak.goobi.repec;

import java.util.ArrayList;
import java.util.List;

public class JournalArticle {

	private static final String TEMPLATE_TYPE = "ReDIF-Article 1.0";
	private String templateType = new String();
	private String journalTitle = new String();
	private List<String> authorNames = new ArrayList<String>();
	private String articleTitle = new String();
	private String articleAbstract = new String();
	private String year = new String();
	private String volumeNo = new String();
	private String issueNo = new String();
	private String firstPage = new String();
	private String lastPage = new String();
	private String pages = new String();
	private List<String> imageNos = new ArrayList<String>();
	private String fileURL = new String();
	private String fileFormat = new String();
	private String handle = new String();
	private String keywords = new String();
	private String pi = new String();
	
	public JournalArticle (
			String journalTitle,
			List<String> authorNames,
			String articleTitle,
			String articleAbstract,
			String year,
			String volumeNo,
			String issueNo,
			String firstPage,
			String lastPage,
			String pages,
			List<String> imageNos,
			String fileURL,
			String fileFormat,
			String handle,
			String keywords,
			String pi) {
		
		this.setJournalTitle(journalTitle);
		this.setTemplateType(JournalArticle.TEMPLATE_TYPE);
		this.setAuthorNames(authorNames);
		this.setArticleTitle(articleTitle);
		this.setArticleAbstract(articleAbstract);
		this.setYear(year);
		this.setVolumeNo(volumeNo);
		this.setIssueNo(issueNo);
		this.setFirstPage(firstPage);
		this.setLastPage(lastPage);
		this.setPages(pages);
		this.setImageNos(imageNos);
		this.setFileURL(fileURL);
		this.setFileFormat(fileFormat);
		this.setHandle(handle);
		this.setKeywords(keywords);
		this.setPi(pi);
	}
	
	
	
	public List<String> getAuthorNames() {
		return authorNames;
	}
	public void setAuthorNames(List<String> authorNames) {
		this.authorNames = authorNames;
	}
	public String getArticleTitle() {
		return articleTitle;
	}
	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}
	public String getArticleAbstract() {
		return articleAbstract;
	}
	public void setArticleAbstract(String articleAbstract) {
		this.articleAbstract = articleAbstract;
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
	public String getFirstPage() {
		return firstPage;
	}



	public void setFirstPage(String firstPage) {
		this.firstPage = firstPage;
	}



	public String getLastPage() {
		return lastPage;
	}



	public void setLastPage(String lastPage) {
		this.lastPage = lastPage;
	}



	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	public List<String> getImageNos() {
		return imageNos;
	}
	public void setImageNos(List<String> imageNos) {
		this.imageNos = imageNos;
	}
	public String getFileURL() {
		return fileURL;
	}
	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}
	public String getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getTemplateType() {
		return templateType;
	}
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	public String getJournalTitle() {
		return journalTitle;
	}
	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}
	public String getPi() {
		return pi;
	}
	public void setPi(String pi) {
		this.pi = pi;
	}
	
}
