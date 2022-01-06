package org.monarchinitiative.fenominal.gui.questionnaire.qtable;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.monarchinitiative.fenominal.gui.questionnaire.phenoitem.AgeRule;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

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
        TableColumn<Qphenorow, AgeBox> ageThresholdCol = ageSliderColumn();
        getColumns().add(titleCol);
        getColumns().add(segmentedCol);
        getColumns().add(questionCol);
        getColumns().add(ageThresholdCol);
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
                            String msg = String.format("Question for %s (%s)", term.getName(), term.getId().getValue());
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
                b.setOnAction((e) ->{
                    pr.updateAnswer();
                });
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
                            int i = getTableRow().getIndex();
                            Qphenorow myModel = getTableView().getItems().get(i);
                            Term term = myModel.getHpoTerm();
                            Optional<AgeRule> opt = myModel.ageRuleOpt();
                            if (opt.isPresent()) {
                                AgeRule ageRule = opt.get();
                                String msg = String.format("Threshold: %s", ageRule);
                                tooltip.setText(msg);
                                setTooltip(tooltip);
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

    private TableColumn<Qphenorow, AgeBox> ageSliderColumn() {
        TableColumn<Qphenorow, AgeBox> ageBoxCol = new TableColumn<>();
        ageBoxCol.setCellValueFactory(cdf -> {
            Qphenorow pr = cdf.getValue();
            if (pr.ageRuleOpt().isPresent()) {
                AgeBox ageBox = new AgeBox();
                pr.yearsProperty().bind(ageBox.yearsPropertyProperty());
                pr.monthsProperty().bind(ageBox.monthsPropertyProperty());
                pr.daysProperty().bind(ageBox.daysPropertyProperty());
                // wrap it so it can be displayed in the TableView
                return new ReadOnlyObjectWrapper<>(ageBox);
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        ageBoxCol.setEditable(false);
        ageBoxCol.setSortable(false);
        ageBoxCol.setMinWidth(300);
        return ageBoxCol;
    }



}
