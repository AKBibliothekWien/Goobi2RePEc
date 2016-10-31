package ak.goobi.repec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import ak.goobi.pdfmaker.PdfByOrderNos;



/*
 * This script writes the details of every journal article to one seperate .rdf-file.
 * So for each article there will be written one seperate file.
 */

public class WriteRDF {

	File rdfFile;
	
	public void writeSerialVolume(String pathToMetadataFolder, String configSection, String localPath, String remotePath, boolean pdfNiceUrl, boolean verbose) {
		
		try {
			File folder = new File(localPath);
			if (folder.exists() == false && folder.isDirectory() == false) {
				throw new FileNotFoundException("The folder " + localPath + " does not exist! Please create it and try again.");
			}
			
			List<SerialVolume> sVolumes = new ParseMetadataXML(pathToMetadataFolder, configSection).getSerialVolume();
			for (SerialVolume sVolume : sVolumes) { // There should only be one
				
				String fileName = sVolume.getCreationDate() + "_" + sVolume.getVolumeNumber() + ".rdf";
				rdfFile = new File(localPath + File.separator + fileName);
				
				try {
					FileOutputStream fileStream = new FileOutputStream(rdfFile);
					OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");

					writer.write("\ufeff"); // Write BOM to beginning of file - is needed in RePEc
					
					String volumeTitle = sVolume.getVolumeTitle();
					volumeTitle = (volumeTitle != null) ? volumeTitle.replaceAll("(\\s)", " ") : null;
					List<String> authorNames = sVolume.getAuthorNames();
					String volumeAbstract = sVolume.getVolumeAbstract();
					volumeAbstract = (volumeAbstract != null) ? volumeAbstract.replaceAll("(\\s)", " ") : null;
					String volumeNumber = sVolume.getVolumeNumber();
					String lengthInPages = sVolume.getLengthInPages();
					String creationDate = sVolume.getCreationDate();
					String publicationStatus = sVolume.getPublicationStatus();
					//String fileUrl = sVolume.getFileUrl();
					//String fileFormat = sVolume.getFileFormat();
					//String fileFunction = sVolume.getFileFunction();
					List<String> keywords = sVolume.getKeywords();
					String handle = sVolume.getHandle();
					String pi = sVolume.getPi();
					
					// Get name of PDF file we need to link to:
					File sourceFolder = new File(getViewerSourceFolder(pathToMetadataFolder));
					File[] listOfPdfFiles = sourceFolder.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.toLowerCase().endsWith(".pdf");
						}
					});
					String pdfFileName = listOfPdfFiles[0].getName(); // There should only be one! 
					
					
					writer.write("Template-Type: ReDIF-Paper 1.0");
					writer.write(System.getProperty("line.separator"));
					
					if (authorNames != null) {
						for (String authorName : authorNames) {
							writer.write("Author-Name: " + authorName);
							writer.write(System.getProperty("line.separator"));
						}
					} else { // Author-Name is mandatory and needs to be set!
						writer.write("Author-Name: No authors listed");
						writer.write(System.getProperty("line.separator"));
					}

					if (volumeTitle != null) {
						writer.write("Title: " + volumeTitle);
						writer.write(System.getProperty("line.separator"));
					} else { // Title is mandatory and needs to be set!
						writer.write("Title: No title found");
						writer.write(System.getProperty("line.separator"));
					}

					if (volumeAbstract != null) {
						writer.write("Abstract: " + volumeAbstract);
						writer.write(System.getProperty("line.separator"));
					}
					
					if (lengthInPages != null) {
						writer.write("Length: " + lengthInPages);
						writer.write(System.getProperty("line.separator"));
					}
					
					if (creationDate != null) {
						writer.write("Creation-Date: " + creationDate);
						writer.write(System.getProperty("line.separator"));
					}
					
					if (publicationStatus != null) {
						writer.write("Publication-Status: " + publicationStatus);
						writer.write(System.getProperty("line.separator"));
					}
					
					if (pdfFileName != null && pi != null) {
						if (pdfNiceUrl) {
							// E. g. http://emedien.arbeiterkammer.at/viewer/pdf/AC00564651_2016_001/2016_42_1.pdf
							writer.write("File-URL: http://emedien.arbeiterkammer.at/viewer/pdf/"+pi+"/"+pdfFileName);
						} else {
							// E. g. http://emedien.arbeiterkammer.at/viewer/file?pi=AC00564651_2016_001&file=2016_42_1.pdf
							writer.write("File-URL: http://emedien.arbeiterkammer.at/viewer/file?pi="+pi+"&file="+pdfFileName);
						}
						writer.write(System.getProperty("line.separator"));
						
						writer.write("File-Format: Application/pdf");
						writer.write(System.getProperty("line.separator"));
						
						writer.write("File-Function: Fulltext PDF of publication");
						writer.write(System.getProperty("line.separator"));
					}
					
					if (volumeNumber != null) {
						writer.write("Number: " + volumeNumber);
						writer.write(System.getProperty("line.separator"));
					}
					
					if (keywords != null) {
						String strKeywords = (keywords.size() > 0) ? keywords.toString().replace("[", "").replace("]", "") : null;
						writer.write("Keywords: " + strKeywords);
						writer.write(System.getProperty("line.separator"));
					}
					
					if (handle != null) {
						writer.write("Handle: " + handle);
						writer.write(System.getProperty("line.separator"));
					}
					
					writer.flush();
					writer.close();
					fileStream.flush();
					fileStream.close();

					if (!remotePath.equals("false")) {
						// Upload to FTP-Server:
						String ftpServer = Main.akConfig.get("Repec."+configSection+".FtpServer");
						String ftpPort = Main.akConfig.get("Repec."+configSection+".FtpPort");
						String ftpUser = Main.akConfig.get("Repec."+configSection+".FtpUser");
						String ftpPass = Main.akConfig.get("Repec."+configSection+".FtpPass");
						boolean ftpOK = FtpUpload.uploadFile(localPath + File.separator + fileName, remotePath + File.separator + fileName, ftpServer, Integer.valueOf(ftpPort), ftpUser, ftpPass, false, verbose);

						if (ftpOK == false) {
							System.err.println("Error: File " + fileName + " not written on FTP-Server.");
						}
					}					
				} catch (IOException e) {
					System.err.println("Error: " + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
	}
	
	
	public void writeJournalIssue(String pathToMetadataFolder, String configSection, String localPath, String remotePath, String pdfPrefix, boolean pdfNiceUrl,  boolean verbose) {
		JournalIssue jIssue = new ParseMetadataXML(pathToMetadataFolder, configSection).getIssue();
		

		// Check if local folder exists
		try {
			List<JournalArticle> jArticles = new ParseMetadataXML(pathToMetadataFolder, configSection).getArticles();
			File folder = new File(localPath);
			if (folder.exists() == false && folder.isDirectory() == false) {
				throw new FileNotFoundException("The folder " + localPath + " does not exist! Please create it and try again.");
			}

			String fileName = jIssue.getYear() + "_" + jIssue.getVolumeNo() + "_" + jIssue.getIssueNo() + ".rdf";
			rdfFile = new File(localPath + File.separator + fileName);

			try {
				FileOutputStream fileStream = new FileOutputStream(rdfFile);
				OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");

				writer.write("\ufeff"); // Write BOM to beginning of file - is needed in RePEc
				
				for (JournalArticle jArticle : jArticles) {

					// RDF information:
					String journalTitle = jArticle.getJournalTitle();
					String templateType = jArticle.getTemplateType();
					String articleTitle = jArticle.getArticleTitle();
					articleTitle = (articleTitle != null) ? articleTitle.replaceAll("(\\s)", " ") : null;
					List<String> authors = jArticle.getAuthorNames();
					String articleAbstract = jArticle.getArticleAbstract();
					articleAbstract = (articleAbstract != null) ? articleAbstract.replaceAll("(\\s)", " ") : null;
					String year = jArticle.getYear();
					String volumeNo = jArticle.getVolumeNo();
					String issueNo = jArticle.getIssueNo();
					String firstPage = jArticle.getFirstPage();
					String pages = jArticle.getPages();
					//String fileUrl = jArticle.getFileURL();
					//String fileFormat = jArticle.getFileFormat();
					String handle = jArticle.getHandle();
					//String language = jArticle.getLanguage();
					String keywords = jArticle.getKeywords();
					List<String> imageNos = jArticle.getImageNos();
					String pi = jArticle.getPi();
					
					// PDF naming:
					String sourcePdfFolder = getSourcePdfFolder(pathToMetadataFolder);
					String destinationFolder = getViewerSourceFolder(pathToMetadataFolder);
					
					/*
					// Naming with running number:
					int intRunningNo = (jArticles.indexOf(jArticle)+1);
					String strRunningNo = String.format("%02d", intRunningNo); // Number-string with leading zeros by using String.format
					String destinationFileName =  pdfPrefix + year + "_" + volumeNo + "_" + issueNo + "_" + strRunningNo;
					*/

					// Naming with 4-digit-first-page-number:
					int intFirstPage = Integer.valueOf(firstPage);
					String strFirstPage = String.format("%04d", intFirstPage); // Number-string with leading zeros by using String.format
					String destinationFileName =  pdfPrefix + year + "_" + volumeNo + "_" + issueNo + "_" + strFirstPage;
					
					
					writer.write("Template-Type: " + templateType);
					writer.write(System.getProperty("line.separator"));

					if (journalTitle != null) {
						writer.write("Journal: " + journalTitle);
						writer.write(System.getProperty("line.separator"));
					}

					if (articleTitle != null) {
						writer.write("Title: " + articleTitle);
						writer.write(System.getProperty("line.separator"));
					} else { // Title is mandatory and needs to be set!
						writer.write("Title: No title");
						writer.write(System.getProperty("line.separator"));
					}

					if (authors != null) {
						for (String author : authors) {
							writer.write("Author-Name: " + author);
							writer.write(System.getProperty("line.separator"));
						}
					} else { // Author-Name is mandatory and needs to be set!
						writer.write("Author-Name: No authors listed");
						writer.write(System.getProperty("line.separator"));
					}

					if (articleAbstract != null) {
						writer.write("Abstract: " + articleAbstract);
						writer.write(System.getProperty("line.separator"));
					}

					if (keywords != null) {
						writer.write("Keywords: " + keywords);
						writer.write(System.getProperty("line.separator"));
					}

					writer.write("Year: " + year);
					writer.write(System.getProperty("line.separator"));

					writer.write("Volume: " + volumeNo);
					writer.write(System.getProperty("line.separator"));

					writer.write("Issue: " + issueNo);
					writer.write(System.getProperty("line.separator"));

					writer.write("Pages: " + pages);
					writer.write(System.getProperty("line.separator"));

					/*
					// Link to HTML-Page of Goobi-Viewer:
					writer.write("File-URL: " + fileUrl);
					writer.write(System.getProperty("line.separator"));

					writer.write("File-Format: " + fileFormat);
					writer.write(System.getProperty("line.separator"));
					
					writer.write("File-Function: Link to article in repository");
					writer.write(System.getProperty("line.separator"));
					*/
					
					if (pdfNiceUrl) {
						// E. g. http://emedien.arbeiterkammer.at/viewer/pdf/AC00564651_2016_001/2016_42_1.pdf
						writer.write("File-URL: http://emedien.arbeiterkammer.at/viewer/pdf/"+pi+"/"+destinationFileName+".pdf");
					} else {
						// E. g. http://emedien.arbeiterkammer.at/viewer/file?pi=AC00564651_2016_001&file=2016_42_1.pdf
						writer.write("File-URL: http://emedien.arbeiterkammer.at/viewer/file?pi="+pi+"&file="+destinationFileName+".pdf");
					}
					
					
					writer.write(System.getProperty("line.separator"));

					writer.write("File-Format: Application/pdf");
					writer.write(System.getProperty("line.separator"));
					
					writer.write("File-Function: PDF-file of article");
					writer.write(System.getProperty("line.separator"));
					
					writer.write("Handle: " + handle);

					writer.write(System.getProperty("line.separator"));
					writer.write(System.getProperty("line.separator"));

					new PdfByOrderNos(sourcePdfFolder, imageNos, destinationFolder, destinationFileName);

				}

				writer.flush();
				writer.close();
				fileStream.flush();
				fileStream.close();

				if (!remotePath.equals("false")) {
					// Upload to FTP-Server:
					String ftpServer = Main.akConfig.get("Repec."+configSection+".FtpServer");
					String ftpPort = Main.akConfig.get("Repec."+configSection+".FtpPort");
					String ftpUser = Main.akConfig.get("Repec."+configSection+".FtpUser");
					String ftpPass = Main.akConfig.get("Repec."+configSection+".FtpPass");
					boolean ftpOK = FtpUpload.uploadFile(localPath + File.separator + fileName, remotePath + File.separator + fileName, ftpServer, Integer.valueOf(ftpPort), ftpUser, ftpPass, false, verbose);

					if (ftpOK == false) {
						System.err.println("Error: File " + fileName + " not written on FTP-Server.");
					}
				}

			} catch (IOException e) {
				System.err.println("Error: " + e.getLocalizedMessage());
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private String getSourcePdfFolder(String pathToMetadataFolder) {
		String sourcePdfFolder = null;

		File ocrSourcePath = new File(pathToMetadataFolder + File.separator + "ocr");
		File[] listOfFilesAndDirs = ocrSourcePath.listFiles();

		for (int i = 0; i < listOfFilesAndDirs.length; i++) {

			if (listOfFilesAndDirs[i].isDirectory()) {

				Path pdfSourcePath = FileSystems.getDefault().getPath(listOfFilesAndDirs[i].getName());
				PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*_pdf"); // Get Directory that ends with _pdf
				if (matcher.matches(pdfSourcePath)) {
					sourcePdfFolder = listOfFilesAndDirs[i].getAbsolutePath();
				}
			}
		}
		return sourcePdfFolder;
	}

	
	private String getViewerSourceFolder(String pathToMetadataFolder) {
		String viewerSourceFolder = null;

		File imgSourcePath = new File(pathToMetadataFolder + File.separator + "images");
		File[] listOfFilesAndDirs = imgSourcePath.listFiles();

		for (int i = 0; i < listOfFilesAndDirs.length; i++) {

			if (listOfFilesAndDirs[i].isDirectory()) {

				Path pdfSourcePath = FileSystems.getDefault().getPath(listOfFilesAndDirs[i].getName());
				PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*_source"); // Get Directory that ends with _source
				if (matcher.matches(pdfSourcePath)) {
					viewerSourceFolder = listOfFilesAndDirs[i].getAbsolutePath();
				}
			}
		}
		return viewerSourceFolder;
	}

}
