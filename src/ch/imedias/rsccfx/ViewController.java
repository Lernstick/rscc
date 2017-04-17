package ch.imedias.rsccfx;

import java.util.HashMap;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Displays all of the views and handles the switching between views.
 * Because of the interface "ControlledPresenter", this object gets passed onto every presenter,
 * which gives them access to the methods in this class.
 */
public class ViewController extends StackPane {

  private HashMap<String, Node> views = new HashMap<>();
  private HashMap<String, ControlledPresenter> presenters = new HashMap<>();

  /**
   * Returns an already loaded presenter.
   *
   * @param name of the view / presenter.
   */
  public ControlledPresenter getPresenter(String name) {
    return presenters.get(name);
  }

  /**
   * Adds a view / presenter pair to the respective HashMaps.
   */
  private void addView(String name, Node view, ControlledPresenter presenter) {
    views.put(name, view);
    presenters.put(name, presenter);
  }

  /**
   * Loads a view / presenter pair and sets up a reference in the presenter to this object.
   * Usually only needs to be called once for every pair before the start of the app.
   */
  public boolean loadView(String name, Node view, ControlledPresenter presenter) {
    // properly initialize view and presenter and put into HashMap
    presenter.setViewParent(this);
    addView(name, view, presenter);
    return true;
  }

  /**
   * Sets the current view to the one referenced by 'name'.
   * This method can be called in the presenter to switch to a different view.
   * Controls the way views are being transitioned from one to another.
   */
  public boolean setView(final String name) {
    if (views.get(name) != null) { // view is loaded
      // If at least one view is already being displayed
      if (!getChildren().isEmpty()) {
        //remove displayed view
        getChildren().remove(0);
        //add new view
        getChildren().add(0, views.get(name));
      } else {
        // No view is currently being displayed, so just add it
        getChildren().add(views.get(name));
      }
      return true;
    } else {
      System.out.println("View " + name + " hasn't been loaded!\n");
      return false;
    }
  }

  /**
   * Unloads a view / presenter pair from the HashMaps.
   * This method can be used in case a view / presenter pair needs to be reloaded.
   */
  public boolean unloadView(String name) {
    if (views.remove(name) == null | presenters.remove(name) == null) {
      System.out.println("View " + name + " doesn't exist");
      return false;
    } else {
      return true;
    }
  }

}
