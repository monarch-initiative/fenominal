package org.monarchinitiative.fenominal.gui.questionnaire.qtable;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.monarchinitiative.fenominal.gui.guitools.PopUps;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Main table of this widget. Each row contains a question for a questionnaire.
 * @author Peter N Robinson
 */
public class PhenoqTable extends TableView<Qphenorow> {
    Logger LOGGER = LoggerFactory.getLogger(PhenoqTable.class);
    private final ObservableList<Qphenorow> phenolist = FXCollections.observableArrayList();

    /**
     * .table-row-cell refers to the row height
     */
    public static final String CSS_STYLE = """
            .table-view .corner {
                -fx-background-color: transparent;
            }
            .table-row-cell {
                -fx-cell-size: 50px;
            }
            """;



    public PhenoqTable(List<Qphenorow> items) {
        this.setEditable(true);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setStyle(CSS_STYLE);
        TableColumn<Qphenorow, String> titleCol = titleColumn();
        TableColumn<Qphenorow, TermSelectionButton> segmentedCol = segmentedButtonColumn();
        TableColumn<Qphenorow, String> questionCol = questionColumn();
        TableColumn<Qphenorow, Button> explanationColumn = explanationColumn();
        getColumns().add(titleCol);
        getColumns().add(segmentedCol);
        getColumns().add(questionCol);
        getColumns().add(explanationColumn);
        phenolist.addAll(items);
        getItems().addAll(phenolist);
    }


    private TableColumn<Qphenorow, String> titleColumn() {
        TableColumn<Qphenorow, String> titleCol = new TableColumn<>();
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Qphenorow, String> call(TableColumn<Qphenorow, String> p) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setTooltip(null);
                            setText(null);
                        } else {
                            Tooltip tooltip = new Tooltip();
                            if (getTableRow() == null || getTableRow().getIndex() < 0) {
                                return;
                            }
                            int i = getTableRow().getIndex();
                            Qphenorow myModel = getTableView().getItems().get(i);
                            Term term = myModel.getHpoTerm();
                            String msg = String.format("Question for %s (%s)", term.getName(), term.id().getValue());
                            tooltip.setText(msg);
                            setTooltip(tooltip);
                            Text text = new Text(item);
                            text.setStyle("-fx-text-alignment:justify;");
                            text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(25));
                            setGraphic(text);
                        }
                    }
                };
            }
        });
        titleCol.setEditable(false);
        titleCol.setSortable(false);
        titleCol.setMinWidth(200);
        return titleCol;
    }

    private TableColumn<Qphenorow, TermSelectionButton> segmentedButtonColumn() {
        TableColumn<Qphenorow, TermSelectionButton> segmentedButtonCol = new TableColumn<>();
        segmentedButtonCol.setCellValueFactory(cdf -> {
            Qphenorow pr = cdf.getValue();
            TermSelectionButton button = pr.termSelectionButton;
            for (var b : button.getButtons()) {
                b.setOnAction((e) -> pr.updateAnswer());
            }
            // wrap it so it can be displayed in the TableView
            return new ReadOnlyObjectWrapper<>(button);
        });
        segmentedButtonCol.setEditable(false);
        segmentedButtonCol.setSortable(false);
        segmentedButtonCol.setMinWidth(150);
        return segmentedButtonCol;
    }


    private TableColumn<Qphenorow, String> questionColumn() {
        TableColumn<Qphenorow, String> titleCol = new TableColumn<>();
        titleCol.setCellValueFactory(new PropertyValueFactory<>("question"));
        titleCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Qphenorow, String> call(TableColumn<Qphenorow, String> p) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setTooltip(null);
                            setText(null);
                        } else {
                            Tooltip tooltip = new Tooltip();
                            if (getTableRow() == null || getTableRow().getIndex() < 0) {
                                return;
                            }
                            Text text = new Text(item);
                            text.setStyle("-fx-text-alignment:justify;");
                            text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                            setGraphic(text);
                        }
                    }
                };
            }
        });
        titleCol.setEditable(false);
        titleCol.setSortable(false);
        titleCol.setMinWidth(200);
        return titleCol;
    }

    private TableColumn<Qphenorow, Button> explanationColumn() {
        TableColumn<Qphenorow, Button> explCol = new TableColumn<>();
        explCol.setCellValueFactory(cdf -> {
            Qphenorow pr = cdf.getValue();
            String explanation = pr.getExplanation();
            Button button = new Button("Explanation");
            button.setOnAction((e) -> {
                PopUps.showInfoMessage(pr.getExplanation(),pr.getQuestion());
                e.consume();
            });

            return new ReadOnlyObjectWrapper<>(button);

        });
        explCol.setEditable(false);
        explCol.setSortable(false);
        explCol.setMinWidth(300);
        return explCol;
    }



}
