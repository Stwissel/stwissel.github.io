/**
 *  InspectDesign reads a XML File that has been produced as DXL Export
 *  and reports various design metrics
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author stw
 * 
 */
public class InspectDesign {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage InspectDesign [Source]");
			System.exit(1);
		}

		String sourceName = args[0];

		InspectDesign inspect = new InspectDesign(sourceName);

		inspect.process();

		System.out.print(inspect.toString());

	}

	/**
	 * The file name we processed
	 */
	private String fileName = null;

	/**
	 * Map to count all the different tags
	 */
	TreeMap<String, Long> tagCount;

	/**
	 * The Dom document to be inspected
	 */
	private Document xDoc = null;

	/**
	 * TagNames for LOC count
	 */
	HashMap<String, Boolean> tagsForLOC;

	/**
	 * 
	 * @param sourceName
	 *            File name to inspect.
	 */
	InspectDesign(String sourceName) {
		this.populateTagsForLoc();
		this.setSource(sourceName);
	}

	/**
	 * @param curName
	 *            Name of the Tag
	 * @param addCount
	 *            how much to add (1 for tags, more for LOC
	 */
	private void addElementCount(String curName, long addCount) {
		Long newValue;
		if (this.tagCount.containsKey(curName)) {
			Long curVal = this.tagCount.get(curName);
			newValue = new Long(curVal.longValue() + addCount);
		} else {
			newValue = new Long(addCount);
		}
		this.tagCount.put(curName, newValue);

	}

	/**
	 * @param curName
	 */
	private void addOneToElementCount(String curName) {
		this.addElementCount(curName, 1);
	}

	/**
	 * @return the DOM Document
	 */
	public Document getDocument() {
		return this.xDoc;
	}

	/**
	 * @param xmlFile
	 * @return Document the DOM of the file we did read
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private Document getDOM(InputStream xmlSource) {
		Document result = null;
		DocumentBuilder documentBuilder = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			// If we validate here it mostly blows
			documentBuilderFactory.setValidating(false);
			documentBuilderFactory.setSchema(null);
			// Namespace aware MUST be set to true!!!!
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setIgnoringComments(true);
			documentBuilderFactory.setIgnoringElementContentWhitespace(false);
			documentBuilderFactory.setXIncludeAware(false);

			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			result = documentBuilder.parse(xmlSource);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * @param tagName
	 *            What are we looking for
	 * @return number of occurences -1 if not found
	 */
	public long getTagCount(String tagName) {
		long result = -1;
		if (this.tagCount.containsKey(tagName)) {
			result = this.tagCount.get(tagName).longValue();
		}
		return result;
	}

	/**
	 * Returns all Tag names found
	 * 
	 * @return Set : the list of all keys
	 * 
	 */
	public TreeMap<String, Long> getTagNames() {
		return this.tagCount;

	}

	/**
	 * @param element
	 */
	private void iterateAndCount(Element element) {
		// Recursive function to get all the elements for inspection
		NodeList nodeList = element.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			System.out.print(".");
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element nodeElement = (Element) node;
				String curName = node.getNodeName();
				this.addOneToElementCount(curName);
				if (this.tagsForLOC.containsKey(curName)) {
					this.updateLOC(nodeElement);
				}
				this.iterateAndCount(nodeElement);
			}
		}
		System.out.print("*");

	}

	/**
	 * 
	 */
	private void populateTagsForLoc() {
		this.tagsForLOC = new HashMap<String, Boolean>();
		this.tagsForLOC.put("lotusscript", true);
		this.tagsForLOC.put("formula", true);
		this.tagsForLOC.put("java", true);
		this.tagsForLOC.put("javascript", true);
	}

	/**
	 * Parses the XML File and extracts several metrics from it
	 * 
	 */
	public void process() {
		// We traverse through the XML file and count all the different tag
		// names
		this.tagCount = new TreeMap<String, Long>();

		try {
			// Root of our XML document
			if (this.xDoc == null) {
				System.out.println("Empty document returned, can't inspect design");
				return;
			}
			Element element = this.xDoc.getDocumentElement();
			
			this.iterateAndCount(element);

		} catch (Exception e) {
			System.out
					.println("InspectDesign.process error: " + e.getMessage());
		}

	}

	/**
	 * @param sourceName
	 * @return Document The DOM Object
	 */
	public Document setSource(String sourceName) {
		this.fileName = sourceName;
		File xmlFile = new File(sourceName);

		try {
			InputStream xmlSource = new FileInputStream(xmlFile);
			this.xDoc = this.getDOM(xmlSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return this.getDocument();

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append("database : ");
		out.append(this.fileName);
		out.append("\n");
		Iterator<String> iter = this.tagCount.keySet().iterator();
		while (iter.hasNext()) {
			String curKey = iter.next();
			out.append(curKey);
			out.append(" : ");
			out.append(this.tagCount.get(curKey));
			out.append("\n");
		}
		//TODO: Add a formfeed characters

		return out.toString();
	}

	/**
	 * 
	 * @param element
	 *            The LotusScript Element to check
	 */
	private void updateLOC(Element element) {
		// Extracts the # of lines inside an element
		// typically used for lotuscript elements
		long addCount = 0;
		String curName = element.getNodeName();

		NodeList nodeList = element.getChildNodes();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {

				BufferedReader source = new BufferedReader(new StringReader(
						node.getNodeValue()));

				String curLine;
				try {
					while ((curLine = source.readLine()) != null) {
						String workLine = curLine.trim().toLowerCase();

						if (!workLine.equals("") && !workLine.startsWith("'")) {
							addCount += 1;
						}

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		// Now add our findings
		this.addElementCount("LOC_" + curName, addCount);

	}

}
