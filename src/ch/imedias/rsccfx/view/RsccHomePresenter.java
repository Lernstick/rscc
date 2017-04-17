package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.ControlledPresenter;
import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.ViewController;
import ch.imedias.rsccfx.model.Rscc;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

// TODO: Check mockup for reference here:
// https://www.cs.technik.fhnw.ch/confluence16/display/VTDESGB/Mockups+-+Remote+Support+-+Version+0.8?preview=/15991708/15991716/Startscreen.png

/**
 * Defines the behaviour of interactions
 * and initializes the size of the GUI components.
 */
public class RsccHomePresenter implements ControlledPresenter {
  private static final Double IMG_VIEW_DIVISOR = 3d;
  private static final Double VIEW_BTN_HEIGHT_DIVISOR = 2.5d;
  private static final Double VIEW_BTN_WIDTH_DIVISOR = 1.5d;

  private final Rscc model;
  private final RsccHomeView view;
  private final HeaderPresenter headerPresenter;
  private ViewController viewParent;

  /**
   * Initializes a new RsccHomePresenter with the matching view.
   */
  public RsccHomePresenter(Rscc model, RsccHomeView view) {
    this.model = model;
    this.view = view;
    headerPresenter = new HeaderPresenter(model, view.headerView);
    attachEvents();
    initHeader();
  }

  /**
   * Defines the ViewController to allow changing of views.
   */
  public void setViewParent(ViewController viewParent) {
    this.viewParent = viewParent;
  }

  /**
   * Initializes the size of the RsccHomeView.
   */
  public void initSize(Scene scene) {
    headerPresenter.initSize(scene);

    view.requestImgView.fitHeightProperty().bind(scene.heightProperty()
        .subtract(view.headerView.heightProperty()).divide(IMG_VIEW_DIVISOR));
    view.supportImgView.fitHeightProperty().bind(scene.heightProperty()
        .subtract(view.headerView.heightProperty()).divide(IMG_VIEW_DIVISOR));

    view.supportViewBtn.prefWidthProperty().bind(scene.widthProperty()
        .divide(VIEW_BTN_WIDTH_DIVISOR));
    view.supportViewBtn.prefHeightProperty().bind(scene.heightProperty()
        .subtract(view.headerView.heightProperty()).divide(VIEW_BTN_HEIGHT_DIVISOR));

    view.requestViewBtn.prefWidthProperty().bind(scene.widthProperty()
        .divide(VIEW_BTN_WIDTH_DIVISOR));
    view.requestViewBtn.prefHeightProperty().bind(scene.heightProperty()
        .subtract(view.headerView.heightProperty()).divide(VIEW_BTN_HEIGHT_DIVISOR));

    view.contentBox.setAlignment(Pos.CENTER);
    view.contentBox.setPadding(new Insets(25));
    view.contentBox.setSpacing(20);
  }

  private void attachEvents() {
    view.supportViewBtn.setOnAction(event -> viewParent.setView(RsccApp.SUPPORT_VIEW));
    view.requestViewBtn.setOnAction(event -> {
      model.requestTokenFromServer();
      viewParent.setView(RsccApp.REQUEST_VIEW);
    });
  }

  private void initHeader() {
    // set all the actions regarding buttons in this method
    headerPresenter.setBackBtnVisibility(false);
  }
}
