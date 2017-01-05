package ch.imedias.rsccfx;

import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.SystemCommander;
import ch.imedias.rsccfx.view.RsccHomeView;
import ch.imedias.rsccfx.view.RsccPresenter;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class RsccApp extends Application {
  public static final String APP_NAME = "Remote Support - Enter Token";

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {

    Rscc model = new Rscc(new SystemCommander());
    RsccHomeView view = new RsccHomeView(model);

    // Must set the scene before creating the presenter that uses
    // the scene to listen for the focus change
    Scene scene = new Scene(view);
    String stSheet = getClass().getClassLoader().getResource("css/HomeStyle.css").toExternalForm();
    scene.getStylesheets().add(stSheet);
    RsccPresenter presenter = new RsccPresenter(model, view);

    stage.setScene(scene);
    view.initBtnPanel(scene);

    // Get Screensize
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

    //set Stage boundaries to visible bounds of the main screen
    stage.setWidth(primaryScreenBounds.getWidth() / 1.8);
    stage.setHeight(primaryScreenBounds.getHeight() / 1.5);
    stage.setX(primaryScreenBounds.getWidth() / 2 - stage.getWidth() / 2);
    stage.setY(primaryScreenBounds.getHeight() / 2 - stage.getHeight() / 2);

    stage.setTitle(APP_NAME);
    stage.show();
  }
}
