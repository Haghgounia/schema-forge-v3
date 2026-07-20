package com.behsazan.schemaforge.discovery.core;

import com.behsazan.schemaforge.discovery.domain.DiscoveryIssue;
import java.util.List;

public interface DiscoveryRule {

    List<DiscoveryIssue> evaluate(DiscoveryContext context);
}
