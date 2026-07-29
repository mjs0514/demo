package com.tmax;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Locale;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class VersionCommand {
    public static void printVersion(
            String[] args,
            Class<?> sourceClass
    ) {
        try {
            Manifest manifest = loadManifest(sourceClass);
            if (manifest == null) {
                System.err.println("Manifest not found.");
                System.exit(1);
                return;
            }

            manifest.write(System.out);
        } catch (IOException | URISyntaxException exception) {
            System.err.println("Failed to read manifest: " + exception.getMessage());
            System.exit(1);
        }
    }

    public static boolean isVersionRequest(String[] args) {
        if (args == null || args.length != 1) {
            return false;
        }

        String command = args[0].toLowerCase(Locale.ROOT);
        return "-version".equals(command) || "-v".equals(command);
    }

    private static Manifest loadManifest(Class<?> sourceClass) throws IOException, URISyntaxException {
        Manifest manifest = loadManifestFromCodeSource(sourceClass);
        if (manifest != null) {
            return manifest;
        }

        try (InputStream inputStream = sourceClass.getResourceAsStream("/META-INF/MANIFEST.MF")) {
            return inputStream == null ? null : new Manifest(inputStream);
        }
    }

    private static Manifest loadManifestFromCodeSource(Class<?> sourceClass) throws IOException, URISyntaxException {
        CodeSource codeSource = sourceClass.getProtectionDomain().getCodeSource();
        if (codeSource == null || codeSource.getLocation() == null) {
            return null;
        }

        URL location = codeSource.getLocation();
        if ("file".equals(location.getProtocol())) {
            return loadManifestFromPath(Paths.get(location.toURI()));
        }

        Path jarPath = findJarPath(location.toExternalForm());
        return jarPath == null ? null : loadManifestFromPath(jarPath);
    }

    private static Manifest loadManifestFromPath(Path sourcePath) throws IOException {
        if (Files.isRegularFile(sourcePath)) {
            try (JarFile jarFile = new JarFile(sourcePath.toFile())) {
                return jarFile.getManifest();
            }
        }

        Path classesManifest = sourcePath.resolve("META-INF/MANIFEST.MF");
        if (Files.isRegularFile(classesManifest)) {
            try (InputStream inputStream = Files.newInputStream(classesManifest)) {
                return new Manifest(inputStream);
            }
        }

        return null;
    }

    private static Path findJarPath(String location) throws URISyntaxException {
        String lowerLocation = location.toLowerCase(Locale.ROOT);
        int jarIndex = lowerLocation.indexOf(".jar");
        if (jarIndex < 0) {
            return null;
        }

        String candidate = location.substring(0, jarIndex + 4);
        candidate = stripPrefix(candidate, "jar:");
        candidate = stripPrefix(candidate, "nested:");

        if (candidate.startsWith("file:")) {
            return Paths.get(new URI(candidate));
        }

        String decodedCandidate = URLDecoder.decode(candidate, StandardCharsets.UTF_8);
        if (decodedCandidate.matches("^/[A-Za-z]:/.*")) {
            decodedCandidate = decodedCandidate.substring(1);
        }

        return Paths.get(decodedCandidate);
    }

    private static String stripPrefix(String value, String prefix) {
        return value.startsWith(prefix) ? value.substring(prefix.length()) : value;
    }

    private VersionCommand() {}
}
