package org.example.shell.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.example.shell.config.AccessProp;
import org.example.shell.config.S3Config;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.shell.component.MultiItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.nio.file.Files.list;
import static java.nio.file.Paths.get;

@RequiredArgsConstructor

@ShellComponent
public class CopyShellCommand extends AbstractShellComponent  {

    private final ObjectFactory<AmazonS3> amazonS3Proxy;
    private final AccessProp prop;
    private final S3Config config;

    @ShellMethod(key = "upload")
    public String copyToS3() throws IOException {

        try (var list = list(get("./upload"))) {

            var files = new ArrayList<SelectorItem<String>>();

            list
                    .filter(path -> path.toFile().isFile())
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .forEach(name -> files.add(SelectorItem.of(name, name)));

            var component = new MultiItemSelector<>(getTerminal(),
                    files, "listFiles", null);

            component.setResourceLoader(getResourceLoader());
            component.setTemplateExecutor(getTemplateExecutor());

            var context = component
                    .run(MultiItemSelector.MultiItemSelectorContext.empty());

            var bucketName = prop.getEnvs()
                    .get(config.getCurrentEnv())
                    .getBucket();

            var client = amazonS3Proxy.getObject();

            context.getResultItems().forEach(item -> {
                        var name = item.getItem();

                        var request = new PutObjectRequest(bucketName, name, new File("./upload/" + name));
                client.putObject(request);
            });
        }

        return "DONE";
    }

}