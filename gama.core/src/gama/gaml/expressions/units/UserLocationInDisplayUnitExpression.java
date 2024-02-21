/*******************************************************************************************************
 *
 * UserLocationUnitExpression.java, in gama.core, is part of the source code of the GAMA modeling and simulation
 * platform .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.gaml.expressions.units;

import gama.core.metamodel.shape.GamaPoint;
import gama.core.runtime.IScope;
import gama.gaml.types.Types;

/**
 * The Class UserLocationUnitExpression.
 */
public class UserLocationInDisplayUnitExpression extends UnitConstantExpression {

	/**
	 * Instantiates a new user location unit expression.
	 *
	 * @param doc
	 *            the doc
	 */
	public UserLocationInDisplayUnitExpression(final String doc) {
		super(new GamaPoint(), Types.POINT, "user_location_in_display", doc, null);
	}

	@Override
	public GamaPoint _value(final IScope scope) {
		return scope.getGui().getMouseLocationInDisplay();
	}

	@Override
	public boolean isConst() { return false; }

	@Override
	public boolean isContextIndependant() { return false; }

	@Override
	public boolean isAllowedInParameters() { return false; }
}
