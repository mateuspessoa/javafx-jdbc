package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
	
	private DepartmentService service; // 1º Declaração de dependência
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList; // 3º SERVE PARA CARREGAR OS DEPARTAMENTOS
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}
	
	public void setDepartmentService(DepartmentService service) { // 2º INJEÇÃO DE DEPENDÊNCIA
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		//CÓDIGO ABAIXO SERVE PARA DEIXAR A TABELA USANDO O ESPAÇO COMPLETO DA JANELA.
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());	
	}
	
	public void updateTableView() { // 4° MÉTODO PARA ACESSAR O SERVIÇO, CARREGAR OS DEPARTAMENTOS E COLOCAR NA LISTA
		if (service == null) {
			throw new IllegalStateException("O service estava nulo!");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list); // 5º CARREGA A LISTA DENTRO DO OBSLIST.
		tableViewDepartment.setItems(obsList); // 6º SERVE PARA CARREGAR OS ITENS NA TABELA E MOSTRAR NA TELA
	}
}
