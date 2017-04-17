package ch.imedias.rsccfx;

import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.SystemCommander;
import ch.imedias.rsccfx.view.RsccHomePresenter;
import ch.imedias.rsccfx.view.RsccHomeView;
import ch.imedias.rsccfx.view.RsccRequestPresenter;
import ch.imedias.rsccfx.view.RsccRequestView;
import ch.imedias.rsccfx.view.RsccSupportPresenter;
import ch.imedias.rsccfx.view.RsccSupportView;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Starts the Rscc Application.
 */
public class RsccApp extends Application {
  public static final String APP_NAME = "Remote Support";

  /**
   * Declares views for use with ViewController.
   */
  public static final String HOME_VIEW = "home";
  public static final String REQUEST_VIEW = "requestHelp";
  public static final String SUPPORT_VIEW = "supporter";

  private static Rscc model;

  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
    model = new Rscc(new SystemCommander());
    ViewController mainView = new ViewController();

    Group root = new Group();
    root.getChildren().addAll(mainView);
    final Scene scene = new Scene(root);

    // Initialize the views and load them into ViewController
    // HomeView
    Node view = new RsccHomeView(model);
    ControlledPresenter presenter = new RsccHomePresenter(model, (RsccHomeView) view);
    mainView.loadView(RsccApp.HOME_VIEW, view, presenter);

    // RequestHelpView
    view = new RsccRequestView(model);
    presenter = new RsccRequestPresenter(model, (RsccRequestView) view);
    mainView.loadView(RsccApp.REQUEST_VIEW, view, presenter);

    // SupporterView
    view = new RsccSupportView(model);
    presenter = new RsccSupportPresenter(model, (RsccSupportView) view);
    mainView.loadView(RsccApp.SUPPORT_VIEW, view, presenter);

    // Set initial screen
    mainView.setView(RsccApp.HOME_VIEW);

    // Get Screensize
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

    //set Stage boundaries to visible bounds of the main screen
    stage.setWidth(primaryScreenBounds.getWidth() / 1.8);
    stage.setHeight(primaryScreenBounds.getHeight() / 1.5);
    stage.setX(primaryScreenBounds.getWidth() / 2 - stage.getWidth() / 2);
    stage.setY(primaryScreenBounds.getHeight() / 2 - stage.getHeight() / 2);

    stage.setMinWidth((primaryScreenBounds.getWidth() / 1.8) / 1.2);
    stage.setMinHeight((primaryScreenBounds.getHeight() / 1.5) / 1.3);

    stage.setScene(scene);
    stage.setTitle(APP_NAME);
    stage.show();

    // Initialize sizing of views
    ((RsccHomePresenter) mainView.getPresenter(HOME_VIEW)).initSize(scene);
    ((RsccRequestPresenter) mainView.getPresenter(REQUEST_VIEW)).initSize(scene);
    ((RsccSupportPresenter) mainView.getPresenter(SUPPORT_VIEW)).initSize(scene);

    // Initialize stylesheets
    String supporterSheet = getClass().getClassLoader()
        .getResource("css/supporterStyle.css").toExternalForm();
    String headerSheet = getClass().getClassLoader()
        .getResource("css/headerStyle.css").toExternalForm();
    String homeSheet = getClass().getClassLoader()
        .getResource("css/HomeStyle.css").toExternalForm();
    String requestSheet = getClass().getClassLoader()
        .getResource("css/requestStyle.css").toExternalForm();
    scene.getStylesheets().addAll(supporterSheet, headerSheet, homeSheet, requestSheet);
  }

  @Override
  public void stop() throws Exception {
    String key = model.getKey();
    if (key != null) {
      model.killConnection();
    }
    super.stop();
  }
}
