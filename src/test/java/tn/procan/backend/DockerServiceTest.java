package tn.procan.backend;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.procan.backend.service.DockerService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
	void listContainers() {
		List<Container> expectedContainers = Arrays.asList(new Container(), new Container());
		ListContainersCmd listContainersCmd = mock(ListContainersCmd.class);

		when(dockerClient.listContainersCmd()).thenReturn(listContainersCmd);
		when(listContainersCmd.withShowAll(true)).thenReturn(listContainersCmd);
		when(listContainersCmd.exec()).thenReturn(expectedContainers);

		List<Container> actualContainers = dockerService.listContainers();

		assertEquals(expectedContainers, actualContainers);
		verify(dockerClient).listContainersCmd();
		verify(listContainersCmd).withShowAll(true);
		verify(listContainersCmd).exec();
	}

	@Test
	void createContainer() {
		String imageName = "test-image";
		String expectedContainerId = "test-container-id";
		CreateContainerCmd createContainerCmd = mock(CreateContainerCmd.class);
		CreateContainerResponse createContainerResponse = mock(CreateContainerResponse.class);

		when(dockerClient.createContainerCmd(imageName)).thenReturn(createContainerCmd);
		when(createContainerCmd.exec()).thenReturn(createContainerResponse);
		when(createContainerResponse.getId()).thenReturn(expectedContainerId);

		String actualContainerId = dockerService.createContainer(imageName);

		assertEquals(expectedContainerId, actualContainerId);
		verify(dockerClient, times(1)).createContainerCmd(imageName);
		verify(createContainerCmd, times(1)).exec();
	}

	@Test
	void pullImage() throws InterruptedException {
		String imageName = "test-image";
		PullImageCmd pullImageCmd = mock(PullImageCmd.class);
		PullImageResultCallback pullImageResultCallback = new PullImageResultCallback();

		when(dockerClient.pullImageCmd(imageName)).thenReturn(pullImageCmd);
		when(pullImageCmd.exec(any(PullImageResultCallback.class))).thenAnswer(invocation -> {
			PullImageResultCallback callback = invocation.getArgument(0);
			// Simulate the callback completion
			callback.onComplete();
			return callback;
		});

		dockerService.pullImage(imageName);

		verify(dockerClient).pullImageCmd(imageName);
		verify(pullImageCmd).exec(any(PullImageResultCallback.class));
	}
}