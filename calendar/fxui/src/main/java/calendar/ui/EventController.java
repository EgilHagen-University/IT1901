package calendar.ui;

import calendar.core.Event;
import calendar.core.User;
import calendar.json.UserPersistence;
import java.io.IOException;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EventController {

    @FXML
    private TextField title;
    @FXML
    private DatePicker date;
    @FXML
    private TextArea description;
    @FXML
    private Button addEvent;
    @FXML
    private ChoiceBox<String> hours, minutes;
    @FXML
    private Label errorMessage;

    private Event currentEvent = null;

    private User user;

    private static final String[] HOURS = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" };
    private static final String[] MINUTES = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
            "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" };

    @FXML
    void initialize() {
        this.hours.getItems().setAll(HOURS);
        this.minutes.getItems().setAll(MINUTES);

        if (this.currentEvent != null) {
            this.title.setText(this.currentEvent.getHeader());
            this.description.setText(this.currentEvent.getDescription());

            if (this.currentEvent.getTimeHour() == 0) {
                this.hours.setValue("00");
            } else
                this.hours.setValue(String.valueOf(this.currentEvent.getTimeHour()));

            if (this.currentEvent.getTimeMinute() == 0) {
                this.minutes.setValue("00");
            } else
                this.minutes.setValue(String.valueOf(this.currentEvent.getTimeMinute()));

            this.date.setValue(this.currentEvent.getDate());
        } else {
            this.date.setValue(LocalDate.now());
        }
    }

    @FXML
    private void onAddEvent() throws IOException {
        try {
            if (this.title.getText().isEmpty()) {
                errorMessage.setText("The event must have a title.");
                return;
            }
            if (this.hours.getSelectionModel().isEmpty() || this.minutes.getSelectionModel().isEmpty()) {
                errorMessage.setText("Set a time for the event.");
                return;
            }
            String title = this.title.getText();
            LocalDate date = this.date.getValue();
            String description = " ";
            if (!this.description.getText().isEmpty()) {
                description = this.description.getText();
            }
            StringBuilder time = new StringBuilder();
            time.append(this.hours.getSelectionModel().getSelectedItem() + ":"
                    + this.minutes.getSelectionModel().getSelectedItem());
            Event event = new Event(title, description, date, time.toString());
            this.user.getCalendar().getEvents().remove(this.currentEvent);
            this.user.getCalendar().addEvent(event);

            UserPersistence userPersistence = new UserPersistence();
            userPersistence.setSaveFile(user.getUsername() + ".json");
            userPersistence.saveUser(this.user);

            switchScene();
        } catch (IOException e) {
            errorMessage.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void cancelEvent() throws IOException {
        try {
            switchScene();
        } catch (IOException e) {
            errorMessage.setText("Error: " + e.getMessage());
        }

    }

    @FXML
    private void switchScene() throws IOException {
        Stage stage = (Stage) addEvent.getScene().getWindow();
        stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/calendar/ui/Calendar.fxml"));
        CalendarController controller = new CalendarController();
        controller.setUser(this.user);
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEvent(Event event) {
        this.currentEvent = event;
    }
}
