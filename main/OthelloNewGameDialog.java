//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class OthelloNewGameDialog extends JDialog implements ActionListener, ItemListener {
    private JRadioButton blackHumanButton;
    private JRadioButton blackCPU1Button;
    private JRadioButton blackCPU2Button;
    private JRadioButton whiteHumanButton;
    private JRadioButton whiteCPU1Button;
    private JRadioButton whiteCPU2Button;
    private JCheckBox animateCheckbox;
    private JButton okButton;
    private JButton cancelButton;
    private boolean okPressed;
    private boolean blackIsHuman;
    private boolean whiteIsHuman;

    public boolean blackIsCPU1() {
        return blackIsCPU1;
    }

    public boolean blackIsCPU2() {
        return blackIsCPU2;
    }

    public boolean whiteIsCPU1() {
        return whiteIsCPU1;
    }

    public boolean whiteIsCPU2() {
        return whiteIsCPU2;
    }

    private boolean blackIsCPU1;
    private boolean blackIsCPU2;
    private boolean whiteIsCPU1;
    private boolean whiteIsCPU2;
    private boolean animateTiles;

    public OthelloNewGameDialog(Frame frame, boolean bl, boolean bl2, boolean bl3) {
        super(frame, "Start New Game", true);
        this.setDefaultCloseOperation(2);
        this.okPressed = false;
        this.blackIsHuman = bl;
        this.whiteIsHuman = bl2;
        this.animateTiles = bl3;
        this.buildUI();
        this.setSize(300, 200);
    }

    public boolean okPressed() {
        return this.okPressed;
    }

    public boolean blackIsHuman() {
        return this.blackIsHuman;
    }

    public boolean whiteIsHuman() {
        return this.whiteIsHuman;
    }

    public boolean animateTiles() {
        return this.animateTiles;
    }

    private void buildUI() {
        Container container = this.getContentPane();
        GridBagLayout gridBagLayout = new GridBagLayout();
        container.setLayout(gridBagLayout);
        JLabel jLabel = new JLabel("Black: ");
        container.add(jLabel);
        gridBagLayout.setConstraints(jLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 13, 0, new Insets(5, 5, 5, 5), 0, 0));
        this.blackHumanButton = new JRadioButton("Human", this.blackIsHuman);
        this.blackHumanButton.addActionListener(this);
        container.add(this.blackHumanButton);
        gridBagLayout.setConstraints(this.blackHumanButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));

        this.blackCPU1Button = new JRadioButton("CPU1", !this.blackIsHuman);
        this.blackCPU1Button.addActionListener(this);
        container.add(this.blackCPU1Button);
        gridBagLayout.setConstraints(this.blackCPU1Button, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));

        this.blackCPU2Button = new JRadioButton("CPU2", !this.blackIsHuman);
        this.blackCPU2Button.addActionListener(this);
        container.add(this.blackCPU2Button);
        gridBagLayout.setConstraints(this.blackCPU2Button, new GridBagConstraints(3, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));

        JLabel jLabel2 = new JLabel("White: ");
        container.add(jLabel2);
        gridBagLayout.setConstraints(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 13, 0, new Insets(5, 5, 5, 5), 0, 0));
        this.whiteHumanButton = new JRadioButton("Human", this.whiteIsHuman);
        this.whiteHumanButton.addActionListener(this);
        container.add(this.whiteHumanButton);
        gridBagLayout.setConstraints(this.whiteHumanButton, new GridBagConstraints(1, 1, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));

        this.whiteCPU1Button = new JRadioButton("CPU1", !this.whiteIsHuman);
        this.whiteCPU1Button.addActionListener(this);
        container.add(this.whiteCPU1Button);
        gridBagLayout.setConstraints(this.whiteCPU1Button, new GridBagConstraints(2, 1, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));

        this.whiteCPU2Button = new JRadioButton("CPU2", !this.whiteIsHuman);
        this.whiteCPU2Button.addActionListener(this);
        container.add(this.whiteCPU2Button);
        gridBagLayout.setConstraints(this.whiteCPU2Button, new GridBagConstraints(3, 1, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));

        this.animateCheckbox = new JCheckBox("Animate tile-flipping", this.animateTiles);
        this.animateCheckbox.addItemListener(this);
        container.add(this.animateCheckbox);
        gridBagLayout.setConstraints(this.animateCheckbox, new GridBagConstraints(0, 2, 3, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));
        this.okButton = new JButton("OK");
        this.okButton.addActionListener(this);
        container.add(this.okButton);
        gridBagLayout.setConstraints(this.okButton, new GridBagConstraints(1, 3, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));
        this.cancelButton = new JButton("Cancel");
        this.cancelButton.addActionListener(this);
        container.add(this.cancelButton);
        gridBagLayout.setConstraints(this.cancelButton, new GridBagConstraints(2, 3, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(5, 5, 5, 5), 0, 0));
    }

    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        if (object == this.okButton) {
            this.okPressed = true;
            this.processWindowEvent(new WindowEvent(this, 201));
        } else if (object == this.cancelButton) {
            this.okPressed = false;
            this.processWindowEvent(new WindowEvent(this, 201));
        } else if (object == this.blackHumanButton) {
            this.blackCPU1Button.setSelected(false);
            this.blackCPU2Button.setSelected(false);
            this.blackIsHuman = true;
            this.blackIsCPU1 = false;
            this.blackIsCPU2 = false;
        } else if (object == this.blackCPU1Button) {
            this.blackHumanButton.setSelected(false);
            this.blackCPU2Button.setSelected(false);
            this.blackIsHuman = false;
            this.blackIsCPU1 = true;
            this.blackIsCPU2 = false;
        } else if (object == this.blackCPU2Button) {
            this.blackHumanButton.setSelected(false);
            this.blackCPU1Button.setSelected(false);
            this.blackIsHuman = false;
            this.blackIsCPU1 = false;
            this.blackIsCPU2 = true;
        } else if (object == this.whiteHumanButton) {
            this.whiteCPU1Button.setSelected(false);
            this.whiteCPU2Button.setSelected(false);
            this.whiteIsHuman = true;
            this.whiteIsCPU1 = false;
            this.whiteIsCPU2 = false;
        } else if (object == this.whiteCPU1Button) {
            this.whiteHumanButton.setSelected(false);
            this.whiteCPU2Button.setSelected(false);
            this.whiteIsCPU1 = true;
            this.whiteIsCPU2 = false;
            this.whiteIsHuman = false;
        } else if (object == this.whiteCPU2Button) {
            this.whiteHumanButton.setSelected(false);
            this.whiteCPU1Button.setSelected(false);
            this.whiteIsCPU1 = false;
            this.whiteIsCPU2 = true;
            this.whiteIsHuman = false;
        }

    }

    public void itemStateChanged(ItemEvent itemEvent) {
        Object object = itemEvent.getSource();
        if (object == this.animateCheckbox) {
            this.animateTiles = itemEvent.getStateChange() == 1;
        }

    }
}
