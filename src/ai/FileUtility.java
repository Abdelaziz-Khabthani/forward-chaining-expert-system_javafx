package ai;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 *
 * @author Abdelaziz Khabthani
 */
public class FileUtility {

    private FileUtility() {

    }

    public static String openDialogAndGetStringFromFile(String message, Window window) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(message);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            return fileToString(selectedFile.toPath(), StandardCharsets.UTF_8);
        }
        return null;
    }

    private static String fileToString(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }
}
