package org.monarchinitiative.fenominal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.stage.FileChooser;
import org.monarchinitiative.fenominal.corenlp.MappedSentencePart;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class FenominalMainController {
    @FXML
    public TextArea parseArea;
    @FXML
    public Button parseButton;
    @FXML
    public Button pasteClipboard;
    @FXML public Button importTextFile;
    @FXML public Button importHpObo;

    private EntityMapper mapper = null;


    public FenominalMainController() {
    }



    @FXML
    private void parseButtonPressed(ActionEvent e) {
        String contents = parseArea.getText();
        if (this.mapper == null) {
            System.err.println("[ERROR] hp.obo not initialized");
            return;
        }
        List<MappedSentencePart> mappedSentenceParts = mapper.mapText(contents);
        StringBuilder sb = new StringBuilder();
        for (var mp : mappedSentenceParts) {
            sb.append(mp).append("\n\n");
        }
        this.parseArea.setText(sb.toString());

        e.consume();
    }
    @FXML
    private void pasteClipboard(ActionEvent e) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            this.parseArea.setText(clipboard.getString());
        }
        e.consume();
    }

    @FXML
    private void importHpObo(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open hp.obo");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            this.mapper = new EntityMapper(file.getAbsolutePath());
        }
    }
}
