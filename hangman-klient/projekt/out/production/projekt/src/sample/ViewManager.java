package sample;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import view.GUIController;
import view.LoginController;
import view.MenuController;
import view.GameController;

import java.util.ArrayList;

public class ViewManager {
    public static LoginController logInController;
    public static MenuController menuController;
    public static GUIController guiController;
    public static GameController gameController;
    //public static ArrayList<ChatController> chatControllers = new ArrayList<>();
    public static Parent loggInRoot;
    public static Parent menuRoot;
    public static BorderPane guiRoot;


}
