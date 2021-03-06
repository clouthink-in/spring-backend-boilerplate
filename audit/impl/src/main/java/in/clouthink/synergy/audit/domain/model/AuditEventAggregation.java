package in.clouthink.synergy.audit.domain.model;

import in.clouthink.synergy.shared.domain.model.StringIdentifier;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author dz
 */
@Document(collection = "AuditEventAggregations")
public class AuditEventAggregation extends StringIdentifier {

	@Indexed
	private String realm;

	@Indexed
	private AggregationType aggregationType;

	private String aggregationKey;

	private long totalCount;

	private long errorCount;

	private long totalTimeCost;

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public AggregationType getAggregationType() {
		return aggregationType;
	}

	public void setAggregationType(AggregationType aggregationType) {
		this.aggregationType = aggregationType;
	}

	public String getAggregationKey() {
		return aggregationKey;
	}

	public void setAggregationKey(String aggregationKey) {
		this.aggregationKey = aggregationKey;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(long errorCount) {
		this.errorCount = errorCount;
	}

	public long getTotalTimeCost() {
		return totalTimeCost;
	}

	public void setTotalTimeCost(long totalTimeCost) {
		this.totalTimeCost = totalTimeCost;
	}
}
