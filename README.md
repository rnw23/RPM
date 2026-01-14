User Guide – How to Use the RPM App
----

1.Run the application

	•	Requirements: Java 17 installed
	•	From the project root directory, run:
            ./gradlew clean installDist
		./build/install/RPM/bin/RPM
   
2.Login

	•	Username: admin
	•	Password: 1234
	•	Click Login

----
3.Main Screen (Dashboard)

After login, the system starts automatically.
You will see:

	•	Selected patient name and details
	•	Their vital signs (temperature, heart rate, respiration, blood pressure, ECG)
	•	Each panel shows real-time data

----
4.Alarm Colours

Each vital sign panel uses colour to show risk level:

	•	Green = Normal
	•	Amber = Warning
	•	Red = Critical
	
For RED alarms:

	•	A sound alert is played
	•	A small popup alert appears on the right cornor
	•	An email is sent (if email is configured)

----
5.Switch Between Patients

At the top-left, there is a patient selector (dropdown box).

	•	Click a patient name
	•	The dashboard updates to show that patient’s data

----
6.Heartbeat Sound

Under the Heart Rate panel, there is a button:

	•	Heartbeat Sound OFF → click to turn sound ON
	•	When it shows Heartbeat Sound ON, you will hear heartbeat sounds
	•	Click again to turn it OFF

----

7.Email Alert Setup

At the top of the screen you will see:

	•	Alert To → the email that will receive alerts
	•	Sender → the Gmail address that sends alerts
	•	AppPwd → the Gmail App Password (not normal password)
(This panel is intentionally left open so hospitals can configure their own sender email)

(For testing purposes, testers should enter the provided sender email:( rmp064015@gmail.com )and App Password:(fzza yfgl vyte meud),
then enter their own email in Alert To to receive alarm emails.)
Steps:

	1.	Fill in the three fields
	2.	Click Apply Email
	3.	When a RED alarm happens, an email will be sent automatically
	
！！Emails are only sent for RED alarms, and the system limits repeated alerts to avoid spam.

----

8.Time Window Slider

Next to the email settings is a Window slider.

	•	Drag the slider left or right
	•	This changes how many seconds of data are shown in each chart
	•	Smaller window = more zoomed-in view
	•	Larger window = longer history view

----

9.Permanent Record

In the Patient Details panel, and below the contact, there is a button where you can download a permanent record:

	•	Click "Download Permanent Record"
	•	Choose the location you want the record to be
    •	Permanent record is saved in your computer as xlsx.

----

10.Daily Report

On the right upper corner, next to the Apply Email button, you can download daily report of a specific patient in a specific date:

	•	Click "Generate Daily Report"
	•	Enter the patient id
	•	Enter the date you want to view
	•	Choose the location you want the record to be
    •	Daily Report is saved in your computer as xlsx.

----
11.End programme