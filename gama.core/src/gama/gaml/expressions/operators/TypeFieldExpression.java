/*******************************************************************************************************
 *
 * TypeFieldExpression.java, in gama.core, is part of the source code of the GAMA modeling and simulation platform
 * .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.gaml.expressions.operators;

import gama.annotations.precompiler.GamlProperties;
import gama.annotations.precompiler.GamlAnnotations.variable;
import gama.annotations.precompiler.GamlAnnotations.vars;
import gama.core.runtime.IScope;
import gama.gaml.descriptions.IDescription;
import gama.gaml.descriptions.OperatorProto;
import gama.gaml.expressions.IExpression;

/**
 * The Class TypeFieldExpression.
 */
public class TypeFieldExpression extends UnaryOperator {

	/**
	 * Instantiates a new type field expression.
	 *
	 * @param proto
	 *            the proto
	 * @param context
	 *            the context
	 * @param expr
	 *            the expr
	 */
	public TypeFieldExpression(final OperatorProto proto, final IDescription context, final IExpression expr) {
		super(proto, context, expr);
	}

	@Override
	public TypeFieldExpression resolveAgainst(final IScope scope) {
		return new TypeFieldExpression(prototype, null, child.resolveAgainst(scope));
	}

	@Override
	public String serializeToGaml(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		parenthesize(sb, child);
		sb.append(".").append(getName());
		return sb.toString();
	}

	@Override
	public String toString() {
		if (child == null) return prototype.signature.toString() + "." + getName();
		return child.serializeToGaml(false) + "." + getName();
	}

	@Override
	public Doc getDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		if (child != null) { sb.append("Defined on objects of type " + child.getGamlType().getName()); }
		final vars annot = prototype.getJavaBase().getAnnotation(vars.class);
		if (annot != null) {
			final variable[] allVars = annot.value();
			for (final variable v : allVars) {
				if (v.name().equals(getName()) && v.doc().length > 0) {
					sb.append("<br/>");
					sb.append(v.doc()[0].value());
				}
			}
		}
		return new RegularDoc(sb);
	}

	@Override
	public String getTitle() { return "field <b>" + getName() + "</b> of type " + getGamlType().getName(); }

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		meta.put(GamlProperties.ATTRIBUTES, getName());
	}

}
