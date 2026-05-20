// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//added imports
import edu.wpi.first.wpilibj.xrp.XRPMotor;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.xrp.XRPServo;

//tele-op
import edu.wpi.first.wpilibj.XboxController;
//import edu.wpi.first.wpilibj.Joystick;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private static final String kCustomAuto2 = "My Auto 2";

  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private final XRPMotor leftDrive = new XRPMotor(0);
  private final XRPMotor rightDrive = new XRPMotor(1);
   private final DifferentialDrive mDrive = new DifferentialDrive(leftDrive, rightDrive);

   private final Timer mTimer = new Timer();
   private final XRPServo backServo = new XRPServo(4);
   private final XboxController mController = new XboxController(0);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    m_chooser.addOption("My second Auto", kCustomAuto2);
    SmartDashboard.putData("Auto choices", m_chooser);

    rightDrive.setInverted(true);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    mTimer.start();
    mTimer.reset();

  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        backServo.setPosition(1);
        break;
      case kCustomAuto2:
        backServo.setPosition(0.5);
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        //leftDrive.set(.6);
        //rightDrive.set(.6);

        if (mTimer.get() < 1.7) { //Drive forward an unknown distance
          mDrive.tankDrive(1, 1);
          backServo.setPosition(1);
        } else if (mTimer.get() < 2.2) { // Turn 90 degreess
          mDrive.tankDrive(0.7, -0.7);
        } else if (mTimer.get() < 4.4) { // back up
          mDrive.tankDrive(-0.5, -0.5);
        } else if (mTimer.get() < 6.4) {
          mDrive.tankDrive(0, 0);
          backServo.setPosition(0);
        } else { // the last step; shuts off the Motors
          mDrive.tankDrive(0, 0);
          backServo.setPosition(1);
        }
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    //mDrive.tankDrive(-mController.getLeftY(), -mController.getRightY());

    double MultiplerMotion = 1;
    double MultiplerRotation = 1;

    //boolean MotionNegative = false;
    //boolean RotationNegative = false;

    if (mController.getLeftY() < 0) {
      //MotionNegative = true;
      MultiplerMotion = -MultiplerMotion;
    }
    if (mController.getRightY() < 0 ) {
      //MotionNegative = true;
      MultiplerRotation = -MultiplerRotation;
    }

    

    if (mController.getLeftBumperButton()) {
      MultiplerMotion =+ 1;
    }
    if (mController.getLeftStickButton()) {
      MultiplerMotion =+ mController.getLeftTriggerAxis()*2;
    }

    if (mController.getRightBumperButton()) {
      MultiplerRotation =+ 1;
    }
    if (mController.getRightStickButton()) {
      MultiplerRotation =+ mController.getRightTriggerAxis()*2;
    }

    mDrive.arcadeDrive(/*-*/(Math.max(Math.abs(mController.getLeftY()), 0.25)*MultiplerMotion), /*-*/(Math.max(Math.abs(mController.getLeftY()), 0.25)*MultiplerRotation));
    /*Thread Thread = new Thread(() -> {
      while (false) {
        System.out.printf("Left JoyStick: %f, Right JoyStick: %f; Left Bumper: %b, Right Bumper: %b;; Multipler for motion: %f, Multipler for rotation: %f. If motion is negative: %b, If rotation is negative: %b.%n", mController.getLeftTriggerAxis(), mController.getRightTriggerAxis(), mController.getRightBumperButton(), mController.getLeftBumperButton(), MultiplerMotion, MultiplerRotation, MotionNegative, RotationNegative);
        
      }
    });
    Thread.start();*/
  }

  /** This function is called once s=when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
