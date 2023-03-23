package com.setplex.movieservice.adapter.filestorage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class InMemoryFileStorageComponentTest {

    @TempDir
    private static Path SHARED_TEMP_DIR;

    private static final UUID FILE_UUID = UUID.randomUUID();

    private static final MultipartFile MULTIPART_FILE = new MockMultipartFile(
        "image", 
        "image.txt", 
        MediaType.TEXT_PLAIN_VALUE, 
        "image".getBytes()
    );
 
    @InjectMocks
	private InMemoryStorageComponent target;


    @Test
    public void whenStore__fileIsEmpty__shouldThrowException(@TempDir Path tempDir) {
        // given
        var fileUUID = FILE_UUID;
        var multipartFile = new MockMultipartFile("image.txt", new byte[0]);

        // when
        Executable result = () -> target.store(tempDir.toString(), fileUUID, multipartFile);
        
        // then
        assertThrows(RuntimeException.class, result);
    }

    @Test
    public void whenLoad__fileIsNotPresent__shouldThrowException(@TempDir Path tempDir) {
        // given
        var fileUUID = FILE_UUID;

        // when
        Executable result = () -> {
            try(InputStream inputStream = target.load(tempDir.toString(), fileUUID)) {
                
            }
        };
        
        // then
        assertThrows(RuntimeException.class, result);
    }

    @Test
    @Order(1)
    public void whenStore__fileSuccessfullyStored__shouldReturnId() {
        // given
        var fileUUID = FILE_UUID;
        var multipartFile = MULTIPART_FILE;

        // when
        var result = target.store(SHARED_TEMP_DIR.toString(), fileUUID, multipartFile);
        
        // then
        assertNotNull(result);
    }

    @Test
    @Order(2)
    public void whenLoad__fileSuccessfullyLoaded__shouldReturnSavedFile() throws IOException {
        // given
        var fileUUID = FILE_UUID;

        // when
        try (InputStream result = target.load(SHARED_TEMP_DIR.toString(), fileUUID)) {
            // then
            assertArrayEquals(MULTIPART_FILE.getBytes(), result.readAllBytes());
        }
        
    }

    @Test
    @Order(3)
    public void whenLoadAsResource__fileSuccessfullyLoaded__shouldReturnSavedFile() {
        // given
        var fileUUID = FILE_UUID;

        // when
        var result = target.loadAsResource(SHARED_TEMP_DIR.toString(), fileUUID);
        
        // then
        assertTrue(result.exists());
    }

    @Test
    @Order(4)
    public void whenDelete__fileSuccessfullyRemoved__shouldRemoveSavedFile() throws IOException {
        // given
        var fileUUID = FILE_UUID;

        // when
        target.delete(SHARED_TEMP_DIR.toString(), fileUUID);
        
        // then
        assertFalse(SHARED_TEMP_DIR.resolve(fileUUID.toString()).toFile().exists());
    }
}
