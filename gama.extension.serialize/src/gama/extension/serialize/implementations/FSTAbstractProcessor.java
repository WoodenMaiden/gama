/*******************************************************************************************************
 *
 * FSTAbstractProcessor.java, in gama.extension.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extension.serialize.implementations;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import gama.core.common.geometry.GamaCoordinateSequenceFactory;
import gama.core.common.geometry.GamaGeometryFactory;
import gama.core.common.geometry.GeometryUtils;
import gama.core.metamodel.agent.AgentReference;
import gama.core.metamodel.agent.IAgent;
import gama.core.metamodel.agent.ISerialisedAgent;
import gama.core.metamodel.agent.SerialisedAgent;
import gama.core.metamodel.population.ISerialisedPopulation;
import gama.core.metamodel.population.SerialisedGrid;
import gama.core.metamodel.population.SerialisedPopulation;
import gama.core.metamodel.shape.GamaShape;
import gama.core.metamodel.shape.GamaShapeFactory;
import gama.core.metamodel.shape.IShape;
import gama.core.metamodel.shape.IShape.Type;
import gama.core.metamodel.topology.grid.IGrid;
import gama.core.runtime.IScope;
import gama.core.runtime.exceptions.GamaRuntimeException;
import gama.core.util.GamaFont;
import gama.core.util.GamaListFactory;
import gama.core.util.GamaMapFactory;
import gama.core.util.IList;
import gama.core.util.IMap;
import gama.gaml.species.ISpecies;
import gama.gaml.types.GamaType;
import gama.gaml.types.IType;

/**
 * The Class FSTImplementation. Allows to provide common initializations to FST Configurations and do the dirty work.
 * Not thread / simulation safe.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 2 août 2023
 */
public abstract class FSTAbstractProcessor extends AbstractSerialisationProcessor<SerialisedAgent> {

	/** The fst. */
	FSTConfiguration fst;

	/** The in agent. */
	boolean inAgent;

	/**
	 * Instantiates a new gama FST serialiser.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope.
	 * @date 5 août 2023
	 */
	public FSTAbstractProcessor(final FSTConfiguration conf) {
		fst = initConfiguration(conf);
	}

	/**
	 * Register serialisers.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 5 août 2023
	 */
	protected void registerSerialisers(final FSTConfiguration conf) {

		register(conf, GamaShape.class, new FSTIndividualSerialiser<GamaShape>() {

			@Override
			protected boolean shouldRegister() {
				return false;
			}

			// TODO The inner attributes of the shape should be saved (ie the ones that do not belong to the var names
			// of the species
			@Override
			public void serialise(final FSTObjectOutput out, final GamaShape toWrite) throws Exception {
				Double d = toWrite.getDepth();
				IShape.Type t = toWrite.getGeometricalType();
				out.writeDouble(d == null ? 0d : d);
				out.writeInt(t.ordinal());
				out.writeObject(toWrite.getInnerGeometry());
				out.writeObject(AgentReference.of(toWrite.getAgent()));
			}

			@Override
			public GamaShape deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				double d = in.readDouble();
				IShape.Type t = IShape.Type.values()[in.readInt()];
				GamaShape result = GamaShapeFactory.createFrom((Geometry) in.readObject());
				AgentReference agent = (AgentReference) in.readObject();
				if (agent != AgentReference.NULL) { result.setAgent(agent.getReferencedAgent(scope)); }
				if (d > 0d) { result.setDepth(d); }
				if (t != Type.NULL) { result.setGeometricalType(t); }
				return result;
			}
		});

		register(conf, IAgent.class, new FSTIndividualSerialiser<IAgent>() {

			@Override
			protected boolean shouldRegister() {
				return false;
			}

			@Override
			public void serialise(final FSTObjectOutput out, final IAgent o) throws Exception {

				if (inAgent) {
					out.writeBoolean(true); // isRef
					out.writeObject(AgentReference.of(o));
				} else {
					inAgent = true;
					out.writeBoolean(false); // isRef
					out.writeObject(SerialisedAgent.of(o, true));
					inAgent = false;
				}
			}

			@Override
			public IAgent deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				boolean isRef = in.readBoolean();
				if (isRef) {
					AgentReference ref = (AgentReference) in.readObject(AgentReference.class);
					return ref.getReferencedAgent(scope);
				}
				SerialisedAgent sa = (SerialisedAgent) in.readObject(SerialisedAgent.class);
				return sa.recreateIn(scope);
			}

		});

		register(conf, IType.class, new FSTIndividualSerialiser<IType>() {

			@Override
			public void serialise(final FSTObjectOutput out, final IType toWrite) throws Exception {
				out.writeStringUTF(toWrite.getGamlType().getName());
				if (toWrite.isCompoundType()) {
					out.writeObject(toWrite.getKeyType());
					out.writeObject(toWrite.getContentType());
				}
			}

			@Override
			public IType deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				String name = in.readStringUTF();
				IType type = scope.getType(name);
				if (type.isCompoundType()) {
					IType key = (IType) in.readObject();
					IType content = (IType) in.readObject();
					return GamaType.from(type, key, content);
				}
				return type;
			}

		});

		register(conf, IScope.class, new FSTIndividualSerialiser<IScope>() {

			@Override
			public void serialise(final FSTObjectOutput out, final IScope toWrite) throws Exception {
				out.writeStringUTF(toWrite.getName());
			}

			@Override
			public IScope deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				String name = in.readStringUTF();
				return scope.copy(name);
			}

		});

		register(conf, ISpecies.class, new FSTIndividualSerialiser<ISpecies>() {

			@Override
			public void serialise(final FSTObjectOutput out, final ISpecies o) throws Exception {
				out.writeStringUTF(o.getName());
			}

			@Override
			public ISpecies deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				String name = in.readStringUTF();
				return scope.getModel().getSpecies(name);
			}

		});

		register(conf, AgentReference.class, new FSTIndividualSerialiser<AgentReference>() {

			@Override
			public void serialise(final FSTObjectOutput out, final AgentReference o) throws Exception {
				out.writeObject(o.species());
				out.writeObject(o.index());
			}

			@Override
			public AgentReference deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return AgentReference.of((String[]) in.readObject(), (Integer[]) in.readObject());
			}
		});

		register(conf, SerialisedAgent.class, new FSTIndividualSerialiser<SerialisedAgent>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedAgent o) throws Exception {
				out.writeInt(o.index());
				out.writeStringUTF(o.species());
				out.writeObject(o.attributes());
				out.writeObject(o.innerPopulations());
			}

			@SuppressWarnings ("unchecked")
			@Override
			public SerialisedAgent deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedAgent(in.readInt(), in.readStringUTF(), (Map<String, Object>) in.readObject(),
						(Map<String, ISerialisedPopulation>) in.readObject());
			}
		});

		register(conf, SerialisedPopulation.class, new FSTIndividualSerialiser<SerialisedPopulation>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedPopulation o) throws Exception {
				out.writeStringUTF(o.speciesName());
				out.writeObject(o.agents());
			}

			@SuppressWarnings ("unchecked")
			@Override
			public SerialisedPopulation deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedPopulation(in.readStringUTF(), (List<ISerialisedAgent>) in.readObject());
			}
		});

		register(conf, SerialisedGrid.class, new FSTIndividualSerialiser<SerialisedGrid>() {

			@Override
			public void serialise(final FSTObjectOutput out, final SerialisedGrid o) throws Exception {
				out.writeStringUTF(o.speciesName());
				out.writeObject(o.agents());
				out.writeObject(o.matrix());
			}

			@SuppressWarnings ("unchecked")
			@Override
			public SerialisedGrid deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new SerialisedGrid(in.readStringUTF(), (List<ISerialisedAgent>) in.readObject(),
						(IGrid) in.readObject());
			}
		});

		register(conf, GamaGeometryFactory.class, new FSTIndividualSerialiser<GamaGeometryFactory>() {

			@Override
			public void serialise(final FSTObjectOutput out, final GamaGeometryFactory o) throws Exception {
				out.writeStringUTF("*GGF*");
			}

			@Override
			public GamaGeometryFactory deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				in.readStringUTF();
				return GeometryUtils.GEOMETRY_FACTORY;
			}
		});

		register(conf, GamaFont.class, new FSTIndividualSerialiser<GamaFont>() {

			@Override
			public void serialise(final FSTObjectOutput out, final GamaFont o) throws Exception {
				out.writeStringUTF(o.getName());
				out.writeInt(o.getStyle());
				out.writeInt(o.getSize());
			}

			@Override
			public GamaFont deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				return new GamaFont(in.readStringUTF(), in.readInt(), in.readInt());
			}
		});

		register(conf, IMap.class, new FSTIndividualSerialiser<IMap>() {

			@Override
			public void serialise(final FSTObjectOutput out, final IMap o) throws Exception {
				out.writeObject(o.getGamlType().getKeyType());
				out.writeObject(o.getGamlType().getContentType());
				out.writeBoolean(o.isOrdered());
				out.writeInt(o.size());
				o.forEach((k, v) -> {
					try {
						out.writeObject(k);
						out.writeObject(v);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}

			@Override
			public IMap deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				IType k = (IType) in.readObject();
				IType c = (IType) in.readObject();
				boolean ordered = in.readBoolean();
				IMap<Object, Object> result = GamaMapFactory.create(k, c, ordered);
				int size = in.readInt();
				for (int i = 0; i < size; i++) { result.put(in.readObject(), in.readObject()); }
				return result;
			}

		});

		register(conf, IList.class, new FSTIndividualSerialiser<IList>() {

			@Override
			protected boolean shouldRegister() {
				return false;
			}

			@Override
			public void serialise(final FSTObjectOutput out, final IList o) throws Exception {
				out.writeObject(o.getGamlType().getContentType());
				out.writeInt(o.size());
				o.forEach(v -> {
					try {
						out.writeObject(v);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}

			@Override
			public IList deserialise(final IScope scope, final FSTObjectInput in) throws Exception {
				IType c = (IType) in.readObject();
				IList<Object> result = GamaListFactory.create(c);
				int size = in.readInt();
				for (int i = 0; i < size; i++) { result.add(in.readObject()); }
				return result;
			}

		});

		register(conf, GamaCoordinateSequenceFactory.class,
				new FSTIndividualSerialiser<GamaCoordinateSequenceFactory>() {

					@Override
					public void serialise(final FSTObjectOutput out, final GamaCoordinateSequenceFactory o)
							throws Exception {
						out.writeStringUTF("*GCSF*");
					}

					@Override
					public GamaCoordinateSequenceFactory deserialise(final IScope scope, final FSTObjectInput in)
							throws Exception {
						in.readStringUTF();
						return GeometryUtils.GEOMETRY_FACTORY.getCoordinateSequenceFactory();
					}
				});
	}

	/**
	 * Register.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @date 5 août 2023
	 */
	public <T> void register(final FSTConfiguration conf, final Class<T> clazz, final FSTIndividualSerialiser<T> ser) {
		ser.setName(clazz.getSimpleName());
		conf.registerSerializer(clazz, ser, true);
	}

	/**
	 * Inits the common.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param conf
	 *            the conf
	 * @return the FST configuration
	 * @date 2 août 2023
	 */
	public FSTConfiguration initConfiguration(final FSTConfiguration conf) {
		registerSerialisers(conf);
		return conf;
	}

	@Override
	protected SerialisedAgent encodeToSerialisedForm(final IAgent agent) {
		return SerialisedAgent.of(agent, true);
	}

	/**
	 * Save.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 6 août 2023
	 */
	@Override
	public byte[] write(final IScope scope, final SerialisedAgent sa) {
		inAgent = false;
		return fst.asByteArray(sa);
	}

	@Override
	public byte[] write(final IScope scope, final Object obj) {
		inAgent = false;
		return fst.asByteArray(obj);
	}

	/**
	 * Restore object from bytes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param input
	 *            the input
	 * @return the object
	 * @date 29 sept. 2023
	 */
	@Override
	public Object createObjectFromBytes(final IScope scope, final byte[] input) {
		try {
			fst.setScope(scope);
			return fst.asObject(input);
		} finally {
			fst.setScope(null);
		}

	}

	@Override
	public IAgent createAgentFromBytes(final IScope scope, final byte[] input) {
		try {
			fst.setScope(scope);
			Object o = fst.asObject(input);
			if (o instanceof SerialisedAgent sa) return sa.recreateIn(scope);
			return null;
		} catch (Exception e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			fst.setScope(null);
		}

	}

	/**
	 * Restore.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 6 août 2023
	 */
	@Override
	public SerialisedAgent read(final IScope scope, final byte[] input) {
		try {
			fst.setScope(scope);
			return (SerialisedAgent) fst.asObject(input);
		} finally {
			fst.setScope(null);
		}
	}

	/**
	 * Restore.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @param image
	 *            the new sim
	 * @date 6 août 2023
	 */
	@Override
	public void restoreFromSerialisedForm(final IAgent sim, final SerialisedAgent image) {
		image.restoreAs(sim.getScope(), sim);
	}

}
