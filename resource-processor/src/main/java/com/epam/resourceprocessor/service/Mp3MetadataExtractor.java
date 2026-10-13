package com.epam.resourceprocessor.service;

import com.epam.resourceprocessor.exception.InvalidAudioFormatException;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public class Mp3MetadataExtractor {

    private final Tika tika = new Tika();

    public Metadata extract(byte[] audioData) {
        if (audioData == null || audioData.length == 0) {
            throw new InvalidAudioFormatException("The request body is invalid MP3: body is empty");
        }

        String detectedType = tika.detect(audioData);
        if (!"audio/mpeg".equalsIgnoreCase(detectedType)) {
            throw new InvalidAudioFormatException("The request body is invalid MP3");
        }

        Metadata metadata = new Metadata();
        try (InputStream inputStream = new ByteArrayInputStream(audioData)) {
            ContentHandler handler = new DefaultHandler();
            ParseContext parseContext = new ParseContext();
            Mp3Parser parser = new Mp3Parser();

            parser.parse(inputStream, handler, metadata, parseContext);
        } catch (Exception e) {
            throw new InvalidAudioFormatException("The request body is invalid MP3");
        }

        if (metadata.get("xmpDM:audioCompressor") == null && metadata.get("version") == null && metadata.get("channels") == null) {
            throw new InvalidAudioFormatException("The request body is invalid MP3");
        }

        return metadata;
    }
}
