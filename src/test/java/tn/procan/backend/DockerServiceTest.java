package tn.procan.backend;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Image;
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
        CreateContainerResponse expectedResponse = mock(CreateContainerResponse.class);
        com.github.dockerjava.api.command.CreateContainerCmd createContainerCmd = mock(com.github.dockerjava.api.command.CreateContainerCmd.class);

        when(dockerClient.createContainerCmd(imageName)).thenReturn(createContainerCmd);
        when(createContainerCmd.exec()).thenReturn(expectedResponse);

        CreateContainerResponse actualResponse = dockerService.createContainer(imageName);

        assertEquals(expectedResponse, actualResponse);
        verify(dockerClient).createContainerCmd(imageName);
        verify(createContainerCmd).exec();
    }
}
