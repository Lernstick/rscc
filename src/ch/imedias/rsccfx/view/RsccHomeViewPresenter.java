package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

class RsccHomeViewPresenter {
  private final Rscc model;
  private final RsccHomeView view;

  /**
   * Javadoc comment here.
   */
  public RsccHomeViewPresenter(Rscc model, RsccHomeView view) {
    this.model = model;
    this.view = view;
    attachEvents();
  }

  private void attachEvents() {
    view.requestSupportBtn
        .setOnAction(event -> loadSceenOnButtonAction(new ShowTokenView(model)));
    view.offerSupportBtn
        .setOnAction(event -> loadSceenOnButtonAction(new RsccEnterTokenView(model)));
  }

  private void loadSceenOnButtonAction(Parent nextScreen) {
    Stage stage = (Stage) view.getScene().getWindow();
    stage.setScene(new Scene(nextScreen));
  }
}
