package ch.imedias.rsccfx.view;

/** import statements. */
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Creates HeaderView.
 */
public class HeaderView extends VBox {

  VBox headerbox;
  HBox boxLine1;
  HBox boxLine2;
  Label lblHead;
  Button btnHead;
  Button btnSett;
  Button btnHelp;
  Separator sep;

  /** class HeaderView. */
  public HeaderView() {
    //this.headerbox = headerbox;
    layoutForm();
    initFieldData();
    bindFieldsToModel();
  }

  private void layoutForm() {
    //setup layout (aka setup specific pane etc.)

    headerbox = new VBox();
    boxLine1 = new HBox();
    boxLine2 = new HBox();

    boxLine1.setPadding(new Insets(10, 10, 10, 10));
    boxLine2.setPadding(new Insets(10, 10, 10, 10));


    sep = new Separator();
    boxLine2.getChildren().add(sep);


  }

  private void initFieldData() {
    //populate fields which require initial data

    btnHead = new Button();
    btnHead.setAlignment(Pos.TOP_LEFT);
    btnHead.setGraphic(new ImageView(new Image(getClass()
        .getClassLoader().getResource("images/back1.png").toExternalForm())));
    btnHelp = new Button();
    btnHelp.setGraphic(new ImageView(new Image(getClass()
        .getClassLoader().getResource("images/help1.png").toExternalForm())));
    btnSett = new Button();
    btnSett.setGraphic(new ImageView(new Image(getClass()
        .getClassLoader().getResource("images/setting1.png").toExternalForm())));
    btnSett.setAlignment(Pos.TOP_RIGHT);
    lblHead = new Label("I need Help");
    lblHead.setFont(new Font("Cantarell",20));
    lblHead.setAlignment(Pos.CENTER);
    boxLine1.getChildren().add(btnHead);
    boxLine1.getChildren().add(lblHead);
    boxLine1.getChildren().add(btnHelp);
    boxLine1.getChildren().add(btnSett);
  }

  /** initSize method. */
  public void initSize(Scene scene) {
    sep.prefWidthProperty().bind(scene.widthProperty());
    lblHead.prefWidthProperty().bind(scene.widthProperty());
    boxLine1.prefWidthProperty().bind(scene.widthProperty());
    boxLine2.prefWidthProperty().bind(scene.widthProperty());
    headerbox.prefWidthProperty().bind(scene.widthProperty());


  }

  private void bindFieldsToModel() {
    //make the bindings to the model

    headerbox.getChildren().add(boxLine1);
    headerbox.getChildren().add(boxLine2);

    this.getChildren().add(headerbox);
  }


}



