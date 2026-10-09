package com.epam.resourceservice.service;

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

    public Metadata extract(byte[] audioData) {
        Metadata metadata = new Metadata();
        try (InputStream inputStream = new ByteArrayInputStream(audioData)) {
            ContentHandler handler = new DefaultHandler();
            ParseContext parseContext = new ParseContext();
            Mp3Parser parser = new Mp3Parser();

            parser.parse(inputStream, handler, metadata, parseContext);
        } catch (Exception e) {
            System.err.println("Failed to extract MP3 metadata: " + e.getMessage());
        }
        return metadata;
    }
}
