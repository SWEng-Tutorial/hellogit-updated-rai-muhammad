package il.cshaifasweng.OCSFMediatorExample.client;


import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.HashMap;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PrimaryController {

	@FXML
	private TextField submitterID1;

	@FXML
	private TextField submitterID2;

	@FXML
	private TextField timeTF;

	@FXML
	private TextField MessageTF;

	@FXML
	private Button SendBtn;

	@FXML
	private TextField DataFromServerTF;

	private int msgId;
	private ObservableList<Student> students;
	private ListView<Student> studentListView;
	private Label resultLabel;
	private void showGrade(Student student) {
		int grade = student.getGrade();
		resultLabel.setText(student.getName() + "'s grade is: " + grade);
	}
	@FXML
	void sendMessage(ActionEvent event) {
		try {
			Message message = new Message(msgId++, MessageTF.getText());
			MessageTF.clear();
			SimpleClient.getClient().sendToServer(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PrimaryController() {

			students = createSampleStudentData();

			studentListView = new ListView<>(students);
			studentListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue != null) {
					showGrade(newValue);
				}
			});

			resultLabel = new Label();

			VBox vbox = new VBox(10, studentListView, resultLabel);
			// Add the VBox to the main scene or another container in your existing layout
		}
	private ObservableList<Student> createSampleStudentData() {
		ObservableList<Student> students = FXCollections.observableArrayList();
		students.add(new Student("John Doe", 85));
		students.add(new Student("Jane Smith", 92));
		students.add(new Student("Michael Johnson", 78));
		students.add(new Student("Emily Williams", 95));
		students.add(new Student("Robert Brown", 88));
		students.add(new Student("Olivia Davis", 91));
		students.add(new Student("William Miller", 84));
		students.add(new Student("Sophia Wilson", 79));
		students.add(new Student("James Taylor", 87));
		students.add(new Student("Elizabeth Anderson", 90));
		return students;
	}
	@Subscribe
	public void setDataFromServerTF(MessageEvent event) {
		DataFromServerTF.setText(event.getMessage().getMessage());
	}
	private HashMap<String, Integer> studentGrades;


	@Subscribe
	public void setSubmittersTF(UpdateMessageEvent event) {
		submitterID1.setText(event.getMessage().getData().substring(0,9));
		submitterID2.setText(event.getMessage().getData().substring(11,20));
	}

	@Subscribe
	public void getStarterData(NewSubscriberEvent event) {
		try {
			Message message = new Message(msgId, "send Submitters IDs");
			SimpleClient.getClient().sendToServer(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Subscribe
	public void errorEvent(ErrorEvent event){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR,
					String.format("Message:\nId: %d\nData: %s\nTimestamp: %s\n",
							event.getMessage().getId(),
							event.getMessage().getMessage(),
							event.getMessage().getTimeStamp().format(dtf))
			);
			alert.setTitle("Error!");
			alert.setHeaderText("Error:");
			alert.show();
		});
	}

	@FXML
	void initialize() {
		EventBus.getDefault().register(this);
		MessageTF.clear();
		DataFromServerTF.clear();
		msgId=0;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
			LocalTime currentTime = LocalTime.now();
			timeTF.setText(currentTime.format(dtf));
		}),
				new KeyFrame(Duration.seconds(1))
		);
		clock.setCycleCount(Animation.INDEFINITE);
		clock.play();
		try {
			Message message = new Message(msgId, "add client");
			SimpleClient.getClient().sendToServer(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}