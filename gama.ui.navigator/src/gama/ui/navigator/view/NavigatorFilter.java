/*******************************************************************************************************
 *
 * NavigatorFilter.java, in gama.ui.navigator.view, is part of the source code of the GAMA modeling and simulation
 * platform .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.ui.navigator.view;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import gama.ui.navigator.metadata.FileMetaDataProvider;
import gama.ui.navigator.view.contents.ResourceManager;
import gama.ui.navigator.view.contents.WrappedFolder;
import gama.ui.shared.utils.PreferencesHelper;

/**
 * The Class NavigatorFilter.
 */
public class NavigatorFilter extends ViewerFilter {

	/**
	 * Instantiates a new navigator filter.
	 */
	public NavigatorFilter() {}

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
		IResource file = ResourceManager.getResource(element);
		if (file == null) return true;
		if (file.getName().charAt(0) == '.' && !PreferencesHelper.NAVIGATOR_HIDDEN.getValue()) return false;
		if (parentElement instanceof final TreePath p && p.getLastSegment() instanceof WrappedFolder
				&& file instanceof IFile f) {
			final IResource r = FileMetaDataProvider.shapeFileSupportedBy(f);
			if (r != null) return false;
		}
		return true;
	}

}
