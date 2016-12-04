package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;
import javafx.scene.image.Image;

public class RsccEnterTokenPresenter {

  private final Rscc model;
  private final RsccEnterTokenView view;

  /**
   * Javadoc comment here.
   */
  public RsccEnterTokenPresenter(Rscc model, RsccEnterTokenView view) {
    this.model = model;
    this.view = view;

    attachEvents();
  }

  private void attachEvents() {
    //TODO put all setOnAction/addListeners in here
    view.tokentxt.setOnKeyPressed(event -> {
      view.isValidimg.setImage(new Image(validationImage(view.tokentxt.getText())));
    });

  }

  /**
   * Javadoc comment here.
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
