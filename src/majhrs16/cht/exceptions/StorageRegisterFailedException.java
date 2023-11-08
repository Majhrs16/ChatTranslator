package majhrs16.cht.exceptions;

public class StorageRegisterFailedException extends Exception {
	public StorageRegisterFailedException(String message, Throwable cause) { super(message, cause); }
	public StorageRegisterFailedException(String message)                  { super(message); }
	public StorageRegisterFailedException(Throwable cause)                 { super(cause); }

	private static final long serialVersionUID = 1352322319700187713L;
}
