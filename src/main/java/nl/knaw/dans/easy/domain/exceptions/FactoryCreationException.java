package nl.knaw.dans.easy.domain.exceptions;

public class FactoryCreationException extends DomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4586815837693426002L;

	public FactoryCreationException() {
	}

	public FactoryCreationException(String message) {
		super(message);
	}

	public FactoryCreationException(Throwable cause) {
		super(cause);
	}

	public FactoryCreationException(String message, Throwable cause) {
		super(message, cause);
	}

}
