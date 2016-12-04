package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class RsccEnterTokenView extends VBox {

  private final Rscc model;
  private final RsccEnterTokenPresenter presenter;

  //declare all elements here
  Label enterTokenlbl;
  Label loremIpsumlbl;
  Label examplelbl;
  Label instructionlbl;

  VBox groupingbox;
  HBox tokenValidationbox;
  TextField tokentxt;
  ImageView isValidimg;

  Button connectbtn;
  Button expandOptionbtn;

  /**
   * Javadoc comment here.
   */
  public RsccEnterTokenView(Rscc model) {
    this.model = model;

    initFieldData();
    layoutForm();
    bindFieldsToModel();
    this.presenter = new RsccEnterTokenPresenter(model, this);
  }

  private void layoutForm() {
    //setup layout (aka setup specific pane etc.)
    this.setPadding(new Insets(5, 25, 5, 25));
    this.setSpacing(10);

    enterTokenlbl.setFont(new Font(25));

    loremIpsumlbl.setWrapText(true);

    tokenValidationbox = new HBox();
    tokentxt = new TextField();
    tokentxt.setFont(new Font(30));

    isValidimg.setSmooth(true);
    tokenValidationbox.getChildren().addAll(tokentxt, isValidimg);
    tokenValidationbox.setSpacing(5);
    tokenValidationbox.setHgrow(tokentxt, Priority.ALWAYS);
    tokenValidationbox.setAlignment(Pos.CENTER_LEFT);

    groupingbox = new VBox();
    groupingbox.getChildren().addAll(tokenValidationbox, instructionlbl);

    this.getChildren().addAll(enterTokenlbl,
            loremIpsumlbl,
            examplelbl,
            groupingbox,
            connectbtn,
            expandOptionbtn);

    connectbtn.setFont(new Font(30));
  }

  private void initFieldData() {
    //populate fields which require initial data
    //enterTokenlbl = new Label(Strings.remoteSupportFrameEnterTokenLabelText);
    //loremIpsumlbl = new Label(Strings.remoteSupportFrameDescriptionLabelText);
    //examplelbl = new Label(Strings.remoteSupportFrameUsageExampleLabelText);
    //instructionlbl = new Label(Strings.remoteSupportFrameInstructionLabelText);
    //connectbtn = new Button(Strings.remoteSupportDialogConnectButtonText);
    //expandOptionbtn = new Button(Strings.remoteSupportDialogExpandButtonText);

    isValidimg = new ImageView(getClass()
            .getClassLoader()
            .getResource("dialog-error.png")
            .toExternalForm());
    enterTokenlbl = new Label("EnterToken");
    loremIpsumlbl = new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit");
    examplelbl = new Label("Number of characters: 8\nexample: 666xx666");
    instructionlbl = new Label("Instructions!");
    connectbtn = new Button("Beam me up scotty");
    expandOptionbtn = new Button("MOARRR");
  }

  private void bindFieldsToModel() {
    //make the bindings to the model
  }

}
