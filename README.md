# Loyalty - applet

Smart card Loyalty applet which can be used as payment for goods in 
[loyalty-GUI application - Guitar store](https://github.com/gecko031/loyalty-GUI/tree/master).<br/>
This applet was developed and ran on Windows 7, because of card reader's<br/>
and Java Card technology support. Project has been made for Bacheler's of science degree.

## Getting Started

There are two ways to get this software up and running.<br/>
First - install applet on physical card.<br/>
Second - run through emulator.<br/>
(personally)Best way to connect applet with GUI application is to install it on a card.

### Prerequisites

#### Run applet on physical card through GlobalPlatform:

* Buy PC/SC card reader (Must support T=0 protocol) - (I used Gemalto IDBridge CT30)
* Buy Smart card (Java Card OS) - (I used one of the cheapest,
 model J2A040 from aliexpress.com - [link here](https://www.aliexpress.com/i/32856082491.html)
* Download [GPShell](https://sourceforge.net/projects/globalplatform/files/GPShell/)
* Download and install card reader drivers
* Download only .cap file from link to this repository


#### Run through eclipse emulator:

* Eclipse latest version
* (JCDK) Java Card Development Kit - ver 3.0.5u3
* EclipseJCDE plugin - [download](https://sourceforge.net/projects/eclipse-jcde/files/)

### Installing

#### To install applet with GlobalPlatform follow these instructions:

Instructions are decribed in `GPShell_scripts` directory in `README.md` file.

#### To install Eclipse emulator follow these instructions:

1.	Install Eclipse.
2.	Install JCDK.
3.	Unpack EclipseJCDE and extract all files from plugin directory to plugin in (yourEclipseLocation)/plugin.
4.	Open Java Card View through `Window>Show view>Other>Oracle Java Card SDK>Java Card View`.
5.	Righ click on Sample_device and chose properties.
6.	Set file path in "Output file for EEPROM data:" for .bat extension file (any location).
7.	Run Sample_Device.
8.	Run scripts from `adpu_scripts` directory in following order to install applet on virtual card machine:
	1.	Run `cap-loyalty.script`
	2.	Run `create-loyalty.Loyalty.script`
9.	Stop Sample_Device.
10.	Go to `properties` in Sample_device and cut filepath from "Output file for EEPROM data:" to<br/>"Combined (input and output) file for EEPROM data:". 

Now applet is installed and ready to perform tests.

## Running the tests

#### Run tests on physical card through GlobalPlatform:

Tests are explained in `README.md` inside `GPShell_scripts`

#### Run tests through eclipse JCDE emulator

Tests are decribed in `README.md` inside `apdu_scripts` directory.

## Built With

* JCDK - Java Card Development Kit- ver 3.0.5.3
* Eclipse 
* [GlobalPlatform](https://sourceforge.net/p/globalplatform/wiki/Home/) 

## License

This project is licensed under the MIT License

## Acknowledgments

* [GPShell Wiki](https://sourceforge.net/p/globalplatform/wiki/GPShell/) - documentation of GPShell


