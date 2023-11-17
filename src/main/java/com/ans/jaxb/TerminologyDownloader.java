package com.ans.jaxb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;

/**
 * TerminologyDownloader
 * 
 * @author bensalem Nizar
 */
public class TerminologyDownloader {
	private static String username = "";
	private static String password = "";
	private static final String client_id = Constante.clientId;
	private static final String client_secret = "";
	private static File newFile = null;
	private String tokenurl;
	private String downloadurl;
	private String tokenopen;

	/**
	 * main principale
	 * @param textLogin
	 * @param textPwd
	 * @param listTerminology
	 * @param map
	 * @param tokenurl
	 * @param downloadurl
	 * @param tokenopen
	 * @return
	 */
	public List<File> main(final String textLogin, final String textPwd, final List<String> listTerminology,
			final Map<String, String> map, String tokenurl, String downloadurl, String tokenopen) {
		this.tokenurl = tokenurl;
		this.downloadurl = downloadurl;
		this.tokenopen = tokenopen;
		username = textLogin.trim();
		password = textPwd.trim();
		List<String> terminologyIdList = new ArrayList<String>();
		if (map != null) {
			if (!map.isEmpty()) {
				for (@SuppressWarnings("rawtypes")
				Map.Entry mapentry : map.entrySet()) {
					if (listTerminology != null) {
						if (!listTerminology.isEmpty()) {
							for (String termino : listTerminology) {
								if (termino.equalsIgnoreCase((String) mapentry.getKey())) {
									terminologyIdList.add((String) mapentry.getValue());
								}
							}
						}
					}
				}
			}
		}

		final String outputDir = Constante.textFieldRDF;
		if (!new File(outputDir).exists()) {
			new File(outputDir).mkdir();
		}
		final List<File> list = new ArrayList<File>();
		for (final String str1 : terminologyIdList) {
			String zipFilename = null;
			String filename = null;
			final String latestVersion = getLatestVersionDetails(str1.trim());
			if (latestVersion == null) {
				System.out.println("No zip file available for " + str1.trim() + " or failed to extract RDF.");
			} else {
				final String localFilename = Constante.textFieldRDF + str1.trim() + "_" + latestVersion + ".zip";
				if (new File(localFilename).exists()) {
					new File(localFilename).delete();
				}

				zipFilename = downloadZip(str1.trim(), latestVersion);
				try {
					final String str = new File(zipFilename).getName();
					final int dotIndex = str.lastIndexOf('.');
					filename = str.substring(0, dotIndex);

					final File file = unzipFile(new File(zipFilename).getAbsolutePath(),
							new File(outputDir + filename));
					findFile(file);
					list.add(newFile);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	/**
	 * findFile
	 * @param file
	 */
	private static void findFile(final File file) {
		final File[] list = file.listFiles();
		if (list != null)
			for (final File fil : list) {
				if (fil.isDirectory() && fil.getName().equals("dat")) {
					findFile(fil);
				} else if (fil.getName().endsWith(".rdf")) {
					newFile = fil;
				}
			}
	}

	/**
	 * getLatestVersionDetails
	 * @param terminologyId
	 * @return
	 */
	private String getLatestVersionDetails(final String terminologyId) {
		final String token = getToken();
		final String tokenUrl = this.tokenurl + terminologyId;
		try {
			final URL url = new URL(tokenUrl);
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "Bearer " + token);
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				final InputStream inputStream = connection.getInputStream();
				final byte[] responseBytes = inputStream.readAllBytes();
				final String response = new String(responseBytes);
				final ObjectMapper mapper = new ObjectMapper();
				final List<Map<String, Object>> versions = mapper.readValue(response,
						new TypeReference<List<Map<String, Object>>>() {
						});
				if (!versions.isEmpty()) {
					final Map<String, Object> latestVersion = versions.stream()
							.max((v1, v2) -> LocalDateTime
									.parse((String) v1.get("publishedDate"), DateTimeFormatter.ISO_DATE_TIME)
									.compareTo(LocalDateTime.parse((String) v2.get("publishedDate"),
											DateTimeFormatter.ISO_DATE_TIME)))
							.orElse(null);
					if (latestVersion != null) {
						return (String) latestVersion.get("versionInfo");
					}
				}
			} else {
				System.out.println("No versions found for " + terminologyId);
			}
		} catch (final IOException e) {
			System.out.println(
					"An error occurred while fetching version details for " + terminologyId + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * downloadZip
	 * @param terminologyId
	 * @param version
	 * @return
	 */
	private String downloadZip(final String terminologyId, final String version) {
		final String token = getToken();
		final String downloadUrl = this.downloadurl + terminologyId + "&version=" + version
				+ "&licenceConsent=true&dataTransferConsent=true";
		final String localFilename = Constante.textFieldRDF + terminologyId + "_" + version + ".zip";
		try {
			final URL url = new URL(downloadUrl);
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "Bearer " + token);
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				final InputStream inputStream = connection.getInputStream();
				Files.copy(inputStream, Path.of(localFilename), StandardCopyOption.REPLACE_EXISTING);
				return localFilename;
			} else {
				System.out
						.println("An error occurred while downloading zip for " + terminologyId + ": " + responseCode);
			}
		} catch (final IOException e) {
			System.out.println("An error occurred while downloading zip for " + terminologyId + ": " + e.getMessage());
		}
		return null;
	}

	/**
	 * unzipFile
	 * @param fileZip
	 * @param destDir
	 * @return
	 * @throws IOException
	 */
	public static File unzipFile(final String fileZip, final File destDir) throws IOException {

		final byte[] buffer = new byte[1024];
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				if (new File(destDir, zipEntry.getName()).exists()) {
					new File(destDir, zipEntry.getName()).delete();
				}
				final File newFile = new File(destDir, zipEntry.getName());
				if (zipEntry.isDirectory()) {
					if (!newFile.isDirectory() && !newFile.mkdirs()) {
						throw new IOException("Failed to create directory " + newFile);
					}
				} else {
					// fix for Windows-created archives
					final File parent = newFile.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("Failed to create directory " + parent);
					}

					// write file content
					final FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				zipEntry = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();
		}
		return destDir;
	}

	/**
	 * getToken
	 * @return
	 */
	private String getToken() {
		final String tokenUrl = this.tokenopen;
		final String data = "grant_type=password&username=" + username + "&password=" + password + "&client_id="
				+ client_id + "&client_secret=" + client_secret;
		try {
			final URL url = new URL(tokenUrl);
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.getOutputStream().write(data.getBytes());

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				final InputStream inputStream = connection.getInputStream();
				final byte[] responseBytes = inputStream.readAllBytes();
				final String response = new String(responseBytes);
				final ObjectMapper mapper = new ObjectMapper();
				final Map<String, Object> responseJson = mapper.readValue(response,
						new TypeReference<Map<String, Object>>() {
						});

				return (String) responseJson.get("access_token");
			} else {
				System.out.println("An error occurred while getting token: " + responseCode);
				final Alert alert = new Alert(AlertType.ERROR);
				final DialogPane dialogPane = alert.getDialogPane();
				dialogPane.getStylesheets().add(getClass().getResource(Constante.style).toExternalForm());
				dialogPane.getStyleClass().add(Constante.dialog);
				dialogPane.setMinHeight(130);
				dialogPane.setMaxHeight(130);
				dialogPane.setPrefHeight(130);
				alert.setContentText(Constante.alert24);
				alert.setHeaderText(null);
				alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
				alert.showAndWait();
			}
		} catch (final IOException e) {
			System.out.println("An error occurred while getting token: " + e.getMessage());
		}
		return null;
	}
}
