package in.clouthink.synergy.attachment.event;


import in.clouthink.synergy.account.domain.model.User;
import in.clouthink.synergy.attachment.domain.model.Attachment;
import in.clouthink.synergy.attachment.domain.request.DownloadAttachmentEvent;

/**
 * The download attachment event object
 */
public class DownloadAttachmentEventObject implements DownloadAttachmentEvent {

	private Attachment attachment;

	private User user;

	public DownloadAttachmentEventObject(Attachment attachment, User user) {
		this.attachment = attachment;
		this.user = user;
	}

	@Override
	public Attachment getAttachment() {
		return attachment;
	}

	@Override
	public User getUser() {
		return user;
	}

}
