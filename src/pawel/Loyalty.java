package pawel;

import javacard.framework.*;


public class Loyalty extends Applet {

	public final static byte INS_CREDIT = 0x02;
	public final static byte INS_DEBIT = 0x04;
	public final static byte INS_ACCOUNT_BALANCE = 0x06;
	public final static byte INS_VERIFY = (byte) 0x20;
	public final static byte INS_CONFIRM_TRANSACTION = 0x66;
	
	private final static byte[] PIN_INIT_VALUE = {(byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55};
	public final static byte PIN_TRY_LIMIT = (byte) 3;
	public final static byte PIN_MAX_SIZE = (byte) 16;

	private final static short SW_PIN_TRY_LOCKED = (short) 0x63c0;
    private final static short SW_VERIFICATION_FAILED = 0x6300;
    private final static short SW_PIN_VERIFICATION_REQUIRED = 0x6301;
    private final static short SW_TRANSACTION_NOT_MANDATORY = 0x7777;
	
	private short[] tokenVault;
	private short[] balance = JCSystem.makeTransientShortArray((short) 1, (byte) JCSystem.CLEAR_ON_DESELECT);
	OwnerPIN pin;
	
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new Loyalty(bArray, bOffset, bLength);
	}
	
	public boolean select() {
		if ( pin.getTriesRemaining() == 0 )
	           return false;
		return true;
	}
	public void deselect() {
		pin.reset();
	}

	private Loyalty(byte[] bArray, short bOffset, byte bLength) {	
		//balance[0] = 0x0000;
		pin = new OwnerPIN(PIN_TRY_LIMIT, PIN_MAX_SIZE);
		pin.update(PIN_INIT_VALUE, (short)0, (byte) 0x04);
		
		tokenVault = new short[1];
		//balance[0] += tokenVault[0];
	}

	public void process(APDU apdu) {
		
		// Nothing particular to do on SELECT
		if (selectingApplet())
			return;

		byte[] buffer = apdu.getBuffer();

		switch (buffer[ISO7816.OFFSET_INS]) {
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
		
		if(apdu.setIncomingAndReceive() != 2)
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		
		balance[0] += (short) Util.getShort(buffer, ISO7816.OFFSET_CDATA);
		accountBalance(apdu,buffer);
	}

	private void debitToken(APDU apdu, byte[] buffer) { 		
		if ( ! pin.isValidated() )
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		
		if(apdu.setIncomingAndReceive() != 2)
		ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		
		if(balance[0] >= (short) Util.getShort(buffer, ISO7816.OFFSET_CDATA)) {
			balance[0] -= (short) Util.getShort(buffer, ISO7816.OFFSET_CDATA);//(byte) buffer[ISO7816.OFFSET_CDATA];
		} else
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		accountBalance(apdu,buffer);
			
	}
	 
	private void accountBalance(APDU apdu, byte[] buffer) {
		
		apdu.setOutgoing();
		apdu.setOutgoingLength((byte) 4);

		Util.setShort(buffer, (short) 0, balance[0]);
		Util.setShort(buffer, (short) 2, tokenVault[0]);
		//tokenVault checking is only for tests
		
		apdu.sendBytes((byte) 0, (byte) 4);
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
		if ( ! pin.isValidated() )
            ISOException.throwIt(SW_PIN_VERIFICATION_REQUIRED);
		
		if(tokenVault[0] != balance[0] && balance[0] >= (short)0 )
			tokenVault[0] += balance[0];
		else  
			ISOException.throwIt(SW_TRANSACTION_NOT_MANDATORY);			 
	}
}
