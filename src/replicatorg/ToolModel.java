/*
  ToolModel.java

  A class to model a toolhead.

  Part of the ReplicatorG project - http://www.replicat.org
  Copyright (c) 2008 Zach Smith

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package replicatorg;

import java.util.concurrent.atomic.AtomicReference;

import org.w3c.dom.Node;

enum ToolheadType {
	UNKNOWN(1, "Unknown"), MK1(1, "Mk1"), MK2(2, "Mk2"), MK3(3, "Mk3"), MK4(4, "Mk4"), MK5(5, "Mk5"), FROSTRUDER(
			6, "Frostruder"), UNICORN(6, "Unicorn"), MK6(7, "Mk6"), MK6A(8, "Mk7a"), MK7(9, "Mk7"), MK8(
			10, "Mk8");

	public String guiName;
	public int number;

	ToolheadType(int number, String name) {
		this.guiName = name;
		this.number = number;
	}
}

public class ToolModel {
	public static int MOTOR_CLOCKWISE = 1;
	public static int MOTOR_COUNTER_CLOCKWISE = 2;

	// TODO: should this be a bitfield?
	protected int toolStatus;


	// descriptive stuff
	protected String name;
	protected String toolClass;
	protected String material;
	protected int index;
	protected ToolheadAlias toolheadAlias;
	protected ToolheadType type = ToolheadType.UNKNOWN;

	// motor stuff
	protected boolean motorEnabled;
	protected int motorDirection;
	protected double motorSpeedRPM;
	protected int motorSpeedPWM;

	protected double motorSpeedReadingRPM;
	protected int motorSpeedReadingPWM;

	protected boolean motorUsesRelay = false;
	protected boolean motorHasEncoder;
	protected int motorEncoderPPR;
	protected boolean motorIsStepper;
	protected double motorSteps; // motor steps per full rotation

	protected AxisId motorStepperAxis; // Stepper axis this motor is connected to

	// spindle stuff
	protected boolean spindleEnabled;
	protected int spindleDirection;
	protected double spindleSpeedRPM;
	protected int spindleSpeedPWM;
	protected double spindleSpeedReadingRPM;
	protected int spindleSpeedReadingPWM;
	protected boolean spindleHasEncoder;
	protected int spindleEncoderPPR;

	// temperature variables
	protected final AtomicReference<Double> currentTemperature = new AtomicReference<Double>(0.0);
	protected double targetTemperature;

	// platform temperature variables
	protected AtomicReference<Double> platformCurrentTemperature = new AtomicReference<Double>(0.0);
	protected double platformTargetTemperature;

	// various coolant/control stuff
	protected boolean floodCoolantEnabled;
	protected boolean mistCoolantEnabled;
	protected boolean fanEnabled;
	protected boolean valveOpen;
	protected boolean colletOpen;

	// capabilities
	protected boolean hasMotor = false;
	protected boolean hasSpindle = false;
	protected boolean hasHeater = false;
	protected boolean hasHeatedPlatform = false;
	protected boolean hasAutomatedPlatform = false;
	protected boolean hasFloodCoolant = false;
	protected boolean hasMistCoolant = false;
	protected boolean hasFan = false;
	protected boolean hasValve = false;
	protected boolean hasCollet = false;
	protected boolean alwaysReadHBP = false;

	protected boolean automatedBuildPlatformEnabled;

	/*************************************
	 * Creates the model object.
	 *************************************/
	public ToolModel() {
		_initialize();
	}

	public ToolModel(Node n) {
		_initialize();
	}

	private void _initialize() {
		// default information
		name = "Generic Tool";
		toolClass = "tool";
		material = "unknown";
		type = ToolheadType.UNKNOWN;
		index = 0;

		// default our spindles/motors
		setMotorDirection(MOTOR_CLOCKWISE);
		disableMotor();
		setSpindleDirection(MOTOR_CLOCKWISE);
		disableMotor();

		// default our accessories
		disableFloodCoolant();
		disableMistCoolant();
		disableFan();
		closeValve();
		closeCollet();
	}

	/**
	 * Returns true if the parameter is "1" or "true".
	 */
	private boolean isTrueOrOne(String s) {
		if (s == null) {
			return false;
		}
		if (Boolean.parseBoolean(s)) {
			return true;
		}
		try {
			if (Integer.parseInt(s) == 1) {
				return true;
			}
		} catch (NumberFormatException e) {
		}
		return false;
	}

	/*************************************
	 * Generic tool information
	 *************************************/

	public String getName() {
		return name;
	}

	public void setIndex(int i) {
		index = i;
	}

	public int getIndex() {
		return index;
	}

	public String getToolClass() {
		return toolClass;
	}

	/* use Get Class instead */
	@Deprecated
	public String getType() {
		return getToolClass();
	}

	public int getToolStatus() {
		return toolStatus;
	}

	public void setToolStatus(int status) {
		toolStatus = status;
	}

	/*************************************
	 * Motor interface functions
	 *************************************/
	public void setMotorDirection(int dir) {
		motorDirection = dir;
	}

	public int getMotorDirection() {
		return motorDirection;
	}

	/**
	 * Motor speed *read from the XML*
	 * 
	 * @return
	 */
	public void setMotorSpeedRPM(double rpm) {
		motorSpeedRPM = rpm;
	}

	/**
	 * Motor speed *read from the XML*
	 * 
	 * @return
	 */
	public void setMotorSpeedPWM(int pwm) {
		motorSpeedPWM = pwm;
	}

	public double getMotorSpeedRPM() {
		return motorSpeedRPM;
	}

	/**
	 * Get number of steps per revolution
	 */
	public double getMotorSteps() {
		return motorSteps;
	}

	/**
	 * Motor speed *read from the XML*
	 * 
	 * @return
	 */
	public int getMotorSpeedPWM() {
		return motorSpeedPWM;
	}

	public boolean getMotorUsesRelay() {
		return motorUsesRelay;
	}

	/**
	 * Motor speed *read from the device*
	 * 
	 * @param rpm
	 */
	public void setMotorSpeedReadingRPM(double rpm) {
		motorSpeedReadingRPM = rpm;
	}

	/**
	 * Motor speed *read from the device*
	 * 
	 * @param rpm
	 */
	public void setMotorSpeedReadingPWM(int pwm) {
		motorSpeedReadingPWM = pwm;
	}

	/**
	 * Motor speed *read from the device*
	 * 
	 * @param rpm
	 */
	public double getMotorSpeedReadingRPM() {
		return motorSpeedReadingRPM;
	}

	/**
	 * Motor speed *read from the device*
	 * 
	 * @param rpm
	 */
	public int getMotorSpeedReadingPWM() {
		return motorSpeedReadingPWM;
	}

	public void enableMotor() {
		motorEnabled = true;
	}

	public void disableMotor() {
		motorEnabled = false;
	}

	public boolean isMotorEnabled() {
		return motorEnabled;
	}

	public boolean hasMotor() {
		return hasMotor;
	}

	public boolean motorHasEncoder() {
		return motorHasEncoder;
	}

	public boolean motorIsStepper() {
		return motorIsStepper;
	}

	/**
	 * 
	 * @return null if motorstepperaxis wasn't specified. The axis identifier otherwise
	 */
	public String getMotorStepperAxisName() {
		if (motorStepperAxis == null)
			return "";
		return motorStepperAxis.name();
	}

	public AxisId getMotorStepperAxis() {
		return motorStepperAxis;
	}

	/*************************************
	 * Spindle interface functions
	 *************************************/
	public void setSpindleDirection(int dir) {
		spindleDirection = dir;
	}

	public int getSpindleDirection() {
		return spindleDirection;
	}

	public void setSpindleSpeedRPM(double rpm) {
		spindleSpeedRPM = rpm;
	}

	public void setSpindleSpeedPWM(int pwm) {
		spindleSpeedPWM = pwm;
	}

	public double getSpindleSpeedRPM() {
		return spindleSpeedRPM;
	}

	public int getSpindleSpeedPWM() {
		return spindleSpeedPWM;
	}

	public void setSpindleSpeedReadingRPM(double rpm) {
		spindleSpeedReadingRPM = rpm;
	}

	public void setSpindleSpeedReadingPWM(int pwm) {
		spindleSpeedReadingPWM = pwm;
	}

	public double getSpindleSpeedReadingRPM() {
		return spindleSpeedReadingRPM;
	}

	public int getSpindleSpeedReadingPWM() {
		return spindleSpeedReadingPWM;
	}

	public void enableSpindle() {
		spindleEnabled = true;
	}

	public void disableSpindle() {
		spindleEnabled = false;
	}

	public boolean isSpindleEnabled() {
		return spindleEnabled;
	}

	public boolean hasSpindle() {
		return hasSpindle;
	}

	public boolean spindleHasEncoder() {
		return spindleHasEncoder;
	}

	/*************************************
	 * Heater interface functions
	 *************************************/
	public void setTargetTemperature(double temperature) {
		targetTemperature = temperature;
	}

	public double getTargetTemperature() {
		return targetTemperature;
	}

	public void setCurrentTemperature(double temperature) {
		currentTemperature.set(temperature);
	}

	public double getCurrentTemperature() {
		return currentTemperature.get();
	}

	public boolean hasHeater() {
		return hasHeater;
	}

	/*************************************
	 * Heated Platform interface functions
	 *************************************/
	public void setPlatformTargetTemperature(double temperature) {
		platformTargetTemperature = temperature;
	}

	public double getPlatformTargetTemperature() {
		return platformTargetTemperature;
	}

	public void setPlatformCurrentTemperature(double temperature) {
		platformCurrentTemperature.set(temperature);
	}

	public double getPlatformCurrentTemperature() {
		return platformCurrentTemperature.get();
	}

	public boolean hasHeatedPlatform() {
		return hasHeatedPlatform;
	}

	public boolean hasAutomatedPlatform() {
		return hasAutomatedPlatform;
	}

	/*************************************
	 * Flood Coolant interface functions
	 *************************************/
	public void enableFloodCoolant() {
		floodCoolantEnabled = true;
	}

	public void disableFloodCoolant() {
		floodCoolantEnabled = false;
	}

	public boolean isFloodCoolantEnabled() {
		return floodCoolantEnabled;
	}

	public boolean hasFloodCoolant() {
		return hasFloodCoolant;
	}

	/*************************************
	 * Mist Coolant interface functions
	 *************************************/
	public void enableMistCoolant() {
		mistCoolantEnabled = true;
	}

	public void disableMistCoolant() {
		mistCoolantEnabled = false;
	}

	public boolean isMistCoolantEnabled() {
		return mistCoolantEnabled;
	}

	public boolean hasMistCoolant() {
		return hasMistCoolant;
	}

	public void setAutomatedBuildPlatformRunning(boolean state) {
		automatedBuildPlatformEnabled = state;
	}

	public boolean isAutomatedBuildPlatformEnabled(boolean state) {
		return automatedBuildPlatformEnabled;
	}

	/*************************************
	 * Fan interface functions
	 *************************************/
	public void enableFan() {
		fanEnabled = true;
	}

	public void disableFan() {
		fanEnabled = false;
	}

	public boolean isFanEnabled() {
		return fanEnabled;
	}

	public boolean hasFan() {
		return hasFan;
	}

	public boolean alwaysReadBuildPlatformTemp() {
		return alwaysReadHBP;
	}

	/*************************************
	 * Valve interface functions
	 *************************************/
	public void openValve() {
		valveOpen = true;
	}

	public void closeValve() {
		valveOpen = false;
	}

	public boolean isValveOpen() {
		return valveOpen;
	}

	public boolean hasValve() {
		return hasValve;
	}

	/*************************************
	 * Collet interface functions
	 *************************************/
	public void openCollet() {
		colletOpen = true;
	}

	public void closeCollet() {
		colletOpen = false;
	}

	public boolean isColletOpen() {
		return colletOpen;
	}

	public boolean hasCollet() {
		return hasCollet;
	}

	// returns true of an extruder has a thermocouple (implies it has a PID)
	public boolean hasExtruderThermocouple() {
		return true;
	}

	public boolean hasExtruderThermistor() {
		// / Mk6/7/8 use Thermocouple
		String nameLower = this.name.toLowerCase();
		if (nameLower.contains("Unicorn"))
			return false;
		else if (nameLower.contains("mk6") || nameLower.contains("mk7")
				|| nameLower.contains("mk8"))
			return false;
		// Mk1 to ? use thermistor
		else if (nameLower.contains("mk5") || nameLower.contains("mk5")
				|| nameLower.contains("mk3") || nameLower.contains("mk2"))
			return true;
		// default to false, sice we don't know
		return false;
	}
	
	public void setToolheadAlias(ToolheadAlias alias) {
		toolheadAlias = alias;
	}
	
	public ToolheadAlias getAlias() {
		return toolheadAlias;
	}

}
