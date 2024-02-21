/*******************************************************************************************************
 *
 * NodeOverlapCallback.java, in gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.collision.shapes;

import com.bulletphysics.collision.broadphase.DispatcherInfo;

/**
 * Callback for operating with {@link OptimizedBvh}.
 *
 * @author jezek2
 */
public interface NodeOverlapCallback {

	/**
	 * Process node.
	 *
	 * @param subPart the sub part
	 * @param triangleIndex the triangle index
	 */
	void processNode( int subPart, int triangleIndex);

}
