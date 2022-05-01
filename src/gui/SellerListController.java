package gui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service; // 1º Declaração de dependência

	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tableColumnName;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;

	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Seller> obsList; // 3º SERVE PARA CARREGAR OS DEPARTAMENTOS

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	public void setSellerService(SellerService service) { // 2º INJEÇÃO DE DEPENDÊNCIA
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// CÓDIGO ABAIXO SERVE PARA DEIXAR A TABELA USANDO O ESPAÇO COMPLETO DA JANELA.
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() { // 4° MÉTODO PARA ACESSAR O SERVIÇO, CARREGAR OS DEPARTAMENTOS E COLOCAR NA
									// LISTA
		if (service == null) {
			throw new IllegalStateException("O service estava nulo!");
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list); // 5º CARREGA A LISTA DENTRO DO OBSLIST.
		tableViewSeller.setItems(obsList); // 6º SERVE PARA CARREGAR OS ITENS NA TABELA E MOSTRAR NA TELA
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		//try {
		//FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
		//Pane pane = loader.load();

		//SellerFormController controller = loader.getController();
		//controller.serSeller(obj);
		//controller.setSellerService(new SellerService());
		//controller.subscribleDataChangeListener(this);
		//controller.updateFormData();

		//Stage dialogStage = new Stage(); // SERVE PARA CONFIGURAR E CARREGAR A JANELA DO FORMULÁRIO
		//dialogStage.setTitle("Digite os dados do departamento!");
		//dialogStage.setScene(new Scene(pane));
		//dialogStage.setResizable(false);
		//dialogStage.initOwner(parentStage);
		//dialogStage.initModality(Modality.WINDOW_MODAL);
		//dialogStage.showAndWait();
		//} catch (IOException e) {
		//Alerts.showAlert("IO Execption", "Erro ao carregar Janela", e.getMessage(), AlertType.ERROR);
		//}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Tem certeza que quer deletar?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("O service está nulo!");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch(DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover objeto!", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
