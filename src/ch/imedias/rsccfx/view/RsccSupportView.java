package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Defines all elements shown in the support section.
 */
public class RsccSupportView extends BorderPane {
  private final Rscc model;

  HeaderView headerView;

  //FIXME: Those fields are never used. Consider using it in the future. Thx.

  Label enterTokenLbl = new Label();
  Label keyDescriptionLbl = new Label();
  Label exampleLbl = new Label();
  Label instructionLbl = new Label();

  VBox centerBox = new VBox();
  VBox groupingBox = new VBox();
  HBox tokenValidationBox = new HBox();

  TextField tokenTxt = new TextField();

  TitledPane keyInputPane = new TitledPane();
  TitledPane predefinedAdressesPane = new TitledPane();

  ImageView isValidImg = new ImageView();

  Button connectBtn = new Button();
  Button expandOptionBtn = new Button();


  /**
   * Initializes all the GUI components needed to enter the token the supporter received.
   */
  public RsccSupportView(Rscc model) {
    this.model = model;
    initFieldData();
    layoutForm();
    bindFieldsToModel();
  }

  private void initFieldData() {
    // populate fields which require initial data
    headerView = new HeaderView(model);

    // TODO: Move initialization to matching class...
    enterTokenLbl.textProperty().set("EnterToken");
    keyDescriptionLbl.textProperty().set("Test");
    exampleLbl.textProperty().set("Number of characters: 8\nexample: 666xx666");
    instructionLbl.textProperty().set("Instructions");

    isValidImg = new ImageView(getClass()
        .getClassLoader()
        .getResource("dialog-error.png")
        .toExternalForm());                     // TODO: Check what to do here.

    connectBtn.textProperty().set("Connect");
    expandOptionBtn.textProperty().set("More");

    // TODO: Implement String Class
    keyInputPane.setText("Key Input");
    keyInputPane.setExpanded(true);

    predefinedAdressesPane.setText("Predefined Adresses");
    predefinedAdressesPane.setExpanded(false);
  }

  private void layoutForm() {
    // TODO: import CSS accordingly. Ask SA where it needs to be defined.
    // this.setPadding(new Insets(5, 25, 5, 25)); // TODO: set paddings for "center"
    // this.setId("SupporterView");

    //enterTokenLbl.setFont(new Font(25));
    enterTokenLbl.setId("EnterTokenLbl");

    keyDescriptionLbl.setWrapText(true);

    tokenTxt.setFont(new Font(30)); // TODO: Move to CSS

    isValidImg.setSmooth(true);

    tokenValidationBox.getChildren().addAll(tokenTxt, isValidImg);
    tokenValidationBox.setSpacing(5);       // TODO: Move to CSS.
    tokenValidationBox.setHgrow(tokenTxt, Priority.ALWAYS);
    tokenValidationBox.setAlignment(Pos.CENTER_LEFT);

    groupingBox.getChildren().addAll(tokenValidationBox, instructionLbl);

    centerBox.getChildren().addAll(enterTokenLbl,
        keyDescriptionLbl,
        exampleLbl,
        groupingBox,
        connectBtn,
        expandOptionBtn);

    keyInputPane.setContent(centerBox);
    // TODO: Set content for predefinedAdressesPane

    connectBtn.setFont(new Font(30));       // TODO: Move to CSS.
    setCenter(keyInputPane);
    setTop(headerView);
    setBottom(predefinedAdressesPane);
  }


  private void bindFieldsToModel() {
    // make bindings to the model

  }

}