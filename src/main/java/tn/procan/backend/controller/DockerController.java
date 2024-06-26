package tn.procan.backend.controller;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.procan.backend.service.DockerService;

import java.util.List;

@RestController
@RequestMapping("/api/docker")
@Tag(name = "Docker API", description = "Docker operations")
public class DockerController {

    private final DockerService dockerService;

    @Autowired
    public DockerController(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @GetMapping("/images")
    @Operation(summary = "List all Docker images")
    public ResponseEntity<List<Image>> listImages() {
        return ResponseEntity.ok(dockerService.listImages());
    }

    @PostMapping("/images/pull")
    @Operation(summary = "Pull a Docker image")
    public ResponseEntity<String> pullImage(@RequestParam String repository, @RequestParam String tag) throws InterruptedException {
        boolean success = dockerService.pullImage(repository, tag);
        if (success) {
            return ResponseEntity.ok("Image pulled successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to pull image");
        }
    }

    @DeleteMapping("/images")
    @Operation(summary = "Delete a Docker image")
    public ResponseEntity<String> deleteImage(@RequestParam String imageName) {
        dockerService.deleteImage(imageName);
        return ResponseEntity.ok("Image deleted successfully: " + imageName);
    }

    @PostMapping("/containers/create")
    @Operation(summary = "Create a Docker container")
    public ResponseEntity<CreateContainerResponse> createContainer(
            @RequestParam String imageName,
            @RequestParam int hostPort,
            @RequestParam int containerPort
    ) {
        return ResponseEntity.ok(dockerService.createContainer(imageName, hostPort, containerPort));
    }

    @PostMapping("/containers/start")
    @Operation(summary = "Start a Docker container")
    public ResponseEntity<String> startContainer(@RequestParam String containerId) {
        dockerService.startContainer(containerId);
        return ResponseEntity.ok("Container started successfully: " + containerId);
    }

    @GetMapping("/containers")
    @Operation(summary = "List all Docker containers")
    public ResponseEntity<List<Container>> listContainers() {
        return ResponseEntity.ok(dockerService.listContainers());
    }

    @GetMapping("/containers/running")
    @Operation(summary = "List running Docker containers")
    public ResponseEntity<List<Container>> listRunningContainers() {
        return ResponseEntity.ok(dockerService.listRunningContainers());
    }

    @PostMapping("/containers/stop")
    @Operation(summary = "Stop a Docker container")
    public ResponseEntity<String> stopContainer(@RequestParam String containerId) {
        dockerService.stopContainer(containerId);
        return ResponseEntity.ok("Container stopped successfully: " + containerId);
    }

    @DeleteMapping("/containers")
    @Operation(summary = "Delete a Docker container")
    public ResponseEntity<String> deleteContainer(@RequestParam String containerId) {
        dockerService.deleteContainer(containerId);
        return ResponseEntity.ok("Container deleted successfully: " + containerId);
    }
}