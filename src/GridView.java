import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;

public class GridView extends JFrame {


    // field for delimiter
    private JTextField delimiterFiled;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(GridView::new);
    }


    private GridView() {


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignore) {
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(800, 550));
        putWindowInCenter(this);
        setVisible(true);


        FileParser parser=null;
        try {
            parser = new FileParser(new FileReader("emp.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        DefaultTableModel model = new DefaultTableModel() {
            String[] cNames = {"Employee ID #1", "Employee ID #2", "Project ID", "Days worked"};

            @Override
            public int getColumnCount() {
                return cNames.length;
            }

            @Override
            public String getColumnName(int index) {
                return cNames[index];
            }
        };

        if (parser !=null ){

            for (List<Vector<Long>> v : parser.getData().values()) {
                for (Vector<Long> longs : v) {
                    model.addRow(longs);
                }
            }
        }

        JTable table = new JTable(model) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setFillsViewportHeight(true);
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(143, 171, 221));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        int fontSize = table.getTableHeader().getFont().getSize();
        String fontName = table.getTableHeader().getName();
        Font newBoldFont = new Font(fontName, Font.BOLD, fontSize);
        table.getTableHeader().setFont(newBoldFont);


        // set up main menu
        JMenuBar mBar = new JMenuBar();
        JMenu mbtnFile = new JMenu();
        mbtnFile.setText("File");
        JMenuItem load_file = new JMenuItem("Import File");
        mbtnFile.add(load_file);
        mBar.add(mbtnFile);
        this.setJMenuBar(mBar);

        load_file.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            int returnValue = chooser.showOpenDialog(GridView.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File toImport = chooser.getSelectedFile();
                if (toImport.exists()) {

                    try {
                        model.getDataVector().clear();
                        String del = delimiterFiled.getText();
                        FileParser pr;
                        if (del != null && del.length() == 1)
                            pr = new FileParser(new FileReader(toImport), del.charAt(0));
                        else
                            pr = new FileParser(new FileReader(toImport));
                        for (List<Vector<Long>> v : pr.getData().values()) {
                            for (Vector<Long> longs : v) {
                                model.addRow(longs);
                            }
                        }
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }

                } else {
                    JOptionPane.showMessageDialog(GridView.this,
                            "File do not exist", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });


        delimiterFiled = new JTextField();
        delimiterFiled.setPreferredSize(new Dimension(110, 30));
        delimiterFiled.setMaximumSize(new Dimension(110, 30));
        delimiterFiled.setMinimumSize(new Dimension(110, 30));
        delimiterFiled.setSize(new Dimension(110, 30));
        delimiterFiled.setToolTipText("Provide delimiter if needed, Delimiter can not be the delimiter used in Date format Strings");

        JPanel delimiter_panel = new JPanel();
        delimiter_panel.setPreferredSize(new Dimension(130, 30));
        delimiter_panel.setMaximumSize(new Dimension(130, 30));
        delimiter_panel.setMinimumSize(new Dimension(130, 30));
        delimiter_panel.setSize(new Dimension(130, 30));
        delimiter_panel.add(delimiterFiled);


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(delimiter_panel, BorderLayout.WEST);

        add(panel);


    }


    /**
     * Center the Frame in window
     *
     * @param f A Frame to be Centered
     */
    private static void putWindowInCenter(java.awt.Window f) {
        Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double sWidth = screenSize.getWidth();
        double sHeight = screenSize.getHeight();
        Dimension windowSize = f.getSize();

        double wWidth = windowSize.getWidth();
        double wHeight = windowSize.getHeight();
        double x = 0;
        if (wWidth < winSize.width)
            x = (sWidth - wWidth) / 2.0;
        double y = 0;
        if (wHeight < winSize.height)
            y = (sHeight - wHeight) / 2.0;
        f.setLocation((int) x, (int) y);
    }


}
