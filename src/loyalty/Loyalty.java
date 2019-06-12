package loyalty;

import javacard.framework.*;


public class Loyalty extends Applet {

	public final static byte INS_CREDIT = 0x02;
	public final static byte INS_DEBIT = 0x04;
	public final static byte INS_ACCOUNT_BALANCE = 0x06;
	public final static byte INS_VERIFY = (byte) 0x20;
	public final static byte INS_CONFIRM_TRANSACTION = 0x66;
	public final static byte INS_CREATE_INSTANCE = 0x10;
	
	public final static byte PIN_TRY_LIMIT = (byte) 3;
	public final static byte PIN_MAX_SIZE = (byte) 16;

	private final static short SW_PIN_TRY_LOCKED = (short) 0x63c0;
    private final static short SW_VERIFICATION_FAILED = 0x6300;
    private final static short SW_PIN_VERIFICATION_REQUIRED = 0x6301;
    private final static short SW_TRANSACTION_FAILED = 0x7777;
	private final static byte[] PIN_INIT_VALUE = {(byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55};
    
	private byte[] tokenVault;
	private byte[] balance;
	OwnerPIN pin;
	
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new Loyalty(bArray, bOffset, bLength);
	}
	
	public boolean select() {
		createInstance();
		if ( pin.getTriesRemaining() == 0 )
	           return false;
		return true;
	}
	public void deselect() {
		pin.reset();
	}

	private Loyalty(byte[] bArray, short bOffset, byte bLength) {
		
		pin = new OwnerPIN(PIN_TRY_LIMIT, PIN_MAX_SIZE);
		pin.update(PIN_INIT_VALUE, (short)0, (byte) 0x04);
		
		tokenVault = new byte[1];
		balance = JCSystem.makeTransientByteArray((short) 1, (byte) JCSystem.CLEAR_ON_DESELECT);
		createInstance();
        register();
	}

	public void process(APDU apdu) {
		
		// Nothing particular to do on SELECT
		if (selectingApplet())
			return;

		byte[] buffer = apdu.getBuffer();

		switch (buffer[ISO7816.OFFSET_INS]) {
		/*
		 * case INS_CREATE_INSTANCE: createInstance(); break;
		 */
		case INS_CREDIT:
			creditToken(apdu, buffer);
			break;
		case INS_DEBIT:
			debitToken(apdu, buffer);
			break;
		case INS_ACCOUNT_BALANCE:
			accountBalance(apdu, buffer);
			break;
        case INS_VERIFY:
            verify(apdu);
            break;
        case INS_CONFIRM_TRANSACTION:
        	confirmTransaction();
        	break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

	void creditToken(APDU apdu, byte[] buffer) {		
		if ( ! pin.isValidated() )
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		
		if(apdu.setIncomingAndReceive() != 1)
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		
		balance[0] += (byte) buffer[ISO7816.OFFSET_CDATA];
	}
	
	private void debitToken(APDU apdu, byte[] buffer) { 		
		if ( ! pin.isValidated() )
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		
		if(apdu.setIncomingAndReceive() != 1)
		ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		
		if(balance[0] >= (byte)buffer[ISO7816.OFFSET_CDATA]) {
			balance[0] -= (byte) buffer[ISO7816.OFFSET_CDATA];
			//tokenVault[0] -= (byte) buffer[ISO7816.OFFSET_CDATA];
		} else
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
	}
	private void accountBalance(APDU apdu, byte[] buffer) {
		apdu.setOutgoing();
		apdu.setOutgoingLength((byte) 2);
		buffer[0] = balance[0];
		buffer[1] = tokenVault[0];
		apdu.sendBytes((byte) 0, (byte) 2);
	}
	private void verify(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        // retrieve the PIN data for validation.
        byte byteRead = (byte)(apdu.setIncomingAndReceive());        
        // check pin
        if ( pin.check(buffer, ISO7816.OFFSET_CDATA, byteRead) == false ) {
            if (pin.getTriesRemaining() == (byte) 0x00)
        		ISOException.throwIt(SW_PIN_TRY_LOCKED);
            else {ISOException.throwIt(SW_VERIFICATION_FAILED);}
        }
    }
	private void confirmTransaction() {
		if(tokenVault[0] >= (byte)0x00) 
			tokenVault[0] = balance[0];
		else
			ISOException.throwIt(SW_TRANSACTION_FAILED);
	}
	private void createInstance() {
		balance[0] += tokenVault[0];
	}
}
