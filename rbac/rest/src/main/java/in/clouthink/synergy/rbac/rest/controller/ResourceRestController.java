package in.clouthink.synergy.rbac.rest.controller;

import in.clouthink.synergy.rbac.rest.view.PrivilegedResourceWithChildrenView;
import in.clouthink.synergy.rbac.rest.support.ResourceRestSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "/api/resources", description = "资源列表")
@RestController
@RequestMapping("/api/resources")
public class ResourceRestController {

    @Autowired
    private ResourceRestSupport resourceRestSupport;

    @ApiOperation(value = "获取资源列表(不包括open)")
    @RequestMapping(method = RequestMethod.GET)
    public List<PrivilegedResourceWithChildrenView> listResources() {
        return resourceRestSupport.listResources();
    }

}
