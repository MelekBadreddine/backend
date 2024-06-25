package tn.procan.backend;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.procan.backend.service.DockerService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DockerServiceTest {

    @Mock
    private DockerClient dockerClient;

    private DockerService dockerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dockerService = new DockerService(dockerClient);
    }

    @Test
    void listImages() {
        List<Image> expectedImages = Arrays.asList(new Image(), new Image());
        ListImagesCmd listImagesCmd = mock(ListImagesCmd.class);
        when(dockerClient.listImagesCmd()).thenReturn(listImagesCmd);
        when(listImagesCmd.exec()).thenReturn(expectedImages);
        List<Image> actualImages = dockerService.listImages();
        assertEquals(expectedImages, actualImages);
        verify(dockerClient).listImagesCmd();
        verify(listImagesCmd).exec();
    }

    @Test
    void pullImage() throws InterruptedException {
        String repository = "test/repo";
        String tag = "latest";
        PullImageCmd pullImageCmd = mock(PullImageCmd.class);
        PullImageResultCallback pullImageResultCallback = mock(PullImageResultCallback.class);
        when(dockerClient.pullImageCmd(repository)).thenReturn(pullImageCmd);
        when(pullImageCmd.withTag(tag)).thenReturn(pullImageCmd);
        when(pullImageCmd.exec(any())).thenReturn(pullImageResultCallback);
        when(pullImageResultCallback.awaitCompletion(anyLong(), any())).thenReturn(true);
        boolean result = dockerService.pullImage(repository, tag);
        assertTrue(result);
        verify(dockerClient).pullImageCmd(repository);
        verify(pullImageCmd).withTag(tag);
        verify(pullImageCmd).exec(any());
        verify(pullImageResultCallback).awaitCompletion(anyLong(), any());
    }

    @Test
    void createContainer() {
        String imageName = "test/image";
        int hostPort = 9090;
        int containerPort = 8080;

        CreateContainerResponse expectedResponse = mock(CreateContainerResponse.class);
        com.github.dockerjava.api.command.CreateContainerCmd createContainerCmd = mock(com.github.dockerjava.api.command.CreateContainerCmd.class);
        ExposedPort exposedPort = ExposedPort.tcp(containerPort);
        Ports portBindings = new Ports();
        portBindings.bind(exposedPort, Ports.Binding.bindPort(hostPort));
        HostConfig hostConfig = HostConfig.newHostConfig().withPortBindings(portBindings);
        when(dockerClient.createContainerCmd(imageName)).thenReturn(createContainerCmd);
        when(createContainerCmd.withExposedPorts(exposedPort)).thenReturn(createContainerCmd);
        when(createContainerCmd.withHostConfig(any(HostConfig.class))).thenReturn(createContainerCmd);
        when(createContainerCmd.exec()).thenReturn(expectedResponse);
        CreateContainerResponse actualResponse = dockerService.createContainer(imageName, hostPort, containerPort);
        assertEquals(expectedResponse, actualResponse);
        verify(dockerClient).createContainerCmd(imageName);
        verify(createContainerCmd).withExposedPorts(exposedPort);
        verify(createContainerCmd).withHostConfig(argThat(config ->
                config.getPortBindings().getBindings().get(exposedPort)[0].getHostPortSpec().equals(String.valueOf(hostPort))
        ));
        verify(createContainerCmd).exec();
    }


    @Test
    void startContainer() {
        String containerId = "containerId";
        StartContainerCmd startContainerCmd = mock(StartContainerCmd.class);
        when(dockerClient.startContainerCmd(containerId)).thenReturn(startContainerCmd);
        doNothing().when(startContainerCmd).exec();
        dockerService.startContainer(containerId);
        verify(dockerClient).startContainerCmd(containerId);
        verify(startContainerCmd).exec();
    }
}
