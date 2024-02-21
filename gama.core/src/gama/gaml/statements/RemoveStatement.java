/*******************************************************************************************************
 *
 * RemoveStatement.java, in gama.core, is part of the source code of the GAMA modeling and simulation platform
 * .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.gaml.statements;

import gama.annotations.precompiler.IConcept;
import gama.annotations.precompiler.ISymbolKind;
import gama.annotations.precompiler.GamlAnnotations.doc;
import gama.annotations.precompiler.GamlAnnotations.example;
import gama.annotations.precompiler.GamlAnnotations.facet;
import gama.annotations.precompiler.GamlAnnotations.facets;
import gama.annotations.precompiler.GamlAnnotations.inside;
import gama.annotations.precompiler.GamlAnnotations.symbol;
import gama.annotations.precompiler.GamlAnnotations.usage;
import gama.core.common.interfaces.IKeyword;
import gama.core.runtime.IScope;
import gama.core.runtime.exceptions.GamaRuntimeException;
import gama.core.util.IContainer;
import gama.core.util.graph.IGraph;
import gama.gaml.compilation.annotations.serializer;
import gama.gaml.compilation.annotations.validator;
import gama.gaml.descriptions.IDescription;
import gama.gaml.descriptions.StatementDescription;
import gama.gaml.descriptions.SymbolDescription;
import gama.gaml.descriptions.SymbolSerializer;
import gama.gaml.expressions.IExpression;
import gama.gaml.statements.AbstractContainerStatement.ContainerValidator;
import gama.gaml.statements.RemoveStatement.RemoveSerializer;
import gama.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 *
 * @todo Description
 *
 */

@facets (
		value = { @facet (
				name = IKeyword.ITEM,
				type = IType.NONE,
				optional = true,
				doc = @doc ("the right member of the removal assignment ('cont >> expr;') is an expression expr that evaluates to the element(s) to be removed from the container")),
				@facet (
						name = IKeyword.FROM,
						type = { IType.CONTAINER, IType.SPECIES, IType.AGENT, IType.GEOMETRY },
						optional = false,
						doc = { @doc ("the left member of the removal assignment ('cont >> expr;') is an expression cont that evaluates to a container (list, map, graph) ") }),
				@facet (
						name = IKeyword.INDEX,
						type = IType.NONE,
						optional = true,
						doc = @doc ("any expression, the key at which to remove the element from the container ")),
				@facet (
						name = IKeyword.KEY,
						type = IType.NONE,
						optional = true,
						doc = @doc ("If a key/index is to be removed (instead of a value), it must be indicated by using `container[]` instead of `container`")),
				@facet (
						name = IKeyword.ALL,
						type = IType.NONE,
						optional = true,
						doc = @doc ("the symbol '>>-' allows to pass a container as item so as to remove all of its elements from the receiving container. If the item is not a container, all of its occurrences are removed")) },
		omissible = IKeyword.ITEM)
@symbol (
		name = IKeyword.REMOVE,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.CONTAINER, IConcept.GRAPH, IConcept.NODE, IConcept.EDGE, IConcept.ATTRIBUTE,
				IConcept.SPECIES, IConcept.MAP, IConcept.MATRIX, IConcept.LIST })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER },
		symbols = IKeyword.CHART)
@doc (
		value = "A statement used to remove items from containers. It can be written using the classic syntax (`remove ... from: ...`) or a compact one, which is now preferred."
				+ "\n- To remove an element from a container (other than a matrix), use `container >> element;` or `container >- element;` (classic form: `remove element from: container;`) "
				+ "\n- To remove an index/key from a container (other than a matrix) use `container[] >> index` or `container[] >- index` (classic form: `remove key: index from: container;`)"
				+ "\n- To remove all the elements contained in another container, use `container >>- elements;` (classic form: `remove all: elements from: container;`)"
				+ "\n- To remove all the indexes contained in another container, use `container[] >>- indices;` (classic form: `remove key: indices all: true from: container;`)"
				+ "\n- To remove all the occurences of an element in the container, use `container >>- element;` (classic form: `remove element from: container all: true;`)",
		usages = { @usage (
				value = "This statement should be used in the following ways, depending on the kind of container used and the expected action on it:",
				examples = { @example (
						value = "expr_container >> expr (or expr_container >- expr) to remove an element",
						isExecutable = false),
						@example (
								value = "expr_container[] >> expr (or expr_container[] >- expr) to remove an index or a key;",
								isExecutable = false),
						@example (
								value = "expr_container >>- expr (to remove all occurences of expr), expr_container >>- container_of_expr (to remove all the elements in the passed argument), expr_container[] >>- container_of_expr (to remove all the index/keys in the passed argument)",
								isExecutable = false) }),
				@usage (
						value = "In the case of list, `>-` of `>>` is used to remove the first occurence of a given expression, whereas `>>-` is used to remove all its occurrences. Indices can also be removed in the same way",
						examples = { @example ("list<int> removeList <- [3,2,1,2,3];"), @example (
								value = "removeList >- 2;",
								var = "removeList",
								equals = "[3,1,2,3]",
								returnType = "null"),
								@example (
										value = "removeList >>- 3;",
										var = "removeList",
										equals = "[1,2]",
										returnType = "null"),
								@example (
										value = "removeList[] >- 1;",
										var = "removeList",
										equals = "[1]",
										returnType = "null") }),
				@usage (
						value = "In the case of map, to remove the pair identified by a given key, we have to specify that we are working on the keys. Same for lists",
						examples = { @example ("map<string,int> removeMap <- [\"x\"::5, \"y\"::7, \"z\"::7];"),
								@example (
										value = "removeMap[] >- \"x\";",
										var = "removeMap",
										equals = "[\"y\"::7, \"z\"::7]",
										returnType = "null"),
								@example (
										value = "removeMap[] >>- removeMap.keys;",
										var = "removeMap",
										equals = "map([])",
										returnType = "null") }),
				@usage (
						value = "A map can be managed as a list of pairs: remove then operates on the values by default",
						examples = {
								@example ("map<string,int> removeMapList <- [\"x\"::5, \"y\"::7, \"z\"::7, \"t\"::5];"),
								@example (
										value = "removeMapList >> 7;",
										var = "removeMapList",
										equals = "[\"x\"::5, \"z\"::7, \"t\"::5]",
										returnType = "null"),
								@example (
										value = "removeMapList >>- [5,7];",
										var = "removeMapList",
										equals = "[\"t\"::5]",
										returnType = "null"),
								@example (
										value = "removeMapList[] >- \"t\";",
										var = "removeMapList",
										equals = "map([])",
										returnType = "null") }),
				@usage (
						value = "In the case of a graph, if a node is removed, all edges to and from this node are also removed. Note that the use of edge()/node()/edges()/nodes() operators is necessary",
						examples = { @example ("graph removeGraph <- as_edge_graph([{1,2}::{3,4},{3,4}::{5,6}]);"),
								@example (
										value = "removeGraph >> node(1,2);"),
								@example (
										value = "removeGraph.vertices",
										returnType = "list",
										equals = "[{3,4},{5,6}]"),
								@example (
										value = "removeGraph.edges",
										returnType = "list",
										equals = "[polyline({3,4}::{5,6})]"),
								@example (
										value = "removeGraph >> edge({3,4},{5,6});"),
								@example (
										value = "removeGraph.vertices",
										returnType = "list",
										equals = "[{3,4},{5,6}]"),
								@example (
										value = "removeGraph.edges",
										returnType = "list",
										equals = "[]") }),
				@usage (
						value = "In the case of an agent or a shape, `remove` allows to remove an attribute from the attributes map of the receiver. However, for agents, it will only remove attributes that have been added dynamically, not the ones defined in the species or in its built-in parent.",
						examples = { @example (
								value = "global {",
								isExecutable = false),
								@example (
										value = "   init {",
										isExecutable = false),
								@example (
										value = "      create speciesRemove;",
										isExecutable = false),
								@example (
										value = "      speciesRemove sR <- speciesRemove(0);",
										isExecutable = false),
								@example (
										value = "      sR['a'] <- 100; 	// sR.a now equals 100",
										isExecutable = false),
								@example (
										value = "      sR[] >> \"a\"; 	// sR.a does not exist anymore",
										isExecutable = false),
								@example (
										value = "   }",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false),
								@example (
										value = "",
										isExecutable = false), }),
				@usage (
						value = "This statement can not be used on *matrix*.") },
		see = { "add", "put" })
@serializer (RemoveSerializer.class)
@validator (ContainerValidator.class)
public class RemoveStatement extends AbstractContainerStatement {

	/**
	 * The Class RemoveSerializer.
	 */
	public static class RemoveSerializer extends SymbolSerializer<StatementDescription> {

		@Override
		protected void serialize(final SymbolDescription cd, final StringBuilder sb, final boolean includingBuiltIn) {
			final IExpression item = cd.getFacetExpr(ITEM);
			final IExpression list = cd.getFacetExpr(TO);
			final IExpression allFacet = cd.getFacetExpr(ALL);
			final IExpression at = cd.getFacetExpr(AT);
			final boolean isAll = allFacet != null && allFacet.isConst() && "true".equals(allFacet.literalValue());
			sb.append(list.serializeToGaml(includingBuiltIn));
			if (at != null) {
				sb.append('[');
				sb.append(']');
			}
			sb.append(isAll ? " >>- " : " >- ");
			sb.append(at == null ? item.serializeToGaml(includingBuiltIn) : at.serializeToGaml(includingBuiltIn))
					.append(';');
		}
	}

	/**
	 * Instantiates a new removes the statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public RemoveStatement(final IDescription desc) {
		super(desc);
		setName("remove from " + list.serializeToGaml(false));
	}

	@SuppressWarnings ("rawtypes")
	@Override
	protected void apply(final IScope scope, final Object object, final Object position,
			final IContainer.Modifiable container) throws GamaRuntimeException {

		if (position == null) {
			// If key/at/index/node is not mentioned
			if (asAll) {
				// if we "remove all"
				if (asAllValues) {
					// if a container is passed
					container.removeValues(scope, (IContainer) object);
				} else {
					// otherwise if it is a simple value
					container.removeAllOccurrencesOfValue(scope, object);
				}
			} else {
				// if it is a simple remove
				container.removeValue(scope, object);
			}
		} else if (asAllIndexes) {
			container.removeIndexes(scope, (IContainer) position);
		} else {
			// If a key/index/at/node is mentioned
			// simply remove the index.
			container.removeIndex(scope, position);
		}

	}

	@Override
	protected Object buildValue(final IScope scope, final IGraph container) {
		return this.item.value(scope);
	}

	@Override
	protected Object buildIndex(final IScope scope, final IGraph container) {
		return this.index.value(scope);
	}
}
