package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;
import javafx.scene.image.Image;

/**
 * This is the presenter for the EnterTokenView in which the supporter will enter the token.
 */
public class RsccEnterTokenPresenter {

  private final Rscc model;
  private final RsccEnterTokenView view;

  /**
   * Constructor.
   */
  public RsccEnterTokenPresenter(Rscc model, RsccEnterTokenView view) {
    this.model = model;
    this.view = view;

    attachEvents();
  }
  
  /**
   * Updates the validation image after every key pressed.
   */
  private void attachEvents() {

    view.tokentxt.setOnKeyPressed(event -> {
      view.isValidimg.setImage(new Image(validationImage(view.tokentxt.getText())));
    });

  }

  /**
   * Validates the token and displays a symbolic image.
   */
  public String validationImage(String token) {

    if (validateToken(token)) {
      return getClass().getClassLoader().getResource("emblem-default.png").toExternalForm();
    }
    return getClass().getClassLoader().getResource("dialog-error.png").toExternalForm();
  }

  private static boolean validateToken(String token) {
    return (int) (Math.random() * 2) == 1;
    //TODO Validate token
  }
}
