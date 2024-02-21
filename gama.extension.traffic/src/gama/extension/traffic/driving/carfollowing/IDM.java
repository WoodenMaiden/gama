/*******************************************************************************************************
 *
 * IDM.java, in gaml.extensions.traffic, is part of the source code of the
 * GAMA modeling and simulation platform .
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.extension.traffic.driving.carfollowing;

import static gama.extension.traffic.driving.DrivingSkill.getDeltaIDM;
import static gama.extension.traffic.driving.DrivingSkill.getMaxAcceleration;
import static gama.extension.traffic.driving.DrivingSkill.getMaxDeceleration;
import static gama.extension.traffic.driving.DrivingSkill.getMaxSpeed;
import static gama.extension.traffic.driving.DrivingSkill.getMinSafetyDistance;
import static gama.extension.traffic.driving.DrivingSkill.getSpeed;
import static gama.extension.traffic.driving.DrivingSkill.getSpeedCoeff;
import static gama.extension.traffic.driving.DrivingSkill.getTimeHeadway;

import gama.core.metamodel.agent.IAgent;
import gama.core.runtime.IScope;
import gama.extension.traffic.driving.RoadSkill;

/**
 * The Class IDM.
 */
public class IDM {
	/**
	 * Computes the acceleration according to the Intelligent Driver Model
	 * (https://traffic-simulation.de/info/info_IDM.html), DrivingSkill.isViolatingOneway(driver)
	 *
	 * @param scope
	 * @param vehicle      the vehicle whose acceleration will be computed
	 * @param leadingDist  the bumper-to-bumper gap with its leading vehicle
	 * @param leadingSpeed the speed of the leading vehicle
	 * @return the resulting acceleration (deceleration if it is < 0)
	 */
	public static double computeAcceleration(final IScope scope,
			final IAgent vehicle,
			final IAgent road,
			final double leadingDist,
			final double leadingSpeed) {
		// IDM params
		double T = getTimeHeadway(vehicle);
		double a = getMaxAcceleration(vehicle);
		double b = getMaxDeceleration(vehicle);
		double v0 = Math.min(getMaxSpeed(vehicle), getSpeedCoeff(vehicle) * RoadSkill.getMaxSpeed(road));
		double s0 = getMinSafetyDistance(vehicle);
		double delta = getDeltaIDM(vehicle);

		double s = leadingDist;
		double v = getSpeed(vehicle);
		double dv = v - leadingSpeed;

		double sStar = s0 + Math.max(0, v * T + v * dv / 2 / Math.sqrt(a * b));
		double accel = a * (1 - Math.pow(v / v0, delta) - Math.pow(sStar / s, 2));

		return accel;
	}
}
