import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextArea;

import java.awt.Scrollbar;

import javax.swing.JScrollPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JSeparator;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class App {

	private JFrame frmDnaSequence;
	private JTextField textField;
	private JTextField textField_1, textField_2;
	private JTextField patchingDNASequenceFilePath;
	private JTextField patchingDiffFilePath;
	private JButton btnBrowse, button, patchingDNASequenceBrowseBttn, patchingDiffBrowseBttn, btnFindEditScript,
			patchingPatchBttn, generateSequenceBttn;

	private File DNA1File, DNA2File, patchingDNASequenceFile, diffFile;
	private static String projectPath = "C:\\Users\\Joey\\Desktop\\IDPProject";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frmDnaSequence.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public App() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDnaSequence = new JFrame();
		frmDnaSequence.setTitle("DNA Sequences Differencing and Patching Tool");
		frmDnaSequence.setBounds(100, 100, 900, 500);
		frmDnaSequence.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmDnaSequence.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("String Edit Distance", null, panel_1, null);
		panel_1.setLayout(null);

		JLabel lblDnaSequence = new JLabel("DNA Sequence 1: ");
		lblDnaSequence.setBounds(27, 58, 157, 14);
		panel_1.add(lblDnaSequence);

		textField = new JTextField();
		textField.setBounds(153, 55, 203, 20);
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(textField);
		textField.setColumns(20);

		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(376, 54, 85, 23);
		panel_1.add(btnBrowse);

		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fileChooser = new JFileChooser(projectPath);
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
						"XML Files", "xml"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					DNA1File = fileChooser.getSelectedFile();
					textField.setText(DNA1File.getPath());
				}
			}
		});

		JLabel lblDnaSequence_1 = new JLabel("DNA Sequence 2: ");
		lblDnaSequence_1.setBounds(27, 94, 157, 14);
		panel_1.add(lblDnaSequence_1);

		textField_1 = new JTextField();
		textField_1.setBounds(153, 91, 203, 20);
		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		textField_1.setColumns(20);
		panel_1.add(textField_1);

		button = new JButton("Browse");
		button.setBounds(376, 90, 85, 23);
		panel_1.add(button);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fileChooser = new JFileChooser(projectPath);
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
						"XML Files", "xml"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					DNA2File = fileChooser.getSelectedFile();
					textField_1.setText(DNA2File.getPath());
				}
			}
		});

		JLabel lblParameters = new JLabel("Parameters");
		lblParameters.setFont(new Font("Calisto MT", Font.BOLD, 20));
		lblParameters.setBounds(27, 11, 146, 36);
		panel_1.add(lblParameters);
		
		JLabel label_4 = new JLabel("Generate DNA Sequence:");
		label_4.setFont(new Font("Calisto MT", Font.BOLD, 20));
		label_4.setBounds(530, 11, 300, 36);
		panel_1.add(label_4);
		
		JSeparator separator1 = new JSeparator();
		separator1.setBounds(530, 43, 350, 2);
		panel_1.add(separator1);
		
		JLabel lblDnaSequence_5 = new JLabel("Length limit: ");
		lblDnaSequence_5.setBounds(530, 58, 157, 14);
		panel_1.add(lblDnaSequence_5);

		textField_2 = new JTextField();
		textField_2.setBounds(620, 56, 203, 20);
		textField_2.setHorizontalAlignment(SwingConstants.CENTER);
		textField_2.setColumns(20);
		panel_1.add(textField_2);

		generateSequenceBttn = new JButton("Generate");
		generateSequenceBttn.setBounds(530, 90, 100, 23);
		panel_1.add(generateSequenceBttn);
		
		generateSequenceBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				boolean isValidInteger = false;
				int size = 0;
			      try
			      {
			         size = Integer.parseInt(textField_2.getText());
			 
			         // s is a valid integer
			 
			         isValidInteger = true;
			      }
			      catch (NumberFormatException ex)
			      {
			         // s is not an integer
			      }
			      
				if (!isValidInteger) {
					JOptionPane.showMessageDialog(null,"DNA length should be an integer !", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				DNASequenceGenerator generator = new DNASequenceGenerator(size);
				
				JOptionPane.showMessageDialog(null,"The DNA sequence was successfully generated !", "Success", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		});
		
		button = new JButton("Browse");
		button.setBounds(376, 90, 85, 23);
		panel_1.add(button);

		JSeparator separator = new JSeparator();
		separator.setBounds(27, 43, 434, 2);
		panel_1.add(separator);

		JLabel lblResult = new JLabel("Result");
		lblResult.setFont(new Font("Calisto MT", Font.BOLD, 20));
		lblResult.setBounds(27, 165, 146, 36);
		panel_1.add(lblResult);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(27, 199, 434, 2);
		panel_1.add(separator_1);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(27, 212, 434, 210);
		panel_1.add(scrollPane);

		final JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		btnFindEditScript = new JButton("String Edit Distance");
		btnFindEditScript.setBounds(27, 131, 146, 23);
		panel_1.add(btnFindEditScript);
		
		btnFindEditScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				DNA1File = new File(textField.getText());
				DNA2File = new File(textField_1.getText());
				
				if (textField_1.getText().isEmpty() || textField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null,"XML files should be provided !", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (!(textField_1.getText().endsWith(".xml") && textField.getText().endsWith(".xml"))) {
					JOptionPane.showMessageDialog(null,
							"Files provided should be under XML format !",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if(!DNA1File.exists() || !DNA2File.exists()) {
					JOptionPane.showMessageDialog(null, "File does not exist !", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
					
				long startTime = System.currentTimeMillis();
				
				try {
					WFAlgo algo = new WFAlgo(DNA1File, DNA2File);
				
					long endTime = System.currentTimeMillis();
					long executionTime = endTime - startTime;
					
					textArea.setText("INPUTS:\n");
					textArea.append("----------------------------- \n");
					textArea.append("DNA Sequence 1: " + algo.getDNASequence1() + "\n");
					textArea.append("DNA Sequence 2: " + algo.getDNASequence2() + "\n");
					textArea.append("----------------------------- \n");
					textArea.append("OUTPUTS: \n");
					textArea.append("----------------------------- \n");
					textArea.append("SED: " + algo.getSED() + "\n");
					textArea.append("Similarity: " + algo.getSimilarity() + "\n\n");
					//textArea.append(algo.formatTable() + "\n\n");
					textArea.append(algo.formatES() + "\n");
					textArea.append(algo.formatReversedES() + "\n");
					textArea.append("----------------------------- \n");
					if((algo.ESWriterWithDeletePriority().compareTo(algo.formatES()) != 0) || (algo.ESWriterWithInsertPriority().compareTo(algo.formatES()) != 0)) {
						textArea.append("Other acceptable edit scripts (same cost):  \n");
						textArea.append(((!algo.ESWriterWithDeletePriority().isEmpty()) ? algo.ESWriterWithDeletePriority() + "\n" : ""));
						textArea.append(((!algo.ESWriterWithInsertPriority().isEmpty()  && algo.ESWriterWithInsertPriority().compareTo(algo.ESWriterWithDeletePriority()) != 0) ? algo.ESWriterWithInsertPriority() + "\n" : ""));
						textArea.append("----------------------------- \n");
					}
					textArea.append("Execution time: " + executionTime + " milliseconds \n");
					textArea.append("Edit script has been saved in an XML File !");
				} catch (ParserConfigurationException | SAXException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		JPanel panel = new JPanel();
		panel.setLayout(null);
		tabbedPane.addTab("Patching", null, panel, null);

		JLabel patchingLabelDNASequence = new JLabel("DNA Sequence: ");
		patchingLabelDNASequence.setBounds(27, 58, 116, 14);
		panel.add(patchingLabelDNASequence);

		patchingDNASequenceFilePath = new JTextField();
		patchingDNASequenceFilePath.setHorizontalAlignment(SwingConstants.CENTER);
		patchingDNASequenceFilePath.setColumns(20);
		patchingDNASequenceFilePath.setBounds(153, 55, 203, 20);
		panel.add(patchingDNASequenceFilePath);

		patchingDNASequenceBrowseBttn = new JButton("Browse");
		patchingDNASequenceBrowseBttn.setBounds(376, 54, 85, 23);
		panel.add(patchingDNASequenceBrowseBttn);

		patchingDNASequenceBrowseBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fileChooser = new JFileChooser(projectPath);
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
						"XML Files", "xml"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					patchingDNASequenceFile = fileChooser.getSelectedFile();
					patchingDNASequenceFilePath.setText(patchingDNASequenceFile.getPath());
				}
			}
		});

		JLabel lblEditScript = new JLabel("Edit Script:");
		lblEditScript.setBounds(27, 94, 116, 14);
		panel.add(lblEditScript);

		patchingDiffFilePath = new JTextField();
		patchingDiffFilePath.setHorizontalAlignment(SwingConstants.CENTER);
		patchingDiffFilePath.setColumns(20);
		patchingDiffFilePath.setBounds(153, 91, 203, 20);
		panel.add(patchingDiffFilePath);

		patchingDiffBrowseBttn = new JButton("Browse");
		patchingDiffBrowseBttn.setBounds(376, 90, 85, 23);
		panel.add(patchingDiffBrowseBttn);

		patchingDiffBrowseBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fileChooser = new JFileChooser(projectPath);
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
						"XML Files", "xml"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					diffFile = fileChooser.getSelectedFile();
					patchingDiffFilePath.setText(diffFile.getPath());
				}
			}
		});

		JLabel label_2 = new JLabel("Parameters");
		label_2.setFont(new Font("Calisto MT", Font.BOLD, 20));
		label_2.setBounds(27, 11, 146, 36);
		panel.add(label_2);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(27, 43, 434, 2);
		panel.add(separator_2);

		JLabel label_3 = new JLabel("Result");
		label_3.setFont(new Font("Calisto MT", Font.BOLD, 20));
		label_3.setBounds(27, 165, 146, 36);
		panel.add(label_3);

		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(27, 199, 434, 2);
		panel.add(separator_3);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(27, 212, 434, 210);
		panel.add(scrollPane_1);

		final JTextArea patchingResult = new JTextArea();
		patchingResult.setEditable(false);
		scrollPane_1.setViewportView(patchingResult);

		patchingPatchBttn = new JButton("Patch");
		patchingPatchBttn.setBounds(27, 127, 89, 23);
		panel.add(patchingPatchBttn);

		patchingPatchBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				diffFile = new File(patchingDiffFilePath.getText());
				patchingDNASequenceFile = new File(patchingDNASequenceFilePath.getText());
				
				if (patchingDNASequenceFilePath.getText().isEmpty() || patchingDiffFilePath.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null,"XML files should be provided !", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (!(patchingDNASequenceFilePath.getText().endsWith(".xml") && patchingDiffFilePath.getText().endsWith(".xml"))) {
					JOptionPane.showMessageDialog(null,
							"Files provided should be under XML format !",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if(!patchingDNASequenceFile.exists() || !diffFile.exists()) {
					JOptionPane.showMessageDialog(null, "File does not exist !", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					
					long startTime = System.currentTimeMillis();
					Patching patching = new Patching(diffFile, patchingDNASequenceFile);
					long endTime = System.currentTimeMillis();
					long executionTime = endTime - startTime;
					
					patchingResult.setText("INPUTS: \n");
					patchingResult.append("----------------------------- \n");
					patchingResult.append("DNA Sequence: " + patching.getDNASequence() + "\n");
					patchingResult.append(patching.getEditScript() + "\n");
					patchingResult.append(patching.getReversedEditScript() + "\n");
					patchingResult.append("----------------------------- \n");
					patchingResult.append("OUTPUTS: \n");
					patchingResult.append("----------------------------- \n");
					patchingResult.append("Original DNA Sequence: " + patching.getBuiltDNASequence() + "\n");
					patchingResult.append("----------------------------- \n");
					patchingResult.append("Execution time: " + executionTime + " milliseconds \n");
					patchingResult.append("The recovered DNA Sequence has been saved in an XML File !");
				} catch (ParserConfigurationException | SAXException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
