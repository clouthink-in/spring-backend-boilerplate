package in.clouthink.synergy.team.rest.dto;

import in.clouthink.synergy.shared.domain.request.impl.PageQueryParameter;

/**
 */
public class TaskReceiverNameQueryParameter extends PageQueryParameter {

	private String receiverName;

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

}