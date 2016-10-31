package ak.goobi.repec;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ak.goobi.metahelper.main.GoobiMetaHelper;
import ak.goobi.oaihelper.classes.Id;



public class ParseMetadataXML {

	private String pathToMetadataFolder;
	private String configSection;
	private GoobiMetaHelper gmh;
	private Document document;


	public ParseMetadataXML(String pathToMetadataFolder, String configSection) {
		this.configSection = configSection;
		this.pathToMetadataFolder = pathToMetadataFolder;
		gmh = new GoobiMetaHelper(pathToMetadataFolder, false);
		document = gmh.getDocument();
	}
	
	
	public List<SerialVolume> getSerialVolume() {
		List<SerialVolume> sVolumes = new ArrayList<SerialVolume>();
		
		String strStructElements = Main.akConfig.get("Repec."+configSection+".StructElements");
		List<String> lstStructElements = Arrays.asList(strStructElements.split("\\s*,\\s*"));
		
		try {
			List<Id> volumeIDs = gmh.getIds(document, lstStructElements);
			
			for(Id volumeID : volumeIDs) {
				String dmdlogId = volumeID.getDmdlogId();
				List<String> physIDs = volumeID.getPhysIds();
				List<String> lstAuthorNames = gmh.getAuthorsByDmdlogId(document, dmdlogId);
				
				String xpathFirstName		= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi//metadata[@name='Author']/firstName";
				String xpathLastName		= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi//metadata[@name='Author']/lastName";
				String xpathTitle			= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi/metadata[@name='TitleDocMain']";
				String xpathAbstract		= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi/metadata[@name='ArticleAbstract']";
				String xpathVolumeNo		= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi/metadata[@name='CurrentNo']";
				String xpathCreationDate	= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi/metadata[@name='PublicationYear']";
				String xpathKeywords		= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi/metadata[@name='Classification']";
				String xpathUrn				= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi/metadata[@name='_urn']";
				
				String strTitle = gmh.getTextValue(document, xpathTitle);
				List<String> strAuthorFirstNames = gmh.getTextValues(document, xpathFirstName);
				List<String> strAuthorLastNames = gmh.getTextValues(document, xpathLastName);
				String strAbstract = gmh.getTextValue(document, xpathAbstract);
				String strVolumeNo = gmh.getTextValue(document, xpathVolumeNo);
				String strLengthInPages = String.valueOf(physIDs.size());
				String strCreationDate = gmh.getTextValue(document, xpathCreationDate);
				List<String> keywords = gmh.getTextValues(document, xpathKeywords);
				String urn = gmh.getTextValue(document, xpathUrn);
				String strFileURL = (urn != null) ? "http://emedien.arbeiterkammer.at/viewer/resolver?urn=" + urn : null;
				/*String strKeywords = null;
				if (keywords != null) {
					strKeywords = (keywords.size() > 0) ? keywords.toString().replace("[", "").replace("]", "") : null;
				}*/
				String strFileFormat = "text/html";
				//String strFileFormat = "Application/pdf";
				String strFileFunction = "Fulltext of publication";
				String strPublicationStatus = "Published in: Working Paper Reihe der AK Wien - Materialien zu Wirtschaft und Gesellschaft";
				String strHandle = "RePEc:clr:mwugar:"+strVolumeNo;
				String strPi = gmh.getPi();

				sVolumes.add(new SerialVolume(lstAuthorNames, strAuthorFirstNames, strAuthorLastNames, strTitle, strAbstract, strVolumeNo, strLengthInPages, strCreationDate, strPublicationStatus, strFileURL, strFileFormat, strFileFunction, keywords, strHandle, strPi));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sVolumes;
	}


	public List<JournalArticle> getArticles() throws Exception {

		List<JournalArticle> jArticles = new ArrayList<JournalArticle>();

		try {
			String strStructElements = Main.akConfig.get("Repec."+configSection+".StructElements");
			List<String> lstStructElements = Arrays.asList(strStructElements.split("\\s*,\\s*"));
			List<Id> articleIDs = gmh.getIds(document, lstStructElements);
			jArticles = this.parseArticles(document, articleIDs, gmh);

		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return jArticles;
	}


	public JournalIssue getIssue() {

		GoobiMetaHelper gmh = new GoobiMetaHelper(pathToMetadataFolder, false);
		Document document = gmh.getDocument();
		JournalIssue jIssue = null;

		try {
			jIssue = this.parseIssue(document, gmh);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return jIssue;
	}


	private JournalIssue parseIssue(Document document, GoobiMetaHelper gmh) throws XPathExpressionException {

		String xpathYear				= "/mets/dmdSec/mdWrap/xmlData/mods/extension/goobi/metadata[@name='PublicationYear']";
		String xpathVolumeNo			= "/mets/dmdSec/mdWrap/xmlData/mods/extension/goobi/metadata[@name='VolumeNo']";
		String xpathIssueNo				= "/mets/dmdSec/mdWrap/xmlData/mods/extension/goobi/metadata[@name='CurrentNo']";

		String strJournalTitle = getJournalTitle();
		String strYear = gmh.getTextValue(document, xpathYear);
		String strVolumeNo = gmh.getTextValue(document, xpathVolumeNo);
		String strIssueNo = gmh.getTextValue(document, xpathIssueNo);

		JournalIssue journalIssue = new JournalIssue(strJournalTitle, strYear, strVolumeNo, strIssueNo);

		return journalIssue;
	}

	private String getJournalTitle() {
		String strJournalTitle = null;
		GoobiMetaHelper gmh = new GoobiMetaHelper(pathToMetadataFolder, true);
		Document document = gmh.getDocument();
		String xpathJournalTitle		= "/mets/dmdSec/mdWrap/xmlData/mods/extension/goobi/metadata[@name='TitleDocMain']";

		try {
			strJournalTitle = gmh.getTextValue(document, xpathJournalTitle);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return strJournalTitle;
	}


	private List<JournalArticle> parseArticles(Document document, List<Id> articleIDs, GoobiMetaHelper gmh) throws XPathExpressionException {

		List<JournalArticle> journalArticles = new ArrayList<JournalArticle>();

		for(Id articleID : articleIDs) {
			String dmdlogId = articleID.getDmdlogId();
			List<String> physIDs = articleID.getPhysIds();

			String xpathTitle		= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi/metadata[@name='TitleDocMain']";
			String xpathAbstract	= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi/metadata[@name='ArticleAbstract']";
			String xpathYear		= "/mets/dmdSec/mdWrap/xmlData/mods/extension/goobi/metadata[@name='PublicationYear']";
			String xpathVolumeNo	= "/mets/dmdSec/mdWrap/xmlData/mods/extension/goobi/metadata[@name='VolumeNo']";
			String xpathIssueNo		= "/mets/dmdSec/mdWrap/xmlData/mods/extension/goobi/metadata[@name='CurrentNo']";
			String xpathKeywords	= "/mets/dmdSec[@ID='" + dmdlogId + "']/mdWrap/xmlData/mods/extension/goobi/metadata[@name='Classification']";

			String strJournalTitle = getJournalTitle();
			String strArticleTitle = gmh.getTextValue(document, xpathTitle);
			String strArticleAbstract = gmh.getTextValue(document, xpathAbstract);
			List<String> lstAuthorNames = gmh.getAuthorsByDmdlogId(document, dmdlogId);
			String strYear = gmh.getTextValue(document, xpathYear);
			String strVolumeNo = gmh.getTextValue(document, xpathVolumeNo);
			String strIssueNo = gmh.getTextValue(document, xpathIssueNo);
			List<String> pageValues = gmh.getFirstLastLabelByPhysId(document, physIDs);
			String firstPage = (pageValues.get(0).length() > 0) ? pageValues.get(0).replace("[", "").replace("]", "") : "";
			String lastPage = (pageValues.get(1).length() > 1) ? pageValues.get(1) : "";
			String strPages = firstPage + "-" + lastPage;
			List<String> imageNosForPdf = gmh.getOrderNoByPhysId(document, articleID.getPhysIds());
			List<String> urns = gmh.getUrnsByPhysIds(document, physIDs);
			String strFileURL = (urns.isEmpty() == false) ? "http://emedien.arbeiterkammer.at/viewer/resolver?urn=" + urns.get(0) : null;
			String strFileFormat = "text/html";
			String strHandle = "RePEc:clr:wugarc:y:"+strYear+"v:"+strVolumeNo+"i:"+strIssueNo+"p:"+firstPage;
			String strPi = gmh.getPi();

			List<String> keywords = gmh.getTextValues(document, xpathKeywords);
			String strKeywords = null;
			if (keywords != null) {
				strKeywords = (keywords.size() > 0) ? keywords.toString().replace("[", "").replace("]", "") : null;
			}
			
			JournalArticle jArticle = new JournalArticle(strJournalTitle, lstAuthorNames, strArticleTitle, strArticleAbstract, strYear, strVolumeNo, strIssueNo, firstPage, lastPage, strPages, imageNosForPdf, strFileURL, strFileFormat, strHandle, strKeywords, strPi);
			journalArticles.add(jArticle);

		}

		return journalArticles;

	}


}
