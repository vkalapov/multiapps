package com.sap.cloud.lm.sl.mta.mergers.v2;

import java.util.Map;

import com.sap.cloud.lm.sl.mta.handlers.v2.DescriptorHandler;
import com.sap.cloud.lm.sl.mta.model.ElementContext;
import com.sap.cloud.lm.sl.mta.model.PropertiesContainer;
import com.sap.cloud.lm.sl.mta.model.Visitor;
import com.sap.cloud.lm.sl.mta.model.v2.DeploymentDescriptor;
import com.sap.cloud.lm.sl.mta.model.v2.ExtensionDescriptor;
import com.sap.cloud.lm.sl.mta.model.v2.ExtensionModule;
import com.sap.cloud.lm.sl.mta.model.v2.ExtensionRequiredDependency;
import com.sap.cloud.lm.sl.mta.model.v2.ExtensionResource;
import com.sap.cloud.lm.sl.mta.model.v2.Module;
import com.sap.cloud.lm.sl.mta.model.v2.RequiredDependency;
import com.sap.cloud.lm.sl.mta.model.v2.Resource;
import com.sap.cloud.lm.sl.mta.util.PropertiesUtil;

public class ExtensionDescriptorMerger extends Visitor {

    protected final ExtensionDescriptor extensionDescriptor;
    protected final DescriptorHandler handler;

    public ExtensionDescriptorMerger(ExtensionDescriptor extensionDescriptor, DescriptorHandler handler) {
        this.extensionDescriptor = extensionDescriptor;
        this.handler = handler;
    }

    @Override
    public void visit(ElementContext context, DeploymentDescriptor descriptor) {
        DeploymentDescriptor descriptorV2 = descriptor;
        ExtensionDescriptor extensionDescriptorV2 = extensionDescriptor;
        descriptorV2
            .setParameters(PropertiesUtil.mergeExtensionProperties(descriptorV2.getParameters(), extensionDescriptorV2.getParameters()));
    }

    @Override
    public void visit(ElementContext context, RequiredDependency requiredDependency) {
        String containerName = context.getPreviousElementContext()
            .getVisitableElementName();
        ExtensionRequiredDependency extension = ((DescriptorHandler) handler)
            .findRequiredDependency((ExtensionDescriptor) extensionDescriptor, containerName, requiredDependency.getName());
        if (extension != null) {
            merge(requiredDependency, extension);
        }
    }

    protected void merge(RequiredDependency requiredDependency, ExtensionRequiredDependency extension) {
        requiredDependency
            .setParameters(PropertiesUtil.mergeExtensionProperties(requiredDependency.getParameters(), extension.getParameters()));
        requiredDependency
            .setProperties(PropertiesUtil.mergeExtensionProperties(requiredDependency.getProperties(), extension.getProperties()));
    }

    public void merge(Module module, ExtensionModule extension) {
        module.setProperties(mergeProperties(module, extension));
        module.setParameters(PropertiesUtil.mergeExtensionProperties(module.getParameters(), extension.getParameters()));
    }

    protected Map<String, Object> mergeProperties(PropertiesContainer container, PropertiesContainer extensionContainer) {
        return PropertiesUtil.mergeExtensionProperties(container.getProperties(), extensionContainer.getProperties());
    }

    public void merge(Resource resource, ExtensionResource extension) {
        resource.setProperties(mergeProperties(resource, extension));
        resource.setParameters(PropertiesUtil.mergeExtensionProperties(resource.getParameters(), extension.getParameters()));
    }

}
