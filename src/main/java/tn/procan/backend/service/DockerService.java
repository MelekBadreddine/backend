package tn.procan.backend.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DockerService {

    private final DockerClient dockerClient;

    @Autowired
    public DockerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public List<Image> listImages() {
        return dockerClient.listImagesCmd().exec();
    }

    public List<Container> listContainers() {
        return dockerClient.listContainersCmd().withShowAll(true).exec();
    }

    public void pullImage(String imageName) throws InterruptedException {
        dockerClient.pullImageCmd(imageName)
                .exec(new PullImageResultCallback())
                .awaitCompletion(30, TimeUnit.SECONDS);
    }

    public String createContainer(String imageName) {
        return dockerClient.createContainerCmd(imageName)
                .exec()
                .getId();
    }
}