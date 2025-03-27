package lifeplanner.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import lifeplanner.exception.user.CloudinaryUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CloudinaryServiceUTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setup() {
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void uploadFile_successfulUpload_returnsSecureUrl() throws Exception {
        byte[] fileBytes = "dummy content".getBytes();
        when(multipartFile.getBytes()).thenReturn(fileBytes);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://res.cloudinary.com/sample/image/upload/v1234567/sample.jpg");
        when(uploader.upload(eq(fileBytes), any(Map.class))).thenReturn(uploadResult);

        String secureUrl = cloudinaryService.uploadFile(multipartFile);

        assertEquals("https://res.cloudinary.com/sample/image/upload/v1234567/sample.jpg", secureUrl);
        verify(uploader).upload(eq(fileBytes), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void uploadFile_whenIOException_thenThrowCloudinaryUploadException() throws Exception {
        when(multipartFile.getBytes()).thenThrow(new IOException("I/O error"));

        CloudinaryUploadException exception = assertThrows(CloudinaryUploadException.class,
                () -> cloudinaryService.uploadFile(multipartFile));
        assertTrue(exception.getMessage().contains("Failed to upload file to Cloudinary"));
    }
}