package ak.goobi.repec;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import main.java.ak.goobi.akconfig.AkConfig;

public class Main {

	public static Map<String, String> akConfig;
	static boolean exportToRepec;
	static String publication;
	static String processesPath;
	static String processId;
	static String pathToProcess;

	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.err.println("Error: You have to supply the ID of the Goobi process. In a script-step, you can use {processid}");
		} else {

			// Get AK configs in goobi config folder:
			akConfig = new AkConfig().getAkConfig();

			// Get process ID:
			processId = args[0];

			// Get path to goobi processes (normally this is "/opt/digiverso/goobi/metadata")
			processesPath = akConfig.get("General.ProcessesPath");

			// Build path to process folder (e. g. "/opt/digiverso/goobi/metadata/1234")
			pathToProcess = stripFileSeperatorFromPath(processesPath) + File.separator + processId;

			// Check if the process should be exported to RePEc:
			exportToRepec = exportToRepec(processId);

			if (exportToRepec) {
				String configSection = publication; // E. g. "Wug" or "MWug"
				String localPath = stripFileSeperatorFromPath(akConfig.get("Repec."+configSection+".LocalPath"));
				String remotePath = stripFileSeperatorFromPath(akConfig.get("Repec."+configSection+".FtpPath")); // E. g. "/htdocs/RePEc/abc/xxxarc"
				String pdfPrefix = akConfig.get("Repec."+configSection+".FilePrefix"); // E. g. "wug_"
				String docType = akConfig.get("Repec."+configSection+".DocType"); // E. g. "Journal" or "Series"
				boolean pdfNiceUrl = Boolean.valueOf(akConfig.get("Repec."+configSection+".PdfNiceUrl")); // "true" for URLs to PDF-Download like http://emedien.arbeiterkammer.at/viewer/pdf/AC00564651_2016_001/2016_42_1.pdf, "false" for URLs with GET-Parameters like http://emedien.arbeiterkammer.at/viewer/file?pi=AC00564651_2016_001&file=2016_42_1.pdf - RePEc (or CitEC) has problems with GET-Parameters!
				boolean verbose = Boolean.valueOf(akConfig.get("Repec.General.Verbose")); // true or false

				// Do not upload to FTP - only for TESTING
				//remotePath = "false"; 

				if (docType.equals("Journal")) {
					new WriteRDF().writeJournalIssue(pathToProcess, configSection, localPath, remotePath, pdfPrefix, pdfNiceUrl, verbose);
					new WriteIndex().writeIndexFile(localPath, remotePath, configSection, verbose);

					if (remotePath.equals("false")) {
						System.out.println("Fertig. Die Dateien fuer die RePEc wurden lokal unter \"" + localPath + "\" gespeichert. Die PDFs auf Artikelbasis wurden fuer den Export in den Viewer vorbereitet. Es wurden keine Dateien auf den FTP-Server uebertragen!");
					} else {
						System.out.println("Fertig. Die Dateien fuer die RePEc wurden lokal unter \"" + localPath + "\" und auf dem WUG-Server unter \"" + remotePath + "\" gespeichert. Die PDFs auf Artikelbasis wurden fuer den Export in den Viewer vorbereitet.");
					}
				} else if (docType.equals("Series")) {
					new WriteRDF().writeSerialVolume(pathToProcess, configSection, localPath, remotePath, pdfNiceUrl, verbose);
					new WriteIndex().writeIndexFile(localPath, remotePath, configSection, verbose);

					if (remotePath.equals("false")) {
						System.out.println("Fertig. Die Dateien fuer die RePEc wurden lokal unter \"" + localPath + "\" gespeichert. Es wurden keine Dateien auf den FTP-Server uebertragen!");
					} else {
						System.out.println("Fertig. Die Dateien fuer die RePEc wurden lokal unter \"" + localPath + "\" und auf dem WUG-Server unter \"" + remotePath + "\" gespeichert.");
					}

				}
			} else {
				System.out.println("Publikation wurde NICHT nach RePEc exportiert.");
			}
		}
	}


	private static boolean exportToRepec(String processId) {
		// Check if data should be exported to RePEc.
		// We use a "Prozesseigenschaft" of the proccess for it. It can be "Ja" or "Nein"
		// Example SQL Query with process ID 1819: SELECT Titel, WERT FROM prozesseeigenschaften WHERE prozesseID=1819 AND (Titel="RePEc" OR Titel="RePEc Publikation");
		boolean exportToRepec = false;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			exportToRepec = false;
			e.printStackTrace();
			return exportToRepec;
		}

		Connection jdbcConnection = null;

		try {

			String jdbc = Main.akConfig.get("General.Jdbc");
			String dbUser = Main.akConfig.get("General.DbUser");
			String dbPass = Main.akConfig.get("General.DbPass");

			jdbcConnection = DriverManager.getConnection("jdbc:"+jdbc, dbUser, dbPass);

			Statement statement = jdbcConnection.createStatement();
			String sql = "SELECT Titel, WERT FROM prozesseeigenschaften WHERE prozesseID=" + processId + " AND (Titel=\"RePEc\" OR Titel=\"RePEc Publikation\")";
			ResultSet resultSet = statement.executeQuery(sql);

			while(resultSet.next()){
				//Retrieve by column name
				String title = resultSet.getString("Titel");
				String value = resultSet.getString("WERT");

				if (title.equals("RePEc") && value.equals("Ja")) {
					exportToRepec = true;
				} else if (title.equals("RePEc") && value.equals("Nein")) {
					exportToRepec = false;
				}

				if (title.equals("RePEc Publikation")) {
					publication = value;
				}
			}

		} catch (SQLException ex) {
			// Error:
			exportToRepec = false;
			ex.printStackTrace();
			System.err.println("\n");
			System.err.println("SQLException: " + ex.getMessage());
			System.err.println("SQLState: " + ex.getSQLState());
			System.err.println("ErrorCode: " + ex.getErrorCode());
		}

		if (exportToRepec) {
			if (publication.equals("") || publication == null) {
				System.err.println("In den Prozesseigenschften muss eine Publikation ausgewaehlt werden! Das Kuerzel der Publikation muss gleich lauten wie der Konfigurationsabschnitt fuer die Publikation in der Konfigurationsdatei \"goobi_ak.xml\" (z. B. \"WuG\" oder \"MWuG\").");
				exportToRepec = false;
			}
		}

		return exportToRepec;
	}

	private static String stripFileSeperatorFromPath (String path) {
		if (path != null) {
			if ((path.length() > 0) && (path.charAt(path.length()-1) == File.separatorChar)) {
				path = path.substring(0, path.length()-1);
			}
		}
		return path;
	}


}
