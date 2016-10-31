package ak.goobi.repec;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class WriteIndex {

	FileWriter writer;
	File file;

	private List<String> listRDFfiles(String remotePath, String configSection) {

		List<String> rdfFileNames = new ArrayList<String>();

		try {

			// FTP Variables from config file:
			String ftpServer = Main.akConfig.get("Repec."+configSection+".FtpServer");
			String ftpPort = Main.akConfig.get("Repec."+configSection+".FtpPort");
			String ftpUser = Main.akConfig.get("Repec."+configSection+".FtpUser");
			String ftpPass = Main.akConfig.get("Repec."+configSection+".FtpPass");

			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(ftpServer, Integer.parseInt(ftpPort));
			ftpClient.login(ftpUser, ftpPass);
			FTPFile[] files = ftpClient.listFiles(remotePath);

			for (FTPFile file : files) {
				String fileName = file.getName();
				boolean isRDF = fileName.endsWith(".rdf");

				if (isRDF == true) {
					rdfFileNames.add(fileName);
				}
			}

		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println(e.getStackTrace());
		}

		return rdfFileNames;
	}


	private List<String> listLocalRDFfiles(String localPath) {
		List<String> rdfFileNames = new ArrayList<String>();

		File dir = new File(localPath);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".rdf");
			}
		});

		Arrays.sort(files);

		for (File file : files) {
			String fileName = file.getName();
			rdfFileNames.add(fileName);
		}

		return rdfFileNames;
	}


	public void writeIndexFile(String localPath, String remotePath, String configSection, boolean verbose) {

		List<String> rdfFileNames = null;

		if (remotePath.equals("false") == false) {
			rdfFileNames = this.listRDFfiles(remotePath, configSection);
		} else {
			rdfFileNames = this.listLocalRDFfiles(localPath);
		}

		String fileName = "index.htm";
		file = new File(localPath + File.separator + fileName);

		try {

			writer = new FileWriter(file, false);
			writer.write("<HTML>");
			writer.write(System.getProperty("line.separator"));
			for (String rdfFileName : rdfFileNames) {
				writer.write("<BR><A HREF=\"" + rdfFileName + "\">" + rdfFileName + "</A>");
				writer.write(System.getProperty("line.separator"));
			}
			writer.write("</HTML>");

			writer.flush();
			writer.close();

			if (remotePath.equals("false") == false) {
				// Upload to FTP-Server:
				String ftpServer = Main.akConfig.get("Repec."+configSection+".FtpServer");
				String ftpPort = Main.akConfig.get("Repec."+configSection+".FtpPort");
				String ftpUser = Main.akConfig.get("Repec."+configSection+".FtpUser");
				String ftpPass = Main.akConfig.get("Repec."+configSection+".FtpPass");

				boolean ftpOK = FtpUpload.uploadFile(localPath + File.separator + fileName, remotePath + File.separator + fileName, ftpServer, Integer.parseInt(ftpPort), ftpUser, ftpPass, false, verbose);

				if (ftpOK == false) {
					System.err.println("Error: File " + fileName + " not written on FTP-Server.");
				}
			}
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println(e.getStackTrace());
		}
	}



}
