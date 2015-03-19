// Assignment: 4
// Name: Cory Siebler
// StudentID: 1000832292
// Lecture Topic: Lecture 14 - JPA
// Description: 
package addressbook;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import static java.lang.Integer.parseInt;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author csiebler
 */
public class AddressBook extends JFrame {

    // create an EntityManagerFactory for the persistence unit
    private final EntityManagerFactory entityManagerFactory
            = Persistence.createEntityManagerFactory("AddressBookPU");

    // create an EntityManager for interacting with the persistence unit
    private final EntityManager entityManager
            = entityManagerFactory.createEntityManager();

    private List<Addresses> results;
    private int numberOfEntries = 0;
    private int currentEntryIndex;
    private final JButton browseButton = new JButton();
    private final JLabel emailLabel = new JLabel();
    private final JTextField emailTextField = new JTextField(10);
    private final JLabel firstNameLabel = new JLabel();
    private final JTextField firstNameTextField = new JTextField(10);
    private final JLabel idLabel = new JLabel();
    private final JTextField idTextField = new JTextField(10);
    private final JTextField indexTextField = new JTextField(2);
    private final JLabel lastNameLabel = new JLabel();
    private final JTextField lastNameTextField = new JTextField(10);
    private final JTextField maxTextField = new JTextField(2);
    private final JButton nextButton = new JButton();
    private final JLabel ofLabel = new JLabel();
    private final JLabel phoneLabel = new JLabel();
    private final JTextField phoneTextField = new JTextField(10);
    private final JButton previousButton = new JButton();
    private final JButton queryButton = new JButton();
    private final JLabel queryLabel = new JLabel();
    private final JPanel queryPanel = new JPanel();
    private final JPanel navigatePanel = new JPanel();
    private final JPanel displayPanel = new JPanel();
    private final JTextField queryTextField = new JTextField(10);
    private final JButton insertButton = new JButton();
    private final JButton updateButton = new JButton();
    private final JButton deleteButton = new JButton();

    /**
     * Constructor
     */
    public AddressBook() {
        super("Address Book");
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setSize(400,375);
        setResizable(false);
        navigatePanel.setLayout(new BoxLayout(navigatePanel, BoxLayout.X_AXIS));
        previousButton.setText("Previous");
        previousButton.setEnabled(false);
        previousButton.addActionListener(this::previousButtonActionPerformed);
        navigatePanel.add(previousButton);
        navigatePanel.add(Box.createHorizontalStrut(10));
        indexTextField.setHorizontalAlignment(JTextField.CENTER);
        indexTextField.addActionListener(
                this::indexTextFieldActionPerformed);
        navigatePanel.add(indexTextField);
        navigatePanel.add(Box.createHorizontalStrut(10));
        ofLabel.setText("of");
        navigatePanel.add(ofLabel);
        navigatePanel.add(Box.createHorizontalStrut(10));
        maxTextField.setHorizontalAlignment(JTextField.CENTER);
        maxTextField.setEditable(false);
        navigatePanel.add(maxTextField);
        navigatePanel.add(Box.createHorizontalStrut(10));
        nextButton.setText("Next");
        nextButton.setEnabled(false);
        nextButton.addActionListener(this::nextButtonActionPerformed);
        navigatePanel.add(nextButton);
        add(navigatePanel);
        displayPanel.setLayout(new GridLayout(5, 2, 4, 4));
        idLabel.setText("Address ID:");
        displayPanel.add(idLabel);
        idTextField.setEditable(true);
        displayPanel.add(idTextField);
        firstNameLabel.setText("First Name:");
        displayPanel.add(firstNameLabel);
        displayPanel.add(firstNameTextField);
        lastNameLabel.setText("Last Name:");
        displayPanel.add(lastNameLabel);
        displayPanel.add(lastNameTextField);
        emailLabel.setText("Email:");
        displayPanel.add(emailLabel);
        displayPanel.add(emailTextField);
        phoneLabel.setText("Phone Number:");
        displayPanel.add(phoneLabel);
        displayPanel.add(phoneTextField);
        add(displayPanel);
        queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.X_AXIS));
        queryPanel.setBorder(BorderFactory.createTitledBorder(
                "Find an entry by last name"));
        queryLabel.setText("Last Name:");
        queryPanel.add(Box.createHorizontalStrut(5));
        queryPanel.add(queryLabel);
        queryPanel.add(Box.createHorizontalStrut(10));
        queryPanel.add(queryTextField);
        queryPanel.add(Box.createHorizontalStrut(10));
        queryButton.setText("Find");
        queryButton.addActionListener(this::queryButtonActionPerformed);
        queryPanel.add(queryButton);
        queryPanel.add(Box.createHorizontalStrut(5));
        add(queryPanel);
        browseButton.setText("Browse All Entries");
        browseButton.addActionListener(this::browseButtonActionPerformed);
        add(browseButton);
        insertButton.setText("Insert New Entry");
        insertButton.addActionListener(this::insertButtonActionPerformed);
        add(insertButton);
        updateButton.setText("Update Entry");
        updateButton.addActionListener(this::updateButtonActionPerformed);
        add(updateButton);
        deleteButton.setText("Delete Entry");
        deleteButton.addActionListener(this::deleteButtonActionPerformed);
        add(deleteButton);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
        setVisible(true);
    }

    /**
     * Handles call when previousButton is clicked
     * 
     * @param evt
     */
    private void previousButtonActionPerformed(ActionEvent evt) {
        --currentEntryIndex;

        if (currentEntryIndex < 0) {
            currentEntryIndex = numberOfEntries - 1;
        }

        indexTextField.setText("" + (currentEntryIndex + 1));
        indexTextFieldActionPerformed(evt);
    }

    /**
     * Handles call when nextButton is clicked
     * 
     * @param e
     */
    private void nextButtonActionPerformed(ActionEvent e) {
        ++currentEntryIndex;
        if (currentEntryIndex >= numberOfEntries) {
            currentEntryIndex = 0;
        }
        indexTextField.setText("" + (currentEntryIndex + 1));
        indexTextFieldActionPerformed(e);
    }

    /**
     * Handles call when queryButton is clicked
     * 
     * @param evt
     */
    private void queryButtonActionPerformed(ActionEvent evt) {
        /*
         queryButtonActionPerformed creates a TypedQuery for the
         "Addresses.findBylastname" query that returns a List<Addresses>
         containing all the entities with the specified last name.
         
         The autogenerated Addresses class query requires a parameter
         */
        TypedQuery<Addresses> findByLastname = entityManager.createNamedQuery(
                "Addresses.findByLastname", Addresses.class);

        /*
         Before executing the query, set arguments for each query parameter by
         calling TypedQuery method setParameter with the parameter name as the
         first argument and the corresponding value as the second argument.
         */
        findByLastname.setParameter("lastname", queryTextField.getText());

        /*
         The query returns a List containing any matching entities in database
         */
        results = findByLastname.getResultList();

        numberOfEntries = results.size();

        // Update theGUI
        if (numberOfEntries != 0) {
            currentEntryIndex = 0;
            displayRecord();
            nextButton.setEnabled(true);
            previousButton.setEnabled(true);
        } else {
            browseButtonActionPerformed(evt);
        }
    }

    /**
     * Displays record at currentEntrylndex in results
     */
    private void displayRecord() {
        Addresses currentEntry = results.get(currentEntryIndex);
        idTextField.setText("" + currentEntry.getAddressid());
        firstNameTextField.setText(currentEntry.getFirstname());
        lastNameTextField.setText(currentEntry.getLastname());
        emailTextField.setText(currentEntry.getEmail());
        phoneTextField.setText(currentEntry.getPhonenumber());
        maxTextField.setText("" + numberOfEntries);
        indexTextField.setText("" + (currentEntryIndex + 1));
    }

    /**
     * Handles call when a new value is entered in indexTextField
     * 
     * @param evt
     */
    private void indexTextFieldActionPerformed(ActionEvent evt) {
        currentEntryIndex = (Integer.parseInt(indexTextField.getText()) - 1);
        if (numberOfEntries != 0 && currentEntryIndex < numberOfEntries) {
            displayRecord();
        }
    }

    /**
     * Handles calls when browseButton is clicked.
     *
     * Create a TypedQuery for the "Addresses.findAll" query that returns a
     * List<Addresses> containing all the entities in the database.
     * 
     * @param evt
     */
    private void browseButtonActionPerformed(ActionEvent evt) {
        // query that returns all contacts
        TypedQuery<Addresses> findAllAddresses
                = entityManager.createNamedQuery("Addresses.findAll", Addresses.class);
        results = findAllAddresses.getResultList(); // get all addresses 
        numberOfEntries = results.size();

        // Update the GUI
        if (numberOfEntries != 0) {
            currentEntryIndex = 0;
            displayRecord();
            nextButton.setEnabled(true);
            previousButton.setEnabled(true);
        }
    }

    /**
     * Handles calls when insertButton is clicked.
     *
     * A new row is added to the Addresses table.
     *
     * To create a new entity, create an instance of the entity class, set
     * values for its instance variables, then use a transaction to insert the
     * data in the database.
     *
     * getTransaction gets the EntityTransaction that manages the transaction.
     *
     * begin starts the transaction.
     *
     * persist inserts the new entity into the database.
     *
     * If this operation executes successfully, commit commits the changes to
     * the database.
     *
     * If the persist operation fails, rollback returns the database to its
     * state prior to the transaction.
     * 
     * @param evt
     */
    private void insertButtonActionPerformed(ActionEvent evt) {
        // Initialize a Address entity
        Addresses address = new Addresses();
        
        // Insert the fields into the Address entity
        address.setFirstname(firstNameTextField.getText());
        address.setLastname(lastNameTextField.getText());
        address.setPhonenumber(phoneTextField.getText());
        address.setEmail(emailTextField.getText());

        // Get an EntityTransaction to manage insert operation
        EntityTransaction transaction = entityManager.getTransaction();
        
        try {
            transaction.begin(); // start transaction
            entityManager.persist(address); // store new entry
            transaction.commit(); // commit changes to the database
            JOptionPane.showMessageDialog(this, "Person added!",
                    "Person added", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            transaction.rollback(); // undo database operations
            JOptionPane.showMessageDialog(this, "Person not added!",
                    e.getMessage(), JOptionPane.PLAIN_MESSAGE);
        }
        browseButtonActionPerformed(evt); // reload Addresses
    }
    
    /**
     * 
     * @param evt 
     */
    private void updateButtonActionPerformed(ActionEvent evt) {
        // Find the Address entity for the currently viewed person
        Addresses address = entityManager.find(Addresses.class, parseInt(idTextField.getText()));
        
        // Get an EntityTransaction to manage insert operation
        EntityTransaction transaction = entityManager.getTransaction();
        
        try {
            transaction.begin(); // start transaction
            
            // Update the fields for the Address entity
            address.setFirstname(firstNameTextField.getText());
            address.setLastname(lastNameTextField.getText());
            address.setPhonenumber(phoneTextField.getText());
            address.setEmail(emailTextField.getText());
            
            transaction.commit(); // commit changes to the database
            JOptionPane.showMessageDialog(this, "Person updated!",
                    "Person updated", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            transaction.rollback(); // undo database operations
            JOptionPane.showMessageDialog(this, "Person not updated!",
                    e.getMessage(), JOptionPane.PLAIN_MESSAGE);
        }
        browseButtonActionPerformed(evt); // reload Addresses
    }
    
    /**
     * 
     * @param evt 
     */
    private void deleteButtonActionPerformed(ActionEvent evt) {
        // Find the Address entity for the currently viewed person
        Addresses address = entityManager.find(Addresses.class, parseInt(idTextField.getText()));

        // Get an EntityTransaction to manage insert operation
        EntityTransaction transaction = entityManager.getTransaction();
        
        try {
            transaction.begin(); // start transaction
            entityManager.remove(address); // delete entry
            transaction.commit(); // commit changes to the database
            JOptionPane.showMessageDialog(this, "Person deleted!",
                    "Person added", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            transaction.rollback(); // undo database operations
            JOptionPane.showMessageDialog(this, "Person not deleted!",
                    e.getMessage(), JOptionPane.PLAIN_MESSAGE);
        }
        browseButtonActionPerformed(evt); // reload Addresses
    }

    /**
     * Main method
     * 
     * @param args 
     */
    public static void main(String args[]) {
        AddressBook addressBook = new AddressBook();
    }
    
}
