package tn.procan.backend.controller;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.procan.backend.service.DockerService;

import java.util.List;

@RestController
@RequestMapping("/api/docker")
public class DockerController {

    private final DockerService dockerService;

    @Autowired
    public DockerController(DockerService dockerService) {
        this.dockerService = dockerService;
    }

    @GetMapping("/images")
    public ResponseEntity<List<Image>> listImages() {
        return ResponseEntity.ok(dockerService.listImages());
    }

    @GetMapping("/containers")
    public ResponseEntity<List<Container>> listContainers() {
        return ResponseEntity.ok(dockerService.listContainers());
    }

    @PostMapping("/images/pull")
    public ResponseEntity<String> pullImage(@RequestParam String imageName) {
        try {
            dockerService.pullImage(imageName);
            return ResponseEntity.ok("Image pulled successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("Error pulling image: " + e.getMessage());
        }
    }

    @PostMapping("/containers/create")
    public ResponseEntity<String> createContainer(@RequestParam String imageName) {
        String containerId = dockerService.createContainer(imageName);
        return ResponseEntity.ok("Container created with ID: " + containerId);
    }
}