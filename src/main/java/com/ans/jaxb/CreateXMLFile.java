package com.ans.jaxb;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * create xml file from excel
 * 
 * @author bensalem Nizar
 */
public class CreateXMLFile {

	public static File createdFile = null;

	/**
	 * createXMLFile
	 * 
	 * @param list
	 * @param out
	 */
	public static boolean createXMLFile(final List<RetrieveValueSetResponse> list, final String out) {
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		boolean isOk = false;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.newDocument();
			// add elements to Document
			final Element rootElement = doc.createElement("RetrieveValueSetResponse");
			rootElement.setAttribute("xmlns:xsi", Constante.attribute);
			rootElement.setAttribute("xsi:schemaLocation", Constante.attribute1);
			rootElement.setAttribute("xmlns", Constante.attribute2);
			// append root element to document
			doc.appendChild(rootElement);
			if (list != null) {
				if (list.size() > 0) {
					final String oid = (list.get(0)).getValueSetOID();
					final String name = (list.get(0)).getValueSetName();
					// append first child element to root element
					final Node valueSet = createValueSetElement(doc, oid, name, "test");
					rootElement.appendChild(valueSet);
					final Node conceptList = valueSet.getFirstChild();
					for (int i = 0; i < list.size(); i++) {
						final String code = (list.get(i)).getCode();
						final String displayName = (list.get(i)).getDisplayName();
						final String codeSystem = (list.get(i)).getCodeSystem();
						final String dateValid = (list.get(i)).getDateDebut();
						final String dateFin = (list.get(i)).getDateFin();
						String dateValidFinal = "";
						String dateFinFinal = "";
						if (dateValid != null) {
							if (!dateValid.isEmpty()) {
								final String[] words = dateValid.split("/");
								for (@SuppressWarnings("unused")
								final String word : words) {
									dateValidFinal = words[2].substring(0, 4) + words[1] + words[0] + Constante.zero;
								}
							}
						}
						if (dateFin != null) {
							if (!dateFin.isEmpty()) {
								final String[] words = dateFin.split("/");
								for (@SuppressWarnings("unused")
								final String word : words) {
									dateFinFinal = words[2].substring(0, 4) + words[1] + words[0] + Constante.zero;
								}
							}
						}
						// append first child element to root element
						conceptList.appendChild(
								createConceptElement(doc, code, displayName, codeSystem, dateValidFinal, dateFinFinal));
					}

					// for output to file, console
					final TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					// for pretty print
					transformer.setOutputProperty(OutputKeys.INDENT, Constante.yes);
					transformer.setOutputProperty(OutputKeys.ENCODING, Constante.utf8);
					final DOMSource source = new DOMSource(doc);
					// write to console or file
					if (!new File(out).exists()) {
						Files.createDirectories(new File(out).toPath());
					}
					final StreamResult file = new StreamResult(new File(out + "\\" + name + ".xml"));
					createdFile = new File(out + "\\" + name + ".xml");
					// write data
					transformer.transform(source, file);
					isOk = true;
				}
			}
		} catch (final Exception e) {
			isOk = false;
			e.printStackTrace();
		}
		return isOk;
	}

	/**
	 * createValueSetElement
	 * 
	 * @param doc
	 * @param id
	 * @param name
	 * @param version
	 * @return
	 */
	private static Node createValueSetElement(final Document doc, final String id, final String name,
			final String version) {
		final Element valueSet = doc.createElement("ValueSet");
		// set id attribute
		valueSet.setAttribute("id", id);
		// set id attribute
		valueSet.setAttribute("displayName", name);
		// set id attribute
		valueSet.setAttribute("version", version);
		// create firstName element
		final Node node = createValueSetElement(doc, valueSet, "ConceptList", "");
		valueSet.appendChild(node);
		return valueSet;
	}

	/**
	 * createConceptElement
	 * 
	 * @param doc
	 * @param code
	 * @param displayName
	 * @param codeSystem
	 * @param dateValid
	 * @param dateFin
	 * @return
	 */
	private static Node createConceptElement(final Document doc, final String code, final String displayName,
			final String codeSystem, final String dateValid, final String dateFin) {
		final Element concept = doc.createElement("Concept");
		// set id attribute
		concept.setAttribute("code", code);
		// set id attribute
		concept.setAttribute("displayName", displayName);
		// set id attribute
		concept.setAttribute("codeSystem", codeSystem);
		// set id attribute
		concept.setAttribute("dateValid", dateValid);
		// set id attribute
		concept.setAttribute("dateFin", dateFin);
		return concept;
	}

	/**
	 * utility method to create text node
	 * 
	 * @param doc
	 * @param element
	 * @param name
	 * @param value
	 * @return
	 */
	private static Node createValueSetElement(final Document doc, final Element element, final String name,
			final String value) {
		final Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}

	/**
	 * @return the createdFile
	 */
	public static File getCreatedFile() {
		return createdFile;
	}

}