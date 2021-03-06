package in.clouthink.synergy.shared.domain.request;

/**
 * page query request
 */
public interface PageSearchRequest extends AbstractSearchRequest {

	/**
	 * @return 0 as default
	 */
	int getStart();

	/**
	 * @return 20 as default
	 */
	int getLimit();

}
