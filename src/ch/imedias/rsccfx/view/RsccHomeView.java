package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

/**
 * HomeScreen of the remotesupport.
 */
public class RsccHomeView extends BorderPane {
  private final Rscc model;
  private final RsccHomeViewPresenter presenter;
  Button requestSupportBtn;
  Button offerSupportBtn;
  private ImageView offerSupportImgVw;
  private ImageView requestSupportImgVw;

  /**
   * Constructor.
   * @param model is the model
   */
  public RsccHomeView(Rscc model) {
    this.model = model;
    SvgImageLoaderFactory.install();

    initFieldData();
    this.presenter = new RsccHomeViewPresenter(model, this);
    layoutForm();
    bindFieldsToModel();
  }

  private void initFieldData() {
    requestSupportBtn = new Button();
    requestSupportBtn.textProperty().setValue("I need help");
    //TODO: replace Text, multilangual
    Image requestSupportImg = new Image(this.getClass().getClassLoader()
        .getResourceAsStream("images/network-wireless-no-route-symbolic.svg"));
    requestSupportImgVw = new ImageView(requestSupportImg);
    requestSupportImgVw.setPreserveRatio(true);
    requestSupportBtn.setGraphic(requestSupportImgVw);
    requestSupportBtn.setId("HomeNavigationBtn");

    offerSupportBtn = new Button();
    offerSupportBtn.textProperty().setValue("I want to help someone");
    // TODO: replace Text, multilangual
    Image offerSupportImg = new Image(this.getClass().getClassLoader()
        .getResourceAsStream("images/audio-headset-symbolic.svg"));
    offerSupportImgVw = new ImageView(offerSupportImg);
    offerSupportImgVw.setPreserveRatio(true);
    offerSupportBtn.setGraphic(offerSupportImgVw);
    offerSupportBtn.setId("HomeNavigationBtn");
  }

  private void bindFieldsToModel() {
    //dynamic growth
  }

  private void layoutForm() {
    this.setLeft(requestSupportBtn);
    this.setRight(offerSupportBtn);
  }

  /**
   * @param scene initially loaded scene by RsccApp.
   */
  public void initBtnPanel(Scene scene) {
    offerSupportImgVw.fitWidthProperty().bind(scene.widthProperty().divide(4));
    offerSupportImgVw.fitHeightProperty().bind(scene.widthProperty().divide(2));
    requestSupportImgVw.fitWidthProperty().bind(scene.widthProperty().divide(4));
    offerSupportBtn.prefWidthProperty().bind(scene.widthProperty().divide(2));
    offerSupportBtn.prefHeightProperty().bind(scene.heightProperty());
    requestSupportBtn.prefWidthProperty().bind(scene.widthProperty().divide(2));
    requestSupportBtn.prefHeightProperty().bind(scene.heightProperty());
  }
}
