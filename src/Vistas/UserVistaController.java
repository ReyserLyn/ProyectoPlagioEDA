package Vistas;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import models.Resultado;
import models.Trie;
import trying.Controlador;
import trying.ResultChecker;

public class UserVistaController implements Initializable {

    private Controlador controler;
    private File article;
    SpinnerValueFactory<Integer> svfLevel;

    @FXML
    private Button searchFile;
    @FXML
    private Button calcularPlagio;
    @FXML
    private AnchorPane grid2;
    @FXML
    private AnchorPane grid3;
    @FXML
    private Spinner<Integer> minWrods;
    @FXML
    private CheckBox cbplagioparcial;
    @FXML
    private CheckBox cbplagiototal;
    @FXML
    private Button limpiarArchivo;
    @FXML
    private ListView<String> listaDB;
    @FXML
    private Label percentage;
    @FXML
    private CheckBox noplagio;
    @FXML
    private TableView<Resultado> tabla;
    @FXML
    private TableColumn<Resultado, String> title;
    @FXML
    private TableColumn<Resultado, Double> porcentaje;
    @FXML
    private Tab resEspecificos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        svfLevel = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 20, 4);
        minWrods.setValueFactory(svfLevel);
        controler = new Controlador();

        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        porcentaje.setCellValueFactory(new PropertyValueFactory<>("percent"));
    }

    @FXML
    private void searchFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        //fc.setInitialDirectory(new File("/home/danten/Documentos/txts"));//esta linea solo funciona en mi pc :p
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));

        File selectFile = fc.showOpenDialog(null);

        if (selectFile != null) {
            putButtons(false, selectFile.getName());
            article = selectFile;
        }
    }

    private void putButtons(boolean bol, String txtbutn) {
        this.calcularPlagio.setDisable(bol);
        this.limpiarArchivo.setDisable(bol);
        this.searchFile.setDisable(!bol);
        this.searchFile.setText(txtbutn);
    }

    @FXML
    private void calcularPlagio(ActionEvent event) {
        if (this.controler.getContenedor().getTries().size() != 0) {
            double percen = this.controler.comparacion(article, minWrods.getValue());
            percen = Math.round(percen * 100) / 100.0;

            showGrids(true);
            if (percen == 100.0) {
                this.cbplagiototal.setSelected(true);
            } else {
                if (percen == 0.0) {
                    this.noplagio.setSelected(true);
                } else {
                    this.cbplagioparcial.setSelected(true);
                }
            }
            this.percentage.setText(percen + " %");
            setGrafico(percen);

            tabla.setItems(this.controler.getContenedor().getArticulo().getListaResultados());
        } else {
            ResultChecker.btnInf("[!] Base de Datos VACÍA", "No hay archivos en la Base de Datos para realizar la comparación");
        }
    }

    public void setGrafico(double copyed) {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Plagio", copyed),
                new PieChart.Data("No plagio", 100 - copyed)
        );

        PieChart piechart = new PieChart(pieData);

        this.grid3.getChildren().add(piechart);
    }

    private void showGrids(boolean bol) {
        this.grid2.setVisible(bol);
        this.grid3.setVisible(bol);
        resEspecificos.setDisable(!bol);
    }

    @FXML
    private void addDB(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();

        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        List<File> selectedFiles = fc.showOpenMultipleDialog(null);

        if (selectedFiles != null) {
            for (File selectedFile : selectedFiles) {
                Trie aux = this.controler.obtener(selectedFile.getName());
                if (aux == null) {
                    if (controler.addDBArticle(selectedFile)) {
                        listaDB.getItems().add(selectedFile.getName());
                    } else {
                        System.out.println("Error al subir los archivos");
                    }
                } else {
                    boolean replace = ResultChecker.btnConf("Archivo ya existente", "El archivo " + selectedFile.getName() + " ya existe, deseas reemplazarlo?");
                    if (replace) {
                        controler.getContenedor().deleteThis(aux);
                        controler.addDBArticle(selectedFile);
                        ResultChecker.btnInf("Exitoso", "El archivo se ha reemplazado");
                    }
                }
            }
        } else {
            System.out.println("Busqueda cancelada");
        }
    }

    @FXML
    private void limpiarArchivo(ActionEvent event) {
        this.grid3.getChildren().clear();
        showGrids(false);
        putButtons(true, "Search File");
        this.article = null;
        this.cbplagioparcial.setSelected(false);
        this.cbplagiototal.setSelected(false);
        this.noplagio.setSelected(false);
    }

}
