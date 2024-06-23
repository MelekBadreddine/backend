package tn.procan.backend.exception;

public class DockerApiException extends RuntimeException {
    public DockerApiException(String message) {
        super(message);
    }

    public DockerApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
