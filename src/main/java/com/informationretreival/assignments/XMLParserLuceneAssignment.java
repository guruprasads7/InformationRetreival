package com.informationretreival.assignments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class XMLParserLuceneAssignment {

	private static final Logger LOG = LoggerFactory.getLogger(XMLParserLuceneAssignment.class);

	public static void main(String[] args) {

		SAXParserFactory saxParserfactory = SAXParserFactory.newInstance();

		if (args.length != 1) {
			System.out.println("No input file Xml file found, Please run the program with xmlfile as input");
			System.exit(1);
		}

		File file = new File(args[0]);
		boolean fileExists = file.exists();
		List<DocumentStructure> doclist = null;

		if (!fileExists) {
			LOG.error("Input XML File Not found : " + file.getName());
		}

		try {
			SAXParser saxParser = saxParserfactory.newSAXParser();
			InputXMLHandler handler = new InputXMLHandler();
			saxParser.parse(file, handler);
			// Get DocumentStructure list
			doclist = handler.getDocumentList();
			if (doclist.size() <= 0) {
				System.out.println("No document of the xml is parsed, Exiting the program");
				System.exit(1);
			}
		} catch (ParserConfigurationException e) {
			LOG.error("Parser configuration Exception caught : " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (SAXException e) {
			LOG.error("SAX Exception caught : " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			LOG.error("IO Exception caught : " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		List<DocumentStructure> mergedlist = prepareDocument(doclist);
		HashMap<String, ArrayList<String>> termsdict = null;

		DocumentIndexerSearcher index = new DocumentIndexerSearcher();
		try {
			index.indexDocument(mergedlist);
			termsdict = index.computeIndexStatsAndFrequentTerms();

		} catch (ParseException e) {
			LOG.error("Exception occured while parsing the query : " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			LOG.error("Exception occured while fetching index : " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			LOG.error("Exception occured while indexing document : " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		try {
			// "Performing Boolean OR Operation"
			index.searchUsingBooleanQuery(termsdict, ProgramConstants.BOOLEAN_OR);

			// "Performing Boolean AND Operation"
			index.searchUsingBooleanQuery(termsdict, ProgramConstants.BOOLEAN_AND);

		} catch (ParseException e) {
			LOG.error("Exception occured while parsing the query : " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			LOG.error("Exception occured while fetching index : " + e.getMessage());
			e.printStackTrace();
			
		}

	}

	private static List<DocumentStructure> prepareDocument(List<DocumentStructure> documentlist) {

		List<DocumentStructure> mergedList = new ArrayList<>();

		// System.out.println(documentlist.size());
		for (int i = 0; i <= documentlist.size() - 6; i = i + 6) {

			// System.out.println(i);
			DocumentStructure doc = new DocumentStructure();
			doc.setLineId((documentlist.get(i)).getLineId());
			doc.setPlayName((documentlist.get(i + 1)).getPlayName());
			doc.setSpeechNumber((documentlist.get(i + 2)).getSpeechNumber());
			doc.setLineNumber(((documentlist.get(i + 3)).getLineNumber()));
			doc.setSpeaker((documentlist.get(i + 4)).getSpeaker());
			doc.setTextEntry(((documentlist.get(i + 5)).getTextEntry()));

			mergedList.add(doc);
		}
		return mergedList;
	}

}
