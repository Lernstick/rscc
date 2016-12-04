package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;

/**
 * This is the old GUI of Swing in JavaFX.
 */
public class RsccOldPresenter {
  private final Rscc model;
  private final RsccView view;

  /**
   * Javadoc comment here.
   */
  public RsccOldPresenter(Rscc model, RsccView view) {
    this.model = model;
    this.view = view;
    attachEvents();
  }

  private void attachEvents() {
    //TODO put all setOnAction/addListeners in here
  }
}
