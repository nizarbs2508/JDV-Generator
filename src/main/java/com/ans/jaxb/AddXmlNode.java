package com.ans.jaxb;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.CheckListView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.spire.xls.CellRange;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * convert JDV files
 * 
 * @author bensalem Nizar
 */
public class AddXmlNode extends Application {

	/**
	 * desktop
	 */
	private Desktop desktop = Desktop.getDesktop();
	/**
	 * files list
	 */
	private static List<File> files;
	/**
	 * files list
	 */
	private static List<File> filesB;

	/**
	 * files list to inster
	 */
	private static List<File> finalFiles;
	/**
	 * isOkGenerate xml file
	 */
	private boolean isOkGenerate = false;
	/**
	 * secondStage for treeview
	 */
	private Stage secondStage;
	/**
	 * BUFFER_SIZE
	 */
	private static final int BUFFER_SIZE = 4096;
	/**
	 * list view
	 */
	private static CheckListView<File> list;

	/**
	 * list view B
	 */
	private static CheckListView<File> listB;
	/**
	 * isOk
	 */
	private boolean isOk = false;
	/**
	 * selectAll
	 */
	private CheckBox selectAll = new CheckBox();
	/**
	 * labelSelectAll
	 */
	private Label labelSelectAll = new Label(Constante.selectAll);
	/**
	 * hb
	 */
	final static HBox hb = new HBox();
	/**
	 * list file local
	 */
	public static List<File> listF = new ArrayList<File>();
	/**
	 * size
	 */
	public static Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	/**
	 * width
	 */
	int width = (int) size.getWidth();
	/**
	 * primaryStage
	 */
	public Stage primaryStage = new Stage();

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

	/**
	 * start javafx main
	 */
	public void start(final Stage stage) {
		hb.setVisible(false);
		selectAll.setSelected(false);
		list = new CheckListView<File>();
		listB = new CheckListView<File>();
		finalFiles = new ArrayList<File>();
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

		final ImageView imgView = new ImageView("UIControls/multiple_files.png");
		imgView.setFitWidth(20);
		imgView.setFitHeight(20);

		final ImageView imgValid = new ImageView("UIControls/check.jpg");
		imgValid.setFitWidth(20);
		imgValid.setFitHeight(20);

		final ImageView imgRead = new ImageView("UIControls/lisezmoi.png");
		imgRead.setFitWidth(20);
		imgRead.setFitHeight(20);

		final Menu file = new Menu(Constante.openFile);
		file.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
		final MenuItem item = new MenuItem(Constante.openToFile, imgView);
		final MenuItem item1 = new MenuItem(Constante.validateFile, imgValid);
		item.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
		item1.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
		file.getItems().addAll(item, item1);

		final Menu apropos = new Menu(Constante.apropos);
		apropos.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
		final MenuItem item2 = new MenuItem(Constante.lisezmoi, imgRead);
		item2.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
		apropos.getItems().addAll(item2);

		// Creating a File chooser
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(Constante.chooseFile);
		fileChooser.getExtensionFilters()
				.addAll(new ExtensionFilter("ALL Files", "*.xml*", "*.xlsx*", "*.xlsm*", "*.rdf*"));
		files = new ArrayList<File>();
		filesB = new ArrayList<File>();
		list.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		listB.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		final Label labelEmpty = new Label(Constante.empty1);
		labelEmpty.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
		list.setPlaceholder(labelEmpty);
		list.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
		list.setPrefHeight(550);
		list.setMaxHeight(550);
		list.setMinHeight(550);
		list.setPrefWidth((width - 10) / 2);
		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		final Label labelEmpty1 = new Label(Constante.empty);
		labelEmpty1.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
		listB.setPlaceholder(labelEmpty1);
		listB.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
		listB.setPrefHeight(550);
		listB.setMaxHeight(550);
		listB.setMinHeight(550);
		listB.setPrefWidth((width - 10) / 2);
		listB.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		// Adding action on the menu item
		item.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				// Opening a dialog box
				final List<File> file = fileChooser.showOpenMultipleDialog(stage);
				if (file != null) {
					files.addAll(file);
					ObservableList<File> oblist = FXCollections.observableArrayList();
					for (int i = 0; i < files.size(); i++) {
						oblist.add(files.get(i));
					}
					list.setItems(oblist);
					list.getCheckModel().checkAll();
					if (finalFiles == null) {
						finalFiles = new ArrayList<File>();
					}
					finalFiles.addAll(list.getItems());

					list.setCellFactory(lv -> new CheckBoxListCell<File>(list::getItemBooleanProperty) {
						@Override
						public void updateItem(File employee, boolean empty) {
							super.updateItem(employee, empty);
							setText(employee == null ? "" : String.format(employee.getAbsolutePath()));
						}
					});
				}
			}
		});

		// Adding action on the menu item1
		item1.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				// Opening a dialog box
				final List<File> file = fileChooser.showOpenMultipleDialog(stage);
				final List<File> fileMalFormed = new ArrayList<File>();
				if (file != null) {
					for (int i = 0; i < file.size(); i++) {
						try {
							final DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
							final DocumentBuilder builder = dBF.newDocumentBuilder();
							final InputSource is = new InputSource(file.get(i).getAbsolutePath());
							builder.parse(is);
						} catch (final Exception e) {
							fileMalFormed.add(file.get(i));
						}
					}

					if (fileMalFormed.size() > 0) {
						String name = "";
						for (int i = 0; i < fileMalFormed.size(); i++) {
							name = name + '\n' + fileMalFormed.get(i).getName();
						}
						final Alert alert = new Alert(AlertType.ERROR);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
						dialogPane.getStyleClass().add("myDialog");
						dialogPane.setMinHeight(120 * fileMalFormed.size());
						dialogPane.setMaxHeight(120 * fileMalFormed.size());
						dialogPane.setPrefHeight(120 * fileMalFormed.size());
						alert.setContentText(Constante.alert10 + '\n' + name);
						alert.setHeaderText(null);
						alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
						alert.showAndWait();
					} else {
						final Alert alert = new Alert(AlertType.INFORMATION);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
						dialogPane.getStyleClass().add("myDialog");
						dialogPane.setMinHeight(130);
						dialogPane.setMaxHeight(130);
						dialogPane.setPrefHeight(130);
						alert.setContentText(Constante.alert11);
						alert.setHeaderText(null);
						alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
						alert.showAndWait();
					}
				}
			}
		});

		// Adding action on the menu item2
		item2.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Platform.runLater(() -> {
					final File file = new File(getClass().getResource("/Lisez-moi.md").getFile());
					VBox root = new VBox();
					root.setPadding(new Insets(10));
					root.setSpacing(5);
					TextArea textArea = new TextArea();
					textArea.setEditable(false);
					textArea.setPrefHeight(Integer.MAX_VALUE);
					textArea.setPrefWidth(Integer.MAX_VALUE);
					textArea.setStyle("-fx-background-color: #A9A9A9;");
					textArea.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
					try {
						textArea.setText(readFileContents(file));
						root.getChildren().add(textArea);
						Scene scene = new Scene(root);
						primaryStage.setTitle(Constante.lisezmoi);
						primaryStage.setScene(scene);
						primaryStage.setMaximized(true);
						primaryStage.show();
					} catch (final IOException e) {
						e.printStackTrace();
					}

				});
			}
		});

		// Creating a menu bar and adding menu to it.
		final MenuBar menuBar = new MenuBar(file, apropos);
		final Button button1 = new Button(Constante.button1);
		final Button button10 = new Button(Constante.button10);
		final Button buttonDownload = new Button(Constante.buttonDownload);
		final Button buttonTermino = new Button(Constante.buttonTermino);

		final Image ansImage = new Image(AddXmlNode.class.getResource("/ans01.jpg").toExternalForm());
		// creating ImageView for adding image
		final ImageView imageView = new ImageView();
		imageView.setImage(ansImage);
		imageView.setFitWidth(120);
		imageView.setFitHeight(80);
		imageView.setPreserveRatio(true);
		imageView.setSmooth(true);

		// creating HBox to add imageview
		final HBox hBoxImg = new HBox();
		hBoxImg.getChildren().addAll(imageView);
		hBoxImg.setStyle("-fx-background-color: white;");

		final Region spacer1 = new Region();
		spacer1.setMaxWidth(10);
		HBox.setHgrow(spacer1, Priority.ALWAYS);

		final Region spacer2 = new Region();
		spacer2.setMaxWidth(10);
		HBox.setHgrow(spacer2, Priority.ALWAYS);

		final Region spacer3 = new Region();
		spacer3.setMaxWidth(10);
		HBox.setHgrow(spacer3, Priority.ALWAYS);

		final Region spacer4 = new Region();
		spacer4.setMaxWidth(10);
		HBox.setHgrow(spacer4, Priority.ALWAYS);

		final Region spacer5 = new Region();
		spacer5.setMaxWidth(10);
		HBox.setHgrow(spacer5, Priority.ALWAYS);

		final Region spacer9 = new Region();
		spacer9.setMaxWidth(10);
		HBox.setHgrow(spacer9, Priority.ALWAYS);

		final Region spacer7 = new Region();
		spacer7.setMaxWidth(10);
		HBox.setHgrow(spacer7, Priority.ALWAYS);

		final Region spacer8 = new Region();
		spacer8.setMaxWidth(10);
		HBox.setHgrow(spacer8, Priority.ALWAYS);

		final Region spacer20 = new Region();
		spacer20.setMaxWidth(10);
		HBox.setHgrow(spacer20, Priority.ALWAYS);

		final Region spacer11 = new Region();
		spacer11.setMaxWidth(10);
		HBox.setHgrow(spacer11, Priority.ALWAYS);

		final Region spacer12 = new Region();
		spacer12.setMaxWidth(10);
		HBox.setHgrow(spacer12, Priority.ALWAYS);

		final Button button2 = new Button(Constante.button2);

		final Button button4 = new Button(Constante.button4);

		final Button button3 = new Button(Constante.button3);

		final Region spacer15 = new Region();
		spacer15.setMaxWidth(10);
		HBox.setHgrow(spacer15, Priority.ALWAYS);

		final HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 0, 5, 0));
		ObservableList<Node> listHint = hbox.getChildren();
		listHint.addAll(buttonDownload, spacer15, button1, spacer5, button2, spacer7, button4, spacer11, button10,
				spacer8, buttonTermino, spacer20, button3);
		hbox.setAlignment(Pos.CENTER);

		final HBox hbox1 = new HBox();
		hbox1.setPadding(new Insets(15, 0, 0, 0));

		final Label label = new Label();
		label.setText(Constante.label);
		label.setPadding(new Insets(5, 5, 5, 5));
		label.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		DateTimePicker picker = new DateTimePicker();
		picker.setPadding(new Insets(5, 5, 5, 5));
		LocalDateTime localDateTime = LocalDateTime.of(2021, Month.MARCH, 15, 00, 00, 00);
		picker.setDateTimeValue(localDateTime);
		picker.setPrefWidth(190);
		picker.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
		picker.setEditable(false);

		final Label label1 = new Label();
		label1.setText(Constante.label1);
		label1.setPadding(new Insets(5, 5, 5, 5));
		label1.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final ComboBox<String> comboBox = new ComboBox<String>();
		ObservableList<String> options = FXCollections.observableArrayList(Constante.finale, Constante.partiel,
				Constante.finaletronque, Constante.completed, Constante.active, Constante.aborted);
		comboBox.setItems(options);
		comboBox.getSelectionModel().select(0);
		comboBox.setPadding(new Insets(5, 5, 5, 5));
		comboBox.setPrefWidth(140);
		comboBox.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
		comboBox.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

		final Label label2 = new Label();
		label2.setText(Constante.label2);
		label2.setPadding(new Insets(5, 5, 5, 5));
		label2.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final TextField textField2 = new TextField();
		textField2.setText(Constante.textField2);
		textField2.setPrefWidth(40);
		textField2.setPadding(new Insets(5, 5, 5, 5));
		textField2.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final Label labelL = new Label();
		labelL.setText(Constante.labelL);
		labelL.setPadding(new Insets(5, 5, 5, 5));
		labelL.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final TextField textFieldL = new TextField();
		textFieldL.setPadding(new Insets(5, 5, 5, 5));
		textFieldL.setText(Constante.textFieldL);
		textFieldL.setPrefWidth(40);
		textFieldL.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final Label labelT = new Label();
		labelT.setText(Constante.labelT);
		labelT.setPadding(new Insets(5, 5, 5, 5));
		labelT.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final TextField textFieldT = new TextField();
		textFieldT.setPadding(new Insets(5, 5, 5, 5));
		textFieldT.setText(Constante.textFieldT);
		textFieldT.setPrefWidth(40);
		textFieldT.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final Label labelP = new Label();
		labelP.setText(Constante.labelP);
		labelP.setPadding(new Insets(5, 5, 5, 5));
		labelP.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final TextField textFieldP = new TextField();
		textFieldP.setPadding(new Insets(5, 5, 5, 5));
		textFieldP.setText(Constante.textFieldP);
		textFieldP.setPrefWidth(180);
		textFieldP.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final Label labelPS = new Label();
		labelPS.setText(Constante.labelPS);
		labelPS.setPadding(new Insets(5, 5, 5, 5));
		labelPS.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final TextField textFieldPS = new TextField();
		textFieldPS.setPadding(new Insets(5, 5, 5, 5));
		textFieldPS.setText(Constante.textFieldPS);
		textFieldPS.setPrefWidth(120);
		textFieldPS.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final Label labelUrl = new Label();
		labelUrl.setText(Constante.labelUrl);
		labelUrl.setPadding(new Insets(5, 5, 5, 5));
		labelUrl.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final TextField textFieldUrl = new TextField();
		textFieldUrl.setPadding(new Insets(5, 5, 5, 5));
		textFieldUrl.setText(Constante.textFieldUrl);
		textFieldUrl.setPrefWidth(685);
		textFieldUrl.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");

		final ObservableList<Node> listHint1 = hbox1.getChildren();
		listHint1.addAll(label, picker, spacer1, label1, comboBox, spacer2, label2, textField2, spacer3, labelL,
				textFieldL, spacer4, labelT, textFieldT);
		hbox1.setAlignment(Pos.BASELINE_CENTER);

		final HBox hbox2 = new HBox();
		hbox2.setPadding(new Insets(15, 0, 0, 0));
		ObservableList<Node> listHint2 = hbox2.getChildren();
		listHint2.addAll(labelP, textFieldP, spacer9, labelPS, textFieldPS);
		hbox2.setAlignment(Pos.BASELINE_CENTER);

		final HBox hbox3 = new HBox();
		hbox3.setPadding(new Insets(15, 0, 15, 0));
		ObservableList<Node> listHint3 = hbox3.getChildren();
		listHint3.addAll(labelUrl, textFieldUrl);
		hbox3.setAlignment(Pos.BASELINE_CENTER);

		button2.setPrefWidth(220);
		button2.setPrefHeight(30);
		button2.setMinHeight(30);
		button2.setMaxHeight(30);
		button2.setStyle(
				"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
		button2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				if (new File(textFieldP.getText()).exists()) {
					openFile(new File(textFieldP.getText()));
				} else {
					final Alert alert = new Alert(AlertType.ERROR);
					final DialogPane dialogPane = alert.getDialogPane();
					dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
					dialogPane.getStyleClass().add("myDialog");
					dialogPane.setMinHeight(130);
					dialogPane.setMaxHeight(130);
					dialogPane.setPrefHeight(130);
					alert.setContentText(Constante.alert);
					alert.setHeaderText(null);
					alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
					alert.showAndWait();
				}
			}
		});

		buttonTermino.setPrefWidth(220);
		buttonTermino.setPrefHeight(30);
		buttonTermino.setMinHeight(30);
		buttonTermino.setMaxHeight(30);
		buttonTermino.setStyle(
				"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
		buttonTermino.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				if (files != null) {
					if (!files.isEmpty() && getExtension(files.get(0).getAbsolutePath()).equals(Constante.rdf)) {
						runTask(taskUpdateStage, progress);
						Platform.runLater(() -> {
							Document doc = null;
							for (File file : files) {
								List<String> listStr = new ArrayList<String>();
								final String fileName = file.getName();
								String fileDestName = null;
								if (fileName.toUpperCase().contains(Constante.cis)
										&& !fileName.toUpperCase().contains(Constante.cisp2)) {
									fileDestName = Constante.cisdest;
								} else if (fileName.toUpperCase().contains(Constante.cip)) {
									fileDestName = Constante.cip;
								} else if (fileName.toUpperCase().contains(Constante.ccam)) {
									fileDestName = Constante.ccamdest;
								} else if (fileName.toUpperCase().contains(Constante.adicap)) {
									fileDestName = Constante.adicapdest;
								} else if (fileName.toUpperCase().contains(Constante.medicabase)) {
									fileDestName = Constante.medicabasedest;
								} else if (fileName.toUpperCase().contains(Constante.ucd)) {
									fileDestName = Constante.ucddest;
								} else if (fileName.toUpperCase().contains(Constante.cladimed)) {
									fileDestName = Constante.cladimeddest;
								} else if (fileName.toUpperCase().contains(Constante.emdn)) {
									fileDestName = Constante.emdndest;
								} else if (fileName.toUpperCase().contains(Constante.loinc)) {
									fileDestName = Constante.loincdest;
								} else if (fileName.toUpperCase().contains(Constante.cim10)) {
									fileDestName = Constante.cim10dest;
								} else if (fileName.toUpperCase().contains(Constante.cisp2)) {
									fileDestName = Constante.cisp2dest;
								} else if (fileName.toUpperCase().contains(Constante.atc)) {
									fileDestName = Constante.atcdest;
								} else if (fileName.toUpperCase().contains(Constante.ucum)) {
									fileDestName = Constante.ucumdest;
								}
								try {
									doc = parseXML(file.getAbsolutePath());
									if (doc != null) {
										NodeList nList = doc.getElementsByTagName(Constante.notation);
										for (int i = 0; i < nList.getLength(); i++) {
											org.w3c.dom.Node nNode = (org.w3c.dom.Node) nList.item(i);
											Element eElement = (Element) nNode;
											listStr.add(eElement.getFirstChild().getTextContent());
										}
									}
									org.w3c.dom.Node node = removeAllChildren(doc.getFirstChild());
									for (String str : listStr) {
										org.w3c.dom.Node newNode = null;
										newNode = doc.createElement(Constante.description);
										node.appendChild(newNode);
										org.w3c.dom.Node secondNode = null;
										secondNode = doc.createElement(Constante.notation);
										secondNode.setTextContent(str);
										newNode.appendChild(secondNode);
									}
									doc.normalize();
									prettyPrint(doc, new File(textFieldPS.getText()).getParent(), fileDestName);

								} catch (final ParserConfigurationException e) {
									e.printStackTrace();
								} catch (final SAXException e) {
									e.printStackTrace();
								} catch (final IOException e) {
									e.printStackTrace();
								} catch (final Exception e) {
									e.printStackTrace();
								}

							}
							final Alert alert = new Alert(AlertType.INFORMATION);
							final DialogPane dialogPane = alert.getDialogPane();
							dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
							dialogPane.getStyleClass().add("myDialog");
							dialogPane.setMinHeight(130);
							dialogPane.setMaxHeight(130);
							dialogPane.setPrefHeight(130);
							alert.setContentText(Constante.alert2);
							alert.setHeaderText(null);
							alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
							alert.showAndWait();
							files = new ArrayList<File>();
							finalFiles = new ArrayList<File>();
							list.getItems().clear();
							list.getCheckModel().clearChecks();
						});
					} else {
						final Alert alert = new Alert(AlertType.ERROR);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
						dialogPane.getStyleClass().add("myDialog");
						dialogPane.setMinHeight(130);
						dialogPane.setMaxHeight(130);
						dialogPane.setPrefHeight(130);
						alert.setContentText(Constante.alert8);
						alert.setHeaderText(null);
						alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
						alert.showAndWait();
					}
				} else {
					final Alert alert = new Alert(AlertType.ERROR);
					final DialogPane dialogPane = alert.getDialogPane();
					dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
					dialogPane.getStyleClass().add("myDialog");
					dialogPane.setMinHeight(130);
					dialogPane.setMaxHeight(130);
					dialogPane.setPrefHeight(130);
					alert.setContentText(Constante.alert8);
					alert.setHeaderText(null);
					alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
					alert.showAndWait();
				}
			}
		});

		buttonDownload.setPrefWidth(250);
		buttonDownload.setPrefHeight(30);
		buttonDownload.setMinHeight(30);
		buttonDownload.setMaxHeight(30);
		buttonDownload.setStyle(
				"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
		buttonDownload.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				final String url = textFieldUrl.getText();
				runTask(taskUpdateStage, progress);
				Platform.runLater(() -> {
					final String home = System.getProperty("user.home");
					final File file = new File(home + "/Downloads/" + Constante.urlFile);
					try {
						downloadUsingNIO(url, file.getAbsolutePath());
						isOk = true;
					} catch (final IOException e) {
						e.printStackTrace();
						final Alert alert = new Alert(AlertType.ERROR);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
						dialogPane.getStyleClass().add("myDialog");
						dialogPane.setMinHeight(150);
						dialogPane.setMaxHeight(150);
						dialogPane.setPrefHeight(150);
						alert.setContentText(Constante.alert6);
						alert.setHeaderText(null);
						alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
						alert.showAndWait();
						isOk = false;
					}
					if (isOk == true) {
						final Alert alert = new Alert(AlertType.INFORMATION);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
						dialogPane.getStyleClass().add("myDialog");
						dialogPane.setMinHeight(150);
						dialogPane.setMaxHeight(150);
						dialogPane.setPrefHeight(150);
						alert.setContentText(Constante.alert2);
						alert.setHeaderText(null);
						final ButtonType okButtonType = new ButtonType("OK");
						alert.getButtonTypes().setAll(okButtonType);
						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == okButtonType) {
							String ZipFilePath = file.getAbsolutePath();
							String DestFilePath = Constante.textFieldPS;
							try {
								unzip(ZipFilePath, DestFilePath);
								hb.setVisible(true);
								dialogPane.setVisible(false);
								alert.close();
							} catch (final IOException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
		});

		button4.setPrefWidth(270);
		button4.setPrefHeight(30);
		button4.setMinHeight(30);
		button4.setMaxHeight(30);
		button4.setStyle(
				"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
		button4.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				runTask(taskUpdateStage, progress);
				Platform.runLater(() -> {
					if (new File(textFieldP.getText()).exists()) {
						XMLView view = new XMLView();
						secondStage = view.start(textFieldP.getText());
					} else {
						final Alert alert = new Alert(AlertType.ERROR);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
						dialogPane.getStyleClass().add("myDialog");
						dialogPane.setMinHeight(130);
						dialogPane.setMaxHeight(130);
						dialogPane.setPrefHeight(130);
						alert.setContentText(Constante.alert);
						alert.setHeaderText(null);
						alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
						alert.showAndWait();
					}
				});
			}
		});

		button3.setPrefWidth(150);
		button3.setPrefHeight(30);
		button3.setMinHeight(30);
		button3.setMaxHeight(30);
		button3.setStyle(
				"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
		button3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isOkGenerate = false;
				files = new ArrayList<File>();
				filesB = new ArrayList<File>();
				finalFiles = new ArrayList<File>();
				list.getItems().clear();
				listB.getItems().clear();
				list.getCheckModel().clearChecks();
				listB.getCheckModel().clearChecks();
				selectAll.setSelected(false);
				hb.setVisible(false);
			}
		});

		button1.setPrefWidth(160);
		button1.setPrefHeight(30);
		button1.setMinHeight(30);
		button1.setMaxHeight(30);
		button1.setStyle(
				"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
		button1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				if (finalFiles.isEmpty()) {
					final Alert alert = new Alert(AlertType.ERROR);
					final DialogPane dialogPane = alert.getDialogPane();
					dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
					dialogPane.getStyleClass().add("myDialog");
					dialogPane.setMinHeight(130);
					dialogPane.setMaxHeight(130);
					dialogPane.setPrefHeight(130);
					alert.setContentText(Constante.alert1);
					alert.setHeaderText(null);
					alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
					alert.showAndWait();
				} else if (!finalFiles.isEmpty() && finalFiles.size() == 1
						&& (getExtension(finalFiles.get(0).getAbsolutePath()).equals(Constante.xlsx)
								|| getExtension(finalFiles.get(0).getAbsolutePath()).equals(Constante.xlsm))) {
					final Alert alert = new Alert(AlertType.ERROR);
					final DialogPane dialogPane = alert.getDialogPane();
					dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
					dialogPane.getStyleClass().add("myDialog");
					dialogPane.setMinHeight(130);
					dialogPane.setMaxHeight(130);
					dialogPane.setPrefHeight(130);
					alert.setContentText(Constante.alert1);
					alert.setHeaderText(null);
					alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
					alert.showAndWait();
				}

				else {
					runTask(taskUpdateStage, progress);
					Document doc = null;
					final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setIgnoringElementContentWhitespace(true);
					DocumentBuilder db = null;
					try {
						db = dbf.newDocumentBuilder();
					} catch (final ParserConfigurationException e) {
						e.printStackTrace();
					}
					final Document document = db.newDocument();
					// add elements to Document
					final Element rootElement = document.createElement("Racine");
					// append root element to document
					document.appendChild(rootElement);
					for (File file : finalFiles) {
						if (getExtension(file.getAbsolutePath()).equals(Constante.xml)) {
							try (InputStream is = new FileInputStream(file)) {
								doc = db.parse(is);

								final NodeList nodes = doc.getElementsByTagName("ConceptList");
								if (nodes != null) {
									for (int h = 0; h < nodes.getLength(); h++) {
										if (nodes.item(h) instanceof Element) {
											Element elem = (Element) nodes.item(h);
											doc.renameNode(elem, elem.getNamespaceURI(), "conceptList");
										}
									}
								}
								NodeList nodesC = doc.getElementsByTagName("Concept");
								if (nodesC != null) {
									for (int n = 0; n < nodesC.getLength(); n++) {
										if (nodesC.item(n) instanceof Element) {
											Element elem = (Element) nodesC.item(n);
											doc.renameNode(elem, elem.getNamespaceURI(), "concept");
										}
									}
								}
								NodeList listOfStaff = doc.getElementsByTagName("ValueSet");
								if (listOfStaff != null) {
									for (int i = 0; i < listOfStaff.getLength(); i++) {
										if (listOfStaff.item(i) instanceof Element) {
											Element elem = (Element) listOfStaff.item(i);
											doc.renameNode(elem, elem.getNamespaceURI(), "valueSet");
										}

										org.w3c.dom.Node staff = listOfStaff.item(i);
										org.w3c.dom.Node importedNode = document.importNode(staff, true);
										String key = importedNode.getAttributes().getNamedItem("displayName")
												.getNodeValue();
										if (key.contains(".tabs")) {
											key = key.replace(".tabs", "");
										}
										((Element) importedNode).setAttribute("name", key);
										((Element) importedNode).setAttribute("displayName", key);

										DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
										String formatDateTime = picker.getDateTimeValue().format(format);
										String dateFinal;
										String[] words = formatDateTime.split(" ");
										dateFinal = words[0] + "T" + words[1];

										((Element) importedNode).setAttribute("effectiveDate", dateFinal);
										((Element) importedNode).setAttribute("statusCode",
												comboBox.getSelectionModel().getSelectedItem());
										if (textField2 != null) {
											if (textField2.getText() != null) {
												((Element) importedNode).setAttribute("versionLabel",
														textField2.getText());
											} else {
												((Element) importedNode).setAttribute("versionLabel", "");
											}
										} else {
											((Element) importedNode).setAttribute("versionLabel", "");
										}

										org.w3c.dom.Node att = importedNode.getAttributes().getNamedItem("version");
										if (att != null) {
											importedNode.getAttributes().removeNamedItem(att.getNodeName());
										}
										org.w3c.dom.Node att1 = importedNode.getAttributes().getNamedItem("dateFin");
										if (att1 != null) {
											importedNode.getAttributes().removeNamedItem(att1.getNodeName());
										}
										org.w3c.dom.Node att2 = importedNode.getAttributes().getNamedItem("dateMaj");
										if (att2 != null) {
											importedNode.getAttributes().removeNamedItem(att2.getNodeName());
										}
										org.w3c.dom.Node att3 = importedNode.getAttributes().getNamedItem("dateValid");
										if (att3 != null) {
											importedNode.getAttributes().removeNamedItem(att3.getNodeName());
										}
										org.w3c.dom.Node att4 = importedNode.getAttributes()
												.getNamedItem("description");
										if (att4 != null) {
											importedNode.getAttributes().removeNamedItem(att4.getNodeName());
										}
										org.w3c.dom.Node att5 = importedNode.getAttributes()
												.getNamedItem("typeFichier");
										if (att5 != null) {
											importedNode.getAttributes().removeNamedItem(att5.getNodeName());
										}
										org.w3c.dom.Node att6 = importedNode.getAttributes().getNamedItem("urlFichier");
										if (att6 != null) {
											importedNode.getAttributes().removeNamedItem(att6.getNodeName());
										}

										NodeList list = importedNode.getChildNodes();
										if (list != null) {
											for (int j = 0; j < list.getLength(); j++) {
												org.w3c.dom.Node staf = list.item(j);
												NodeList listk = staf.getChildNodes();
												if (listk != null) {
													for (int k = 0; k < listk.getLength(); k++) {
														org.w3c.dom.Node item = listk.item(k);
														if (item.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
															if (textFieldL != null) {
																if (textFieldL.getText() != null) {
																	((Element) item).setAttribute("level",
																			textFieldL.getText());
																} else {
																	((Element) item).setAttribute("level", "");
																}
															} else {
																((Element) item).setAttribute("level", "");
															}
															if (textFieldT != null) {
																if (textFieldT.getText() != null) {
																	((Element) item).setAttribute("type",
																			textFieldT.getText());
																} else {
																	((Element) item).setAttribute("type", "");
																}
															} else {
																((Element) item).setAttribute("type", "");
															}
															org.w3c.dom.Node att7 = item.getAttributes()
																	.getNamedItem("dateFin");
															if (att7 != null) {
																item.getAttributes()
																		.removeNamedItem(att7.getNodeName());
															}
															org.w3c.dom.Node att8 = item.getAttributes()
																	.getNamedItem("dateValid");
															if (att8 != null) {
																item.getAttributes()
																		.removeNamedItem(att8.getNodeName());
															}

														}
													}
												}
											}
										}
										rootElement.appendChild(importedNode);
									}
								}

							} catch (final FileNotFoundException e) {
								e.printStackTrace();
								init(file);

							} catch (final IOException e) {
								e.printStackTrace();
								init(file);

							} catch (final SAXException e) {
								e.printStackTrace();
								init(file);
							}
						}
					}

					if (!finalFiles.isEmpty()) {
						try (FileOutputStream output = new FileOutputStream(textFieldP.getText())) {
							writeXmlGenerate(document, output);

							if (isOkGenerate == true) {
								final Alert alert = new Alert(AlertType.INFORMATION);
								final DialogPane dialogPane = alert.getDialogPane();
								dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
								dialogPane.getStyleClass().add("myDialog");
								dialogPane.setMinHeight(130);
								dialogPane.setMaxHeight(130);
								dialogPane.setPrefHeight(130);
								alert.setContentText(Constante.alert2);
								alert.setHeaderText(null);
								alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
								alert.showAndWait();
								isOkGenerate = false;
							}
						} catch (final FileNotFoundException e) {
							e.printStackTrace();
						} catch (final IOException e) {
							e.printStackTrace();
						} catch (final TransformerException e) {
							e.printStackTrace();
						}
					}
					files = new ArrayList<File>();
					filesB = new ArrayList<File>();
					finalFiles = new ArrayList<File>();
					list.getItems().clear();
					listB.getItems().clear();
					list.getCheckModel().clearChecks();
					listB.getCheckModel().clearChecks();
					selectAll.setSelected(false);
					hb.setVisible(false);

				}
			}
		});

		final Region spacer16 = new Region();
		spacer16.setMaxWidth(5);
		HBox.setHgrow(spacer16, Priority.ALWAYS);

		final Region spacer17 = new Region();
		spacer17.setMaxWidth(5);
		HBox.setHgrow(spacer17, Priority.ALWAYS);

		hb.setPadding(new Insets(0, 0, 0, 8));
		ObservableList<Node> listHb = hb.getChildren();
		listHb.addAll(selectAll, spacer16, labelSelectAll);

		Platform.runLater(() -> {
			selectAll.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (finalFiles == null) {
						finalFiles = new ArrayList<File>();
					}
					if (newValue != null) {
						if (selectAll.isSelected()) {
							listB.getCheckModel().checkAll();
						} else {
							listB.getCheckModel().clearChecks();
						}
					}
				}
			});
		});

		BorderPane pane = new BorderPane();
		pane.setPrefWidth(width);
		pane.setLeft(listB);
		pane.setRight(list);

		final SplitPane sp = new SplitPane();
		sp.setStyle("-fx-box-border: 0px;");
		sp.setOrientation(Orientation.HORIZONTAL);
		sp.setDividerPositions(0f, 0.9f);

		final VBox root1 = new VBox(hbox1, hbox2, hbox3);
		sp.getItems().addAll(hBoxImg, root1);

		final VBox root = new VBox(menuBar, sp, hbox, hb, pane);

		button10.setPrefWidth(130);
		button10.setPrefHeight(30);
		button10.setMinHeight(30);
		button10.setMaxHeight(30);
		button10.setStyle(
				"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
		button10.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {

				runTask(taskUpdateStage, progress);
				Platform.runLater(() -> {
					if (files.isEmpty()) {
						final Alert alert = new Alert(AlertType.ERROR);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
						dialogPane.getStyleClass().add("myDialog");
						dialogPane.setMinHeight(130);
						dialogPane.setMaxHeight(130);
						dialogPane.setPrefHeight(130);
						alert.setContentText(Constante.alert3);
						alert.setHeaderText(null);
						alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
						alert.showAndWait();
					} else {
						for (File file : files) {
							String ext = getExtension(file.getAbsolutePath());
							if (ext.equals(Constante.xlsm) || ext.equals(Constante.xlsx)) {
								// Create a Workbook instance
								Workbook workbook = new Workbook();
								// Load an Excel file
								workbook.loadFromFile(file.getAbsolutePath());
								Worksheet worksheet = workbook.getWorksheets().get(3);
								Worksheet worksheet1 = workbook.getWorksheets().get(6);
								List<RetrieveValueSetResponse> listR = new ArrayList<RetrieveValueSetResponse>();
								// Get the row count
								int maxRow = worksheet.getLastRow();
								// Get the column count
								int maxColumn = worksheet.getLastColumn();
								// Loop through the rows
								for (int row = 2; row <= maxRow; row++) {
									boolean hide = worksheet.getRowIsHide(row);
									if (hide == false) {
										RetrieveValueSetResponse response = new RetrieveValueSetResponse();
										// Loop through the columns
										for (int col = 1; col <= maxColumn; col++) {
											// Get the current cell
											CellRange cell = worksheet.getCellRange(row, col);
											if (cell.getCellStyle().getExcelFont().isStrikethrough() == false) {
												if (col == 1) {
													response.setValueSetOID(cell.getValue());
												}
												if (col == 2) {
													response.setValueSetName(cell.getValue());
												}
												if (col == 3) {
													response.setCode(cell.getValue());
												}
												if (col == 4) {
													response.setDisplayName(cell.getValue());
												}
												if (col == 5) {
													response.setCodeSystemName(cell.getValue());
												}
												if (col == 6) {
													response.setCodeSystem(cell.getValue());
												}
												if (col == 7) {
													response.setDateDebut(cell.getValue());
												}
												if (col == 8) {
													response.setDateFin(cell.getValue());
												}
											} else {
												break;
											}
										}
										if (response.getValueSetOID() != null) {
											listR.add(response);
										}
									}
								}

								// Get the row count
								int maxRow1 = worksheet1.getLastRow();
								// Get the column count
								int maxColumn1 = worksheet1.getLastColumn();
								// Loop through the rows
								for (int row = 2; row <= maxRow1; row++) {
									boolean hide = worksheet1.getRowIsHide(row);
									if (hide == false) {
										RetrieveValueSetResponse response = new RetrieveValueSetResponse();
										// Loop through the columns
										for (int col = 1; col <= maxColumn1; col++) {
											// Get the current cell
											CellRange cell = worksheet1.getCellRange(row, col);
											if (cell.getCellStyle().getExcelFont().isStrikethrough() == false) {
												if (col == 1) {
													response.setValueSetOID(cell.getValue());
												}
												if (col == 2) {
													response.setValueSetName(cell.getValue());
												}
												if (col == 3) {
													response.setCode(cell.getValue());
												}
												if (col == 4) {
													response.setDisplayName(cell.getValue());
												}
												if (col == 5) {
													response.setCodeSystemName(cell.getValue());
												}
												if (col == 6) {
													response.setCodeSystem(cell.getValue());
												}
												if (col == 7) {
													response.setDateDebut(cell.getValue());
												}
												if (col == 8) {
													response.setDateFin(cell.getValue());
												}
											} else {
												break;
											}
										}
										if (response.getValueSetOID() != null) {
											listR.add(response);
										}
									}
								}

								Boolean isOk = false;
								Map<String, List<RetrieveValueSetResponse>> resultMap = listR.stream()
										.collect(Collectors.groupingBy(RetrieveValueSetResponse::getValueSetOID));

								Iterator<?> iterator = resultMap.entrySet().iterator();
								while (iterator.hasNext()) {
									@SuppressWarnings("rawtypes")
									Map.Entry mapentry = (Map.Entry) iterator.next();
									@SuppressWarnings("unchecked")
									List<RetrieveValueSetResponse> result2 = new ArrayList<RetrieveValueSetResponse>(
											(List<RetrieveValueSetResponse>) mapentry.getValue());
									isOk = CreateXMLFile.createXMLFile(result2, textFieldPS.getText());
									File f = CreateXMLFile.getCreatedFile();
									listF.add(f);
								}
								if (isOk == true) {
									final Alert alert = new Alert(AlertType.INFORMATION);
									final DialogPane dialogPane = alert.getDialogPane();
									dialogPane.getStylesheets()
											.add(getClass().getResource("/style.css").toExternalForm());
									dialogPane.getStyleClass().add("myDialog");
									dialogPane.setMinHeight(130);
									dialogPane.setMaxHeight(130);
									dialogPane.setPrefHeight(130);
									alert.setContentText(Constante.alert2);
									alert.setHeaderText(null);
									alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
									alert.showAndWait();
									isOkGenerate = false;
									files = new ArrayList<File>();
									list.getItems().clear();

								}

								workbook.dispose();
							}
						}
						for (File pathname : listF) {
							files.add(pathname);
						}

						if (files != null) {
							ObservableList<File> oblist = FXCollections.observableArrayList();
							for (int i = 0; i < files.size(); i++) {
								oblist.add(files.get(i));
							}
							list.setItems(oblist);
							if (finalFiles == null) {
								finalFiles = new ArrayList<File>();
							}
							list.setCellFactory(lv -> new CheckBoxListCell<File>(list::getItemBooleanProperty) {
								@Override
								public void updateItem(File employee, boolean empty) {
									super.updateItem(employee, empty);
									setText(employee == null ? "" : String.format(employee.getAbsolutePath()));
								}
							});

							list.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<Integer>() {
								@Override
								public void onChanged(
										javafx.collections.ListChangeListener.Change<? extends Integer> c) {
									while (c.next()) {
										if (c.wasAdded()) {
											for (int i : c.getAddedSubList()) {
												if (!finalFiles.contains(list.getItems().get(i))) {
													finalFiles.add(list.getItems().get(i));
												}
											}
										}
										if (c.wasRemoved()) {
											for (int i : c.getRemoved()) {
												finalFiles.remove(list.getItems().get(i));
											}
										}
									}
								}
							});
							list.getCheckModel().checkAll();
						}
					}
				});
			}
		});

		list.setPrefHeight(650);

		final Scene scene = new Scene(root, Color.BEIGE);
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream(Constante.photo)));
		stage.setTitle("Convertisseur JDV");
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (secondStage != null) {
					if (secondStage.isShowing()) {
						secondStage.close();
					}
				}
				if (primaryStage != null) {
					if (primaryStage.isShowing()) {
						primaryStage.close();
					}
				}
			}
		});

		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
					Number newSceneWidth) {
				if (newSceneWidth.doubleValue() >= 1509 && newSceneWidth.doubleValue() < 1632) { // ecran 16 pouces
					button1.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button10.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button2.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button3.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button4.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					buttonDownload.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					buttonTermino.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					buttonDownload.setPrefWidth(250);
					buttonDownload.setPrefHeight(30);
					buttonDownload.setMinHeight(30);
					buttonDownload.setMaxHeight(30);
					buttonTermino.setPrefWidth(220);
					buttonTermino.setPrefHeight(30);
					buttonTermino.setMinHeight(30);
					buttonTermino.setMaxHeight(30);
					button1.setPrefWidth(160);
					button1.setPrefHeight(30);
					button1.setMinHeight(30);
					button1.setMaxHeight(30);
					button10.setPrefWidth(130);
					button10.setPrefHeight(30);
					button10.setMinHeight(30);
					button10.setMaxHeight(30);
					button2.setPrefWidth(220);
					button2.setPrefHeight(30);
					button2.setMinHeight(30);
					button2.setMaxHeight(30);
					button3.setPrefWidth(150);
					button3.setPrefHeight(30);
					button3.setMinHeight(30);
					button3.setMaxHeight(30);
					button4.setPrefWidth(270);
					button4.setPrefHeight(30);
					button4.setMinHeight(30);
					button4.setMaxHeight(30);
					list.setPrefWidth((newSceneWidth.doubleValue() - 10) / 2);
					listB.setPrefWidth((newSceneWidth.doubleValue() - 10) / 2);
					pane.setPrefWidth(width);
					label.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					label1.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					label2.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textField2.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelL.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldL.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelT.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldT.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelP.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldP.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelPS.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldPS.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelUrl.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldUrl.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelEmpty.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					comboBox.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					picker.setStyle("-fx-font-size: 12; -fx-font-family: Verdana, Tahoma, sans-serif;");
					file.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
					imageView.setFitWidth(120);
					imageView.setFitHeight(120);
					file.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item1.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
					apropos.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item2.setStyle("-fx-font-size: 13; -fx-font-family: Verdana, Tahoma, sans-serif;");

				}
				if (newSceneWidth.doubleValue() >= 1632 && newSceneWidth.doubleValue() >= 1728) { // ecran 17 pouces
					button1.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button10.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button2.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button3.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button4.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					buttonDownload.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					buttonTermino.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 16; -fx-font-family: Verdana, Tahoma, sans-serif;");
					buttonDownload.setPrefWidth(280);
					buttonDownload.setPrefHeight(50);
					buttonDownload.setMinHeight(50);
					buttonDownload.setMaxHeight(50);
					buttonTermino.setPrefWidth(250);
					buttonTermino.setPrefHeight(50);
					buttonTermino.setMinHeight(50);
					buttonTermino.setMaxHeight(50);
					button1.setPrefWidth(180);
					button1.setPrefHeight(50);
					button1.setMinHeight(50);
					button1.setMaxHeight(50);
					button10.setPrefWidth(150);
					button10.setPrefHeight(50);
					button10.setMinHeight(50);
					button10.setMaxHeight(50);
					button2.setPrefWidth(250);
					button2.setPrefHeight(50);
					button2.setMinHeight(50);
					button2.setMaxHeight(50);
					button3.setPrefWidth(170);
					button3.setPrefHeight(50);
					button3.setMinHeight(50);
					button3.setMaxHeight(50);
					button4.setPrefWidth(300);
					button4.setPrefHeight(50);
					button4.setMinHeight(50);
					button4.setMaxHeight(50);
					list.setPrefWidth((newSceneWidth.doubleValue() - 10) / 2);
					listB.setPrefWidth((newSceneWidth.doubleValue() - 10) / 2);
					pane.setPrefWidth(width);
					label.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					label1.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					label2.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textField2.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelL.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldL.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelT.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldT.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelP.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldP.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelPS.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldPS.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelUrl.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldUrl.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelEmpty.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					comboBox.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					picker.setStyle("-fx-font-size: 14; -fx-font-family: Verdana, Tahoma, sans-serif;");
					file.setStyle("-fx-font-size: 15; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item.setStyle("-fx-font-size: 15; -fx-font-family: Verdana, Tahoma, sans-serif;");
					imageView.setFitWidth(140);
					imageView.setFitHeight(140);
					file.setStyle("-fx-font-size: 15; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item.setStyle("-fx-font-size: 15; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item1.setStyle("-fx-font-size: 15; -fx-font-family: Verdana, Tahoma, sans-serif;");
					apropos.setStyle("-fx-font-size: 15; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item2.setStyle("-fx-font-size: 15; -fx-font-family: Verdana, Tahoma, sans-serif;");

				} else if (newSceneWidth.doubleValue() < 1509) {
					button1.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button10.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button2.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button3.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					button4.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					buttonDownload.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					buttonTermino.setStyle(
							"-fx-border-color: #98bb68; -fx-border-radius: 5;-fx-background-color: #B0E0E6;-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					buttonDownload.setPrefWidth(200);
					buttonDownload.setPrefHeight(20);
					buttonDownload.setMinHeight(20);
					buttonDownload.setMaxHeight(20);
					buttonTermino.setPrefWidth(150);
					buttonTermino.setPrefHeight(20);
					buttonTermino.setMinHeight(20);
					buttonTermino.setMaxHeight(20);
					button1.setPrefWidth(120);
					button1.setPrefHeight(20);
					button1.setMinHeight(20);
					button1.setMaxHeight(20);
					button10.setPrefWidth(110);
					button10.setPrefHeight(20);
					button10.setMinHeight(20);
					button10.setMaxHeight(20);
					button2.setPrefWidth(180);
					button2.setPrefHeight(20);
					button2.setMinHeight(20);
					button2.setMaxHeight(20);
					button3.setPrefWidth(110);
					button3.setPrefHeight(20);
					button3.setMinHeight(20);
					button3.setMaxHeight(20);
					button4.setPrefWidth(250);
					button4.setPrefHeight(20);
					button4.setMinHeight(20);
					button4.setMaxHeight(20);
					list.setPrefWidth((newSceneWidth.doubleValue() - 10) / 2);
					listB.setPrefWidth((newSceneWidth.doubleValue() - 10) / 2);
					pane.setPrefWidth(width);
					label.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					label1.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					label2.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textField2.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelL.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldL.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelT.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldT.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelP.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldP.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelPS.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldPS.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelUrl.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					textFieldUrl.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					labelEmpty.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					comboBox.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					picker.setStyle("-fx-font-size: 10; -fx-font-family: Verdana, Tahoma, sans-serif;");
					file.setStyle("-fx-font-size: 11; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item.setStyle("-fx-font-size: 11; -fx-font-family: Verdana, Tahoma, sans-serif;");
					imageView.setFitWidth(100);
					imageView.setFitHeight(100);
					file.setStyle("-fx-font-size: 11; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item.setStyle("-fx-font-size: 11; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item1.setStyle("-fx-font-size: 11; -fx-font-family: Verdana, Tahoma, sans-serif;");
					apropos.setStyle("-fx-font-size: 11; -fx-font-family: Verdana, Tahoma, sans-serif;");
					item2.setStyle("-fx-font-size: 11; -fx-font-family: Verdana, Tahoma, sans-serif;");
				}
				System.out.println(newSceneWidth.doubleValue());
			}
		});

		stage.show();
	}

	/**
	 * write Xml generate JDV final combined
	 * 
	 * @param doc
	 * @param output
	 * @throws TransformerException
	 * @throws UnsupportedEncodingException
	 */
	private void writeXmlGenerate(final Document doc, final OutputStream output)
			throws TransformerException, UnsupportedEncodingException {
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, Constante.yes);
		transformer.setOutputProperty(OutputKeys.ENCODING, Constante.utf8);
		transformer.setOutputProperty(OutputKeys.METHOD, Constante.xml);
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, Constante.yes);
		final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		final InputStream is = classloader.getResourceAsStream("prettyprint.xsl");
		transformer = transformerFactory.newTransformer(new StreamSource((is)));
		final DOMSource source = new DOMSource(doc);
		final StreamResult result = new StreamResult(output);
		transformer.transform(source, result);
		isOkGenerate = true;
	}

	/**
	 * open html file in browser
	 * 
	 * @param file
	 */
	private void openFile(final File file) {
		try {
			this.desktop.open(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * getExtension
	 * 
	 * @param filename
	 * @return
	 */
	public String getExtension(final String filename) {
		return FilenameUtils.getExtension(filename);
	}

	/**
	 * downloadUsingNIO
	 * 
	 * @param urlStr
	 * @param file
	 * @throws IOException
	 */
	private static void downloadUsingNIO(final String urlStr, final String file) throws IOException {
		URL url = new URL(urlStr);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
	}

	/**
	 * removeExtension
	 * 
	 * @param fname
	 * @return
	 */
	public static String removeExtension(String fname) {
		int pos = fname.lastIndexOf('.');
		if (pos > -1)
			return fname.substring(0, pos);
		else
			return fname;
	}

	/**
	 * décompresse le fichier zip dans le répertoire donné
	 * 
	 * @param folder  le répertoire où les fichiers seront extraits
	 * @param zipfile le fichier zip à décompresser
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	public static void unzip(String zipFilePath, String destFilePath) throws IOException {
		final File destination_Directory = new File(destFilePath);
		if (!destination_Directory.exists()) {
			destination_Directory.mkdir();
		}
		final Charset cp866 = Charset.forName(Constante.charset);
		final ZipInputStream zip_Input_Stream = new ZipInputStream(new FileInputStream(zipFilePath), cp866);
		ZipEntry zip_Entry = zip_Input_Stream.getNextEntry();
		File file = null;
		List<File> listFile = new ArrayList<File>();
		try {
			while (zip_Entry != null) {
				final String file_Path = destination_Directory + File.separator + zip_Entry.getName();
				if (!zip_Entry.isDirectory()) {
					if (zip_Entry.getName().startsWith(Constante.jdv)) {
						String str = extractFile(zip_Input_Stream, file_Path);
						file = new File(str);
						listFile.add(file);
					}
				} else {
					final File directory = new File(file_Path);
					directory.mkdirs();
				}
				zip_Input_Stream.closeEntry();
				zip_Entry = zip_Input_Stream.getNextEntry();
			}
			for (File pathname : listFile) {
				filesB.add(pathname);
			}

			if (filesB != null) {
				ObservableList<File> oblist = FXCollections.observableArrayList();
				for (int i = 0; i < filesB.size(); i++) {
					oblist.add(filesB.get(i));
				}
				listB.setItems(oblist);
				if (finalFiles == null) {
					finalFiles = new ArrayList<File>();
				}
				listB.setCellFactory(lv -> new CheckBoxListCell<File>(listB::getItemBooleanProperty) {
					@Override
					public void updateItem(File employee, boolean empty) {
						super.updateItem(employee, empty);
						setText(employee == null ? "" : String.format(employee.getAbsolutePath()));
					}
				});

				listB.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<Integer>() {
					@Override
					public void onChanged(javafx.collections.ListChangeListener.Change<? extends Integer> c) {
						while (c.next()) {
							if (c.wasAdded()) {
								for (int i : c.getAddedSubList()) {
									if (!finalFiles.contains(listB.getItems().get(i))) {
										finalFiles.add(listB.getItems().get(i));
									}
								}
							}
							if (c.wasRemoved()) {
								for (int i : c.getRemoved()) {
									finalFiles.remove(listB.getItems().get(i));
								}
							}
						}
					}
				});
				hb.setVisible(true);
			}
			zip_Input_Stream.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * extractFile
	 * 
	 * @param Zip_Input_Stream
	 * @param File_Path
	 * @throws IOException
	 */
	private static String extractFile(final ZipInputStream zip_Input_Stream, final String file_Path)
			throws IOException {
		BufferedOutputStream buffered_Output_Stream = new BufferedOutputStream(new FileOutputStream(file_Path));
		byte[] bytes = new byte[BUFFER_SIZE];
		int read_Byte = 0;
		while ((read_Byte = zip_Input_Stream.read(bytes)) != -1) {
			buffered_Output_Stream.write(bytes, 0, read_Byte);
		}
		buffered_Output_Stream.close();
		return file_Path;
	}

	/**
	 * parse xml file
	 * 
	 * @param filePath
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document parseXML(String filePath) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(filePath);
		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * pretty Print
	 * 
	 * @param xml
	 * @throws Exception
	 */
	public static final void prettyPrint(final Document xml, final String path, final String dest) throws Exception {
		final Source source = new DOMSource(xml);

		final File f = new File(path + "\\terminologie");
		if (!f.exists()) {
			f.mkdir();
		}
		if (new File(path + "\\terminologie\\" + dest + ".rdf").exists()) {
			new File(path + "\\terminologie\\" + dest + ".rdf").delete();
		}
		final File xmlFile = new File(path + "\\terminologie\\" + dest + ".rdf");
		final StreamResult result = new StreamResult(
				new OutputStreamWriter(new FileOutputStream(xmlFile), Constante.utf8));
		final Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.ENCODING, Constante.utf8);
		xformer.setOutputProperty(OutputKeys.INDENT, Constante.yes);
		xformer.transform(source, result);
	}

	/**
	 * removeAllChildren
	 * 
	 * @param node
	 */
	public static org.w3c.dom.Node removeAllChildren(org.w3c.dom.Node node) {
		removeAllChilderenWithoutHeader(node);
		return node;
	}

	/**
	 * removeAllChilderenWithoutHeader
	 * 
	 * @param node
	 * @param remainChildCount
	 */
	public static void removeAllChilderenWithoutHeader(org.w3c.dom.Node node) {
		final NodeList childNodes = node.getChildNodes();
		List<org.w3c.dom.Node> removeNodeList = new ArrayList<org.w3c.dom.Node>();
		for (int i = 0; i < childNodes.getLength(); i++) {
			removeNodeList.add(childNodes.item(i));
		}

		for (org.w3c.dom.Node childNode : removeNodeList) {
			node.removeChild(childNode);
		}
	}

	/**
	 * initialize
	 * 
	 * @param file
	 */
	public void init(final File file) {
		final Alert alert = new Alert(AlertType.ERROR);
		final DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");
		dialogPane.setMinHeight(150);
		dialogPane.setMaxHeight(150);
		dialogPane.setPrefHeight(150);
		alert.setContentText(Constante.alert9 + "\n" + file.getName());
		alert.setHeaderText(null);
		alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
		alert.showAndWait();
		files = new ArrayList<File>();
		filesB = new ArrayList<File>();
		finalFiles = new ArrayList<File>();
		list.getItems().clear();
		listB.getItems().clear();
		list.getCheckModel().clearChecks();
		listB.getCheckModel().clearChecks();
		selectAll.setSelected(false);
	}

	/**
	 * readFileContents
	 * 
	 * @param selectedFile
	 * @throws IOException
	 */
	private String readFileContents(final File file) throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
		String singleString = null;

		try {
			final StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			singleString = sb.toString();
		} finally {
			br.close();
		}
		return singleString;
	}

	/**
	 * main method
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		launch(args);
	}
}
