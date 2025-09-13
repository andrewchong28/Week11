package recipes.exception;

@SuppressWarnings("serial")//tells Java not to warn about missing serialVersionUID

//class DbException extends to super class RuntimeExcption
//inherits behavior from superclass
public class DbException extends RuntimeException { 

	
	//allows for a DbException with no message
	public DbException() {
		//super() calls the no-argument constructor of RuntimeException.
		super();

	}

	
	//to throw this exception this would be needed in dbconnection
	/*catch (SQLException e) {
	    throw new DbException(
	        "Failed to connect to database", // message
	        e,                               // cause
	        true,                            // enableSuppression
	        false                            // writableStackTrace
	    );
	}*/
	public DbException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}
	

	public DbException(String message, Throwable cause) {
		super(message, cause);

	}

	public DbException(String message) {
		super(message);

	}

	public DbException(Throwable cause) {
		super(cause);

	}

}
