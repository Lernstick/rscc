package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.model.Rscc;
import javafx.scene.Parent;

public class RsccView extends Parent { // change Parent to GridPane etc.
  private final Rscc model;

  //declare all elements here

  /** Javadoc comment here. */
  public RsccView(Rscc model) {
    this.model = model;

    layoutForm();
    initFieldData();
    bindFieldsToModel();
  }

  private void layoutForm() {
    //setup layout (aka setup specific pane etc.)
  }

  private void initFieldData() {
    //populate fields which require initial data
  }

  private void bindFieldsToModel() {
    //make the bindings to the model
  }


}


