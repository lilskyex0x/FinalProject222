
import service.UniversitySystem;
import ui.ConsoleUI;

public class Main {
  public static void main(String[] args) {
    UniversitySystem system = UniversitySystem.createWithSampleData();
    ConsoleUI ui = new ConsoleUI(system);
    ui.run();
  }
}
