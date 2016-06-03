package ai;

import java.net.URL;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;

/**
 * FXML Controller class
 *
 * @author Abdelaziz Khabthani
 */
public class MainWindowController implements Initializable {

    ///////////////////////////////////////////////////////////////////////////////
    private final Paint green = new Color(0.2, 0.7, 0.2, 1);
    private final Paint red = new Color(0.7, 0.2, 0.2, 1);
    private final Paint orange = new Color(0.9, 0.5, 0.1, 1);
    ///////////////////////////////////////////////////////////////////////////////
    private String but;
    private ArrayList<String> faits;
    private ArrayList<HashMap<String, ArrayList<String>>> regles;
    ///////////////////////////////////////////////////////////////////////////////
    @FXML
    private AnchorPane root;
    @FXML
    private Group groupBut;
    @FXML
    private TextField butSaisie;
    @FXML
    private Circle circleBut;
    @FXML
    private Text msgBut;
    @FXML
    private TextArea logText;
    @FXML
    private Group groupeRegles;
    @FXML
    private Circle circleRegle;
    @FXML
    private Text msgRegle;
    @FXML
    private Group groupFaits;
    @FXML
    private Circle circleFait;
    @FXML
    private Text msgFait;
    @FXML
    private Text summaryText;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init();
    }

    @FXML
    private void loadRegleAction(ActionEvent event) {
        try {
            String baseRegle = FileUtility.openDialogAndGetStringFromFile("Charger Base Des Régles", root.getScene().getWindow());

            if (baseRegle != null) {
                regles = TextParser.parseRegle(baseRegle);
                System.out.println(regles);
                logText.appendText("[Info] Information general (41), Base des régles chargé avec succée.\n");
                logText.appendText("-------------------------------------------------------------------------------------------\n");
                msgRegle.setText("Valid");
                msgRegle.setFill(green);
                circleRegle.setFill(green);
                groupeRegles.setDisable(true);
                groupFaits.setDisable(false);
                ///////////////////////////////////////////////////////////////////////////////
                String[] lines = baseRegle.split("\\R");
                int i = 1;
                StringBuilder sb = new StringBuilder();
                sb.append("-Base des régles: \n");
                for (String line : lines) {
                    sb.append("\tR").append(i).append(":\tSI ").append(line).append(".\n");
                    i++;
                }
                summaryText.setText(summaryText.getText() + sb.toString());
                ///////////////////////////////////////////////////////////////////////////////
            }
        } catch (SyntaxException | ParseException e) {
            logText.appendText("[Erreure]: " + e.getMessage() + "\n");
            logText.appendText("-------------------------------------------------------------------------------------------\n");
            msgRegle.setText("Erreure");
            msgRegle.setFill(red);
            circleRegle.setFill(red);
        } catch (IOException ex) {
            logText.appendText("[Erreure]: Erreure systéme (31), Une erreure au niveau du chargement du fichier.\n");
            logText.appendText("-------------------------------------------------------------------------------------------\n");
            msgRegle.setText("Erreure");
            msgRegle.setFill(red);
            circleRegle.setFill(red);
        }
    }

    @FXML
    private void loadFaitsAction(ActionEvent event) {
        try {
            String baseFait = FileUtility.openDialogAndGetStringFromFile("Charger Base Des Faits", root.getScene().getWindow());
            if (baseFait != null) {
                faits = TextParser.parseFait(baseFait);
                logText.appendText("[Info] Information general (42), Base des faits chargé avec succée.\n");
                logText.appendText("-------------------------------------------------------------------------------------------\n");
                msgFait.setText("Valid");
                msgFait.setFill(green);
                circleFait.setFill(green);
                groupFaits.setDisable(true);
                groupBut.setDisable(false);
                ///////////////////////////////////////////////////////////////////////////////
                StringBuilder sb = new StringBuilder();
                sb.append("-Base des Faits: \n");
                sb.append("\tBF:\t{");
                for (String fait : faits) {
                    sb.append(fait).append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                sb.append("}.\n");
                summaryText.setText(summaryText.getText() + sb.toString());
                ///////////////////////////////////////////////////////////////////////////////
            }
        } catch (SyntaxException | ParseException e) {
            logText.appendText("[Erreure]: " + e.getMessage() + "\n");
            logText.appendText("-------------------------------------------------------------------------------------------\n");
            msgFait.setText("Erreure");
            msgFait.setFill(red);
            circleFait.setFill(red);
        } catch (IOException ex) {
            logText.appendText("[Erreure]: Erreure systeme (31), Une erreure au niveau du chargement du fichier.\n");
            logText.appendText("-------------------------------------------------------------------------------------------\n");
            msgFait.setText("Erreure");
            msgFait.setFill(red);
            circleFait.setFill(red);
        }

    }

    @FXML
    private void loadButAction(ActionEvent event) {
        try {
            String butString = butSaisie.getText();
            but = TextParser.parseBut(butString, regles);
            logText.appendText("[Info] Information general (43), le but existe une seule fois dans la parite droit du base des regles.\n");
            logText.appendText("-------------------------------------------------------------------------------------------\n");
            logText.appendText("[Info] Information general (44), Le but est chargé avec succée.\n");
            logText.appendText("-------------------------------------------------------------------------------------------\n");
            msgBut.setText("Valid");
            msgBut.setFill(green);
            circleBut.setFill(green);
            groupBut.setDisable(true);
            summaryText.setText(summaryText.getText() + "-But:\n\t{" + but + "}\n");
            String tmp = ((TextParser.chainageAvant(faits, but, regles)) ? "valid!" : "non valid!");
            summaryText.setText(summaryText.getText() + "-Resultat du chainange est:\n\t" + tmp);
            logText.appendText("[Info]Information general (45), La resultat du chainage avant est " + tmp + "\n");
            logText.appendText("-------------------------------------------------------------------------------------------\n");
            //TODO
        } catch (ParseException | SyntaxException e) {
            logText.appendText("[Erreure]: " + e.getMessage() + "\n");
            logText.appendText("-------------------------------------------------------------------------------------------\n");
            msgBut.setText("Erreure");
            msgBut.setFill(red);
            circleBut.setFill(red);
        }

    }

    @FXML
    private void reset(ActionEvent event) {
        init();
    }

    private void init() {
        groupBut.setDisable(true);
        groupFaits.setDisable(true);
        groupeRegles.setDisable(false);

        circleBut.setFill(orange);
        msgBut.setFill(orange);
        msgBut.setText("En Attante");

        circleFait.setFill(orange);
        msgFait.setFill(orange);
        msgFait.setText("En Attante");

        circleRegle.setFill(orange);
        msgRegle.setFill(orange);
        msgRegle.setText("En Attante");

        logText.clear();
        summaryText.setText("");

        butSaisie.clear();

        faits = null;
        regles = null;
        but = null;
    }
}
