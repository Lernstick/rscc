package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.ControlledPresenter;
import ch.imedias.rsccfx.ViewController;
import ch.imedias.rsccfx.model.Rscc;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.image.Image;

/**
 * Presenter class of RsccSupportView. Defines the behaviour of interactions
 * and initializes the size of the GUI components.
 * The supporter can enter the key given from the help requester to establish a connection.
 */
public class RsccSupportPresenter implements ControlledPresenter {
  private static final double WIDTH_SUBTRACTION_ENTERTOKEN = 80d;
  private static final String VALID_IMAGE_FILENAME = "emblem-default.png";
  private static final String INVALID_IMAGE_FILENAME = "dialog-error.png";

  private final Rscc model;
  private final RsccSupportView view;
  private final HeaderPresenter headerPresenter;
  private ViewController viewParent;

  /**
   * Initializes a new RsccSupportPresenter with the according view.
   */
  public RsccSupportPresenter(Rscc model, RsccSupportView view) {
    this.model = model;
    this.view = view;
    headerPresenter = new HeaderPresenter(model, view.headerView);
    attachEvents();
    initHeader();
    initBindings();
  }

  /**
   * Defines the ViewController to allow changing of views.
   */
  public void setViewParent(ViewController viewParent) {
    this.viewParent = viewParent;
  }

  /**
   * Initializes the size of the whole RsccSupportView elements.
   *
   * @param scene must be initialized and displayed before calling this method;
   *              The size of all header elements are based on it.
   * @throws NullPointerException if called before this object is fully initialized.
   */
  public void initSize(Scene scene) {
    // initialize header
    headerPresenter.initSize(scene);

    // initialize view
    view.enterTokenLbl.prefWidthProperty().bind(scene.widthProperty()
        .subtract(WIDTH_SUBTRACTION_ENTERTOKEN));
  }

  /**
   * Sets the token validation image depending on the validity of the token.
   */
  public void setValidationImage(boolean isValid) {
    String imageFileName = isValid ? VALID_IMAGE_FILENAME : INVALID_IMAGE_FILENAME;
    view.isValidImg.setImage(
        new Image(getClass().getClassLoader().getResource(imageFileName).toExternalForm())
    );
  }

  /**
   * Updates the validation image after every key pressed.
   */
  private void attachEvents() {
    view.connectBtn.disableProperty().addListener(
        (observable, oldValue, newValue) -> {
          setValidationImage(newValue);
        }
    );

    view.connectBtn.setOnAction(event -> {
      model.setKey(view.tokenFld.getText());
      model.connectToUser();
    });

    // Closes the other TitledPane so that just one TitledPane is shown on the screen.
    view.keyInputPane.setOnMouseClicked(event -> view.predefinedAdressesPane.setExpanded(false));
    view.predefinedAdressesPane.setOnMouseClicked(event -> view.keyInputPane.setExpanded(false));
  }

  private void initBindings() {
    // disable connect button if key is not valid
    view.connectBtn.disableProperty().bind(
        Bindings.when(
            view.tokenFld.textProperty()
                .isNotEqualTo("") // disable if key is null
                .and(
                    // disable if key is not 9 digit number
                    Bindings.createBooleanBinding(
                        () -> view.tokenFld.getText().matches("\\d{9}"),
                        view.tokenFld.textProperty()
                    )
                )
        )
            .then(false)             // set disableProperty to false, if key is valid
            .otherwise(true)    // set disableProperty to true, if key is invalid
    );
  }

  /**
   * Initializes the functionality of the header, e.g. back and settings button.
   */
  private void initHeader() {
    // Set all the actions regarding buttons in this method.
    headerPresenter.setBackBtnAction(event -> viewParent.setView("home"));
    // TODO: Set actions on buttons (Help, Settings)
  }

}
