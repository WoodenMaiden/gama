/*******************************************************************************************************
 *
 * EcoreBasedExpressionDescription.java, in gaml.compiler.gaml, is part of the source code of the
 * GAMA modeling and simulation platform .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.compiler.gaml.expression;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;

import gama.core.util.Collector;
import gama.core.util.ICollector;
import gama.gaml.compilation.kernel.GamaSkillRegistry;
import gama.gaml.descriptions.BasicExpressionDescription;
import gama.gaml.descriptions.IDescription;
import gama.gaml.descriptions.IExpressionDescription;
import gama.gaml.interfaces.IGamlIssue;
import gaml.compiler.gaml.EGaml;
import gaml.compiler.gaml.Array;
import gaml.compiler.gaml.Expression;
import gaml.compiler.gaml.VariableRef;

/**
 * The class EcoreBasedExpressionDescription.
 *
 * @author drogoul
 * @since 31 mars 2012
 *
 */
public class EcoreBasedExpressionDescription extends BasicExpressionDescription {

	/**
	 * Instantiates a new ecore based expression description.
	 *
	 * @param exp the exp
	 */
	protected EcoreBasedExpressionDescription(final EObject exp) {
		super(exp);
	}

	@Override
	public IExpressionDescription cleanCopy() {
		return new EcoreBasedExpressionDescription(target);
	}

	@Override
	public String toOwnString() {
		return EGaml.getInstance().toString(target);
	}

	@Override
	public Collection<String> getStrings(final IDescription context, final boolean skills) {
		if (target == null) { return Collections.EMPTY_SET; }
		if (!(target instanceof Array)) {
			final String type = skills ? "skill" : "attribute";

			if (target instanceof VariableRef) {
				final String skillName = EGaml.getInstance().getKeyOf(target);
				context.warning(
						type + "s should be provided as a list of identifiers, for instance [" + skillName + "]",
						IGamlIssue.AS_ARRAY, target, skillName);
				if (skills && !GamaSkillRegistry.INSTANCE.hasSkill(skillName)) {
					context.error("Unknown " + type + " " + skillName, IGamlIssue.UNKNOWN_SKILL, target);
				}
				return Collections.singleton(skillName);
			}
			if (target instanceof Expression) {
				context.error("Impossible to recognize valid " + type + "s in " + EGaml.getInstance().toString(target),
						skills ? IGamlIssue.UNKNOWN_SKILL : IGamlIssue.UNKNOWN_VAR, target);
			} else {
				context.error(type + "s should be provided as a list of identifiers.", IGamlIssue.UNKNOWN_SKILL,
						target);
			}
			return Collections.EMPTY_SET;
		}
		try (final ICollector<String> result = Collector.getOrderedSet()) {
			final Array array = (Array) target;
			for (final Expression expr : EGaml.getInstance().getExprsOf(array.getExprs())) {
				final String type = skills ? "skill" : "attribute";

				final String name = EGaml.getInstance().getKeyOf(expr);
				if (skills && !GamaSkillRegistry.INSTANCE.hasSkill(name)) {
					context.error("Unknown " + type + " " + name, IGamlIssue.UNKNOWN_SKILL, expr);
				} else {
					result.add(name);
				}
			}
			return result.items();
		}
	}

}
