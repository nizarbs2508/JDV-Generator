package com.ans.jaxb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckListView;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;

public class CheckListViewTest extends Application {

	private static List<File> files;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Test");
		files = new ArrayList<File>();
		File[] pathnames;
		File f = new File(Constante.textFieldPS);
		pathnames = f.listFiles();
		for (File pathname : pathnames) {
			files.add(pathname);
		}

		CheckListView<File> checkListView = new CheckListView<File>();

		ObservableList<File> oblist = FXCollections.observableArrayList();
		for (int i = 1; i <= files.size(); i++) {
			oblist.add(files.get(i));
		}
		checkListView.setItems(oblist);

		checkListView.setCellFactory(lv -> new CheckBoxListCell<File>(checkListView::getItemBooleanProperty) {
			@Override
			public void updateItem(File employee, boolean empty) {
				super.updateItem(employee, empty);
				setText(employee == null ? "" : String.format(employee.getAbsolutePath()));
			}
		});

		checkListView.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<Integer>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Integer> c) {
				while (c.next()) {
					if (c.wasAdded()) {
						for (int i : c.getAddedSubList()) {
							System.out.println(checkListView.getItems().get(i) + " selected");
						}
					}
					if (c.wasRemoved()) {
						for (int i : c.getRemoved()) {
							System.out.println(checkListView.getItems().get(i) + " deselected");
						}
					}
				}
			}
		});

		Scene scene = new Scene(checkListView);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
