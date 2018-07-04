# nxt-suite
This software was developed during our lecture in object oriented systems.
It was written for the Lego [NXT Mindstorm Robot](https://de.wikipedia.org/wiki/Lego_Mindstorms_NXT).
It consists of three separate projects.


### nxt-brick
This software is written in Java for the [lejos](http://www.lejos.org/) JVM which runs on the robot. It is able to follow a black line on the ground autonomously using different strategies. In combination with the remote control it can send its log messages to the remote, update the UI of the remotecontrol with the state of the robot and receive commands for the control for manual driving, changing strategies or setting a flag to follow the line when it finds it.  

### nxt-remotecontrol
The remotecontrol is an Android App written with Android 8 as the target. (Should work on older devices, though). Since the communication uses Bluetooth the device needs a Bluetooth adapter. This project uses the some [classes ported form Lejos](https://github.com/Shawn-in-Tokyo/leJOS-Droid) to handle the specific packet format of lejos. It is able to send commands, receive and display log messages and process state updates from the robot.

### nxt-messaging
This is the common project shared by the nxt-brick as well as the remote control.
It specifies how data can be serialized into bytes, and deserialized into objects on the other side. It is the best documented of the three projects, with some comments and tests.

### Miscellaneous
If some questions are left unanwsered, don't hesitate to contact me over this Github Account or some other way of communication.

The developers:
- Felix
- Mouad
- Phillip
- Till

*Steinfurt, 04.07.2018*

![][robot_side]
![][app_view_1]

[robot_side]: https://raw.githubusercontent.com/LostInCoding/nxt-suite/master/assets/robot_side.jpg "Sideview of robot"
[robot_front]: https://raw.githubusercontent.com/LostInCoding/nxt-suite/master/assets/robot_front.jpg "Top view of robot"
[app_view_1]: https://raw.githubusercontent.com/LostInCoding/nxt-suite/master/assets/screenshot1.png "Screenshot of app"
[app_view_2]: https://raw.githubusercontent.com/LostInCoding/nxt-suite/master/assets/screenshot2.png "Screenshot of app"
