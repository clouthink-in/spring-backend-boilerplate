package in.clouthink.synergy.attachment.exception;

import in.clouthink.synergy.exception.BizException;

public class AttachmentException extends BizException {

	public AttachmentException() {
	}

	public AttachmentException(String message) {
		super(message);
	}

	public AttachmentException(String message, Throwable cause) {
		super(message, cause);
	}

	public AttachmentException(Throwable cause) {
		super(cause);
	}
}
