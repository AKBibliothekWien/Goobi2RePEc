package ak.goobi.repec;import java.util.ArrayList;
import java.util.List;public class SerialVolume {	private List<String> authorNames = new ArrayList<String>();
	private List<String> authorFirstNames = new ArrayList<String>();
	private List<String> authorLastNames = new ArrayList<String>();
	private String volumeTitle;
	private String volumeAbstract;
	private String volumeNumber;
	private String lengthInPages;
	private String creationDate;
	private String publicationStatus;
	private String fileUrl;
	private String fileFormat;
	private String fileFunction;
	private List<String> keywords;
	private String handle;
	private String pi;
	
	public SerialVolume(List<String> authorNames, List<String> authorFirstNames, List<String> authorLastNames, String volumeTitle, String volumeAbstract, String volumeNumber, String lengthInPages, String creationDate, String publicationStatus, String fileUrl, String fileFormat, String fileFunction, List<String> keywords, String handle, String pi) {
		this.authorNames = authorNames;
		this.authorFirstNames = authorFirstNames;
		this.authorLastNames = authorLastNames;
		this.volumeTitle = volumeTitle;
		this.volumeAbstract = volumeAbstract;
		this.volumeNumber = volumeNumber;
		this.lengthInPages = lengthInPages;
		this.creationDate = creationDate;
		this.publicationStatus = publicationStatus;
		this.fileUrl = fileUrl;
		this.fileFormat = fileFormat;
		this.fileFunction = fileFunction;
		this.keywords = keywords;
		this.handle = handle;
		this.pi = pi;
	}
	public List<String> getAuthorNames() {
		return authorNames;
	}
	public void setAuthorNames(List<String> authorNames) {
		this.authorNames = authorNames;
	}
	public List<String> getAuthorFirstNames() {
		return authorFirstNames;
	}
	public void setAuthorFirstNames(List<String> authorFirstNames) {
		this.authorFirstNames = authorFirstNames;
	}
	public List<String> getAuthorLastNames() {
		return authorLastNames;
	}
	public void setAuthorLastNames(List<String> authorLastNames) {
		this.authorLastNames = authorLastNames;
	}
	public String getVolumeTitle() {
		return volumeTitle;
	}
	public void setVolumeTitle(String volumeTitle) {
		this.volumeTitle = volumeTitle;
	}
	public String getVolumeAbstract() {
		return volumeAbstract;
	}
	public void setVolumeAbstract(String volumeAbstract) {
		this.volumeAbstract = volumeAbstract;
	}
	public String getVolumeNumber() {
		return volumeNumber;
	}
	public void setVolumeNumber(String volumeNumber) {
		this.volumeNumber = volumeNumber;
	}
	public String getLengthInPages() {
		return lengthInPages;
	}
	public void setLengthInPages(String lengthInPages) {
		this.lengthInPages = lengthInPages;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getPublicationStatus() {
		return publicationStatus;
	}
	public void setPublicationStatus(String publicationStatus) {
		this.publicationStatus = publicationStatus;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
	public String getFileFunction() {
		return fileFunction;
	}
	public void setFileFunction(String fileFunction) {
		this.fileFunction = fileFunction;
	}
	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public String getPi() {
		return pi;
	}
	public void setPi(String pi) {
		this.pi = pi;
	}
	
	@Override
	public String toString() {
		return "SerialVolume [authorNames=" + authorNames + ", authorFirstNames=" + authorFirstNames
				+ ", authorLastNames=" + authorLastNames + ", volumeTitle=" + volumeTitle + ", volumeAbstract="
				+ volumeAbstract + ", volumeNumber=" + volumeNumber + ", lengthInPages=" + lengthInPages
				+ ", creationDate=" + creationDate + ", publicationStatus=" + publicationStatus + ", fileUrl=" + fileUrl
				+ ", fileFormat=" + fileFormat + ", fileFunction=" + fileFunction + ", keywords=" + keywords
				+ ", handle=" + handle + ", pi=" + pi + "]";
	}
	
}