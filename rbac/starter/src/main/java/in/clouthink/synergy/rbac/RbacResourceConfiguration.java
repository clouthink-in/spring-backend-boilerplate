package in.clouthink.synergy.rbac;

import in.clouthink.synergy.rbac.annotation.EnableResource;
import in.clouthink.synergy.rbac.annotation.Permission;
import in.clouthink.synergy.rbac.annotation.Resource;
import in.clouthink.synergy.rbac.model.Action;
import org.springframework.context.annotation.Configuration;

/**
 * @author dz
 */
@Configuration
@EnableResource({
        @Resource(
                code = "permission:manage",
                name = "权限管理",
                permission = {
                        @Permission(api = "/api/permissions/roles**", action = Action.GET),
                        @Permission(api = "/api/permissions/resources**", action = Action.GET)}),
        @Resource(
                parent = "permission:manage",
                code = "permission:grant",
                name = "授权",
                permission = {
                        @Permission(api = "/api/permissions/roles**", action = Action.POST),
                        @Permission(api = "/api/permissions/resources**", action = Action.POST)}),
        @Resource(
                parent = "permission:manage",
                code = "permission:revoke",
                name = "取消授权",
                permission = {
                        @Permission(api = "/api/permissions/roles**", action = Action.DELETE),
                        @Permission(api = "/api/permissions/resources**", action = Action.DELETE)}),
        @Resource(code = "resource:manage",
                name = "查看资源",
                permission = {
                        @Permission(api = "/api/resources/**", action = Action.GET)}),
})
public class RbacResourceConfiguration {

}
