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
package de.ovgu.featureide.featurehouse.refactoring.pullUp;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jface.viewers.CheckboxTableViewer;

import org.eclipse.jdt.internal.corext.refactoring.structure.IMemberActionInfo;

class PullPushCheckboxTableViewer extends CheckboxTableViewer{
	public PullPushCheckboxTableViewer(Table table) {
		super(table);
	}

	/*
	 * @see org.eclipse.jface.viewers.StructuredViewer#doUpdateItem(org.eclipse.swt.widgets.Widget, java.lang.Object, boolean)
	 */
	@Override
	protected void doUpdateItem(Widget widget, Object element, boolean fullMap) {
		super.doUpdateItem(widget, element, fullMap);
		if (! (widget instanceof TableItem))
			return;
		TableItem item= (TableItem)widget;
		IMemberActionInfo info= (IMemberActionInfo)element;
		item.setChecked(PullPushCheckboxTableViewer.getCheckState(info));
		Assert.isTrue(item.getChecked() == PullPushCheckboxTableViewer.getCheckState(info));
	}

	/*
	 * @see org.eclipse.jface.viewers.Viewer#inputChanged(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void inputChanged(Object input, Object oldInput) {
		super.inputChanged(input, oldInput);
		// XXX workaround for http://bugs.eclipse.org/bugs/show_bug.cgi?id=9390
		setCheckState((IMemberActionInfo[])input);
	}

	private void setCheckState(IMemberActionInfo[] infos) {
		if (infos == null)
			return;
		for (int i= 0; i < infos.length; i++) {
			IMemberActionInfo info= infos[i];
			setChecked(info, PullPushCheckboxTableViewer.getCheckState(info));
		}
	}

	private static boolean getCheckState(IMemberActionInfo info) {
		return info.isActive();
	}

	/*
	 * @see org.eclipse.jface.viewers.Viewer#refresh()
	 */
	@Override
	public void refresh() {
		int topIndex = getTable().getTopIndex();
		super.refresh();
		// XXX workaround for http://bugs.eclipse.org/bugs/show_bug.cgi?id=9390
		setCheckState((IMemberActionInfo[])getInput());
		if (topIndex < getTable().getItemCount())
			getTable().setTopIndex(topIndex); //see bug 31645
	}
}