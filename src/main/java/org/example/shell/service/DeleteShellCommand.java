package org.example.shell.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import org.example.shell.config.AccessProp;
import org.example.shell.config.S3Config;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.shell.component.MultiItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.ArrayList;

@RequiredArgsConstructor

@ShellComponent
public class DeleteShellCommand extends AbstractShellComponent {

    private final ObjectFactory<AmazonS3> amazonS3Proxy;
    private final AccessProp prop;
    private final S3Config config;

    @ShellMethod(key = "delete")
    public String deleteFromS3() {

        var client = amazonS3Proxy.getObject();
        var bucket = prop.getEnvs()
                .get(config.getCurrentEnv())
                .getBucket();

        var listResponse = client.listObjects(bucket);

        var fileNames = new ArrayList<SelectorItem<String>>();

        listResponse.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .forEach(fileKey -> fileNames.add(SelectorItem.of(fileKey, fileKey)));

        var component = new MultiItemSelector<>(getTerminal(), fileNames, "deleteFiles", null);

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        var context = component.run(MultiItemSelector.MultiItemSelectorContext.empty());

        context.getResultItems()
                .forEach(fileSelect -> {
                    var request = new DeleteObjectRequest(bucket, fileSelect.getName());
                    client.deleteObject(request);
                });

        return "DONE";
    }

}
