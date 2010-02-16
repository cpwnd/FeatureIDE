/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2009  FeatureIDE Team, University of Magdeburg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package featureide.ui.views.collaboration.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;


/**
 * An instance of this class represents a role. 
 * It is necessary because every figure in GEF needs an associated model.
 * 
 * @author Constanze Adler
 */
public class Role {
	private String name;
	private Class parentClass;
	private Collaboration collaboration;
	private IPath path;
	
	/**
	 * @return the path
	 */
	public IPath getPath() {
		return path;
	}



	/**
	 * @param path the path to set
	 */
	public void setPath(IPath path) {
		this.path = path;
	}



	public Role(String name){
		this.name = name;
	}
	
	

	/**
	 * @return String Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return String Class where this role belongs to.
	 */
	public Class getParentClass(){
		return parentClass;
	}
	/**
	 * Setter for the role's parent Class. 
	 * Class is registered at role and role is registered at class.
	 * @param parent
	 */
	public void setParentClass(Class parent){
		this.parentClass = parent;
		parent.addRole(this);
	}
	
	/**
	 * @return Collaboration where the role belongs to.
	 */
	public Collaboration getCollaboration(){
		return this.collaboration;
	}
	/**
	 * Setter for the role's collaboration.
	 * Collaboration is registered at role
	 * @param c - Collaboration
	 */
	public void setCollaboration(Collaboration c){
		this.collaboration = c;
		c.addRole(this);
	}



	/**
	 * @return
	 */
	public IFile getRoleFile() {
		if (path == null || !path.isAbsolute()) return null;
		IFile file=ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		return file;
	}
	

	

}
