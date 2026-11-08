package com.rescatta.backend.common.storage;

import com.rescatta.backend.common.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImageValidatorTest {

    @Test
    void acceptsJpegByMagicBytesEvenWithGenericContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "photos", "foto.bin", "application/octet-stream", jpegBytes());

        ImageValidator.validate(file);

        assertEquals(".jpg", ImageValidator.extensionFor(file));
        assertEquals("image/jpeg", ImageValidator.contentTypeFor(file));
    }

    @Test
    void rejectsHtmlDisguisedAsJpeg() {
        MockMultipartFile file = new MockMultipartFile(
                "photos",
                "foto.html",
                "image/jpeg",
                "<html><script>alert(1)</script></html>".getBytes(StandardCharsets.UTF_8)
        );

        assertThrows(BadRequestException.class, () -> ImageValidator.validate(file));
    }

    @Test
    void rejectsSvgEvenIfItSaysImage() {
        MockMultipartFile file = new MockMultipartFile(
                "photos",
                "foto.svg",
                "image/svg+xml",
                "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>".getBytes(StandardCharsets.UTF_8)
        );

        assertThrows(BadRequestException.class, () -> ImageValidator.validate(file));
    }

    private byte[] jpegBytes() {
        byte[] bytes = new byte[32];
        bytes[0] = (byte) 0xFF;
        bytes[1] = (byte) 0xD8;
        bytes[2] = (byte) 0xFF;
        bytes[3] = (byte) 0xE0;
        return bytes;
    }
}
