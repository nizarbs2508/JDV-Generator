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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.CheckListView;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
import javafx.scene.control.Accordion;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
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
	 * list file local
	 */
	public static List<File> listFR = new ArrayList<File>();
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
	 * thirdStage
	 */
	public Stage thirdStage;
	/**
	 * tokenurl
	 */
	public String tokenurl = null;
	/**
	 * downloadurl
	 */
	public String downloadurl = null;
	/**
	 * tokenopen
	 */
	public String tokenopen = null;
	/**
	 * fileContent
	 */
	public String fileContent = null;
	/**
	 * mapSnomed
	 */
	public Map<String, String> mapSnomed = new HashMap<String, String>();
	/**
	 * count
	 */
	public int count;
	/**
	 * passed
	 */
	public boolean passed;
	/**
	 * selectedList
	 */
	public List<String> selectedList = new ArrayList<String>();

	/**
	 * load task api
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
	 * start javafx App
	 */
	public void start(final Stage stage) {
		final Button buttonTermino1 = new Button(Constante.buttonTermino);
		final TextField textLogin = new TextField();
		final PasswordField textPwd = new PasswordField();
		final Label labelPwd = new Label(Constante.password);
		final Label labelLog = new Label(Constante.identifiant);
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
		updatePane.setStyle(Constante.style4);
		final Stage taskUpdateStage = new Stage(StageStyle.UNDECORATED);
		taskUpdateStage.setScene(new Scene(updatePane, 170, 170));
		// End progressBar

		final ImageView imgView = new ImageView(Constante.image10);
		imgView.setFitWidth(20);
		imgView.setFitHeight(20);

		final ImageView imgViewM = new ImageView(Constante.image11);
		imgViewM.setFitWidth(20);
		imgViewM.setFitHeight(20);

		final ImageView imgValid = new ImageView(Constante.image12);
		imgValid.setFitWidth(20);
		imgValid.setFitHeight(20);

		final ImageView imgRead = new ImageView(Constante.image13);
		imgRead.setFitWidth(20);
		imgRead.setFitHeight(20);

		final ImageView imgExit = new ImageView(Constante.image14);
		imgExit.setFitWidth(20);
		imgExit.setFitHeight(20);

		final Menu file = new Menu(Constante.openFile);
		file.setStyle(Constante.style2);
		final MenuItem item = new MenuItem(Constante.openToFile, imgView);
		final MenuItem item1 = new MenuItem(Constante.validateFile, imgValid);
		final MenuItem item3 = new MenuItem(Constante.exit, imgExit);
		item1.setStyle(Constante.style2);
		item.setStyle(Constante.style2);
		item3.setStyle(Constante.style2);
		file.getItems().addAll(item, item1, item3);
		// Creating separator menu items
		final SeparatorMenuItem sep = new SeparatorMenuItem();
		// Adding separator objects to menu
		file.getItems().add(2, sep);
		final Menu apropos = new Menu(Constante.apropos);
		apropos.setStyle(Constante.style2);
		final MenuItem item2 = new MenuItem(Constante.lisezmoi, imgRead);
		item2.setStyle(Constante.style2);
		apropos.getItems().addAll(item2);
		// Creating a File chooser
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(Constante.chooseFile);
		fileChooser.getExtensionFilters()
				.addAll(new ExtensionFilter(Constante.allFile, Constante.allXml, Constante.allXlsx, Constante.allxlsm));
		files = new ArrayList<File>();
		filesB = new ArrayList<File>();
		listF = new ArrayList<File>();
		listFR = new ArrayList<File>();
		list.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
		listB.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
		final Label labelEmpty = new Label(Constante.empty1);
		labelEmpty.setStyle(Constante.style5);
		list.setPlaceholder(labelEmpty);
		list.setStyle(Constante.style5);
		list.setPrefHeight(550);
		list.setMaxHeight(550);
		list.setMinHeight(550);
		list.setPrefWidth((width - 10) / 2);
		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		final Label labelEmpty1 = new Label(Constante.empty);
		labelEmpty1.setStyle(Constante.style5);
		listB.setPlaceholder(labelEmpty1);
		listB.setStyle(Constante.style5);
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

		// Adding action on the menu item3
		item3.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
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
				if (thirdStage != null) {
					if (thirdStage.isShowing()) {
						thirdStage.close();
						textLogin.setText("");
						textPwd.setText("");

					}
				}
				stage.close();
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
							name = name + Constante.retourChariot + fileMalFormed.get(i).getName();
						}
						final Alert alert = new Alert(AlertType.ERROR);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
						dialogPane.getStyleClass().add(Constante.dialog);
						dialogPane.setMinHeight(120 * fileMalFormed.size());
						dialogPane.setMaxHeight(120 * fileMalFormed.size());
						dialogPane.setPrefHeight(120 * fileMalFormed.size());
						alert.setContentText(Constante.alert10 + Constante.retourChariot + name);
						alert.setHeaderText(null);
						alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
						alert.showAndWait();
					} else {
						final Alert alert = new Alert(AlertType.INFORMATION);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
						dialogPane.getStyleClass().add(Constante.dialog);
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
					final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
					final InputStream is = classloader.getResourceAsStream(Constante.lisezMoiFile);
					final VBox root = new VBox();
					root.setPadding(new Insets(10));
					root.setSpacing(5);
					final TextArea textArea = new TextArea();
					textArea.setEditable(false);
					textArea.setPrefHeight(Integer.MAX_VALUE);
					textArea.setPrefWidth(Integer.MAX_VALUE);
					textArea.setStyle(Constante.style6);
					textArea.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
					try {
						textArea.setText(readFileContents(is));
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

		final Label lContent = new Label(Constante.jdvFile);
		final TextField field = new TextField();

		final Label lContentOther = new Label(Constante.rdfFile);
		final TextField fieldOther = new TextField();

		final ImageView imgViewA = new ImageView(Constante.image1);
		imgViewA.setFitWidth(20);
		imgViewA.setFitHeight(20);

		final ImageView imgViewO = new ImageView(Constante.image2);
		imgViewO.setFitWidth(20);
		imgViewO.setFitHeight(20);

		final ImageView imgViewT = new ImageView(Constante.image3);
		imgViewT.setFitWidth(20);
		imgViewT.setFitHeight(20);

		final ImageView imgViewR = new ImageView(Constante.image4);
		imgViewR.setFitWidth(20);
		imgViewR.setFitHeight(20);

		final ImageView imgViewI = new ImageView(Constante.image5);
		imgViewI.setFitWidth(20);
		imgViewI.setFitHeight(20);

		// Creating a menu bar and adding menu to it.
		final MenuBar menuBar = new MenuBar(file, apropos);
		final Button button1 = new Button(Constante.button1, imgViewA);
		final Button button10 = new Button(Constante.button10, imgViewM);

		final ImageView imgViewD = new ImageView(Constante.image6);
		imgViewD.setFitWidth(20);
		imgViewD.setFitHeight(20);

		final Button buttonDownload = new Button(Constante.buttonDownload, imgViewD);
		final Button buttonTermino = new Button(Constante.buttonTermino, imgViewR);

		final Image ansImage = new Image(AddXmlNode.class.getResource(Constante.image7).toExternalForm());
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
		hBoxImg.setStyle(Constante.style7);

		final Region spacer1 = new Region();
		spacer1.setMaxWidth(10);
		HBox.setHgrow(spacer1, Priority.ALWAYS);

		final Region spacer13 = new Region();
		spacer13.setMaxWidth(10);
		HBox.setHgrow(spacer13, Priority.ALWAYS);

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

		final Button button2 = new Button(Constante.button2, imgViewO);

		final Button button4 = new Button(Constante.button4, imgViewT);

		final Button button3 = new Button(Constante.button3, imgViewI);

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
		label.setStyle(Constante.style5);

		final DateTimePicker picker = new DateTimePicker();
		picker.setPadding(new Insets(5, 5, 5, 5));
		final LocalDateTime localDateTime = LocalDateTime.of(2021, Month.MARCH, 15, 00, 00, 00);
		picker.setDateTimeValue(localDateTime);
		picker.setPrefWidth(190);
		picker.setStyle(Constante.style8);
		picker.setEditable(false);

		final Label label1 = new Label();
		label1.setText(Constante.label1);
		label1.setPadding(new Insets(5, 5, 5, 5));
		label1.setStyle(Constante.style5);

		final ComboBox<String> comboBox = new ComboBox<String>();
		ObservableList<String> options = FXCollections.observableArrayList(Constante.finale, Constante.partiel,
				Constante.finaletronque, Constante.completed, Constante.active, Constante.aborted);
		comboBox.setItems(options);
		comboBox.getSelectionModel().select(0);
		comboBox.setPadding(new Insets(5, 5, 5, 5));
		comboBox.setPrefWidth(140);
		comboBox.setStyle(Constante.style8);
		comboBox.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());

		final Label label2 = new Label();
		label2.setText(Constante.label2);
		label2.setPadding(new Insets(5, 5, 5, 5));
		label2.setStyle(Constante.style5);

		final TextField textField2 = new TextField();
		textField2.setText(Constante.textField2);
		textField2.setPrefWidth(40);
		textField2.setPadding(new Insets(5, 5, 5, 5));
		textField2.setStyle(Constante.style8);

		final Label labelL = new Label();
		labelL.setText(Constante.labelL);
		labelL.setPadding(new Insets(5, 5, 5, 5));
		labelL.setStyle(Constante.style5);

		final TextField textFieldL = new TextField();
		textFieldL.setPadding(new Insets(5, 5, 5, 5));
		textFieldL.setText(Constante.textFieldL);
		textFieldL.setPrefWidth(40);
		textFieldL.setStyle(Constante.style8);

		final Label labelT = new Label();
		labelT.setText(Constante.labelT);
		labelT.setPadding(new Insets(5, 5, 5, 5));
		labelT.setStyle(Constante.style5);

		final TextField textFieldT = new TextField();
		textFieldT.setPadding(new Insets(5, 5, 5, 5));
		textFieldT.setText(Constante.textFieldT);
		textFieldT.setPrefWidth(40);
		textFieldT.setStyle(Constante.style8);

		final Label labelP = new Label();
		labelP.setText(Constante.labelP);
		labelP.setPadding(new Insets(5, 5, 5, 5));
		labelP.setStyle(Constante.style5);

		final TextField textFieldP = new TextField();
		textFieldP.setPadding(new Insets(5, 5, 5, 5));
		textFieldP.setText(Constante.textFieldP);
		textFieldP.setPrefWidth(180);
		textFieldP.setStyle(Constante.style8);

		final Label labelPS = new Label();
		labelPS.setText(Constante.labelPS);
		labelPS.setPadding(new Insets(5, 5, 5, 5));
		labelPS.setStyle(Constante.style5);

		final TextField textFieldPS = new TextField();
		textFieldPS.setPadding(new Insets(5, 5, 5, 5));
		textFieldPS.setText(Constante.textFieldPS);
		textFieldPS.setPrefWidth(120);
		textFieldPS.setStyle(Constante.style8);

		final Label labelUrl = new Label();
		labelUrl.setText(Constante.labelUrl);
		labelUrl.setPadding(new Insets(5, 5, 5, 5));
		labelUrl.setStyle(Constante.style5);

		final TextField textFieldUrl = new TextField();
		textFieldUrl.setPadding(new Insets(5, 5, 5, 5));
		textFieldUrl.setText(Constante.textFieldUrl);
		textFieldUrl.setPrefWidth(680);
		textFieldUrl.setStyle(Constante.style8);

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
		button2.setStyle(Constante.style1);

		final TitledPane titledPane = new TitledPane();
		final TitledPane titledPaneOther = new TitledPane();
		button2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				if (new File(textFieldP.getText()).exists()) {
					openFile(new File(textFieldP.getText()));
				} else {
					final Alert alert = new Alert(AlertType.ERROR);
					final DialogPane dialogPane = alert.getDialogPane();
					dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
					dialogPane.getStyleClass().add(Constante.dialog);
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

		final Button chooseFile = new Button(Constante.chooseFile);
		final Button chooseFileOther = new Button(Constante.chooseFile);
		final TitledPane firstTitledPane = new TitledPane();
		final Label labelContent = new Label(Constante.infoContent);

		buttonTermino.setPrefWidth(230);
		buttonTermino.setPrefHeight(30);
		buttonTermino.setMinHeight(30);
		buttonTermino.setMaxHeight(30);
		buttonTermino.setStyle(Constante.style1);
		buttonTermino.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				thirdStage = new Stage();
				final VBox root = new VBox();
				root.setPadding(new Insets(10));
				root.setSpacing(5);
				labelLog.setPadding(new Insets(5, 5, 5, 5));
				labelLog.setStyle(Constante.style5);
				labelPwd.setPadding(new Insets(5, 5, 5, 5));
				labelPwd.setStyle(Constante.style5);
				textLogin.setPrefWidth(40);
				textLogin.setPadding(new Insets(5, 5, 5, 5));
				textLogin.setStyle(Constante.style8);
				textPwd.setPrefWidth(40);
				textPwd.setPadding(new Insets(5, 5, 5, 5));
				textPwd.setStyle(Constante.style8);
				// create a tile pane
				HBox rbox = new HBox();
				final List<String> matchingKey = new ArrayList<>();
				final Map<String, String> map = new HashMap<String, String>();
				final Map<String, String> map1 = new HashMap<String, String>();
				try (InputStream input = AddXmlNode.class.getClassLoader().getResourceAsStream(Constante.rdfFileName)) {
					Properties prop = new Properties();
					if (input == null) {
						System.out.println("Sorry, unable to find rdf.properties");
					}
					// load a properties file from class path, inside static method
					prop.load(input);
					// get the property value and print it out
					Pattern patt = Pattern.compile(Constante.samplePattern);
					for (Entry<Object, Object> each : prop.entrySet()) {
						final Matcher m = patt.matcher((String) each.getKey());
						if (m.find()) {
							String[] words = ((String) each.getKey()).split(Constante.samplePattern2);
							matchingKey.add(words[1]);
							map.put(words[1], (String) each.getValue());
						}
					}
					tokenurl = prop.getProperty(Constante.tokenUrl).trim();
					downloadurl = prop.getProperty(Constante.downloadUrl).trim();
					tokenopen = prop.getProperty(Constante.tokenOpen).trim();

				} catch (final IOException ex) {
					ex.printStackTrace();
				}
				titledPane.setText(Constante.taasip);
				titledPane.setPadding(new Insets(5, 5, 5, 5));
				titledPane.setStyle(Constante.style5);

				titledPaneOther.setText(Constante.otherTermino);
				titledPaneOther.setPadding(new Insets(5, 5, 5, 5));
				titledPaneOther.setStyle(Constante.style5);

				final VBox content = new VBox();
				lContent.setPadding(new Insets(5, 5, 5, 5));
				lContent.setStyle(Constante.style5);
				lContentOther.setPadding(new Insets(5, 5, 5, 5));
				lContentOther.setStyle(Constante.style5);

				final HBox box = new HBox();
				field.setPrefWidth(580);
				field.setPrefHeight(30);
				field.setMinHeight(30);
				field.setMaxHeight(30);
				field.setStyle(Constante.style5);
				field.setDisable(true);

				fieldOther.setPrefWidth(580);
				fieldOther.setPrefHeight(30);
				fieldOther.setMinHeight(30);
				fieldOther.setMaxHeight(30);
				fieldOther.setStyle(Constante.style5);
				fieldOther.setDisable(true);

				Accordion accordioneOther = new Accordion();
				final FileChooser fileChooserOther = new FileChooser();
				fileChooserOther.setTitle(Constante.chooseFile);
				fileChooserOther.getExtensionFilters().addAll(new ExtensionFilter(Constante.allFile, Constante.allRdf));
				chooseFileOther.setPrefWidth(150);
				chooseFileOther.setPrefHeight(30);
				chooseFileOther.setMinHeight(30);
				chooseFileOther.setMaxHeight(30);
				chooseFileOther.setStyle(Constante.style1);
				chooseFileOther.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						// Opening a dialog box
						final List<File> file = fileChooserOther.showOpenMultipleDialog(thirdStage);
						if (file != null) {
							fieldOther.setText(file.get(0).getAbsolutePath());
							titledPaneOther.setExpanded(false);
						}
					}
				});

				Accordion accordione = new Accordion();
				chooseFile.setPrefWidth(150);
				chooseFile.setPrefHeight(30);
				chooseFile.setMinHeight(30);
				chooseFile.setMaxHeight(30);
				chooseFile.setStyle(Constante.style1);
				chooseFile.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						// Opening a dialog box
						final List<File> file = fileChooser.showOpenMultipleDialog(thirdStage);
						if (file != null) {
							field.setText(file.get(0).getAbsolutePath());
							titledPane.setExpanded(false);
							accordione.setDisable(true);
						}
					}
				});

				final Region spacere = new Region();
				spacere.setMaxWidth(10);
				HBox.setHgrow(spacere, Priority.ALWAYS);

				box.getChildren().addAll(chooseFile, spacere, field);

				final FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle(Constante.chooseFile);
				fileChooser.getExtensionFilters()
						.addAll(new ExtensionFilter(Constante.allFile, Constante.allXlsx, Constante.allxlsm));

				content.getChildren().addAll(lContent, box);

				titledPane.setContent(content);

				accordione.getPanes().addAll(titledPane);
				accordione.setDisable(true);

				accordioneOther.getPanes().addAll(titledPaneOther);

				for (int i = 0; i < matchingKey.size(); i++) {
					final CheckBox c = new CheckBox(matchingKey.get(i));
					c.setStyle(Constante.style20);
					final String name = matchingKey.get(i);
					final Region spacer = new Region();
					spacer.setMaxWidth(10);
					HBox.setHgrow(spacer, Priority.ALWAYS);
					rbox.getChildren().addAll(c, spacer);

					c.setOnAction(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent e) {
							if (c.isSelected()) {
								selectedList.add(name);
								if (name.contains(Constante.asip)) {
									accordione.setDisable(false);
									titledPane.setExpanded(true);
								}
							} else {
								selectedList.remove(name);
								if (name.contains(Constante.asip)) {
									accordione.setDisable(true);
									titledPane.setExpanded(false);
									field.setText("");
								}
							}

						}
					});
				}

				buttonTermino1.setPrefWidth(200);
				buttonTermino1.setPrefHeight(30);
				buttonTermino1.setMinHeight(30);
				buttonTermino1.setMaxHeight(30);
				buttonTermino1.setStyle(Constante.style1);

				HBox resultButton = new HBox();
				resultButton.getChildren().addAll(buttonTermino1);
				resultButton.setAlignment(Pos.CENTER);

				final Region spacer4 = new Region();
				spacer4.setMaxHeight(10);
				VBox.setVgrow(spacer4, Priority.ALWAYS);

				firstTitledPane.setText(Constante.information);
				firstTitledPane.setPadding(new Insets(5, 5, 5, 5));
				firstTitledPane.setStyle(Constante.style5);

				final VBox content1 = new VBox();
				content1.getChildren().add(labelContent);
				labelContent.setPadding(new Insets(5, 5, 5, 5));
				labelContent.setStyle(Constante.style5);

				firstTitledPane.setContent(content1);

				final VBox contentOther = new VBox();
				lContentOther.setPadding(new Insets(5, 5, 5, 5));
				lContentOther.setStyle(Constante.style5);

				final HBox boxOther = new HBox();

				final Region spaceree = new Region();
				spaceree.setMaxWidth(10);
				HBox.setHgrow(spaceree, Priority.ALWAYS);

				boxOther.getChildren().addAll(chooseFileOther, spaceree, fieldOther);

				contentOther.getChildren().addAll(lContentOther, boxOther);

				titledPaneOther.setContent(contentOther);

				Accordion accordion = new Accordion();
				accordion.getPanes().addAll(firstTitledPane);

				final Region spacer5 = new Region();
				spacer5.setMaxHeight(20);
				VBox.setVgrow(spacer5, Priority.ALWAYS);

				final Region spacer6 = new Region();
				spacer6.setMaxHeight(20);
				VBox.setVgrow(spacer6, Priority.ALWAYS);

				final Region spacer7 = new Region();
				spacer7.setMaxHeight(20);
				VBox.setVgrow(spacer7, Priority.ALWAYS);

				final Region spacer8 = new Region();
				spacer8.setMaxHeight(20);
				VBox.setVgrow(spacer8, Priority.ALWAYS);

				root.getChildren().addAll(labelLog, textLogin, labelPwd, textPwd, rbox, spacer4, resultButton, spacer5,
						accordione, spacer6, accordioneOther, spacer7, accordion);
				Scene scene = new Scene(root, 800, 650);
				thirdStage.setTitle(Constante.terminology);
				thirdStage.setScene(scene);
				thirdStage.setMaximized(false);
				thirdStage.show();

				buttonTermino1.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent event) {
						runTask(taskUpdateStage, progress);
						Platform.runLater(() -> {
							List<String> listTermino = new ArrayList<String>();
							String text = "";
							if (!textLogin.getText().isEmpty() && !textPwd.getText().isEmpty()) {
								TerminologyDownloader downloader = new TerminologyDownloader();
								final String str = downloader.getFirstToken(tokenopen, textLogin.getText(),
										textPwd.getText());
								if (str == null) {
									count = 0;
								} else {
									count = 1;
								}
							} else {
								count = 1;
							}
							if (count == 1) {
								// TA-ASIP
								if (selectedList.size() == 1 && !field.getText().isEmpty()) {
									passed = true;
									final String ext = getExtension(field.getText());
									if (!field.getText().isEmpty()
											&& (ext.equals(Constante.xlsm) || ext.equals(Constante.xlsx))) {
										// Create a Workbook instance
										final Workbook workbookRdf = new Workbook();
										// Load an Excel file
										workbookRdf.loadFromFile(field.getText());
										final Worksheet worksheet = workbookRdf.getWorksheets().get(2);
										List<RetrieveValueSet> listR = new ArrayList<RetrieveValueSet>();
										// Get the row count
										final int maxRow = worksheet.getLastRow();
										// Loop through the rows
										for (int row = 2; row <= maxRow; row++) {
											RetrieveValueSet response = new RetrieveValueSet();
											// Loop through the columns
											// Get the current cell
											CellRange cell = worksheet.getCellRange(row, 1);
											CellRange cell1 = worksheet.getCellRange(row, 10);
											response.setValueSetOID(cell.getValue());
											response.setObsolete(cell1.getValue());
											if (response.getValueSetOID() != null
													&& !response.getObsolete().equalsIgnoreCase(Constante.oui)) {
												listR.add(response);
											}
										}
										if (listR != null) {
											if (!listR.isEmpty()) {
												DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
												DocumentBuilder builder;
												try {
													builder = dbf.newDocumentBuilder();
													Document doc = builder.newDocument();
													String fileDestName = null;
													Element newNode = doc.createElement(Constante.rdf_RDF);
													doc.appendChild(newNode);
													newNode.setAttribute("xmlns:rdf",
															"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
													newNode.setAttribute("xmlns:schema", "https://schema.org/");
													newNode.setAttribute("xmlns:voaf",
															"http://purl.org/vocommons/voaf#");
													newNode.setAttribute("xmlns:rdfs",
															"http://www.w3.org/2000/01/rdf-schema#");
													newNode.setAttribute("xmlns:metadata",
															"http://topbraid.org/metadata#");
													newNode.setAttribute("xmlns:icd", "http://id.who.int/icd/schema/");
													newNode.setAttribute("xmlns:terms", "http://purl.org/dc/terms/");
													newNode.setAttribute("xmlns:adms", "http://www.w3.org/ns/adms#");
													newNode.setAttribute("xmlns:core",
															"http://open-services.net/ns/core#");
													newNode.setAttribute("xmlns:j.0", "http://topbraid.org/swa#");
													newNode.setAttribute("xmlns:owl", "http://www.w3.org/2002/07/owl#");
													newNode.setAttribute("xmlns:skos",
															"http://www.w3.org/2004/02/skos/core#");
													newNode.setAttribute("xmlns:teamwork",
															"http://topbraid.org/teamwork#");
													newNode.setAttribute("xmlns:j.1",
															"http://teamwork.topbraidlive.org/ontologyprojects#");
													newNode.setAttribute("xmlns:dcat", "http://www.w3.org/ns/dcat#");
													newNode.setAttribute("xmlns:ns",
															"https://data.esante.gouv.fr/profile/ns#");
													newNode.setAttribute("xmlns:foaf", "http://xmlns.com/foaf/0.1/");
													newNode.setAttribute("xmlns:xsd",
															"http://www.w3.org/2001/XMLSchema#");

													try (InputStream input = AddXmlNode.class.getClassLoader()
															.getResourceAsStream(Constante.rdfFileName)) {

														Properties prop = new Properties();

														if (input == null) {
															System.out.println("Sorry, unable to find rdf.properties");
														}
														// load a properties file from class path, inside static method
														prop.load(input);
														// get the property value and print it out
														Pattern patt = Pattern.compile(Constante.namePattern);
														for (Entry<Object, Object> each : prop.entrySet()) {
															final Matcher m = patt.matcher((String) each.getKey());
															if (m.find()) {
																String[] words = ((String) each.getKey())
																		.split(Constante.namePattern2);
																matchingKey.add(words[1]);
																map1.put(words[1], (String) each.getValue());
															}
														}
													} catch (final IOException ex) {
														ex.printStackTrace();
													}

													for (final RetrieveValueSet str : listR) {
														Element thirdNode = null;
														thirdNode = doc.createElement(Constante.description);
														newNode.appendChild(thirdNode);
														Element secondNode = null;
														secondNode = doc.createElement(Constante.notation);
														secondNode.setTextContent(str.getValueSetOID());
														thirdNode.appendChild(secondNode);
														doc.normalize();
														for (Map.Entry<String, String> mapentry : map1.entrySet()) {
															if (Constante.nameTaAsip
																	.contains((CharSequence) mapentry.getKey())) {
																fileDestName = (String) mapentry.getValue();
																break;
															}
														}
													}
													File xml = prettyPrint(doc,
															new File(textFieldPS.getText()).getParent(),
															fileDestName.trim());
													listTermino.add(xml.getAbsolutePath());
													workbookRdf.dispose();

												} catch (final ParserConfigurationException e) {

													e.printStackTrace();
												} catch (final Exception e) {

													e.printStackTrace();
												}

											}
										}

									}

								} else if (!textLogin.getText().isEmpty() && !textPwd.getText().isEmpty()
										&& !selectedList.isEmpty() && field.getText().isEmpty()) {
									passed = true;
									List<File> fileRdf = new ArrayList<File>();
									TerminologyDownloader downloader = new TerminologyDownloader();
									fileRdf = downloader.main(textLogin.getText(), textPwd.getText(), selectedList, map,
											tokenurl, downloadurl, tokenopen);
									try (InputStream input = AddXmlNode.class.getClassLoader()
											.getResourceAsStream(Constante.rdfFileName)) {
										Properties prop = new Properties();
										if (input == null) {
											System.out.println("Sorry, unable to find rdf.properties");
										}
										// load a properties file from class path, inside static method
										prop.load(input);
										// get the property value and print it out
										Pattern patt = Pattern.compile(Constante.namePattern);
										for (Entry<Object, Object> each : prop.entrySet()) {
											final Matcher m = patt.matcher((String) each.getKey());
											if (m.find()) {
												String[] words = ((String) each.getKey()).split(Constante.namePattern2);
												matchingKey.add(words[1]);
												map1.put(words[1], (String) each.getValue());
											}
										}
									} catch (final IOException ex) {

										ex.printStackTrace();
									}

									if (fileRdf != null) {
										if (!fileRdf.isEmpty()) {
											for (final File file : fileRdf) {
												if (getExtension(file.getAbsolutePath()).equals(Constante.rdf)) {
													List<String> listStr = new ArrayList<String>();
													final String fileName = file.getName().trim();
													String fileDestName = null;
													for (Map.Entry<String, String> mapentry : map1.entrySet()) {
														if (fileName.toUpperCase()
																.contains((CharSequence) mapentry.getKey())) {
															fileDestName = (String) mapentry.getValue();
															break;
														}
													}
													try {
														listStr = parse(file.getAbsolutePath());
														DocumentBuilderFactory documentFactory = DocumentBuilderFactory
																.newInstance();
														DocumentBuilder documentBuilder = documentFactory
																.newDocumentBuilder();
														Document document = documentBuilder.newDocument();
														// root element
														Element root = document.createElement(Constante.rdf_RDF);
														document.appendChild(root);
														if (!mapSnomed.isEmpty()) {
															for (Map.Entry<String, String> mapentry : mapSnomed
																	.entrySet()) {
																final String key = (String) mapentry.getKey();
																final String value = (String) mapentry.getValue();
																final Attr attr = document.createAttribute(key);
																attr.setValue(value);
																root.setAttributeNode(attr);
															}
														}
														// notation element
														if (listStr != null) {
															if (!listStr.isEmpty()) {
																for (String str : listStr) {
																	// description element
																	Element description = document
																			.createElement(Constante.description);
																	root.appendChild(description);
																	Element notation = document
																			.createElement(Constante.notation);
																	notation.appendChild(document.createTextNode(str));
																	description.appendChild(notation);
																}
																document.normalize();
																File xml = prettyPrint(document,
																		new File(textFieldPS.getText()).getParent(),
																		fileDestName.trim());
																listTermino.add(xml.getAbsolutePath());

															}
														}

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
											}
										} else {
											textLogin.setText("");
											textPwd.setText("");

										}
									} else {
										textLogin.setText("");
										textPwd.setText("");
									}

								} else if (!textLogin.getText().isEmpty() && !textPwd.getText().isEmpty()
										&& !selectedList.isEmpty() && !field.getText().isEmpty()) {
									passed = true;
									try (InputStream input = AddXmlNode.class.getClassLoader()
											.getResourceAsStream(Constante.rdfFileName)) {

										Properties prop = new Properties();

										if (input == null) {
											System.out.println("Sorry, unable to find rdf.properties");
										}
										// load a properties file from class path, inside static method
										prop.load(input);
										// get the property value and print it out
										Pattern patt = Pattern.compile(Constante.namePattern);
										for (Entry<Object, Object> each : prop.entrySet()) {
											final Matcher m = patt.matcher((String) each.getKey());
											if (m.find()) {
												String[] words = ((String) each.getKey()).split(Constante.namePattern2);
												matchingKey.add(words[1]);
												map1.put(words[1], (String) each.getValue());
											}
										}
									} catch (final IOException ex) {

										ex.printStackTrace();
									}
									final String ext = getExtension(field.getText());
									if (!field.getText().isEmpty()
											&& (ext.equals(Constante.xlsm) || ext.equals(Constante.xlsx))) {
										// Create a Workbook instance
										final Workbook workbookRdf = new Workbook();
										// Load an Excel file
										workbookRdf.loadFromFile(field.getText());
										final Worksheet worksheet = workbookRdf.getWorksheets().get(2);
										List<RetrieveValueSet> listR = new ArrayList<RetrieveValueSet>();
										// Get the row count
										final int maxRow = worksheet.getLastRow();
										// Loop through the rows
										for (int row = 2; row <= maxRow; row++) {
											RetrieveValueSet response = new RetrieveValueSet();
											// Loop through the columns
											// Get the current cell
											CellRange cell = worksheet.getCellRange(row, 1);
											CellRange cell1 = worksheet.getCellRange(row, 10);
											response.setValueSetOID(cell.getValue());
											response.setObsolete(cell1.getValue());
											if (response.getValueSetOID() != null
													&& !response.getObsolete().equalsIgnoreCase(Constante.oui)) {
												listR.add(response);
											}
										}
										if (listR != null) {
											if (!listR.isEmpty()) {
												DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
												DocumentBuilder builder;
												try {
													builder = dbf.newDocumentBuilder();
													Document doc = builder.newDocument();
													String fileDestName = null;
													Element newNode = doc.createElement(Constante.rdf_RDF);
													doc.appendChild(newNode);
													newNode.setAttribute("xmlns:rdf",
															"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
													newNode.setAttribute("xmlns:schema", "https://schema.org/");
													newNode.setAttribute("xmlns:voaf",
															"http://purl.org/vocommons/voaf#");
													newNode.setAttribute("xmlns:rdfs",
															"http://www.w3.org/2000/01/rdf-schema#");
													newNode.setAttribute("xmlns:metadata",
															"http://topbraid.org/metadata#");
													newNode.setAttribute("xmlns:icd", "http://id.who.int/icd/schema/");
													newNode.setAttribute("xmlns:terms", "http://purl.org/dc/terms/");
													newNode.setAttribute("xmlns:adms", "http://www.w3.org/ns/adms#");
													newNode.setAttribute("xmlns:core",
															"http://open-services.net/ns/core#");
													newNode.setAttribute("xmlns:j.0", "http://topbraid.org/swa#");
													newNode.setAttribute("xmlns:owl", "http://www.w3.org/2002/07/owl#");
													newNode.setAttribute("xmlns:skos",
															"http://www.w3.org/2004/02/skos/core#");
													newNode.setAttribute("xmlns:teamwork",
															"http://topbraid.org/teamwork#");
													newNode.setAttribute("xmlns:j.1",
															"http://teamwork.topbraidlive.org/ontologyprojects#");
													newNode.setAttribute("xmlns:dcat", "http://www.w3.org/ns/dcat#");
													newNode.setAttribute("xmlns:ns",
															"https://data.esante.gouv.fr/profile/ns#");
													newNode.setAttribute("xmlns:foaf", "http://xmlns.com/foaf/0.1/");
													newNode.setAttribute("xmlns:xsd",
															"http://www.w3.org/2001/XMLSchema#");
													for (final RetrieveValueSet str : listR) {
														Element thirdNode = null;
														thirdNode = doc.createElement(Constante.description);
														newNode.appendChild(thirdNode);
														Element secondNode = null;
														secondNode = doc.createElement(Constante.notation);
														secondNode.setTextContent(str.getValueSetOID());
														thirdNode.appendChild(secondNode);
														doc.normalize();
														for (Map.Entry<String, String> mapentry : map1.entrySet()) {
															if (Constante.nameTaAsip
																	.contains((CharSequence) mapentry.getKey())) {
																fileDestName = (String) mapentry.getValue();
																break;
															}
														}
													}
													File xml = prettyPrint(doc,
															new File(textFieldPS.getText()).getParent(),
															fileDestName.trim());
													listTermino.add(xml.getAbsolutePath());

												} catch (final ParserConfigurationException e) {

													e.printStackTrace();
												} catch (final Exception e) {

													e.printStackTrace();
												}

											}
										}

										workbookRdf.dispose();
									}

									List<File> fileRdf = new ArrayList<File>();
									TerminologyDownloader downloader = new TerminologyDownloader();
									fileRdf = downloader.main(textLogin.getText(), textPwd.getText(), selectedList, map,
											tokenurl, downloadurl, tokenopen);

									try (InputStream input = AddXmlNode.class.getClassLoader()
											.getResourceAsStream(Constante.rdfFileName)) {

										Properties prop = new Properties();

										if (input == null) {
											System.out.println("Sorry, unable to find rdf.properties");
										}
										// load a properties file from class path, inside static method
										prop.load(input);
										// get the property value and print it out
										Pattern patt = Pattern.compile(Constante.namePattern);
										for (Entry<Object, Object> each : prop.entrySet()) {
											final Matcher m = patt.matcher((String) each.getKey());
											if (m.find()) {
												String[] words = ((String) each.getKey()).split(Constante.namePattern2);
												matchingKey.add(words[1]);
												map1.put(words[1], (String) each.getValue());
											}
										}
									} catch (final IOException ex) {

										ex.printStackTrace();
									}
									if (fileRdf != null) {
										if (!fileRdf.isEmpty()) {
											for (final File file : fileRdf) {
												if (getExtension(file.getAbsolutePath()).equals(Constante.rdf)) {
													List<String> listStr = new ArrayList<String>();
													final String fileName = file.getName().trim();
													String fileDestName = null;

													for (Map.Entry<String, String> mapentry : map1.entrySet()) {
														if (fileName.toUpperCase()
																.contains((CharSequence) mapentry.getKey())) {
															fileDestName = (String) mapentry.getValue();
															break;
														}
													}
													try {
														listStr = parse(file.getAbsolutePath());
														DocumentBuilderFactory documentFactory = DocumentBuilderFactory
																.newInstance();
														DocumentBuilder documentBuilder = documentFactory
																.newDocumentBuilder();
														Document document = documentBuilder.newDocument();
														// root element
														Element root = document.createElement(Constante.rdf_RDF);
														document.appendChild(root);
														if (!mapSnomed.isEmpty()) {
															for (Map.Entry<String, String> mapentry : mapSnomed
																	.entrySet()) {
																final String key = (String) mapentry.getKey();
																final String value = (String) mapentry.getValue();
																final Attr attr = document.createAttribute(key);
																attr.setValue(value);
																root.setAttributeNode(attr);
															}
														}
														// notation element
														if (listStr != null) {
															if (!listStr.isEmpty()) {
																for (String str : listStr) {
																	// description element
																	Element description = document
																			.createElement(Constante.description);
																	root.appendChild(description);
																	Element notation = document
																			.createElement(Constante.notation);
																	notation.appendChild(document.createTextNode(str));
																	description.appendChild(notation);
																}
																document.normalize();
																File xml = prettyPrint(document,
																		new File(textFieldPS.getText()).getParent(),
																		fileDestName.trim());
																listTermino.add(xml.getAbsolutePath());
															}
														}

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
											}
										} else {
											textLogin.setText("");
											textPwd.setText("");
										}
									} else {
										textLogin.setText("");
										textPwd.setText("");

									}
								}
								// Importer terminologie
								if (!fieldOther.getText().isEmpty()) {
									passed = true;
									List<File> fileRdf = new ArrayList<File>();
									final File rdfFile = new File(fieldOther.getText());
									fileRdf.add(rdfFile);
									try (InputStream input = AddXmlNode.class.getClassLoader()
											.getResourceAsStream(Constante.rdfFileName)) {
										Properties prop = new Properties();
										if (input == null) {
											System.out.println("Sorry, unable to find rdf.properties");
										}
										// load a properties file from class path, inside static method
										prop.load(input);
										// get the property value and print it out
										Pattern patt = Pattern.compile(Constante.namePattern);
										for (Entry<Object, Object> each : prop.entrySet()) {
											final Matcher m = patt.matcher((String) each.getKey());
											if (m.find()) {
												String[] words = ((String) each.getKey()).split(Constante.namePattern2);
												matchingKey.add(words[1]);
												map1.put(words[1], (String) each.getValue());
											}
										}
									} catch (final IOException ex) {
										ex.printStackTrace();
									}
									if (fileRdf != null) {
										if (!fileRdf.isEmpty()) {
											for (final File file : fileRdf) {
												if (getExtension(file.getAbsolutePath()).equals(Constante.rdf)) {
													List<String> listStr = new ArrayList<String>();
													final String fileName = file.getName().trim();
													String fileDestName = null;
													for (Map.Entry<String, String> mapentry : map1.entrySet()) {
														if (fileName.toUpperCase()
																.contains((CharSequence) mapentry.getKey())) {
															fileDestName = (String) mapentry.getValue();
															break;
														}
													}
													try {
														listStr = parse(file.getAbsolutePath());
														DocumentBuilderFactory documentFactory = DocumentBuilderFactory
																.newInstance();
														DocumentBuilder documentBuilder = documentFactory
																.newDocumentBuilder();
														Document document = documentBuilder.newDocument();
														// root element
														Element root = document.createElement(Constante.rdf_RDF);
														document.appendChild(root);
														if (!mapSnomed.isEmpty()) {
															for (Map.Entry<String, String> mapentry : mapSnomed
																	.entrySet()) {
																final String key = (String) mapentry.getKey();
																final String value = (String) mapentry.getValue();
																final Attr attr = document.createAttribute(key);
																attr.setValue(value);
																root.setAttributeNode(attr);
															}
														}
														// notation element
														if (listStr != null) {
															if (!listStr.isEmpty()) {
																for (String str : listStr) {
																	// description element
																	Element description = document
																			.createElement(Constante.description);
																	root.appendChild(description);
																	Element notation = document
																			.createElement(Constante.notation);
																	notation.appendChild(document.createTextNode(str));
																	description.appendChild(notation);
																}
																document.normalize();
																File xml = prettyPrint(document,
																		new File(textFieldPS.getText()).getParent(),
																		fileDestName.trim());
																listTermino.add(xml.getAbsolutePath());
															}
														}

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
											}
										}
									}
								}
								if (passed == true) {
									final Alert alert = new Alert(AlertType.INFORMATION);
									final DialogPane dialogPane = alert.getDialogPane();
									dialogPane.getStylesheets()
											.add(getClass().getResource(Constante.style).toExternalForm());
									dialogPane.getStyleClass().add(Constante.dialog);
									if (listTermino.size() <= 5) {
										dialogPane.setMinHeight(200);
										dialogPane.setMaxHeight(200);
										dialogPane.setPrefHeight(200);
									} else {
										dialogPane.setMinHeight(270);
										dialogPane.setMaxHeight(270);
										dialogPane.setPrefHeight(270);
									}
									if (listTermino != null) {
										if (!listTermino.isEmpty()) {
											for (final String termino : listTermino) {
												text = text + termino + Constante.retourChariot;
											}
										}
									}
									alert.setContentText(Constante.terminoGenerated + Constante.retourChariot
											+ Constante.retourChariot + text);
									alert.setHeaderText(null);
									alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
									alert.showAndWait();
									textLogin.setText("");
									textPwd.setText("");
									String outputDir = Constante.textFieldRDF;
									fieldOther.setText("");
									field.setText("");
									selectedList = new ArrayList<String>();
									count = 0;
									passed = false;
									thirdStage.close();
									deleteDirectory(new File(outputDir));

								} else {
									final Alert alert = new Alert(AlertType.ERROR);
									final DialogPane dialogPane = alert.getDialogPane();
									dialogPane.getStylesheets()
											.add(getClass().getResource(Constante.style).toExternalForm());
									dialogPane.getStyleClass().add(Constante.dialog);
									dialogPane.setMinHeight(150);
									dialogPane.setMaxHeight(150);
									dialogPane.setPrefHeight(150);
									alert.setContentText(Constante.alert9);
									alert.setHeaderText(null);
									alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
									alert.showAndWait();
									count = 0;
									passed = false;
								}
							} else {
								final Alert alert = new Alert(AlertType.ERROR);
								final DialogPane dialogPane = alert.getDialogPane();
								dialogPane.getStylesheets()
										.add(getClass().getResource(Constante.style).toExternalForm());
								dialogPane.getStyleClass().add(Constante.dialog);
								dialogPane.setMinHeight(150);
								dialogPane.setMaxHeight(150);
								dialogPane.setPrefHeight(150);
								alert.setContentText(Constante.alert9);
								alert.setHeaderText(null);
								alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
								alert.showAndWait();
								count = 0;
								passed = false;
							}
						});
					}
				});

			}
		});

		buttonDownload.setPrefWidth(250);
		buttonDownload.setPrefHeight(30);
		buttonDownload.setMinHeight(30);
		buttonDownload.setMaxHeight(30);
		buttonDownload.setStyle(Constante.style1);
		buttonDownload.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				final String url = textFieldUrl.getText();
				runTask(taskUpdateStage, progress);
				Platform.runLater(() -> {
					final String home = new File(Constante.textFieldPS).getParent();
					final File folder = new File(home + Constante.image9);
					if (!folder.exists()) {
						Path path = Paths.get(folder.getPath());
						try {
							Files.createDirectory(path);
						} catch (final IOException e) {
							e.printStackTrace();
						}
					}
					final File file = new File(home + Constante.image9 + Constante.urlFile);
					try {
						downloadUsingNIO(url, file.getAbsolutePath());
						isOk = true;
					} catch (final IOException e) {
						e.printStackTrace();
						final Alert alert = new Alert(AlertType.ERROR);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
						dialogPane.getStyleClass().add(Constante.dialog);
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
						final String zipFilePath = file.getAbsolutePath();
						final String DestFilePath = file.getParent();
						try {
							unzip(zipFilePath, DestFilePath);
							hb.setVisible(true);
						} catch (final IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});

		button4.setPrefWidth(270);
		button4.setPrefHeight(30);
		button4.setMinHeight(30);
		button4.setMaxHeight(30);
		button4.setStyle(Constante.style1);
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
						dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
						dialogPane.getStyleClass().add(Constante.dialog);
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
		button3.setStyle(Constante.style1);
		button3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isOkGenerate = false;
				files = new ArrayList<File>();
				filesB = new ArrayList<File>();
				listF = new ArrayList<File>();
				listFR = new ArrayList<File>();
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
		button1.setStyle(Constante.style1);
		button1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				if (finalFiles.isEmpty()) {
					final Alert alert = new Alert(AlertType.ERROR);
					final DialogPane dialogPane = alert.getDialogPane();
					dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
					dialogPane.getStyleClass().add(Constante.dialog);
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
					dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
					dialogPane.getStyleClass().add(Constante.dialog);
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
					final File generated = new File(Constante.textFieldP);
					if (generated.exists()) {
						generated.delete();
					}
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
					final Element rootElement = document.createElement(Constante.terminologies);
					// append root element to document
					document.appendChild(rootElement);
					for (File file : finalFiles) {
						if (getExtension(file.getAbsolutePath()).equals(Constante.xml)) {
							try (InputStream is = new FileInputStream(file)) {
								doc = db.parse(is);

								final NodeList nodes = doc.getElementsByTagName(Constante.conceptList);
								if (nodes != null) {
									for (int h = 0; h < nodes.getLength(); h++) {
										if (nodes.item(h) instanceof Element) {
											Element elem = (Element) nodes.item(h);
											doc.renameNode(elem, elem.getNamespaceURI(), Constante.conceptList2);
										}
									}
								}
								final NodeList nodesC = doc.getElementsByTagName(Constante.concept);
								if (nodesC != null) {
									for (int n = 0; n < nodesC.getLength(); n++) {
										if (nodesC.item(n) instanceof Element) {
											final Element elem = (Element) nodesC.item(n);
											doc.renameNode(elem, elem.getNamespaceURI(), Constante.concept2);
										}
									}
								}
								final NodeList listOfStaff = doc.getElementsByTagName(Constante.valueSet);
								if (listOfStaff != null) {
									for (int i = 0; i < listOfStaff.getLength(); i++) {
										if (listOfStaff.item(i) instanceof Element) {
											final Element elem = (Element) listOfStaff.item(i);
											doc.renameNode(elem, elem.getNamespaceURI(), Constante.valueSet2);
										}

										org.w3c.dom.Node staff = listOfStaff.item(i);
										org.w3c.dom.Node importedNode = document.importNode(staff, true);
										String key = importedNode.getAttributes().getNamedItem(Constante.displayName)
												.getNodeValue();
										if (key.contains(Constante.tab)) {
											key = key.replace(Constante.tab, "");
										}
										((Element) importedNode).setAttribute(Constante.nameAttribute, key);
										((Element) importedNode).setAttribute(Constante.displayName, key);

										DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
										String formatDateTime = picker.getDateTimeValue().format(format);
										String dateFinal;
										String[] words = formatDateTime.split(" ");
										dateFinal = words[0] + "T" + words[1];

										((Element) importedNode).setAttribute(Constante.effectiveDate, dateFinal);
										((Element) importedNode).setAttribute(Constante.statusCode,
												comboBox.getSelectionModel().getSelectedItem());
										if (textField2 != null) {
											if (textField2.getText() != null) {
												((Element) importedNode).setAttribute(Constante.versionLabel,
														textField2.getText());
											} else {
												((Element) importedNode).setAttribute(Constante.versionLabel, "");
											}
										} else {
											((Element) importedNode).setAttribute(Constante.versionLabel, "");
										}

										final org.w3c.dom.Node att = importedNode.getAttributes()
												.getNamedItem(Constante.version);
										if (att != null) {
											importedNode.getAttributes().removeNamedItem(att.getNodeName());
										}
										final org.w3c.dom.Node att1 = importedNode.getAttributes()
												.getNamedItem(Constante.dateFin);
										if (att1 != null) {
											importedNode.getAttributes().removeNamedItem(att1.getNodeName());
										}
										final org.w3c.dom.Node att2 = importedNode.getAttributes()
												.getNamedItem(Constante.dateMaj);
										if (att2 != null) {
											importedNode.getAttributes().removeNamedItem(att2.getNodeName());
										}
										final org.w3c.dom.Node att3 = importedNode.getAttributes()
												.getNamedItem(Constante.dateValid);
										if (att3 != null) {
											importedNode.getAttributes().removeNamedItem(att3.getNodeName());
										}
										final org.w3c.dom.Node att4 = importedNode.getAttributes()
												.getNamedItem(Constante.desc);
										if (att4 != null) {
											importedNode.getAttributes().removeNamedItem(att4.getNodeName());
										}
										final org.w3c.dom.Node att5 = importedNode.getAttributes()
												.getNamedItem(Constante.typeFichier);
										if (att5 != null) {
											importedNode.getAttributes().removeNamedItem(att5.getNodeName());
										}
										final org.w3c.dom.Node att6 = importedNode.getAttributes()
												.getNamedItem(Constante.urlFichier);
										if (att6 != null) {
											importedNode.getAttributes().removeNamedItem(att6.getNodeName());
										}

										final NodeList list = importedNode.getChildNodes();
										if (list != null) {
											for (int j = 0; j < list.getLength(); j++) {
												final org.w3c.dom.Node staf = list.item(j);
												final NodeList listk = staf.getChildNodes();
												if (listk != null) {
													for (int k = 0; k < listk.getLength(); k++) {
														final org.w3c.dom.Node item = listk.item(k);
														if (item.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
															final Element elem = (Element) item;
															if (!elem.getAttribute(Constante.dateFin).isEmpty()) {
																elem.getParentNode().removeChild(elem);
															}
															final org.w3c.dom.Node att7 = item.getAttributes()
																	.getNamedItem(Constante.dateFin);
															if (textFieldL != null) {
																if (textFieldL.getText() != null) {
																	((Element) item).setAttribute(Constante.level,
																			textFieldL.getText());
																} else {
																	((Element) item).setAttribute(Constante.level, "");
																}
															} else {
																((Element) item).setAttribute(Constante.level, "");
															}
															if (textFieldT != null) {
																if (textFieldT.getText() != null) {
																	((Element) item).setAttribute(Constante.type,
																			textFieldT.getText());
																} else {
																	((Element) item).setAttribute(Constante.type, "");
																}
															} else {
																((Element) item).setAttribute(Constante.type, "");
															}

															if (att7 != null) {
																item.getAttributes()
																		.removeNamedItem(att7.getNodeName());
															}
															final org.w3c.dom.Node att8 = item.getAttributes()
																	.getNamedItem(Constante.dateValid);
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
								dialogPane.getStylesheets()
										.add(getClass().getResource(Constante.style).toExternalForm());
								dialogPane.getStyleClass().add(Constante.dialog);
								dialogPane.setMinHeight(130);
								dialogPane.setMaxHeight(130);
								dialogPane.setPrefHeight(130);
								alert.setContentText(Constante.alert2);
								alert.setHeaderText(null);
								alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
								alert.showAndWait();
								isOkGenerate = false;
								files = new ArrayList<File>();
								filesB = new ArrayList<File>();
								listF = new ArrayList<File>();
								listFR = new ArrayList<File>();
								finalFiles = new ArrayList<File>();
								list.getCheckModel().clearChecks();
								listB.getCheckModel().clearChecks();
								list.getItems().clear();
								listB.getItems().clear();
								selectAll.setSelected(false);
								hb.setVisible(false);
							}
						} catch (final FileNotFoundException e) {
							e.printStackTrace();
						} catch (final IOException e) {
							e.printStackTrace();
						} catch (final TransformerException e) {
							e.printStackTrace();
						}
					}
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

		listB.setStyle(Constante.style9);
		list.setStyle(Constante.style9);

		final SplitPane sp = new SplitPane();
		sp.setStyle(Constante.style10);
		sp.setOrientation(Orientation.HORIZONTAL);
		sp.setDividerPositions(0f, 0.9f);

		final VBox root1 = new VBox(hbox1, hbox2, hbox3);
		root1.setStyle(Constante.style9);
		sp.getItems().addAll(hBoxImg, root1);

		final VBox root = new VBox(menuBar, sp, hbox, hb, pane);

		button10.setPrefWidth(130);
		button10.setPrefHeight(30);
		button10.setMinHeight(30);
		button10.setMaxHeight(30);
		button10.setStyle(Constante.style1);
		button10.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				runTask(taskUpdateStage, progress);
				Platform.runLater(() -> {
					if (files.isEmpty() && listB.getCheckModel().isEmpty()) {
						final Alert alert = new Alert(AlertType.ERROR);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
						dialogPane.getStyleClass().add(Constante.dialog);
						dialogPane.setMinHeight(130);
						dialogPane.setMaxHeight(130);
						dialogPane.setPrefHeight(130);
						alert.setContentText(Constante.alert3);
						alert.setHeaderText(null);
						alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
						alert.showAndWait();
					} else {
						List<File> lfileFinal = new ArrayList<File>();
						listFR = new ArrayList<File>();
						ObservableList<File> lfile = listB.getCheckModel().getCheckedItems();
						if (lfile != null) {
							if (!lfile.isEmpty()) {
								for (File f : lfile) {
									try {
										Files.copy(f.toPath(), Path.of(Constante.textFieldPS + f.getName()),
												StandardCopyOption.REPLACE_EXISTING);
										lfileFinal.add(f);
									} catch (final IOException e) {
										e.printStackTrace();
									}
								}
								final File[] list = new File(Constante.textFieldPS).listFiles();
								for (File file : list) {
									if (!file.isDirectory()) {
										for (File f : lfileFinal) {
											if (file.getName().equals(f.getName()) && !listFR.contains(f)) {
												listFR.add(file);
											}
										}
									}
								}
								filesB = new ArrayList<File>();
								listB.getItems().clear();
							}
						}
						for (final File file : files) {
							final String ext = getExtension(file.getAbsolutePath());
							if (ext.equals(Constante.xlsm) || ext.equals(Constante.xlsx)) {
								// Create a Workbook instance
								final Workbook workbook = new Workbook();
								// Load an Excel file
								workbook.loadFromFile(file.getAbsolutePath());
								final Worksheet worksheet = workbook.getWorksheets().get(3);
								final Worksheet worksheet1 = workbook.getWorksheets().get(6);
								List<RetrieveValueSetResponse> listR = new ArrayList<RetrieveValueSetResponse>();
								// Get the row count
								final int maxRow = worksheet.getLastRow();
								// Get the column count
								final int maxColumn = worksheet.getLastColumn();
								// Loop through the rows
								for (int row = 2; row <= maxRow; row++) {
									RetrieveValueSetResponse response = new RetrieveValueSetResponse();
									// Loop through the columns
									for (int col = 1; col <= maxColumn; col++) {
										// Get the current cell
										CellRange cell = worksheet.getCellRange(row, col);
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
									}
									if (response.getValueSetOID() != null) {
										listR.add(response);
									}
								}

								// Get the row count
								final int maxRow1 = worksheet1.getLastRow();
								// Get the column count
								final int maxColumn1 = worksheet1.getLastColumn();
								// Loop through the rows
								for (int row = 2; row <= maxRow1; row++) {
									RetrieveValueSetResponse response = new RetrieveValueSetResponse();
									// Loop through the columns
									for (int col = 1; col <= maxColumn1; col++) {
										// Get the current cell
										final CellRange cell = worksheet1.getCellRange(row, col);
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
									}
									if (response.getValueSetOID() != null) {
										listR.add(response);
									}
								}

								Map<String, List<RetrieveValueSetResponse>> resultMap = listR.stream()
										.collect(Collectors.groupingBy(RetrieveValueSetResponse::getValueSetOID));

								Iterator<?> iterator = resultMap.entrySet().iterator();
								while (iterator.hasNext()) {
									@SuppressWarnings("rawtypes")
									Map.Entry mapentry = (Map.Entry) iterator.next();
									@SuppressWarnings("unchecked")
									List<RetrieveValueSetResponse> result2 = new ArrayList<RetrieveValueSetResponse>(
											(List<RetrieveValueSetResponse>) mapentry.getValue());
									CreateXMLFile.createXMLFile(result2, textFieldPS.getText());
									final File f = CreateXMLFile.getCreatedFile();
									listF.add(f);
								}
								workbook.dispose();
							}
						}
						for (final File pathname : listF) {
							files.add(pathname);
						}

						for (final File pathname : listFR) {
							filesB.add(pathname);
						}

						if (files != null) {
							final ObservableList<File> oblist = FXCollections.observableArrayList();
							for (int i = 0; i < files.size(); i++) {
								if (files.get(i).getName().endsWith("xml")) {
									oblist.add(files.get(i));
								}
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
						if (filesB != null) {
							final ObservableList<File> oblist = FXCollections.observableArrayList();
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
								public void onChanged(
										javafx.collections.ListChangeListener.Change<? extends Integer> c) {
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
							selectAll.setSelected(true);
							listB.getCheckModel().checkAll();

						}
						final Alert alert = new Alert(AlertType.INFORMATION);
						final DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
						dialogPane.getStyleClass().add(Constante.dialog);
						dialogPane.setMinHeight(130);
						dialogPane.setMaxHeight(130);
						dialogPane.setPrefHeight(130);
						alert.setContentText(Constante.alert2);
						alert.setHeaderText(null);
						alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
						alert.showAndWait();
						isOkGenerate = false;
					}
				});
			}
		});

		list.setPrefHeight(650);

		// scene principal du convertisseur
		final Scene scene = new Scene(root, Color.BEIGE);
		scene.getRoot().getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream(Constante.photo)));
		stage.setTitle(Constante.name);
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
				if (thirdStage != null) {
					if (thirdStage.isShowing()) {
						thirdStage.close();
						textLogin.setText("");
						textPwd.setText("");
					}
				}
			}
		});

		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
					Number newSceneWidth) {
				if (newSceneWidth.doubleValue() >= 1509 && newSceneWidth.doubleValue() < 1632) { // ecran 16 pouces
					button1.setStyle(Constante.style1);
					button10.setStyle(Constante.style1);
					button2.setStyle(Constante.style1);
					button3.setStyle(Constante.style1);
					button4.setStyle(Constante.style1);
					buttonDownload.setStyle(Constante.style1);
					buttonDownload.setPrefWidth(250);
					buttonDownload.setPrefHeight(30);
					buttonDownload.setMinHeight(30);
					buttonDownload.setMaxHeight(30);
					buttonTermino.setStyle(Constante.style1);
					buttonTermino.setPrefWidth(230);
					buttonTermino.setPrefHeight(30);
					buttonTermino.setMinHeight(30);
					buttonTermino.setMaxHeight(30);
					buttonTermino1.setPrefWidth(200);
					buttonTermino1.setPrefHeight(30);
					buttonTermino1.setMinHeight(30);
					buttonTermino1.setMaxHeight(30);
					buttonTermino1.setStyle(Constante.style1);
					chooseFile.setPrefWidth(150);
					chooseFile.setPrefHeight(30);
					chooseFile.setMinHeight(30);
					chooseFile.setMaxHeight(30);
					chooseFile.setStyle(Constante.style1);
					textPwd.setStyle(Constante.style8);
					textLogin.setStyle(Constante.style8);
					labelPwd.setStyle(Constante.style5);
					labelLog.setStyle(Constante.style5);
					firstTitledPane.setStyle(Constante.style5);
					labelContent.setStyle(Constante.style5);
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
					label.setStyle(Constante.style5);
					lContent.setStyle(Constante.style5);
					label1.setStyle(Constante.style5);
					label2.setStyle(Constante.style5);
					textField2.setStyle(Constante.style5);
					labelL.setStyle(Constante.style5);
					textFieldL.setStyle(Constante.style5);
					labelT.setStyle(Constante.style5);
					textFieldT.setStyle(Constante.style5);
					labelP.setStyle(Constante.style5);
					textFieldP.setStyle(Constante.style5);
					labelPS.setStyle(Constante.style5);
					textFieldPS.setStyle(Constante.style5);
					labelUrl.setStyle(Constante.style5);
					textFieldUrl.setPrefWidth(680);
					textFieldUrl.setStyle(Constante.style5);
					field.setStyle(Constante.style5);
					labelEmpty.setStyle(Constante.style5);
					comboBox.setStyle(Constante.style5);
					titledPane.setStyle(Constante.style5);
					picker.setStyle(Constante.style5);
					file.setStyle(Constante.style2);
					item.setStyle(Constante.style2);
					imageView.setFitWidth(120);
					imageView.setFitHeight(120);
					file.setStyle(Constante.style2);
					item.setStyle(Constante.style2);
					item1.setStyle(Constante.style2);
					apropos.setStyle(Constante.style2);
					item2.setStyle(Constante.style2);

				}
				if (newSceneWidth.doubleValue() >= 1632 && newSceneWidth.doubleValue() <= 1728) { // ecran 17 pouces
					button1.setStyle(Constante.style3);
					button10.setStyle(Constante.style3);
					button2.setStyle(Constante.style3);
					button3.setStyle(Constante.style3);
					button4.setStyle(Constante.style3);
					buttonDownload.setStyle(Constante.style3);
					buttonTermino.setStyle(Constante.style3);
					buttonDownload.setPrefWidth(280);
					buttonDownload.setPrefHeight(50);
					buttonDownload.setMinHeight(50);
					buttonDownload.setMaxHeight(50);
					buttonTermino.setPrefWidth(260);
					buttonTermino.setPrefHeight(50);
					buttonTermino.setMinHeight(50);
					buttonTermino.setMaxHeight(50);
					buttonTermino1.setPrefWidth(230);
					buttonTermino1.setPrefHeight(50);
					buttonTermino1.setMinHeight(50);
					buttonTermino1.setMaxHeight(50);
					buttonTermino1.setStyle(Constante.style3);
					chooseFile.setPrefWidth(180);
					chooseFile.setPrefHeight(50);
					chooseFile.setMinHeight(50);
					chooseFile.setMaxHeight(50);
					chooseFile.setStyle(Constante.style3);
					textPwd.setStyle(Constante.style11);
					textLogin.setStyle(Constante.style11);
					labelPwd.setStyle(Constante.style11);
					labelLog.setStyle(Constante.style11);
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
					textFieldUrl.setPrefWidth(750);
					button4.setMinHeight(50);
					button4.setMaxHeight(50);
					list.setPrefWidth((newSceneWidth.doubleValue() - 10) / 2);
					listB.setPrefWidth((newSceneWidth.doubleValue() - 10) / 2);
					pane.setPrefWidth(width);
					label.setStyle(Constante.style11);
					lContent.setStyle(Constante.style11);
					label1.setStyle(Constante.style11);
					label2.setStyle(Constante.style11);
					textField2.setStyle(Constante.style11);
					labelL.setStyle(Constante.style11);
					textFieldL.setStyle(Constante.style11);
					labelT.setStyle(Constante.style11);
					textFieldT.setStyle(Constante.style11);
					labelP.setStyle(Constante.style11);
					textFieldP.setStyle(Constante.style11);
					labelPS.setStyle(Constante.style11);
					textFieldPS.setStyle(Constante.style11);
					titledPane.setStyle(Constante.style11);
					labelUrl.setStyle(Constante.style11);
					textFieldUrl.setStyle(Constante.style11);
					firstTitledPane.setStyle(Constante.style11);
					labelContent.setStyle(Constante.style11);
					field.setStyle(Constante.style11);
					labelEmpty.setStyle(Constante.style11);
					comboBox.setStyle(Constante.style11);
					picker.setStyle(Constante.style11);
					file.setStyle(Constante.style12);
					item.setStyle(Constante.style12);
					imageView.setFitWidth(140);
					imageView.setFitHeight(140);
					file.setStyle(Constante.style12);
					item.setStyle(Constante.style12);
					item1.setStyle(Constante.style12);
					apropos.setStyle(Constante.style12);
					item2.setStyle(Constante.style12);

				} else if (newSceneWidth.doubleValue() < 1509) {
					button1.setStyle(Constante.style13);
					button10.setStyle(Constante.style13);
					button2.setStyle(Constante.style13);
					button3.setStyle(Constante.style13);
					button4.setStyle(Constante.style13);
					buttonDownload.setStyle(Constante.style13);
					buttonTermino.setStyle(Constante.style13);
					buttonDownload.setPrefWidth(200);
					buttonDownload.setPrefHeight(20);
					buttonDownload.setMinHeight(20);
					buttonDownload.setMaxHeight(20);
					buttonTermino.setPrefWidth(160);
					buttonTermino.setPrefHeight(20);
					buttonTermino.setMinHeight(20);
					buttonTermino.setMaxHeight(20);
					buttonTermino1.setPrefWidth(130);
					buttonTermino1.setPrefHeight(20);
					buttonTermino1.setMinHeight(20);
					buttonTermino1.setMaxHeight(20);
					buttonTermino1.setStyle(Constante.style13);
					chooseFile.setPrefWidth(80);
					chooseFile.setPrefHeight(20);
					chooseFile.setMinHeight(20);
					chooseFile.setMaxHeight(20);
					chooseFile.setStyle(Constante.style13);
					textPwd.setStyle(Constante.style14);
					textLogin.setStyle(Constante.style14);
					labelPwd.setStyle(Constante.style14);
					labelLog.setStyle(Constante.style14);
					firstTitledPane.setStyle(Constante.style14);
					labelContent.setStyle(Constante.style14);
					textFieldUrl.setPrefWidth(550);
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
					label.setStyle(Constante.style14);
					lContent.setStyle(Constante.style14);
					label1.setStyle(Constante.style14);
					label2.setStyle(Constante.style14);
					textField2.setStyle(Constante.style14);
					labelL.setStyle(Constante.style14);
					textFieldL.setStyle(Constante.style14);
					labelT.setStyle(Constante.style14);
					textFieldT.setStyle(Constante.style14);
					labelP.setStyle(Constante.style14);
					textFieldP.setStyle(Constante.style14);
					titledPane.setStyle(Constante.style14);
					labelPS.setStyle(Constante.style14);
					textFieldPS.setStyle(Constante.style14);
					labelUrl.setStyle(Constante.style14);
					textFieldUrl.setStyle(Constante.style14);
					field.setStyle(Constante.style14);
					labelEmpty.setStyle(Constante.style14);
					comboBox.setStyle(Constante.style14);
					picker.setStyle(Constante.style14);
					file.setStyle(Constante.style15);
					item.setStyle(Constante.style15);
					imageView.setFitWidth(100);
					imageView.setFitHeight(100);
					file.setStyle(Constante.style15);
					item.setStyle(Constante.style15);
					item1.setStyle(Constante.style15);
					apropos.setStyle(Constante.style15);
					item2.setStyle(Constante.style15);
				}
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
		final InputStream is = classloader.getResourceAsStream(Constante.pretty);
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
		final URL url = new URL(urlStr);
		final ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		final FileOutputStream fos = new FileOutputStream(file);
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
	public static String removeExtension(final String fname) {
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
	public static void unzip(final String zipFilePath, final String destFilePath) throws IOException {
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
		final BufferedOutputStream buffered_Output_Stream = new BufferedOutputStream(new FileOutputStream(file_Path));
		byte[] bytes = new byte[BUFFER_SIZE];
		int read_Byte = 0;
		while ((read_Byte = zip_Input_Stream.read(bytes)) != -1) {
			buffered_Output_Stream.write(bytes, 0, read_Byte);
		}
		buffered_Output_Stream.close();
		return file_Path;
	}

	/**
	 * 
	 * @param filePath
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private List<String> parse(final String filePath) throws IOException, XMLStreamException {
		List<String> list = new ArrayList<String>();
		mapSnomed = new HashMap<String, String>();
		InputStream is = null;
		File inputFile = null;
		try {
			inputFile = new File(filePath);
			is = new FileInputStream(inputFile);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				boolean bid = false;

				public void startElement(final String uri, final String localName, final String qName,
						final Attributes attributes) throws SAXException {
					if (qName.equalsIgnoreCase(Constante.rdf_RDF)) {
						for (int i = 0; i < attributes.getLength(); i++) {
							String name = attributes.getLocalName(i);
							String val = attributes.getValue(i);
							mapSnomed.put(name, val);
						}
					}
					if (qName.equalsIgnoreCase(Constante.notation)) {
						bid = true;
					}
				}

				public void characters(char ch[], int start, int length) throws SAXException {
					if (bid) {
						String donnees = new String(ch, start, length);
						list.add(donnees);
						bid = false;
					}
				}

				public void endElement(String uri, String localName, String qName) throws SAXException {

					if (qName.equalsIgnoreCase(Constante.notation)) {
						bid = false;
					}

				}

			};
			saxParser.parse(is, handler);

		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}

		return list;
	}

	/**
	 * pretty Print
	 * 
	 * @param xml
	 * @throws Exception
	 */
	public static final File prettyPrint(final Document xml, final String path, final String dest) throws Exception {
		final Source source = new DOMSource(xml);
		final File f = new File(path + Constante.termino);
		if (!f.exists()) {
			f.mkdir();
		}
		if (new File(path + Constante.terminologie + dest + Constante.extRdf).exists()) {
			new File(path + Constante.terminologie + dest + Constante.extRdf).delete();
		}
		final File xmlFile = new File(path + Constante.terminologie + dest + Constante.extRdf);
		final StreamResult result = new StreamResult(
				new OutputStreamWriter(new FileOutputStream(xmlFile), Constante.utf8));
		final Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.ENCODING, Constante.utf8);
		xformer.setOutputProperty(OutputKeys.INDENT, Constante.yes);
		xformer.transform(source, result);
		return xmlFile;
	}

	/**
	 * removeAllChildren
	 * 
	 * @param node
	 */
	public static org.w3c.dom.Node removeAllChildren(final org.w3c.dom.Node node) {
		removeAllChilderenWithoutHeader(node);
		return node;
	}

	/**
	 * removeAllChilderenWithoutHeader
	 * 
	 * @param node
	 * @param remainChildCount
	 */
	public static void removeAllChilderenWithoutHeader(final org.w3c.dom.Node node) {
		final NodeList childNodes = node.getChildNodes();
		List<org.w3c.dom.Node> removeNodeList = new ArrayList<org.w3c.dom.Node>();
		for (int i = 0; i < childNodes.getLength(); i++) {
			removeNodeList.add(childNodes.item(i));
		}
		for (final org.w3c.dom.Node childNode : removeNodeList) {
			node.removeChild(childNode);
		}
	}

	/**
	 * initialize screen
	 * 
	 * @param file
	 */
	public void init(final File file) {
		final Alert alert = new Alert(AlertType.ERROR);
		final DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
		dialogPane.getStyleClass().add(Constante.dialog);
		dialogPane.setMinHeight(150);
		dialogPane.setMaxHeight(150);
		dialogPane.setPrefHeight(150);
		alert.setContentText(Constante.alert9 + Constante.retourChariot + file.getName());
		alert.setHeaderText(null);
		alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
		alert.showAndWait();
		files = new ArrayList<File>();
		filesB = new ArrayList<File>();
		listF = new ArrayList<File>();
		listFR = new ArrayList<File>();
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
	private String readFileContents(final InputStream file) throws IOException {

		final BufferedReader br = new BufferedReader(new InputStreamReader(file));
		String singleString = null;

		try {
			final StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(Constante.retourChariot);
				line = br.readLine();
			}
			singleString = sb.toString();
		} finally {
			br.close();
		}
		return singleString;
	}

	/**
	 * deleteDirectory
	 * 
	 * @param directoryToBeDeleted
	 * @return
	 */
	public void deleteDirectory(final File directoryToBeDeleted) {
		try {
			FileUtils.cleanDirectory(directoryToBeDeleted);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
