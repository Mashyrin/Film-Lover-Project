package masharun.filmLovers.view.forms;

import masharun.filmLovers.Main;
import masharun.filmLovers.models.DAO.UserDAO;
import masharun.filmLovers.models.entities.User;
import masharun.filmLovers.models.tableModels.AdminTableModel;
import masharun.filmLovers.view.OptionPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.sql.SQLException;

public class AdminForm implements Form {
    private JPanel rootPanel;
    private JPanel HeaderPanel;
    private JPanel FooterPanel;
    private JButton exitButton;
    private JLabel mainTitle;
    private JButton deleteButton;
    private JTable table;
    private JLabel yourInfoLabel;
    private JTextField searchField;
    private JComboBox roleComboBox;
    private JButton updateRoleButton;
    private TableRowSorter<TableModel> rowSorter;
    
    public AdminForm() {
        
        Main.setTitle( "Работа с пользователями" );
        init();
        initListeners();
    }
    
    private void init() {
        yourInfoLabel.setText( Main.getCurrentUser().getLogin() );
        table.setModel( new AdminTableModel() );
        rowSorter = new TableRowSorter<>( table.getModel() );
        table.setRowSorter( rowSorter );
    }
    
    private void initListeners() {
        exitButton.addActionListener( eventListener -> {
            Main.setCurrentUser( null );
            Main.getMainForm().clearStackFrame();
            Main.getMainForm().setNewForm( new MainForm() );
        } );
    
        table.getSelectionModel().addListSelectionListener( selectionListener -> {
            roleComboBox.setEnabled( true );
            roleComboBox.setVisible( true );
            
            if ( table.getValueAt( table.getSelectedRow(), 4 ).equals( "A" )) {
                roleComboBox.setSelectedIndex( 0 );
            } else if ( table.getValueAt( table.getSelectedRow(), 4 ).equals( "U" )) {
                roleComboBox.setSelectedIndex( 1 );
            } else if ( table.getValueAt( table.getSelectedRow(), 4 ).equals( "C" )) {
                roleComboBox.setSelectedIndex( 2 );
            } else if ( table.getValueAt( table.getSelectedRow(), 4 ).equals( "S" )) {
                roleComboBox.setSelectedIndex( 3 );
            }
        } );
        
        updateRoleButton.addActionListener( eventListener -> {
            try {
                User user = new UserDAO().selectById( table.getValueAt( table.getSelectedRow(), 0 ).toString() );
                user.setRole( roleComboBox.getSelectedItem().toString().substring( 0, 1 ) );
                new UserDAO().update( user );
                
                table.updateUI();
            } catch ( SQLException e ) {
                OptionPane.showMessage( "Ошибка загрузки данных", "Ошибка" );
                System.err.println( e.toString() );
            }
        } );
        
        deleteButton.addActionListener( eventListener -> {
            try {
                String buffLogin = table.getModel().getValueAt( table.getSelectedRow(), 0 ).toString();
                if( buffLogin.equals( "root" ) ) {
                    OptionPane.showMessage( "root не может быть удалён", "Ошибка" );
                } else if( buffLogin.equals( Main.getCurrentUser().getLogin() ) ) {
                    OptionPane.showMessage( "Нельзя удалить собственный аккаунт", "Ошибка" );
                } else {
                    try {
                        new UserDAO().deleteById( buffLogin );
                        table.updateUI();
                    } catch( SQLException e ) {
                        OptionPane.showMessage( "Ошибка удаления пользователя", "Ошибка" );
                        System.err.println( e.toString() );
                    }
                }
            } catch ( ArrayIndexOutOfBoundsException e ) {
                OptionPane.showMessage( "Вы не выбрали запись", "Ошибка" );
                System.err.println( e.toString() );
            }
        } );
        
        searchField.getDocument().addDocumentListener( new DocumentListener() {
            @Override
            public void insertUpdate( DocumentEvent e ) {
                String text = searchField.getText();
            
                if( text.trim().length() == 0 ) {
                    rowSorter.setRowFilter( null );
                } else {
                    rowSorter.setRowFilter( RowFilter.regexFilter( "(?i)" + text ) );
                }
            }
        
            @Override
            public void removeUpdate( DocumentEvent e ) {
                insertUpdate( e );
            }
        
            @Override
            public void changedUpdate( DocumentEvent e ) {
                //Nothing
            }
        } );
    }
    
    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }
    
    @Override
    public void reinit() {
        Main.setTitle( "Работа с пользователями" );
    }
}
