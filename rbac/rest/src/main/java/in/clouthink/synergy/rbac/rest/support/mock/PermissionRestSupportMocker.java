package in.clouthink.synergy.rbac.rest.support.mock;

import in.clouthink.synergy.rbac.impl.model.TypedRole;
import in.clouthink.synergy.rbac.rest.dto.GrantResourceParameter;
import in.clouthink.synergy.rbac.rest.dto.PrivilegedResourceWithChildren;
import in.clouthink.synergy.rbac.rest.support.PermissionRestSupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionRestSupportMocker implements PermissionRestSupport {

    @Override
    public List<PrivilegedResourceWithChildren> listGrantedResources(String roleCode) {
        return null;
    }

    @Override
    public List<TypedRole> listGrantedRoles(String code) {
        return null;
    }

    @Override
    public void grantResourcesToRole(String roleCode, GrantResourceParameter grantRequest) {

    }

    @Override
    public void revokeResourcesFromRole(String roleCode, String resourceCode) {

    }
}