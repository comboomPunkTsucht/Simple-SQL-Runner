package app;

import java.awt.EventQueue;
import components.ResultSetTableModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Window.Type;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JEditorPane;
import java.awt.GridLayout;
import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.DropMode;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JToolBar;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JDesktopPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.SystemColor;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.ScrollPane;
import javax.swing.JTextPane;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTextField db_tf;
	private JPasswordField passwd_tf;
	private String user;
	private String passwd;
	private String server;
	private Statement statement;
	private ResultSet output;
	private String log  = "";
	private Connection con;
	private JTable output_view;
	private JTextArea log_view;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private String getPasswd() {
		String passwdd = "";
		for (int i = 0; i < passwd.length(); i++) {
			if (passwdd == null) {
				passwdd = "*";
			} else {
				passwdd = passwdd + "*";
			}
		}
		return passwdd;
	}
	
	private void setLog(String logtext) {
		log = logtext;
		log_view.setText(log);
		// Nach dem Hinzufügen des Textes zum JTextArea, scrollen zum Ende erzwingen		
		log_view.setCaretPosition(log_view.getDocument().getLength());
	}
	
	private void addLog(String addedtext) {
		log = log + "\n" + addedtext;
		log_view.setText(log);
		// Nach dem Hinzufügen des Textes zum JTextArea, scrollen zum Ende erzwingen
		log_view.setCaretPosition(log_view.getDocument().getLength());

	}
	
	private String getLog() {
		return log;
	}
	
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
    }
	
	
	public static void loadFonts() {
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

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		loadFonts();
		setVisible(true);
		setForeground(new Color(255, 255, 255));
		setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		setTitle("Simple SQL Runner");
		setBackground(new Color(34, 34, 34));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(1280, 720, 1280, 720);
		setMinimumSize(new Dimension(1280, 720));
		contentPane = new JPanel();
		contentPane.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		contentPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		contentPane.setName("Simple SQL Runner");
		contentPane.setForeground(new Color(255, 255, 255));
		contentPane.setBackground(new Color(34, 34, 34));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane main_tab_pnl = new JTabbedPane(JTabbedPane.LEFT);
		main_tab_pnl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		contentPane.add(main_tab_pnl, BorderLayout.CENTER);
		main_tab_pnl.setBorder(null);
		main_tab_pnl.setForeground(new Color(255, 255, 255));
		main_tab_pnl.setBackground(new Color(34, 34, 34));
		main_tab_pnl.setToolTipText("Tabs");
		main_tab_pnl.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		JScrollPane table_pane = new JScrollPane();
		table_pane.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		table_pane.setForeground(new Color(255, 255, 255));
		table_pane.setBackground(new Color(52, 52, 52));
		table_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		table_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		main_tab_pnl.addTab("Table", null, table_pane, null);
		
		output_view = new JTable();
		output_view.setSelectionBackground(SystemColor.controlHighlight);
		output_view.setSelectionForeground(new Color(255, 255, 255));
		output_view.setGridColor(new Color(0, 0, 0));
		output_view.setFont(new Font("CaskaydiaCove Nerd Font Mono", Font.PLAIN, 12));
		output_view.setForeground(new Color(0, 0, 0));
		output_view.setBackground(new Color(255, 255, 255));
		table_pane.setViewportView(output_view);
		
		JScrollPane statement_pane = new JScrollPane();
		statement_pane.setForeground(new Color(255, 255, 255));
		statement_pane.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		statement_pane.setBackground(new Color(52, 52, 52));
		statement_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		statement_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		main_tab_pnl.addTab("Editor", null, statement_pane, null);
		
		JEditorPane statement_view = new JEditorPane();
		statement_view.setSelectedTextColor(new Color(255, 255, 255));
		statement_view.setSelectionColor(SystemColor.controlHighlight);
		statement_view.setCaretColor(new Color(255, 255, 255));
		statement_view.setBackground(new Color(52, 52, 52));
		statement_view.setForeground(new Color(255, 255, 255));
		statement_view.setFont(new Font("CaskaydiaCove Nerd Font Mono", Font.PLAIN, 24));
		statement_pane.setViewportView(statement_view);
		
		JScrollPane log_pane = new JScrollPane();
		log_pane.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		log_pane.setForeground(new Color(255, 255, 255));
		log_pane.setBackground(new Color(52, 52, 52));
		log_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		log_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		main_tab_pnl.addTab("Log", null, log_pane, null);
		
		log_view = new JTextArea();
		log_view.setEditable(false);
		log_view.setFont(new Font("CaskaydiaCove Nerd Font Mono", Font.PLAIN, 24));
		log_view.setBackground(new Color(52, 52, 52));
		log_view.setForeground(new Color(255, 255, 255));
		log_pane.setViewportView(log_view);
		
		JPanel top_pnl = new JPanel();
		top_pnl.setBackground(new Color(34, 34, 34));
		contentPane.add(top_pnl, BorderLayout.NORTH);
		top_pnl.setLayout(new GridLayout(1, 1, 0, 0));
		
		JLabel protocoll_lbl = new JLabel("Protocoll:");
		protocoll_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		protocoll_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		protocoll_lbl.setForeground(new Color(255, 255, 255));
		protocoll_lbl.setBackground(new Color(34, 34, 34));
		top_pnl.add(protocoll_lbl);
		
		JComboBox protocoll_tf = new JComboBox();
		protocoll_tf.setModel(new DefaultComboBoxModel(new String[] {"", "sqlserver", "oracle", "mysql", "postgresql", "db2"}));
		protocoll_tf.setSelectedIndex(0);
		protocoll_tf.setForeground(new Color(255, 255, 255));
		protocoll_tf.setBackground(new Color(52, 52, 52));
		protocoll_tf.setEditable(true);
		top_pnl.add(protocoll_tf);
		
		JLabel server_lbl = new JLabel("Server:");
		server_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		server_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		server_lbl.setForeground(new Color(255, 255, 255));
		server_lbl.setBackground(new Color(34, 34, 34));
		top_pnl.add(server_lbl);
		
		JComboBox server_tf = new JComboBox();
		server_tf.setModel(new DefaultComboBoxModel(new String[] {"", "localhost", "127.0.0.1", "::1"}));
		server_tf.setSelectedIndex(0);
		server_tf.setForeground(new Color(255, 255, 255));
		server_tf.setBackground(new Color(52, 52, 52));
		server_tf.setEditable(true);
		top_pnl.add(server_tf);
		
		JLabel port_lbl = new JLabel("Port:");
		port_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		port_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		port_lbl.setForeground(new Color(255, 255, 255));
		port_lbl.setBackground(new Color(34, 34, 34));
		top_pnl.add(port_lbl);
		
		JComboBox port_tf = new JComboBox();
		port_tf.setModel(new DefaultComboBoxModel(new String[] {"", "1433", "1521", "3306", "5432", "50000"}));
		port_tf.setSelectedIndex(0);
		port_tf.setForeground(new Color(255, 255, 255));
		port_tf.setBackground(new Color(52, 52, 52));
		port_tf.setEditable(true);
		top_pnl.add(port_tf);
		
		JLabel db_lbl = new JLabel("Database:");
		db_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		db_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		db_lbl.setForeground(new Color(255, 255, 255));
		db_lbl.setBackground(new Color(34, 34, 34));
		top_pnl.add(db_lbl);
		
		db_tf = new JTextField();
		db_tf.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		db_tf.setForeground(new Color(255, 255, 255));
		db_tf.setBackground(new Color(52, 52, 52));
		top_pnl.add(db_tf);
		db_tf.setColumns(10);
		
		JLabel user_lbl = new JLabel("User:");
		user_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		user_lbl.setForeground(new Color(255, 255, 255));
		user_lbl.setBackground(new Color(34, 34, 34));
		top_pnl.add(user_lbl);
		
		JComboBox user_tf = new JComboBox();
		user_tf.setEditable(true);
		user_tf.setBackground(new Color(52, 52, 52));
		user_tf.setForeground(new Color(255, 255, 255));
		user_tf.setModel(new DefaultComboBoxModel(new String[] {"", "sa", "sys", "root", "postgres", "db2admin", "admin"}));
		user_tf.setSelectedIndex(0);
		top_pnl.add(user_tf);
		
		JLabel passwd_lbl = new JLabel("Password:");
		passwd_lbl.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		passwd_lbl.setBackground(new Color(34, 34, 34));
		passwd_lbl.setForeground(new Color(255, 255, 255));
		passwd_lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		top_pnl.add(passwd_lbl);
		
		passwd_tf = new JPasswordField();
		passwd_tf.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.PLAIN, 12));
		passwd_tf.setForeground(new Color(255, 255, 255));
		passwd_tf.setBackground(new Color(52, 52, 52));
		top_pnl.add(passwd_tf);
		passwd_tf.setColumns(10);
		passwd_tf.setToolTipText("Password");
		
		JButton run_statement_btn = new JButton("Run Statement");
		run_statement_btn.setFont(new Font("CaskaydiaCove Nerd Font Propo", Font.BOLD, 12));
		run_statement_btn.setToolTipText("Run Statement");
		run_statement_btn.addActionListener(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent e) {
		        user = user_tf.getSelectedItem().toString();
		        if (log == null) {
		        	setLog("User: " + user);
		        } else {
		        	addLog("User: " + user);
		        }
		        passwd = String.valueOf(passwd_tf.getPassword());
		        addLog("Password: " + getPasswd());
		        
		        server = "jdbc:"+ protocoll_tf.getSelectedItem().toString() + "://" +  server_tf.getSelectedItem().toString() +":" + port_tf.getSelectedItem().toString() + "/" + db_tf.getText(); 
		        addLog("Server + Database: " + server);
		        
		        try {
		            con = DriverManager.getConnection(server, user, passwd);
		            addLog("Connection opened");
		            log_view.setText(getLog());
		            
		            statement = con.createStatement();
		            output = statement.executeQuery(statement_view.getText());
		            
		            // Create a custom TableModel with the ResultSet
		            ResultSetTableModel tableModel = new ResultSetTableModel(output);
		            
		            // Set the custom TableModel to the output_view JTable
		            output_view.setModel(tableModel);
		            
		            // Display formatted result set in the log text area
		            addLog(formatResultSet(output));
		            passwd = " ";
		            con.close();
		            addLog("Connection closed");
		        } catch (SQLException ex) {
		            // Handle possible errors...
		            addLog("Error: " + ex.getMessage());
		            // ... and display an error message, for example
		        }
		    }
        });
		run_statement_btn.setForeground(new Color(0, 0, 0));
		run_statement_btn.setBackground(new Color(255, 255, 255));
		top_pnl.add(run_statement_btn);
		
		
}
}