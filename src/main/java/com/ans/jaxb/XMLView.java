package com.ans.jaxb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * XMLView
 * 
 * @author bensalem Nizar
 */
public class XMLView extends Application {

	/**
	 * treeTableView
	 */
	private TreeTableView<Node> treeTableView;
	/**
	 * fileG
	 */
	private String fileG = "";
	/**
	 * collapseAll
	 */
	private Button collapseAll = new Button("Réduire tout");
	/**
	 * labelCount
	 */
	private Label labelCount = new Label();
	/**
	 * count
	 */
	private Integer count = 0;
	/**
	 * countLine
	 */
	private Integer countLine = 1;

	/**
	 * tagNames
	 */
	private final String tagNames[] = { "Not considered", "Element", "Attribute", "Text", "CDATA section",
			"Processing Instruction", "Comment", "Document", "Document Type" };
	/**
	 * map jdv by line
	 */
	private Map<Integer, String> map = new HashMap<Integer, String>();

	/**
	 * root
	 */
	private TreeItem<Node> root = null;
	/**
	 * textArea
	 */
	private TextArea textArea = new TextArea();
	/**
	 * total
	 */
	private int total = 0;

	/**
	 * start
	 * 
	 * @param file
	 */
	public Stage start(final String file) {
		fileG = file;
		Stage stage = new Stage();
		start(stage);
		return stage;
	}

	/**
	 * readFileContents
	 * 
	 * @param selectedFile
	 * @throws IOException
	 */
	private String readFileContents(final File file) throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(file));
		String singleString = null;

		try {
			final StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(countLine++);
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			singleString = sb.toString();
		} finally {
			br.close();
			countLine = 1;
		}
		return singleString;
	}

	/**
	 * start
	 * 
	 * @param file
	 */
	@Override
	public void start(Stage stage) {
		File file;
		file = new File(fileG);
		if (!file.exists()) {
			createDemo(file);
		}
		treeTableView = createTreeTableView(file);
		treeTableView.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
		treeTableView.setPrefWidth(755);
		final BorderPane layout = new BorderPane();
		final Region spacer2 = new Region();
		spacer2.setPrefHeight(10);
		VBox.setVgrow(spacer2, Priority.ALWAYS);
		final Region spacer3 = new Region();
		spacer3.setPrefHeight(10);
		VBox.setVgrow(spacer3, Priority.ALWAYS);
		layout.setLeft(treeTableView);
		try {
			textArea.setText(readFileContents(file));
			textArea.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
			textArea.getStyleClass().add("text-area");
			textArea.setPrefWidth(755);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		layout.setRight(textArea);
		HBox hbox = new HBox();
		ObservableList<javafx.scene.Node> listHint = hbox.getChildren();
		collapseAll.setStyle(
				"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: BEIGE;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");

		collapseAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				collapseTreeView(root);
			}
		});

		HBox hb = new HBox();
		ObservableList<javafx.scene.Node> lhb = hb.getChildren();
		lhb.add(collapseAll);
		hb.setAlignment(Pos.CENTER_RIGHT);
		HBox hb1 = new HBox();
		ObservableList<javafx.scene.Node> lhb1 = hb1.getChildren();
		lhb1.add(labelCount);
		hb1.setAlignment(Pos.CENTER_LEFT);

		HBox hb2 = new HBox();
		ObservableList<javafx.scene.Node> lhb2 = hb2.getChildren();

		SortedSet<String> listString = new TreeSet<String>();
		SortedSet<Integer> listValue = new TreeSet<Integer>();
		for (Map.Entry<Integer, String> m : map.entrySet()) {
			listString.add((String) m.getValue());
			listValue.add((Integer) m.getKey());
		}

		final Region spacer13 = new Region();
		spacer13.setMaxWidth(50);
		HBox.setHgrow(spacer13, Priority.ALWAYS);

		Label newLabel = new Label();
		AutoCompleteTextField autoComplete = new AutoCompleteTextField(listString);
		autoComplete.textProperty().addListener((observable, oldValue, newValue) -> {
			total = 0;
			textArea.setScrollTop(0);
			for (Map.Entry<Integer, String> mapentry : map.entrySet()) {
				if (mapentry.getValue().trim().equals(newValue.trim())) {
					newLabel.setText(mapentry.getValue() + " Line " + mapentry.getKey());
					final String[] lines = textArea.getText().split("\n");
					for (Integer i = 0; i < mapentry.getKey(); i++) {
						final int numChars = lines[i].length();
						total += numChars;
					}

					final Rectangle2D lineBounds = ((TextAreaSkin) textArea.getSkin()).getCharacterBounds(total);
					textArea.setScrollTop(lineBounds.getMinY());
				}
			}
		});

		lhb2.addAll(autoComplete);

		final Region spacer1 = new Region();
		spacer1.setMaxWidth(1150);
		HBox.setHgrow(spacer1, Priority.ALWAYS);

		final Region spacer12 = new Region();
		spacer12.setMaxWidth(50);
		HBox.setHgrow(spacer12, Priority.ALWAYS);

		listHint.addAll(hb, spacer12, hb2, spacer13, newLabel, spacer1, hb1);

		VBox box = new VBox();
		ObservableList<javafx.scene.Node> listVint = box.getChildren();
		HBox hbox2 = new HBox();
		ObservableList<javafx.scene.Node> listHint2 = hbox2.getChildren();
		listHint2.add(spacer2);
		HBox hbox3 = new HBox();
		ObservableList<javafx.scene.Node> listHint3 = hbox3.getChildren();
		listHint3.add(spacer3);
		listVint.addAll(hbox3, hbox, hbox2);
		layout.setTop(box);
		final Scene scene = new Scene(layout);
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("Tree JDV File");
		stage.setMaximized(true);

		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
					Number newSceneWidth) {
				if (newSceneWidth.doubleValue() >= 1509 && newSceneWidth.doubleValue() < 1632) { // ecran 16 pouces
					treeTableView.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");

					collapseAll.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: BEIGE;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelCount.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textArea.setPrefWidth(755);
					spacer1.setMaxWidth(1150);
					newLabel.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
				}
				if (newSceneWidth.doubleValue() >= 1632 && newSceneWidth.doubleValue() >= 1728) { // ecran 17 pouces
					treeTableView.setStyle("-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");

					collapseAll.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: BEIGE;-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelCount.setStyle("-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textArea.setPrefWidth(855);
					spacer1.setMaxWidth(1150);
					newLabel.setStyle("-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
				} else if (newSceneWidth.doubleValue() < 1509) {
					treeTableView.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");

					collapseAll.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: BEIGE;-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelCount.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textArea.setPrefWidth(555);
					spacer1.setMaxWidth(850);
					newLabel.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
				}
			}
		});

		stage.show();
	}

	/**
	 * createTreeTableView
	 * 
	 * @param file
	 * @return
	 */
	public TreeTableView<Node> createTreeTableView(File file) {
		final TreeTableView<Node> treeTableView = new TreeTableView<>(createTreeItems(file));
		treeTableView.setShowRoot(true);
		final TreeTableColumn<Node, TreeItem<Node>> nameColumn = new TreeTableColumn<>("Nœud");

		nameColumn.setCellValueFactory((
				TreeTableColumn.CellDataFeatures<Node, TreeItem<Node>> cellData) -> new ReadOnlyObjectWrapper<TreeItem<Node>>(
						cellData.getValue()));

		final Image[] images = new Image[tagNames.length];
		try {
			final Image image = new Image(getClass().getResourceAsStream("image.png"));
			for (int i = 0; i < images.length; i++) {
				images[i] = new WritableImage(image.getPixelReader(), i * 16, 0, 16, 16);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		nameColumn.setCellFactory(column -> {
			TreeTableCell<Node, TreeItem<Node>> cell = new TreeTableCell<Node, TreeItem<Node>>() {
				final ImageView[] imageView = new ImageView[images.length];
				{
					for (int i = 0; i < imageView.length; i++)
						imageView[i] = new ImageView(images[i]);
				}

				@Override
				protected void updateItem(TreeItem<Node> item, boolean empty) {
					super.updateItem(item, empty);
					if (item != null && !empty) {
						final Node node = item.getValue();
						Object obj = node.getUserData("lineNumber");
						if (item.getValue() != null) {
							final String s = node.getNodeName();
							if (s.equals("valueSet")) {
								setText(s == null ? "" : s.trim() + "  [" + obj + "]");
							} else {
								setText(s == null ? "" : s.trim());
							}
							setGraphic(imageView[typeIndex(node.getNodeType())]);
							return;
						}
					}
					setText(null);
					setGraphic(null);
				}
			};
			return cell;
		});
		labelCount.setText(Integer.valueOf(count).toString() + " Fichiers de JDV   ");
		labelCount.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
		nameColumn.setPrefWidth(300);
		nameColumn.setSortable(false);
		treeTableView.getColumns().add(nameColumn);

		TreeTableColumn<Node, String> valueColumn = new TreeTableColumn<>("Valeur");
		valueColumn.setCellValueFactory(cellData -> {
			final TreeItem<Node> item = cellData.getValue();
			final Node childNode = item.getValue();
			if (childNode != null) {
				String s = childNode.getNodeType() == Node.DOCUMENT_TYPE_NODE
						? ((DocumentType) childNode).getInternalSubset()
						: childNode.getNodeValue();
				if (s != null) {
					return new ReadOnlyObjectWrapper<String>(s.trim());
				}
			}
			return null;
		});

		valueColumn.setPrefWidth(400);
		valueColumn.setSortable(false);
		treeTableView.getColumns().add(valueColumn);
		treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				final Node node = newValue.getValue();
				if (node != null) {
					return;
				}
			}
		});
		treeTableView.getSelectionModel().selectFirst();
		return treeTableView;
	}

	/**
	 * createTreeItems
	 * 
	 * @param file
	 * @return
	 */
	public TreeItem<Node> createTreeItems(final File file) {
		try {
			InputStream targetStream = new FileInputStream(file);
			final Document doc = PositionalXMLReader.readXML(targetStream);
			doc.getDocumentElement().normalize();
			root = new TreeItem<>(doc);
			root.setExpanded(true);
			addChildrenItem(root);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return root;
	}

	/**
	 * addChildrenItem
	 * 
	 * @param root
	 */
	public void addChildrenItem(TreeItem<Node> root) {
		final Node node = root.getValue();
		if (node.hasAttributes()) {
			final NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				root.getChildren().add(new TreeItem<Node>(attrs.item(i)));
			}
		}
		final NodeList list = node.getChildNodes();
		Object obj = node.getUserData("lineNumber");
		if (node.getNodeName().equals("valueSet")) {
			if (node.hasAttributes()) {
				final NamedNodeMap attrs = node.getAttributes();
				for (int i = 0; i < attrs.getLength(); i++) {
					if (attrs.item(i).getNodeName().equals("displayName")) {
						map.put((Integer) obj, attrs.item(i).getTextContent());
					}
				}
			}
		}
		for (int i = 0; i < list.getLength(); i++) {
			final Node childNode = list.item(i);
			if (childNode != null && (childNode.getNodeType() != Node.TEXT_NODE
					|| (childNode.getNodeValue() != null && childNode.getNodeValue().trim().length() > 0))) {
				final TreeItem<Node> treeItem = new TreeItem<>(childNode);
				treeItem.setExpanded(true);
				root.getChildren().add(treeItem);
				addChildrenItem(treeItem);
				if (childNode.getNodeName().equals("valueSet")) {
					count++;
				}
			}
		}
	}

	/**
	 * typeIndex
	 * 
	 * @param t
	 * @return
	 */
	public int typeIndex(int t) {
		switch (t) {
		case Node.ELEMENT_NODE:
			return 1;
		case Node.ATTRIBUTE_NODE:
			return 2;
		case Node.TEXT_NODE:
			return 3;
		case Node.CDATA_SECTION_NODE:
			return 4;
		case Node.PROCESSING_INSTRUCTION_NODE:
			return 5;
		case Node.COMMENT_NODE:
			return 6;
		case Node.DOCUMENT_NODE:
			return 7;
		case Node.DOCUMENT_TYPE_NODE:
			return 8;
		}
		return 0;
	}

	/**
	 * collapseTreeView
	 * 
	 * @param item
	 */
	private void collapseTreeView(final TreeItem<?> item) {
		if (item != null && !item.isLeaf()) {
			item.setExpanded(false);
			for (TreeItem<?> child : item.getChildren()) {
				collapseTreeView(child);
			}
		}
	}

	/**
	 * createDemo
	 * 
	 * @param file
	 */
	public void createDemo(final File file) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\"?>\n");
		sb.append(" <!DOCTYPE DocType [<!ENTITY name \"Value\">]>\n");
		sb.append(" <?Target Instruction?>\n");
		sb.append(" <!--Comment-->\n");
		sb.append("<Element Attribute=\"Attribute\">\n");
		sb.append(" <Element>\n");
		sb.append("  Text &name;\n");
		sb.append(" </Element>\n");
		sb.append(" <![CDATA[CDATA Section]]>\n");
		sb.append(" <EmptyElement/>\n");
		sb.append("</Element>\n");
		try {
			Files.write(file.toPath(), sb.toString().getBytes());
		} catch (final Exception e) {
		}
	}
}