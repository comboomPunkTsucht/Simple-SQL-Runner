package app;

import java.awt.EventQueue;
import components.NORD_COLORS;
import components.ResultSetTableModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.*;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.GridLayout;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.JButton;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.JSplitPane;
import javax.swing.JFileChooser;
import java.awt.SystemColor;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComboBox;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rsyntaxtextarea.Style;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.Arrays;
import java.util.Locale;
import java.awt.Dialog.ModalExclusionType;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTextField db_tf;
	private JPasswordField passwd_tf;
	private String user;
	private String server;
	private ResultSet output;
	private String log  = "";
	private Connection con;
	private JTable output_view;
	private JTextArea log_view;
	private RTextScrollPane statement_view;
	private RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea();
	public boolean verbose = true;



	private static JMenuBar createMenuBar(RSyntaxTextArea textArea) {

        JMenuBar menuBar = new JMenuBar();

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.UNDO_ACTION)));
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.REDO_ACTION)));
        editMenu.addSeparator();
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.CUT_ACTION)));
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.COPY_ACTION)));
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.PASTE_ACTION)));
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.DELETE_ACTION)));
        editMenu.addSeparator();
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.SELECT_ALL_ACTION)));
        menuBar.add(editMenu);

        return menuBar;
    }

    private static JMenuItem createMenuItem(Action action) {
        JMenuItem item = new JMenuItem(action);
        item.setToolTipText(null); // Swing annoyingly adds tool tip text to the menu item
        return item;
    }
	/*
	private String formatResultSet(ResultSet resultSet) {
        StringBuilder resultString = new StringBuilder();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Get column names
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }

            // Get column widths
            int[] columnWidths = new int[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnWidths[i] = Math.max(columnNames[i].length(), 10); // Minimum width of 10 characters
            }

            // Process each row
            while (resultSet.next()) {
                for (int i = 0; i < columnCount; i++) {
                    String value = resultSet.getString(i + 1);
                    columnWidths[i] = Math.max(columnWidths[i], value.length());
                }
            }

            // Print header
            resultString.append("┌");
            for (int i = 0; i < columnCount; i++) {
                resultString.append("─".repeat(columnWidths[i] + 2));
                if (i < columnCount - 1) {
                    resultString.append("┬");
                }
            }
            resultString.append("┐\n");

            // Print column names
            resultString.append("│");
            for (int i = 0; i < columnCount; i++) {
                resultString.append(String.format(" %-" + columnWidths[i] + "s │", columnNames[i]));
            }
            resultString.append("\n");

            // Print separator
            resultString.append("├");
            for (int i = 0; i < columnCount; i++) {
                resultString.append("─".repeat(columnWidths[i] + 2));
                if (i < columnCount - 1) {
                    resultString.append("┼");
                }
            }
            resultString.append("┤\n");

            // Print rows
            resultSet.beforeFirst(); // Reset cursor
            while (resultSet.next()) {
                resultString.append("│");
                for (int i = 0; i < columnCount; i++) {
                    String value = resultSet.getString(i + 1);
                    resultString.append(String.format(" %-" + columnWidths[i] + "s │", value));
                }
                resultString.append("\n");
            }

            // Print footer
            resultString.append("└");
            for (int i = 0; i < columnCount; i++) {
                resultString.append("─".repeat(columnWidths[i] + 2));
                if (i < columnCount - 1) {
                    resultString.append("┴");
                }
            }
            resultString.append("┘\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultString.toString();
    } */
	/**
	 * Launch the application.
	 */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No option provided. Using default option: -gv");
            args = new String[]{"-gv"};
        }

        String option = args[0];

        switch (option) {
            case "-g":
            case "--gui":
                if (args.length > 1 && (args[1].equals("-v") || args[1].equals("--verbose"))) {
                    run(true);
                } else {
                    run(false);
                }
                return;
            case "-gv":
                run(true);
                return;
            case "-h":
            case "--help":
                System.out.println("Usage:");
                System.out.println("To use the GUI: [-g/--gui]");
                System.out.println("To see the help: [-h/--help]");
                return;
            default:
                System.out.println("Invalid option. Use -h or --help for help.");
                System.exit(1);
        }
    }

    public static void run(boolean v) {
        EventQueue.invokeLater(() -> {
            try {
                MainWindow frame = new MainWindow();
                frame.verbose = v;
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


	private void setLog(String logtext) {
		log = logtext;
		log_view.setText(log);
		if (verbose) {
		System.out.println(logtext);
		}
		// Nach dem Hinzufügen des Textes zum JTextArea, scrollen zum Ende erzwingen
		log_view.setCaretPosition(log_view.getDocument().getLength());
	}

	private void addLog(String addedtext) {
		log = log + "\n" + addedtext;
		log_view.setText(log);
		if (verbose) {
		System.out.println(addedtext);
		}
		// Nach dem Hinzufügen des Textes zum JTextArea, scrollen zum Ende erzwingen
		log_view.setCaretPosition(log_view.getDocument().getLength());

	}

	private String getLog() {
		return log;
	}




	public  void loadFonts() {
        try {
            // Get the directory where font files are located
            File fontDir = new File("fonts");

            if (fontDir.isDirectory()) {
                File[] fontFiles = fontDir.listFiles();

                if (fontFiles != null) {
                    for (File file : fontFiles) {
                        // Load the font
                        Font font = Font.createFont(Font.TRUETYPE_FONT, file);

                        // Register the font
                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        ge.registerFont(font);
                    }
                }
            }
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

	private void importSQLFile() {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Select SQL File to Import");

	    // Create a file filter for SQL files
	    FileNameExtensionFilter sqlFilter = new FileNameExtensionFilter("SQL files (*.sql)", "sql");
	    fileChooser.addChoosableFileFilter(sqlFilter);
	    fileChooser.setFileFilter(sqlFilter);

	    int result = fileChooser.showOpenDialog(this);
	    if (result == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        StringBuilder sb = new StringBuilder();
	        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line).append("\n");
	            }
	            syntaxTextArea.setText(sb.toString());
	            addLog("SQL file imported successfully: " + selectedFile.getName());
	        } catch (IOException ex) {
	            addLog("Error importing SQL file: " + ex.getMessage());
	        }
	    }
	}


	private void exportSQLFile() {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Save SQL File");

	    // Create a file filter for SQL files
	    FileNameExtensionFilter sqlFilter = new FileNameExtensionFilter("SQL files (*.sql)", "sql");
	    fileChooser.addChoosableFileFilter(sqlFilter);
	    fileChooser.setFileFilter(sqlFilter);

	    int result = fileChooser.showSaveDialog(this);
	    if (result == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        // Ensure the file has a .sql extension
	        if (!selectedFile.getName().toLowerCase().endsWith(".sql")) {
	            selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".sql");
	        }
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
	            writer.write(syntaxTextArea.getText());
	            addLog("SQL file exported successfully: " + selectedFile.getName());
	        } catch (IOException ex) {
	            addLog("Error exporting SQL file: " + ex.getMessage());
	        }
	    }
	}


	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setLocale(Locale.GERMANY);
		setName("Simple SQL Runner");
		loadFonts();
		setVisible(true);
		setForeground(NORD_COLORS.FOREGROUND);
		setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		setTitle("Simple SQL Runner");
		setBackground(NORD_COLORS.BACKGROUND);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(1280, 720, 1280, 720);
		setMinimumSize(new Dimension(1280, 720));
		setMaximumSize(new Dimension(2560,1600));

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(NORD_COLORS.BACKGROUND);
		menuBar.setBorder(null);
		menuBar.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		fileMenu.setForeground(NORD_COLORS.FOREGROUND);
		fileMenu.setBackground(NORD_COLORS.BACKGROUND);
		fileMenu.setBorder(null);
		fileMenu.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));

		// Import menu item
		JMenuItem importMenuItem = new JMenuItem("Import SQL File");
		importMenuItem.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));
		importMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importSQLFile();
			}
		});
		fileMenu.add(importMenuItem);

		// Export menu item
		JMenuItem exportMenuItem = new JMenuItem("Export SQL File");
		exportMenuItem.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));
		exportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportSQLFile();
			}
		});
		fileMenu.add(exportMenuItem);

		menuBar.add(fileMenu);

		JMenu editMenu = new JMenu("Edit");
		editMenu.setBackground(NORD_COLORS.BACKGROUND);
		editMenu.setBorder(null);
		editMenu.setForeground(NORD_COLORS.FOREGROUND);
		editMenu.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));

		JMenuItem undo_btn = new JMenuItem();
		undo_btn.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));
		undo_btn.setAction(RTextArea.getAction(RTextArea.UNDO_ACTION));
		if (System.getProperty("os.name").startsWith("Mac")) {
			// On macOS, use Command+A
			undo_btn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_DOWN_MASK));
		} else {
			// On other platforms, use Ctrl+A
			undo_btn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		}
		undo_btn.setText("Undo");
		editMenu.add(undo_btn);

		JMenuItem redo_btn = new JMenuItem();
		redo_btn.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));
		redo_btn.setAction(RTextArea.getAction(RTextArea.REDO_ACTION));
		// Set accelerator based on the OS
		if (System.getProperty("os.name").startsWith("Mac")) {
			// On macOS, use Command+A
			redo_btn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.META_DOWN_MASK));
		} else {
			// On other platforms, use Ctrl+A
			redo_btn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		}
		redo_btn.setText("Redo");
		editMenu.add(redo_btn);

		editMenu.addSeparator();

		JMenuItem cut_btn = new JMenuItem();
		cut_btn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CUT, 0));
		cut_btn.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));
		cut_btn.setAction(RTextArea.getAction(RTextArea.CUT_ACTION));
		cut_btn.setText("Cut");
		editMenu.add(cut_btn);

		JMenuItem copy_btn = new JMenuItem();
		copy_btn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0));
		copy_btn.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));
		copy_btn.setAction(RTextArea.getAction(RTextArea.COPY_ACTION));
		copy_btn.setText("Copy");
		editMenu.add(copy_btn);

		JMenuItem paste_btn = new JMenuItem();
		paste_btn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PASTE, 0));
		paste_btn.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));
		paste_btn.setAction(RTextArea.getAction(RTextArea.PASTE_ACTION));
		paste_btn.setText("Paste");
		editMenu.add(paste_btn);

		JMenuItem del_btn = new JMenuItem();
		del_btn.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));
		del_btn.setAction(RTextArea.getAction(RTextArea.DELETE_ACTION));
		del_btn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		del_btn.setText("Delete");
		editMenu.add(del_btn);

		editMenu.addSeparator();

		JMenuItem sel_all_btn = new JMenuItem();
		// Set accelerator based on the OS
		if (System.getProperty("os.name").startsWith("Mac")) {
			// On macOS, use Command+A
			sel_all_btn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.META_DOWN_MASK));
		} else {
			// On other platforms, use Ctrl+A
			sel_all_btn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		}
		sel_all_btn.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 14));
		sel_all_btn.setAction(RTextArea.getAction(RTextArea.SELECT_ALL_ACTION));
		sel_all_btn.setText("Seleced All");
		editMenu.add(sel_all_btn);

		menuBar.add(editMenu);

		contentPane = new JPanel();
		contentPane.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		contentPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		contentPane.setName("Simple SQL Runner");
		contentPane.setForeground(NORD_COLORS.FOREGROUND);
		contentPane.setBackground(NORD_COLORS.BACKGROUND);
		contentPane.setBorder(null);

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JSplitPane main_split_pnl = new JSplitPane();
		main_split_pnl.setBorder(null);
		main_split_pnl.setResizeWeight(0.7);
		main_split_pnl.setForeground(NORD_COLORS.FOREGROUND);
		main_split_pnl.setBackground(NORD_COLORS.BACKGROUND);
		main_split_pnl.setOneTouchExpandable(true);
		contentPane.add(main_split_pnl, BorderLayout.CENTER);

		JSplitPane sub_split_pnl = new JSplitPane();
		sub_split_pnl.setBorder(null);
		sub_split_pnl.setOrientation(JSplitPane.VERTICAL_SPLIT);
		sub_split_pnl.setResizeWeight(0.7);
		sub_split_pnl.setForeground(NORD_COLORS.FOREGROUND);
		sub_split_pnl.setBackground(NORD_COLORS.BACKGROUND);
		sub_split_pnl.setOneTouchExpandable(true);
		main_split_pnl.setLeftComponent(sub_split_pnl);

		JPanel table_pnl = new JPanel();
		table_pnl.setBorder(null);
		table_pnl.setBackground(NORD_COLORS.BACKGROUND);
		sub_split_pnl.setLeftComponent(table_pnl);
		table_pnl.setLayout(new BorderLayout(0, 0));

		JScrollPane table_pane = new JScrollPane();
		table_pane.setBorder(null);
		table_pnl.add(table_pane);
		table_pane.setAutoscrolls(true);
		table_pane.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		table_pane.setForeground(NORD_COLORS.FOREGROUND);
		table_pane.setBackground(NORD_COLORS.BACKGROUND);

		output_view = new JTable();
		output_view.setLocale(Locale.GERMANY);
		output_view.setSurrendersFocusOnKeystroke(true);
		output_view.setBorder(null);
		table_pane.setViewportView(output_view);
		output_view.setRowHeight(28);
		output_view.setAutoCreateRowSorter(true);
		output_view.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		output_view.setCellSelectionEnabled(true);
		output_view.setColumnSelectionAllowed(true);
		output_view.setFillsViewportHeight(true);
		output_view.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		output_view.setSelectionBackground(NORD_COLORS.SELECTED_BACKGROUND);
		output_view.setSelectionForeground(NORD_COLORS.SELECTED_FOREGROUND);
		output_view.setGridColor(NORD_COLORS.FOREGROUND);
		output_view.setFont(new Font("CaskaydiaCove Nerd Font Mono", Font.PLAIN, 24));
		output_view.setForeground(NORD_COLORS.FOREGROUND);
		output_view.setBackground(NORD_COLORS.BACKGROUND);
		output_view.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		output_view.setAutoscrolls(true);
		output_view.setDragEnabled(true);
		output_view.setLayout(new BorderLayout(0, 0));
		output_view.setSize(new Dimension(2560,1600));

		JLabel table_lbl = new JLabel("Table View");
		table_lbl.setBorder(null);
		table_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 13));
		table_lbl.setBackground(NORD_COLORS.BACKGROUND);
		table_lbl.setForeground(NORD_COLORS.FOREGROUND);
		table_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		table_pnl.add(table_lbl, BorderLayout.NORTH);

		JPanel statement_pnl = new JPanel();
		statement_pnl.setBorder(null);
		statement_pnl.setBackground(NORD_COLORS.BACKGROUND);
		sub_split_pnl.setRightComponent(statement_pnl);
		statement_pnl.setLayout(new BorderLayout(0, 0));

		JLabel statement_lbl = new JLabel("Statement Editor");
		statement_lbl.setLocale(Locale.GERMANY);
		statement_lbl.setBorder(null);
		statement_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 13));
		statement_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		statement_lbl.setForeground(NORD_COLORS.FOREGROUND);
		statement_lbl.setBackground(NORD_COLORS.BACKGROUND);
		statement_pnl.add(statement_lbl, BorderLayout.NORTH);
		syntaxTextArea.setBorder(null);
		syntaxTextArea.setTabSize(3);

		syntaxTextArea.setCodeFoldingEnabled(true);
		syntaxTextArea.setEOLMarkersVisible(true);
		syntaxTextArea.setPaintMarkOccurrencesBorder(true);
		syntaxTextArea.setPaintMatchedBracketPair(true);
		syntaxTextArea.setPaintTabLines(true);
		syntaxTextArea.setWrapStyleWord(false);
		syntaxTextArea.setFractionalFontMetricsEnabled(true);
		syntaxTextArea.setFont(new Font("CaskaydiaCove Nerd Font Mono", Font.PLAIN, 24));
		syntaxTextArea.setAntiAliasingEnabled(true);
		syntaxTextArea.setTabsEmulated(true);
		syntaxTextArea.setWhitespaceVisible(true);
		syntaxTextArea.setUseSelectedTextColor(true);
		syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
		SyntaxScheme s = syntaxTextArea.getSyntaxScheme();

		// Comments
		s.getStyle(Token.COMMENT_DOCUMENTATION).foreground = NORD_COLORS.LIGHT_BLUE;
		s.getStyle(Token.COMMENT_EOL).foreground = NORD_COLORS.LIGHT_BLUE;
		s.getStyle(Token.COMMENT_MULTILINE).foreground = NORD_COLORS.LIGHT_BLUE;

		// Keywords and operators
		s.getStyle(Token.RESERVED_WORD).foreground = NORD_COLORS.BLUE;
		s.getStyle(Token.RESERVED_WORD_2).foreground = NORD_COLORS.BLUE;
		s.getStyle(Token.OPERATOR).foreground = NORD_COLORS.BLUE;

		// Literals
		s.getStyle(Token.LITERAL_BOOLEAN).foreground = NORD_COLORS.MAGENTA;
		s.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = NORD_COLORS.MAGENTA;
		s.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = NORD_COLORS.MAGENTA;
		s.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = NORD_COLORS.MAGENTA;
		s.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = NORD_COLORS.GREEN;

		// Identifiers and functions
		s.getStyle(Token.IDENTIFIER).foreground = NORD_COLORS.WHITE;
		s.getStyle(Token.FUNCTION).foreground = NORD_COLORS.CYAN;

		// Data types
		s.getStyle(Token.DATA_TYPE).foreground = NORD_COLORS.BLUE;

		// Separators
		s.getStyle(Token.SEPARATOR).foreground = NORD_COLORS.WHITE;

		// Errors
		s.getStyle(Token.ERROR_CHAR).foreground = NORD_COLORS.RED;
		s.getStyle(Token.ERROR_IDENTIFIER).foreground = NORD_COLORS.RED;
		s.getStyle(Token.ERROR_NUMBER_FORMAT).foreground = NORD_COLORS.RED;
		s.getStyle(Token.ERROR_STRING_DOUBLE).foreground = NORD_COLORS.RED;

		// Markup-specific styles (if needed)
		s.getStyle(Token.MARKUP_TAG_DELIMITER).foreground = NORD_COLORS.WHITE;
		s.getStyle(Token.MARKUP_TAG_NAME).foreground = NORD_COLORS.BLUE;
		s.getStyle(Token.MARKUP_TAG_ATTRIBUTE).foreground = NORD_COLORS.CYAN;
		s.getStyle(Token.MARKUP_TAG_ATTRIBUTE_VALUE).foreground = NORD_COLORS.GREEN;

		// Set background color for all token types
		for (int i = 0; i < s.getStyleCount(); i++) {
		    Style style = s.getStyle(i);
		    if (style != null) {
		        style.background = NORD_COLORS.BACKGROUND;
		    }
		}

		// Set font for all token types
		Font codeFont = new Font("CaskaydiaCove Nerd Font Mono", Font.PLAIN, 24);
		for (int i = 0; i < s.getStyleCount(); i++) {
		    Style style = s.getStyle(i);
		    if (style != null) {
		        style.font = codeFont;
		    }
		}

		syntaxTextArea.setSyntaxScheme(s);

		// Additional RSyntaxTextArea settings
		syntaxTextArea.setBackground(NORD_COLORS.BACKGROUND);
		syntaxTextArea.setForeground(NORD_COLORS.WHITE);
		syntaxTextArea.setCaretColor(NORD_COLORS.WHITE);
		syntaxTextArea.setCurrentLineHighlightColor(NORD_COLORS.DARK_GRAY);
		syntaxTextArea.setSelectionColor(NORD_COLORS.LIGHT_GRAY);
		syntaxTextArea.setMatchedBracketBGColor(NORD_COLORS.LIGHT_GRAY);
		syntaxTextArea.setMatchedBracketBorderColor(NORD_COLORS.WHITE);




		statement_view = new RTextScrollPane(syntaxTextArea);
		statement_view.getGutter().setBorder(null);
		statement_view.getTextArea().setBorder(null);
		statement_view.setBorder(null);
		statement_pnl.add(statement_view, BorderLayout.CENTER);
		statement_view.getGutter().setExpandedFoldRenderStrategy(ExpandedFoldRenderStrategy.ALWAYS);
		statement_view.getGutter().setCurrentLineNumberColor(NORD_COLORS.FOREGROUND);
		statement_view.getGutter().setActiveLineRangeColor(NORD_COLORS.SELECTED_BACKGROUND);
		statement_view.getGutter().setArmedFoldBackground(NORD_COLORS.BACKGROUND);
		statement_view.getGutter().setBorderColor(NORD_COLORS.BACKGROUND);
		statement_view.getGutter().setFoldIndicatorForeground(NORD_COLORS.FOREGROUND);
		statement_view.getGutter().setFoldBackground(NORD_COLORS.BACKGROUND);
		statement_view.getGutter().setForeground(NORD_COLORS.FOREGROUND);
		statement_view.getGutter().setBackground(NORD_COLORS.BACKGROUND);
		statement_view.getGutter().setLineNumberColor(NORD_COLORS.FOREGROUND);
		statement_view.getGutter().setLineNumberFont(new Font("CaskaydiaCove Nerd Font Mono", Font.PLAIN, 24));
		statement_view.setBackground(NORD_COLORS.BACKGROUND);
		statement_view.setForeground(NORD_COLORS.FOREGROUND);
		statement_view.setFont(new Font("CaskaydiaCove Nerd Font Mono", Font.PLAIN, 24));

		JPanel log_pnl = new JPanel();
		log_pnl.setBorder(null);
		log_pnl.setForeground(NORD_COLORS.FOREGROUND);
		log_pnl.setBackground(NORD_COLORS.BACKGROUND);
		main_split_pnl.setRightComponent(log_pnl);
		log_pnl.setLayout(new BorderLayout(0, 0));

		JLabel log_lbl = new JLabel("Log View");
		log_lbl.setBorder(null);
		log_lbl.setBackground(NORD_COLORS.BACKGROUND);
		log_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 13));
		log_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		log_lbl.setForeground(NORD_COLORS.FOREGROUND);
		log_pnl.add(log_lbl, BorderLayout.NORTH);

		JScrollPane log_pane = new JScrollPane();
		log_pane.setBorder(null);
		log_pnl.add(log_pane, BorderLayout.CENTER);
		log_pane.setAutoscrolls(true);
		log_pane.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		log_pane.setForeground(NORD_COLORS.FOREGROUND);
		log_pane.setBackground(NORD_COLORS.BACKGROUND);
		log_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		log_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		log_view = new JTextArea();
		log_view.setLocale(Locale.GERMANY);
		log_view.setBorder(null);
		log_pane.setViewportView(log_view);
		log_view.setSelectionColor(SystemColor.textHighlight);
		log_view.setEditable(false);
		log_view.setFont(new Font("CaskaydiaCove Nerd Font Mono", Font.PLAIN, 24));
		log_view.setBackground(NORD_COLORS.BACKGROUND);
		log_view.setForeground(NORD_COLORS.FOREGROUND);

		JPanel top_pnl = new JPanel();
		top_pnl.setBorder(null);
		top_pnl.setBackground(NORD_COLORS.BACKGROUND);
		contentPane.add(top_pnl, BorderLayout.NORTH);
		top_pnl.setLayout(new GridLayout(1, 1, 0, 0));

		JLabel protocoll_lbl = new JLabel("Protocoll:");
		protocoll_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		protocoll_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		protocoll_lbl.setForeground(NORD_COLORS.FOREGROUND);
		protocoll_lbl.setBackground(NORD_COLORS.BACKGROUND);
		top_pnl.add(protocoll_lbl);

		JComboBox<String> protocoll_tf = new JComboBox<String>();
		protocoll_tf.setModel(new DefaultComboBoxModel<>(new String[] { "", "sqlserver", "oracle", "mysql", "postgresql", "db2" }));
		protocoll_tf.setSelectedIndex(0);
		protocoll_tf.setForeground(NORD_COLORS.FOREGROUND);
		protocoll_tf.setBackground(NORD_COLORS.BACKGROUND);
		protocoll_tf.setEditable(true);
		top_pnl.add(protocoll_tf);

		JLabel server_lbl = new JLabel("Server:");
		server_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		server_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		server_lbl.setForeground(NORD_COLORS.FOREGROUND);
		server_lbl.setBackground(NORD_COLORS.BACKGROUND);
		top_pnl.add(server_lbl);

		JComboBox<String> server_tf = new JComboBox<String>();
		server_tf.setModel(new DefaultComboBoxModel<>(new String[] { "", "localhost", "127.0.0.1", "::1" }));
		server_tf.setSelectedIndex(0);
		server_tf.setForeground(NORD_COLORS.FOREGROUND);
		server_tf.setBackground(NORD_COLORS.BACKGROUND);
		server_tf.setEditable(true);
		top_pnl.add(server_tf);

		JLabel port_lbl = new JLabel("Port:");
		port_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		port_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		port_lbl.setForeground(NORD_COLORS.FOREGROUND);
		port_lbl.setBackground(NORD_COLORS.BACKGROUND);
		top_pnl.add(port_lbl);

		JComboBox<Long> port_tf = new JComboBox<Long>();
		port_tf.setModel(new DefaultComboBoxModel<>(new Long[]{ null, 1433L, 1521L, 3306L, 5432L, 50000L }));
		port_tf.setSelectedIndex(0);
		port_tf.setForeground(NORD_COLORS.FOREGROUND);
		port_tf.setBackground(NORD_COLORS.BACKGROUND);
		port_tf.setEditable(true);
		top_pnl.add(port_tf);

		JLabel db_lbl = new JLabel("Database:");
		db_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		db_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		db_lbl.setForeground(NORD_COLORS.FOREGROUND);
		db_lbl.setBackground(NORD_COLORS.BACKGROUND);
		top_pnl.add(db_lbl);

		db_tf = new JTextField();
		db_tf.setSelectionColor(NORD_COLORS.SELECTED_BACKGROUND);
		db_tf.setSelectedTextColor(NORD_COLORS.BACKGROUND);
		db_tf.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		db_tf.setForeground(NORD_COLORS.FOREGROUND);
		db_tf.setBackground(NORD_COLORS.BACKGROUND);
		top_pnl.add(db_tf);
		db_tf.setColumns(10);

		JLabel user_lbl = new JLabel("User:");
		user_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		user_lbl.setForeground(NORD_COLORS.FOREGROUND);
		user_lbl.setBackground(NORD_COLORS.BACKGROUND);
		top_pnl.add(user_lbl);

		JComboBox<String> user_tf = new JComboBox<String>();
		user_tf.setEditable(true);
		user_tf.setBackground(NORD_COLORS.BACKGROUND);
		user_tf.setForeground(NORD_COLORS.FOREGROUND);
		user_tf.setModel(new DefaultComboBoxModel<>(new String[] { "", "sa", "sys", "root", "postgres", "db2admin", "admin" }));
		user_tf.setSelectedIndex(0);
		top_pnl.add(user_tf);

		JLabel passwd_lbl = new JLabel("Password:");
		passwd_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		passwd_lbl.setBackground(NORD_COLORS.BACKGROUND);
		passwd_lbl.setForeground(NORD_COLORS.FOREGROUND);
		passwd_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		top_pnl.add(passwd_lbl);

		passwd_tf = new JPasswordField();
		passwd_tf.setSelectionColor(SystemColor.textHighlight);
		passwd_tf.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		passwd_tf.setForeground(NORD_COLORS.FOREGROUND);
		passwd_tf.setBackground(NORD_COLORS.BACKGROUND);
		top_pnl.add(passwd_tf);
		passwd_tf.setColumns(10);
		passwd_tf.setToolTipText("Password");

		JButton run_statement_btn = new JButton("Run Statement");
		run_statement_btn.setIconTextGap(8);
		run_statement_btn.setHorizontalTextPosition(SwingConstants.CENTER);
		run_statement_btn.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		run_statement_btn.setToolTipText("Run Statement");
		run_statement_btn.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        user = user_tf.getSelectedItem().toString();
		        addLog("User: " + user);

		        char[] passwordChars = passwd_tf.getPassword();
		        addLog("Password: " + "*".repeat(passwordChars.length));

		        server = "jdbc:" + protocoll_tf.getSelectedItem().toString() + "://"
		                + server_tf.getSelectedItem().toString() + ":" + port_tf.getSelectedItem().toString() + "/"
		                + db_tf.getText();
		        addLog("Server + Database: " + server);

		        try {
		            con = DriverManager.getConnection(server, user, new String(passwordChars));
		            addLog("Connection opened");
		            log_view.setText(getLog());

		            String sql = syntaxTextArea.getText();
		            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
		                boolean isResultSet = pstmt.execute();
		                if (isResultSet) {
		                    output = pstmt.getResultSet();
		                    addLog("Getting Results...");
		                    output_view.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		                    output_view.revalidate(); // Refreshes the table
		                    output_view.repaint(); // Repaints the table
		                    ResultSetTableModel tableModel = new ResultSetTableModel(output);
		                    addLog("Format data for visualization...");
		                    output_view.setModel(tableModel);
		                    TableColumnAdjuster tca = new TableColumnAdjuster(output_view);
		                    tca.adjustColumns();
		                    output_view.revalidate(); // Refreshes the table
		                    output_view.repaint(); // Repaints the table
		                    if (output_view.getWidth() < table_pane.getWidth()) {
		                    	output_view.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		                    } else if (output_view.getWidth() == table_pane.getWidth()) {
		                    	output_view.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		                    } else if (output_view.getWidth() > table_pane.getWidth()) {
		                    	output_view.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		                    }
		                    
		                    
		                    
		                    addLog("Display data in Table View");
		                } else {
		                    int updateCount = pstmt.getUpdateCount();
		                    addLog("Affected rows: " + updateCount);
		                }
		            }

		            con.close();
		            addLog("Connection closed");
		        } catch (SQLException ex) {
		            addLog("Error: " + ex.getMessage());
		        } finally {
		            Arrays.fill(passwordChars, ' ');
		        }
		    }
		});
		run_statement_btn.setForeground(NORD_COLORS.FOREGROUND);
		run_statement_btn.setBackground(NORD_COLORS.BACKGROUND);
		top_pnl.add(run_statement_btn);

		this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Resize the output_view to the size of the JFrame
                output_view.setSize(new Dimension(2560,1600));
            }
        });
		



	}
}