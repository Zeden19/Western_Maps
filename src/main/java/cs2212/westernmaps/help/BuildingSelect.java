package cs2212.westernmaps.help;

import cs2212.westernmaps.Main;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

/*
 * TODO: - Improve format of selectionPane via text alignment, font format etc.
 *       - Make button sizes consistent.
 *       - Implements button listeners.
 *       - Add a Panel for displaying the current weather.
 *       - Add Javadoc comments.
 */

public class BuildingSelect extends JFrame implements ItemListener {
    JPanel cards;
    final String[] BUILDING_LIST = {"(none)", "Middlesex College", "Talbot College", "Recreation Centre"};

    public BuildingSelect() {
        super(Main.APPLICATION_NAME + ": Building Selection");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cards = new JPanel(new CardLayout());
        JPanel[] cardList = new JPanel[BUILDING_LIST.length];
        for (int i = 0; i < BUILDING_LIST.length; i++) {
            cardList[i] = new JPanel();
            if (i > 0) {
                cardList[i].add(createCardButton(BUILDING_LIST[i]));
            } else {
                cardList[i].add(createCardLabel());
            }
            cards.add(cardList[i], BUILDING_LIST[i]);
        }

        JPanel listPanel = new JPanel();
        JComboBox buildingList = new JComboBox(BUILDING_LIST);
        buildingList.setEditable(false);
        buildingList.addItemListener(this);
        listPanel.add(buildingList);

        JLabel heading = new JLabel("Select a building:");
        heading.setFont(heading.getFont().deriveFont(36.0f));
        heading.setAlignmentX(CENTER_ALIGNMENT);

        JPanel selectionPane = new JPanel();
        selectionPane.setLayout(new BorderLayout());
        selectionPane.add(heading, BorderLayout.PAGE_START);
        selectionPane.add(listPanel, BorderLayout.CENTER);
        selectionPane.add(cards, BorderLayout.PAGE_END);

        JPanel imagePane = new JPanel();
        imagePane.add(new JLabel("pretend this is a picture of the building"), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectionPane, imagePane);
        splitPane.setDividerLocation(640);
        selectionPane.setMinimumSize(new Dimension(640, 720));
        imagePane.setMinimumSize(new Dimension(640, 720));

        setContentPane(splitPane);
        setPreferredSize(new Dimension(1280, 720));
        pack();
    }

    public void itemStateChanged(ItemEvent event) {
        CardLayout layout = (CardLayout) cards.getLayout();
        layout.show(cards, (String) event.getItem());
    }

    // Helper Methods
    private JLabel createCardLabel() {
        JLabel label = new JLabel("Once you select a building, click here to visit its maps.");
        label.setFont(label.getFont().deriveFont(16.0f));
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        // label.setBackground(Color.LIGHT_GRAY);

        return label;
    }

    private JButton createCardButton(String building) {
        JButton button = new JButton("Go to " + building);
        button.setFont(button.getFont().deriveFont(24.0f));

        return button;
    }
}
