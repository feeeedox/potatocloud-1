package net.potatocloud.node.console;

import lombok.RequiredArgsConstructor;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.CommandManager;
import net.potatocloud.node.screen.Screen;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.setup.Setup;
import org.jline.jansi.Ansi;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

@RequiredArgsConstructor
public class ConsoleReader extends Thread {

    private final Console console;
    private final CommandManager commandManager;

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {

                final Node node = Node.getInstance();

                // Wait until the node is ready
                if (!node.ready()) {
                    continue;
                }

                final String input = console.getLineReader().readLine(console.getPrompt());

                final ScreenManager screenManager = node.screenManager();
                final Screen currentScreen = screenManager.getCurrentScreen();
                final boolean isNodeScreen = currentScreen.name().equals(Screen.NODE_SCREEN);

                if (isNodeScreen && input.isBlank()) {
                    // remove blank inputs
                    console.println(Ansi.ansi().cursorUpLine().eraseLine().cursorUp(1).toString());
                    continue;
                }

                if (isNodeScreen) {
                    // add executed commands into log file
                    node.logger().logCommand(input);

                    commandManager.executeCommand(input);
                    continue;
                }

                // the user is in a setup currently
                if (currentScreen.name().contains("setup")) {
                    final Setup currentSetup = node.setupManager().getCurrentSetup();
                    if (currentSetup != null) {
                        currentSetup.handleInput(input);
                    }
                    continue;
                }

                if (input.strip().equalsIgnoreCase("leave") || input.strip().equalsIgnoreCase("exit")) {
                    Node.getInstance().screenManager().switchTo(Screen.NODE_SCREEN);
                    continue;
                }

                node.serviceManager().find(currentScreen.name()).ifPresent(service -> {
                    node.serviceManager().execute(service, input);
                });

            }
        } catch (UserInterruptException e) {
            Node.getInstance().shutdown();
        } catch (EndOfFileException e) {
            console.clearScreen();
            console.updateScreen();
        }
    }
}