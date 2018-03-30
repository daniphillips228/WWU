#pragma config(Motor,  motorB,          LeftWheel,     tmotorNXT, PIDControl, encoder)
#pragma config(Motor,  motorC,          RightWheel,    tmotorNXT, PIDControl, encoder)
/* **************************************************************************** */
/*   Program: assignment1																			 							    */
/*   Description: Traveling around obstacle then end in target square  					*/
/*   Author: Gavin Harris  																											*/
/*   Date: January 28, 2016 																										*/
/*                          																										*/
/*   NOTES:                          																						*/
/*   1.              							                                              */
/*                                                                  						*/
/*    MOTORS & SENSORS:                                              						*/
/*    [I/O Port]   [Name]     [Type]   [Description]                 						*/
/*    Port B        LeftWheel      NXT     Left motor                 					*/
/*    Port C        RightWheel     NXT     Right motor                 					*/
/*                                                                       				*/
/*   References:                                                         				*/
/*   [1]                     																										*/
/* **************************************************************************** */
void forward()
{
	motor[LeftWheel] = 39;
	motor[RightWheel] = 40;
	wait1Msec(3800);

}

task main() {
	//set
	int leftTurn = 180;
	int speedMax = 90;
	int speedMin = -90;

	nMotorEncoderTarget[LeftWheel] = 0;
	nMotorEncoderTarget[RightWheel] = 0;
	nMotorEncoder[LeftWheel] = 0;
	nMotorEncoder[RightWheel] = 0;

	for (int i = 0; i < 7; i++)
	{

		forward();
		//turn one revolution
		nMotorEncoderTarget[LeftWheel] = leftTurn; // incremental quarter revolution
		nMotorEncoderTarget[RightWheel] = leftTurn; // incremental quarter revolution

		motor[LeftWheel] = speedMax;	// motor speed
		motor[RightWheel] = speedMin; // motor speed
		wait1Msec(2000);
	}
}
