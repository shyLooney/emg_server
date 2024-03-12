package com.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

@Configuration
@Slf4j
public class TrayIconCreator {
    private static final String PATH = "tray_icon.png";

    @Bean
    @Conditional(TrayIconSupportChecker.class)
    public CommandLineRunner createAndShowGUI() {
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(createImage(PATH, "EMG Server"));
        final SystemTray tray = SystemTray.getSystemTray();
        trayIcon.setImageAutoSize(true);

        // Create a popup menu components
        MenuItem exitItem = new MenuItem("Выход");

        //Add components to popup menu
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return (a) -> {
                return;
            };
        }

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "This dialog box is run from System Tray");
            }
        });

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuItem item = (MenuItem) e.getSource();
                //TrayIcon.MessageType type = null;
                System.out.println(item.getLabel());
                if ("Error".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.ERROR;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is an error message", TrayIcon.MessageType.ERROR);

                } else if ("Warning".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.WARNING;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is a warning message", TrayIcon.MessageType.WARNING);

                } else if ("Info".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.INFO;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is an info message", TrayIcon.MessageType.INFO);

                } else if ("None".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.NONE;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is an ordinary message", TrayIcon.MessageType.NONE);
                }
            }
        };

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
        return (a) -> {
            return;
        };
    }

    //Obtain the image URL
    private Image createImage(String path, String description) {
        URL imageURL = TrayIconCreator.class.getClassLoader().getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    public static class TrayIconSupportChecker implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            if (!SystemTray.isSupported()) {
                log.warn("TrayIcon not supported");
                return false;
            }
            URL imageURL = TrayIconCreator.class.getClassLoader().getResource(PATH);

            if (imageURL == null) {
                log.warn("Resource not found: " + PATH);
                return false;
            }
            return true;
        }
    }
}

