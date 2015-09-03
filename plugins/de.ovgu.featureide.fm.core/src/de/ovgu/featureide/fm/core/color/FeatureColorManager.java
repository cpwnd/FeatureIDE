/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2015  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.core.color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import de.ovgu.featureide.fm.core.FMCorePlugin;
import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;

/**
 * Manages colors assigned to features.
 * 
 * @author Jens Meinicke
 */
public class FeatureColorManager {
	
	private static final Map<IProject, Map<String, ColorScheme>> colorSchemes = new HashMap<>();

	/**
	 * Returns the current color of the given feature.
	 */
	public static FeatureColor getColor(Feature feature) {
		return getCurrentColorScheme(feature).getColor(feature);
	}

	
	/**
	 * Sets the feature color to the given index.
	 */
	public static void setColor(Feature feature, int index) {
		setColor(feature, FeatureColor.getColor(index));	
	}
	
	/**
	 * Sets the feature color to the given color.
	 */
	public static void setColor(Feature feature, FeatureColor color) {
		getCurrentColorScheme(feature).setColor(feature, color);
		writeColors(getProject(feature), getCurrentColorScheme(feature));
	}

	/**
	 * Checks whether the current scheme is the default scheme without colors.
	 */
	public static boolean isDefault(FeatureModel featureModel) {
		return getCurrentColorScheme(featureModel).isDefault();
	}

	/**
	 * Deletes the profile with the given name.
	 */
	public static void removeProfile(FeatureModel fm, String collName) {
		throw new RuntimeException("TODO implement");
	}

	/**
	 * Returns the current color scheme.
	 */
	public static ColorScheme getCurrentColorScheme(Feature feature) {
		return getCurrentColorScheme(feature.getFeatureModel());
	}

	/**
	 * Returns the current color scheme.
	 */
	public static ColorScheme getCurrentColorScheme(FeatureModel featureModel) {
		IProject project = getProject(featureModel);
		if (!colorSchemes.containsKey(project)) {
			initColorSchemes(project);
		}
		Map<String, ColorScheme> currentSchemes = colorSchemes.get(project);
		
		for (ColorScheme cs : currentSchemes.values()) {
			if (cs.isCurrent()) {
				return cs;
			}
		}
		throw new RuntimeException("at least one schould be active");
	}

	/**
	 * Initializes and loads all color schemes for the given project.
	 */
	private static void initColorSchemes(IProject project) {
		Map<String, ColorScheme> newEntry = new HashMap<>();
		newEntry.put(DefaultColorScheme.defaultName, new DefaultColorScheme());
		colorSchemes.put(project, newEntry);
		
		IFolder profileFolder = project.getFolder(".profiles");
		if (!profileFolder.exists()) {
			return;
		}
		try {
			for (IResource res : profileFolder.members()) {
				if (res instanceof IFile && res.getFileExtension().equals("profile")) {
					readColors(newEntry, res);
				}
			}
		} catch (CoreException e) {
			FMCorePlugin.getDefault().logError(e);
		}
	}

	/**
	 * Reads the colors from the given file.
	 */
	private static void readColors(Map<String, ColorScheme> newEntry, IResource res) {
		try (BufferedReader in = new BufferedReader(new FileReader(new File(res.getLocationURI())))) {
			final String name = res.getName().substring(0, res.getName().lastIndexOf('.'));
			ColorScheme newCs = new ColorScheme(name);
			newEntry.put(newCs.getName(), newCs);
			String line = in.readLine();
			if (line.equals("true")) {
				setActive(res.getProject(), name, false);
			}
			
			while ((line = in.readLine()) != null) {
				String[] split = line.split("=");
				newCs.setColor(split[0], FeatureColor.valueOf(split[1]));
			}
		} catch (IOException e) {
			FMCorePlugin.getDefault().logError(e);
		}
	}
	
	/**
	 * Writes the given color scheme to a file.
	 */
	private static void writeColors(IProject project, ColorScheme colorScheme) {
		if (colorScheme.isDefault()) {
			return;
		}
		IFolder profileFolder = project.getFolder(".profiles");
		if (!profileFolder.exists()) {
			try {
				profileFolder.create(true, true, new NullProgressMonitor());
			} catch (CoreException e) {
				FMCorePlugin.getDefault().logError(e);
			}
		}
		IFile file = profileFolder.getFile(colorScheme.getName() + ".profile");
		if (!file.exists()) {
			try {
				new File(file.getLocationURI()).createNewFile();
			} catch (IOException e) {
				FMCorePlugin.getDefault().logError(e);
			}
		}
		
		try (PrintWriter out = new PrintWriter(new FileWriter(new File(file.getLocationURI()), false), true)) {
			out.println(colorScheme.isCurrent());
			for (Entry<String, FeatureColor> entry : colorScheme.getColors().entrySet()) {
				out.print(entry.getKey());
				out.print('=');
				out.println(entry.getValue());
			}
			
			file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
		} catch (IOException | CoreException e) {
			FMCorePlugin.getDefault().logError(e);
		}
	}

	/**
	 * Returns all profiles for the given model.
	 */
	public static Collection<ColorScheme> getProfiles(FeatureModel featureModel) {
		IProject project = getProject(featureModel);
		if (!colorSchemes.containsKey(project)) {
			initColorSchemes(project);
		}
		return colorSchemes.get(project).values();
	}

	/**
	 * Gets the associated project for the given feature.
	 */
	private static IProject getProject(Feature feature) {
		return getProject(feature.getFeatureModel());
	}
	
	private static IProject getProject(FeatureModel featureModel) {
		File file = featureModel.xxxGetSourceFile();
		IWorkspace workspace= ResourcesPlugin.getWorkspace(); 
		IPath location= Path.fromOSString(file.getAbsolutePath()); 
		IFile iFile= workspace.getRoot().getFileForLocation(location);
		return iFile.getProject();
	}

	/**
	 * Creates a new color scheme for with the given name.
	 */
	public static void newColorScheme(FeatureModel featureModel, String csName) {
		IProject project = getProject(featureModel);
		ColorScheme newColorScheme = new ColorScheme(csName);
		Map<String, ColorScheme> currentSchemes = colorSchemes.get(project);
		if (currentSchemes.containsKey(csName)) {
			throw new RuntimeException("scheme " + csName + " already exists");
		}
		currentSchemes.put(csName, newColorScheme);
	}

	/**
	 * Checks whether there is a color scheme with the given name.
	 */
	public static boolean hasColorScheme(FeatureModel featureModel, String csName) {
		IProject project = getProject(featureModel);
		return colorSchemes.get(project).containsKey(csName);
	}

	/**
	 * Changes the name of the color scheme.
	 */
	public static void renameProfile(FeatureModel featureModel, String oldName, String newName) {
		throw new RuntimeException("TODO implement");
	}

	/**
	 * Activates the color scheme with the given name.
	 */
	public static void setActive(FeatureModel fm, String collName) {
		IProject project = getProject(fm);
		setActive(project, collName, true);
	}
	
	/**
	 * Activates the color scheme with the given name.
	 */
	public static void setActive(IProject project, String collName, boolean write) {
		Map<String, ColorScheme> currentSchemes = colorSchemes.get(project);
		if (!currentSchemes.containsKey(collName)) {
			throw new RuntimeException("tried to activate scheme " + collName);
		}
		for (Entry<String, ColorScheme> cs : currentSchemes.entrySet()) {
			ColorScheme value = cs.getValue();
			if (cs.getKey().equals(collName)) {
				if (!value.isCurrent()) {
					value.setCurrent(true);
					if (write) {
						writeColors(project, value);
					}
				}
			} else {
				if (value.isCurrent()) {
					value.setCurrent(false);
					if (write) {
						writeColors(project, value);
					}
				}
			}
			
		}
	}


	/**
	 * Performs the feature renaming.
	 */
	public static void renameFeature(FeatureModel model, String oldName, String newName) {
		throw new RuntimeException("TODO implement");
	}
}
