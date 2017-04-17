package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.ControlledPresenter;
import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.ViewController;
import ch.imedias.rsccfx.model.Rscc;
import javafx.scene.Scene;

/**
 * Defines the behaviour of interactions
 * and initializes the size of the GUI components.
 */
public class RsccRequestPresenter implements ControlledPresenter {
  private static final double WIDTH_SUBTRACTION_GENERAL = 50d;
  private static final double WIDTH_SUBTRACTION_KEYFIELD = 80d;

  private final Rscc model;
  private final RsccRequestView view;
  private final HeaderPresenter headerPresenter;
  private ViewController viewParent;

  /**
   * Initializes a new RsccRequestPresenter with the matching view.
   */
  public RsccRequestPresenter(Rscc model, RsccRequestView view) {
    this.model = model;
    this.view = view;
    headerPresenter = new HeaderPresenter(model, view.headerView);
    attachEvents();
    initHeader();
  }

  /**
   * Defines the ViewController to allow changing views.
   */
  public void setViewParent(ViewController viewParent) {
    this.viewParent = viewParent;
  }

  private void attachEvents() {
    view.reloadKeyBtn.setOnAction(
        event -> {
          model.refreshKey();
        }
    );

    // Closes the other TitledPane so that just one TitledPane is shown on the screen.
    view.keyGeneratorPane.setOnMouseClicked(event -> view.supporterAdminPane.setExpanded(false));
    view.supporterAdminPane.setOnMouseClicked(event -> view.keyGeneratorPane.setExpanded(false));
  }

  /**
   * Initializes the size of the whole RsccRequestView elements.
   *
   * @param scene must be initialized and displayed before calling this method;
   *              The size of all header elements are based on it.
   * @throws NullPointerException if called before this object is fully initialized.
   */
  public void initSize(Scene scene) {
    // initialize header
    headerPresenter.initSize(scene);

    // initialize view
    view.generatedKeyFld.prefWidthProperty().bind(scene.widthProperty()
        .subtract(WIDTH_SUBTRACTION_KEYFIELD));
    view.descriptionTxt.wrappingWidthProperty().bind(scene.widthProperty()
        .subtract(WIDTH_SUBTRACTION_GENERAL));
    view.additionalDescriptionTxt.wrappingWidthProperty().bind(scene.widthProperty()
        .subtract(WIDTH_SUBTRACTION_GENERAL));
    view.keyGeneratingBox.prefWidthProperty().bind(scene.widthProperty());
  }

  /**
   * Initializes the functionality of the header, e.g. back button and settings button.
   */
  private void initHeader() {
    // Set all the actions regarding buttons in this method.
    headerPresenter.setBackBtnAction(event -> {
      model.killConnection();
      viewParent.setView(RsccApp.HOME_VIEW);
    });
  }
}
