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

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
import javafx.stage.StageStyle;

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
	private Button collapseAll = new Button(Constante.reduire);
	/**
	 * refresh
	 */
	private Button refresh = new Button();

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
	 * newLabel
	 */
	private Label newLabel = new Label();
	/**
	 * listString
	 */
	private SortedSet<String> listString = new TreeSet<String>();
	/**
	 * listValue
	 */
	private SortedSet<Integer> listValue = new TreeSet<Integer>();

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
	private String readFileContents(final File file) {
		String singleString = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			final StringBuilder sb = new StringBuilder();

			String line = br.readLine();
			while (line != null) {
				sb.append(countLine++);
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			singleString = sb.toString();
		} catch (final IOException exp) {
			exp.getStackTrace();
		}

		finally {
			try {
				br.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
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
		TextField autoComplete = new TextField();
		// Start ProgressBar creation
		final double wndwWidth = 150.0d;
		final double wndhHeigth = 150.0d;
		final ProgressIndicator progress = new ProgressIndicator();
		progress.setMinWidth(wndwWidth);
		progress.setMinHeight(wndhHeigth);
		progress.setProgress(0.25F);
		final VBox updatePane = new VBox();
		updatePane.setPadding(new Insets(10));
		updatePane.setSpacing(5.0d);
		updatePane.setAlignment(Pos.CENTER);
		updatePane.getChildren().addAll(progress);
		updatePane.setStyle("-fx-background-color: #FFFAFA");
		final Stage taskUpdateStage = new Stage(StageStyle.UNDECORATED);
		taskUpdateStage.setScene(new Scene(updatePane, 170, 170));
		// End progressBar
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
		textArea.setText(readFileContents(file));
		textArea.setEditable(false);
		textArea.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		textArea.getStyleClass().add("text-area");
		textArea.setPrefWidth(755);
		layout.setRight(textArea);
		HBox hbox = new HBox();
		Image img = new Image("UIControls/collapse.png");
		ImageView view = new ImageView(img);
		view.setPreserveRatio(true);
		ObservableList<javafx.scene.Node> listHint = hbox.getChildren();
		collapseAll.setGraphic(view);
		collapseAll.setStyle(
				"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #eee9da;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");

		collapseAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				if (collapseAll.getText().equals(Constante.reduire)) {

					collapseAll.setText(Constante.expand);
					collapseTreeView(root);
				} else if (collapseAll.getText().equals(Constante.expand)) {
					runTask(taskUpdateStage, progress);
					Platform.runLater(() -> {
						collapseAll.setText(Constante.reduire);
						expandTreeView(root);
					});
				}
			}
		});

		HBox hb = new HBox();
		ObservableList<javafx.scene.Node> lhb = hb.getChildren();
		lhb.add(collapseAll);
		hb.setAlignment(Pos.CENTER_RIGHT);

		Image img1 = new Image("UIControls/refresh.png");
		ImageView view1 = new ImageView(img1);
		view1.setPreserveRatio(true);
		refresh.setGraphic(view1);
		refresh.setAlignment(Pos.CENTER);
		refresh.setPrefHeight(40);
		refresh.setStyle(
				"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #eee9da;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final Region spacer14 = new Region();
		spacer14.setMaxWidth(20);
		HBox.setHgrow(spacer14, Priority.ALWAYS);

		HBox hb1 = new HBox();
		ObservableList<javafx.scene.Node> lhb1 = hb1.getChildren();
		lhb1.add(labelCount);
		hb1.setAlignment(Pos.CENTER_LEFT);

		HBox hb2 = new HBox();
		ObservableList<javafx.scene.Node> lhb2 = hb2.getChildren();

		listString = new TreeSet<String>();
		listValue = new TreeSet<Integer>();
		for (Map.Entry<Integer, String> m : map.entrySet()) {
			listString.add((String) m.getValue());
			listValue.add((Integer) m.getKey());
		}

		final Region spacer13 = new Region();
		spacer13.setMaxWidth(10);
		HBox.setHgrow(spacer13, Priority.ALWAYS);

		newLabel.setPadding(new Insets(10, 0, 0, 0));
		Label lbl = new Label(Constante.filtre);
		lbl.setPadding(new Insets(10, 0, 0, 0));
		SuggestionProvider<String> provider = SuggestionProvider.create(listString);
		new AutoCompletionTextFieldBinding<>(autoComplete, provider);
		autoComplete.setPrefWidth(250);
		autoComplete.setPrefHeight(40);
		autoComplete.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;-fx-border-color: #98bb68; -fx-border-radius: 5;");
		autoComplete.textProperty().addListener((observable, oldValue, newValue) -> {
			total = 0;
			textArea.setScrollTop(0);
			for (Map.Entry<Integer, String> mapentry : map.entrySet()) {
				if (mapentry.getValue().trim().equals(newValue.trim())) {
					newLabel.setText("Ligne " + mapentry.getKey());
					final String[] lines = textArea.getText().split("\n");
					for (Integer i = 0; i < mapentry.getKey(); i++) {
						final int numChars = lines[i].length();
						total += numChars;
					}

					final Rectangle2D lineBounds = ((TextAreaSkin) textArea.getSkin()).getCharacterBounds(total);
					textArea.setScrollTop(lineBounds.getMinY());
				}
			}
			if (newValue != null) {
				if (newValue.isEmpty() || newValue.isBlank()) {
					newLabel.setText("");
				}
			}
		});

		lhb2.addAll(lbl, autoComplete);

		refresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				runTask(taskUpdateStage, progress);
				Platform.runLater(() -> {
					textArea.setText(readFileContents(file));
					count = 0;
					map = new HashMap<Integer, String>();
					treeTableView = createTreeTableView(file);
					treeTableView.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					treeTableView.setPrefWidth(755);
					layout.setLeft(treeTableView);
					listString = new TreeSet<String>();
					listValue = new TreeSet<Integer>();
					for (Map.Entry<Integer, String> m : map.entrySet()) {
						listString.add((String) m.getValue());
						listValue.add((Integer) m.getKey());
					}
					provider.clearSuggestions();
					provider.addPossibleSuggestions(listString);
				});
			}
		});

		final Region spacer1 = new Region();
		spacer1.setMaxWidth(800);
		HBox.setHgrow(spacer1, Priority.ALWAYS);

		final Region spacer12 = new Region();
		spacer12.setMaxWidth(50);
		HBox.setHgrow(spacer12, Priority.ALWAYS);

		listHint.addAll(hb, spacer12, hb2, spacer13, newLabel, spacer14, refresh, spacer1, hb1);

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
		stage.setTitle(Constante.button4);
		stage.setMaximized(true);

		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
					Number newSceneWidth) {
				if (newSceneWidth.doubleValue() >= 1509 && newSceneWidth.doubleValue() < 1632) { // ecran 16 pouces
					treeTableView.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");

					collapseAll.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #eee9da;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					refresh.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #eee9da;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelCount.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textArea.setPrefWidth(755);
					spacer1.setMaxWidth(700);
					newLabel.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					lbl.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
				}
				if (newSceneWidth.doubleValue() >= 1632 && newSceneWidth.doubleValue() >= 1728) { // ecran 17 pouces
					treeTableView.setStyle("-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");

					collapseAll.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #eee9da;-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelCount.setStyle("-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					refresh.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #eee9da;-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textArea.setPrefWidth(855);
					spacer1.setMaxWidth(800);
					newLabel.setStyle("-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					lbl.setStyle("-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
				} else if (newSceneWidth.doubleValue() < 1509) {
					treeTableView.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");

					collapseAll.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #eee9da;-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					refresh.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #eee9da;-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelCount.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textArea.setPrefWidth(555);
					spacer1.setMaxWidth(400);
					newLabel.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					lbl.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
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
			final Image image = new Image(getClass().getResourceAsStream("/image.png"));
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
		labelCount.setText(Integer.valueOf(count).toString() + " Fichier(s) de JDV   ");
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
			PositionalXMLReader positionalXMLReader = new PositionalXMLReader();
			final Document doc = positionalXMLReader.readXML(targetStream);
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
	 * expandTreeView
	 * 
	 * @param item
	 */
	private void expandTreeView(final TreeItem<?> item) {
		if (item != null && !item.isLeaf()) {
			item.setExpanded(true);
			for (TreeItem<?> child : item.getChildren()) {
				expandTreeView(child);
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

	/**
	 * loading in api
	 */
	public void runTask(final Stage taskUpdateStage, final ProgressIndicator progress) {
		Task<Void> longTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				final int max = 20;
				for (int i = 1; i <= max; i++) {
					if (isCancelled()) {
						break;
					}
					updateProgress(i, max);
					updateMessage(Constante.task_part + String.valueOf(i) + Constante.complete);

					Thread.sleep(100);
				}
				return null;
			}
		};

		longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(final WorkerStateEvent t) {
				taskUpdateStage.hide();
			}
		});
		progress.progressProperty().bind(longTask.progressProperty());
		taskUpdateStage.show();
		new Thread(longTask).start();
	}
}