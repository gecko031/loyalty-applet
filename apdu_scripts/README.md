## Emulator scripts 

This directory contains scripts generated by Eclipse and other scripts made for testing the applet.

### Scripts desctiption

* cap-loyalty.script - installs applet on emulator virtual machine
* create-loyalty.Loyalty.script - creates an instance of an applet.
* select-loyalty.Loyalty.script - selects the one particular applet from card's memory.
* pinValidation.script - validates PIN and allows to run credit, debit and confirmTransaction scripts
* check_balance.script - check account balance.
* credit5points.script - add 5 points to card balance.
* debit2points.script - debit 2 points from card balance.
* verifyTransaction.script - save transaction state to card's EEPROM memory.
* scenario1.script - example script which credits 10 points and verify transaction.
* scenario2.script - example script which debits 2 points and verify transaction.

### Instruction

* To run any other operation beyond `check_balance.script` You need to run `pinValidation.script`. 
