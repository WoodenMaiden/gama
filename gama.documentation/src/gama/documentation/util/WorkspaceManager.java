/*******************************************************************************************************
 *
 * WorkspaceManager.java, in gama.documentation, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.documentation.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import gama.annotations.precompiler.doc.utils.Constants;
import gama.annotations.precompiler.doc.utils.XMLUtils;

/**
 * @author bgaudou
 *
 */
public class WorkspaceManager {

	/** The ws file. */
	private final File wsFile;

	/** The is local. */
	private final boolean isLocal;

	/**
	 * Instantiates a new workspace manager.
	 *
	 * @param location
	 *            the location
	 * @param local
	 *            the local
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public WorkspaceManager(final String location, final boolean local) throws IOException {
		File mainFile = new File(new File(location).getCanonicalPath());
		wsFile = new File(mainFile.getParent());
		isLocal = local;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() { return wsFile; }

	/**
	 * Gets the plugin folder.
	 *
	 * @param plugin
	 *            the plugin
	 * @return the plugin folder
	 */
	public File getPluginFolder(final String plugin) {
		return new File(wsFile.getAbsolutePath() + File.separator + plugin);
	}

	/**
	 * Gets the feature file.
	 *
	 * @param feature
	 *            the feature
	 * @return the feature file
	 */
	public File getFeatureFile(final String feature) {
		return new File(wsFile.getAbsolutePath() + File.separator + feature + File.separator + "feature.xml");
	}

	/**
	 * Gets the doc file.
	 *
	 * @param plugin
	 *            the plugin
	 * @return the doc file
	 */
	public File getDocFile(final String plugin) {
		return new File(wsFile.getAbsolutePath() + File.separator + plugin + File.separator + Constants.DOCGAMA_FILE);
	}

	/**
	 * Gets the product file.
	 *
	 * @return the product file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public File getProductFile() throws IOException {
		File productFile = new File(wsFile.getAbsolutePath() + File.separator + Constants.RELEASE_APPLICATION
				+ File.separator + Constants.RELEASE_PRODUCT);
		if (!productFile.exists()) throw new IOException("Product file do not exist");
		return productFile;
	}

	/**
	 * @param pluginName
	 * @return true whether the pluginName plugin exists in the workspace
	 */
	public boolean isGAMAPlugin(final String pluginName) {
		File plugin = getPluginFolder(pluginName);
		return plugin.exists();
	}

	/**
	 * Checks if is feature.
	 *
	 * @param pluginName
	 *            the plugin name
	 * @return true, if is feature
	 */
	public boolean isFeature(final String pluginName) {
		if (!isGAMAPlugin(pluginName)) return false;
		File feature = getFeatureFile(pluginName);
		return feature.exists();
	}

	/**
	 * Checks for plugin doc.
	 *
	 * @param pluginName
	 *            the plugin name
	 * @return true, if successful
	 */
	public boolean hasPluginDoc(final String pluginName) {
		File pluginDoc = getDocFile(pluginName);
		return pluginDoc.exists();
	}

	/**
	 * This method will parse the Eclipse workspace to find project that have a file "docGama.xml".
	 *
	 * @return It will then return the HashMap containing all their project name with their associated files associated
	 * @throws IOException
	 */
	public HashMap<String, File> getAllDocFiles() {
		HashMap<String, File> hmFilesPackages = new HashMap<>();

		for (File f : wsFile.listFiles()) {
			File docGamaFile = new File(f.getAbsolutePath() + File.separator + Constants.DOCGAMA_FILE);
			if (docGamaFile.exists()) { hmFilesPackages.put(f.getName(), docGamaFile); }
		}
		return hmFilesPackages;
	}

	/**
	 * Gets the all doc files local.
	 *
	 * @return the all doc files local
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public HashMap<String, File> getAllDocFilesLocal() {
		HashMap<String, File> hmFilesPackages = new HashMap<>();

		for (File f : wsFile.listFiles()) {
			File docGamaFile = new File(f.getAbsolutePath() + File.separator + Constants.DOCGAMA_FILE_LOCAL);
			if (docGamaFile.exists()) { hmFilesPackages.put(f.getName(), docGamaFile); }
		}
		return hmFilesPackages;
	}

	/**
	 * Gets the product doc files.
	 *
	 * @return the product doc files
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 */
	public HashMap<String, File> getProductDocFiles() throws IOException, ParserConfigurationException, SAXException {
		HashMap<String, File> hmFilesPackages = isLocal ? getAllDocFilesLocal() : getAllDocFiles();
		List<String> pluginsProduct = getAllGAMAPluginsInProduct();
		HashMap<String, File> hmFilesRes = new HashMap<>();

		for (Entry<String, File> eSF : hmFilesPackages.entrySet()) {
			if (pluginsProduct.contains(eSF.getKey())) { hmFilesRes.put(eSF.getKey(), eSF.getValue()); }
		}

		return hmFilesRes;
	}

	/**
	 * Gets the extensions doc files.
	 *
	 * @return the extensions doc files
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 */
	public HashMap<String, File> getExtensionsDocFiles()
			throws IOException, ParserConfigurationException, SAXException {
		HashMap<String, File> hmFilesPackages = isLocal ? getAllDocFilesLocal() : getAllDocFiles();
		List<String> pluginsProduct = getAllGAMAPluginsInProduct();
		HashMap<String, File> hmFilesRes = new HashMap<>();

		for (Entry<String, File> eSF : hmFilesPackages.entrySet()) {
			if (!pluginsProduct.contains(eSF.getKey())) { hmFilesRes.put(eSF.getKey(), eSF.getValue()); }
		}

		return hmFilesRes;
	}

	/**
	 * From a product file, get all the features
	 *
	 * @param feature
	 * @return the list of the name of all features included in the product
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private List<String> getPluginsFromProduct(final File product)
			throws ParserConfigurationException, SAXException, IOException {
		ArrayList<String> listPlugins = new ArrayList<>();

		// Creation of the DOM source
		org.w3c.dom.Document document = XMLUtils.createDoc(product);

		// Test whether the product is based on features (we do not consider plugin-based product)
		NodeList nLProduct = document.getElementsByTagName("product");
		org.w3c.dom.Element eltProduct = (org.w3c.dom.Element) nLProduct.item(0);
		if (!"features".equals(eltProduct.getAttribute("type")))
			throw new IOException("Plugin-based products are not managed");

		// We get the features from the product
		NodeList nLFeatures = document.getElementsByTagName("feature");
		for (int j = 0; j < nLFeatures.getLength(); j++) {
			org.w3c.dom.Element eltFeature = (org.w3c.dom.Element) nLFeatures.item(j);
			listPlugins.add(eltFeature.getAttribute("id"));
		}

		return listPlugins;
	}

	/**
	 * From a feature file, get all the plugins
	 *
	 * @param feature
	 * @return the list of the name of all plugins included in the feature
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private List<String> getPluginsFromFeature(final File feature)
			throws ParserConfigurationException, SAXException, IOException {
		ArrayList<String> listPlugins = new ArrayList<>();

		// Creation of the DOM source
		org.w3c.dom.Document document = XMLUtils.createDoc(feature);

		// We get the plugins from the feature
		NodeList nlPlugins = document.getElementsByTagName("plugin");
		for (int j = 0; j < nlPlugins.getLength(); j++) {
			org.w3c.dom.Element eltPlugin = (org.w3c.dom.Element) nlPlugins.item(j);
			listPlugins.add(eltPlugin.getAttribute("id"));
		}

		// We get the included features from the feature
		NodeList nlFeatures = document.getElementsByTagName("includes");
		for (int j = 0; j < nlFeatures.getLength(); j++) {
			org.w3c.dom.Element eltFeature = (org.w3c.dom.Element) nlFeatures.item(j);
			listPlugins.add(eltFeature.getAttribute("id"));
		}

		return listPlugins;
	}

	/**
	 * Gets the all GAMA plugins in product.
	 *
	 * @return the all GAMA plugins in product
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<String> getAllGAMAPluginsInProduct() throws ParserConfigurationException, SAXException, IOException {
		ArrayList<String> listPlugins = new ArrayList<>();
		List<String> initPluginList = getPluginsFromProduct(getProductFile());
		for (String plugin : initPluginList) { listPlugins.addAll(getList(plugin)); }

		return listPlugins;
	}

	/**
	 * Gets the list.
	 *
	 * @param plugin
	 *            the plugin
	 * @return the list
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private List<String> getList(final String plugin) throws ParserConfigurationException, SAXException, IOException {
		ArrayList<String> listPlugins = new ArrayList<>();
		if (isFeature(plugin)) {
			List<String> pluginsFromFeature = getPluginsFromFeature(getFeatureFile(plugin));
			for (String name : pluginsFromFeature) { listPlugins.addAll(getList(name)); }

		} else if (isGAMAPlugin(plugin)) { listPlugins.add(plugin); }
		return listPlugins;
	}

	/*****************************************************************************************************
	 *
	 *
	 */
	public ArrayList<String> getModelLibrary() {
		ArrayList<String> modelList =
				litRep(wsFile.getAbsolutePath() + File.separator + "gama.library" + File.separator + "models");
		return modelList;
	}

	/**
	 * Lit rep.
	 *
	 * @param dir
	 *            the dir
	 * @return the array list
	 */
	private static ArrayList<String> litRep(final String dir) {
		ArrayList<String> listFiles = new ArrayList<>();
		File rep = new File(dir);

		if (rep.isDirectory()) {
			String t[] = rep.list();

			if (t != null) {
				for (String fName : t) {
					ArrayList<String> newList = litRep(rep.getAbsolutePath() + File.separator + fName);
					listFiles.addAll(newList);
				}
			}
		} else if ("gaml".equals(WorkspaceManager.getFileExtension(rep.getAbsolutePath()))) {
			listFiles.add(rep.getAbsolutePath());
		}

		return listFiles;
	}

	/**
	 * Gets the file extension.
	 *
	 * @param fileName
	 *            the file name
	 * @return the file extension
	 */
	private static String getFileExtension(final String fileName) {
		String extension = null;
		try {
			extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return extension;
	}

	/**
	 * The main method.
	 *
	 * @param arg
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 */
	public static void main(final String[] arg) throws IOException, ParserConfigurationException, SAXException {
		WorkspaceManager ws = new WorkspaceManager(".", false);
		List<String> l = ws.getAllGAMAPluginsInProduct();
		for (String name : l) { System.out.println(name); }
		System.out.println("----------");

		HashMap<String, File> hm = ws.getAllDocFiles();
		for (Entry<String, File> e : hm.entrySet()) { System.out.println(e.getKey()); }
		System.out.println("----------");
		hm = ws.getProductDocFiles();
		for (Entry<String, File> e : hm.entrySet()) { System.out.println(e.getKey()); }
		System.out.println("----------");
		hm = ws.getExtensionsDocFiles();
		for (Entry<String, File> e : hm.entrySet()) { System.out.println(e.getKey()); }
		System.out.println("----------");

		System.out.println("----------");

		l = ws.getModelLibrary();
		for (String name : l) { System.out.println(name); }
		System.out.println(l.size());
		System.out.println("----------");

	}
}
