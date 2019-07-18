import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.border.LineBorder;
import javax.swing.ButtonGroup;
import javax.swing.DefaultRowSorter;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;

import org.apache.lucene.store.Directory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GuiRSN {

	private JFrame frmRsnDemo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiRSN window = new GuiRSN();
					window.frmRsnDemo.setVisible(true);
					window.frmRsnDemo.setLocationRelativeTo(null);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public GuiRSN() throws IOException {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		frmRsnDemo = new JFrame();
		frmRsnDemo.setResizable(false);
		frmRsnDemo.setTitle("RSN DEMO");
		frmRsnDemo.setBounds(100, 100, 1280, 720);
		frmRsnDemo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		SpringLayout springLayout = new SpringLayout();
		frmRsnDemo.getContentPane().setLayout(springLayout);
		
		JPanel panel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel, 10, SpringLayout.NORTH, frmRsnDemo.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, frmRsnDemo.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, panel, 681, SpringLayout.NORTH, frmRsnDemo.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panel, 1264, SpringLayout.WEST, frmRsnDemo.getContentPane());
		frmRsnDemo.getContentPane().add(panel);
		panel.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		panel_1.setBounds(0, 0, 1254, 671);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_2.setBounds(10, 11, 1234, 33);
		panel_1.add(panel_2);
		
		ButtonGroup group2 = new ButtonGroup();
		
		JTable table = new JTable();
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setFillsViewportHeight(true);
		
		Object[] columnNames = {"Tweet", "Data", "favoriteCount", "retweetCount", "link"};
		DefaultTableModel modelTable = new DefaultTableModel() {
			Class[] types = { String.class, String.class, Integer.class, Integer.class, String.class };
			boolean[] canEdit = new boolean [] {
				    false, false, false, false, false
				};
			@Override
			public Class getColumnClass(int columnIndex) {
			        return this.types[columnIndex];
			        }       

			     // This override is just for avoid editing the content of my JTable. 
			@Override
			public boolean isCellEditable(int row, int column) {
			        return false;
			        }
		};
		
		modelTable.setColumnIdentifiers(columnNames);
		table.setModel(modelTable);
		
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		sorter.addRowSorterListener(new RowSorterListener(){
			 @Override
			    public void sorterChanged(RowSorterEvent evt) {
				 group2.clearSelection();
			    }
		});
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sorter.setSortKeys(sortKeys); 
		((DefaultRowSorter<TableModel, Integer>) sorter).setSortable(0, false);  
		((DefaultRowSorter<TableModel, Integer>) sorter).setSortable(4, false); 
		table.setRowSorter(sorter);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 55, 1234, 605);
		panel_1.add(scrollPane);
		
		JMenuItem mntmSelectOneTopic = new JMenuItem("Select one topic:");
		panel_2.add(mntmSelectOneTopic);
		
		JToggleButton tglbtnBusiness = new JToggleButton("Business");
		tglbtnBusiness.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					try {
						table.getRowSorter().setSortKeys(null);
						getTweetList(modelTable, 1);
						tglbtnBusiness.setSelected(true);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
			}
		});
		panel_2.add(tglbtnBusiness);
		tglbtnBusiness.setAlignmentX(Component.CENTER_ALIGNMENT);
		group2.add(tglbtnBusiness);
		
		JToggleButton tglbtnFashion = new JToggleButton("Fashion");
		tglbtnFashion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					table.getRowSorter().setSortKeys(null);
					getTweetList(modelTable, 2);
					tglbtnFashion.setSelected(true);
					} 
				catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		panel_2.add(tglbtnFashion);
		tglbtnFashion.setAlignmentX(Component.CENTER_ALIGNMENT);
		group2.add(tglbtnFashion);
		
		JToggleButton tglbtnMusic = new JToggleButton("Music");
		tglbtnMusic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					table.getRowSorter().setSortKeys(null);
					getTweetList(modelTable, 3);
					tglbtnMusic.setSelected(true);
					} 
				catch (IOException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel_2.add(tglbtnMusic);
		tglbtnMusic.setAlignmentX(Component.CENTER_ALIGNMENT);
		group2.add(tglbtnMusic);
		
		JToggleButton tglbtnNews = new JToggleButton("News");
		tglbtnNews.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					table.getRowSorter().setSortKeys(null);
					getTweetList(modelTable, 4);
					tglbtnNews.setSelected(true);
					} 
				catch (IOException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel_2.add(tglbtnNews);
		tglbtnNews.setAlignmentX(Component.CENTER_ALIGNMENT);
		group2.add(tglbtnNews);
		
		JToggleButton tglbtnPolitic = new JToggleButton("Politic");
		tglbtnPolitic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					table.getRowSorter().setSortKeys(null);
					getTweetList(modelTable, 5);
					tglbtnPolitic.setSelected(true);
					} 
				catch (IOException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel_2.add(tglbtnPolitic);
		tglbtnPolitic.setAlignmentX(Component.CENTER_ALIGNMENT);
		group2.add(tglbtnPolitic);
		
		JToggleButton tglbtnScience = new JToggleButton("Science");
		tglbtnScience.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					table.getRowSorter().setSortKeys(null);
					getTweetList(modelTable, 6);
					tglbtnScience.setSelected(true);
					} 
				catch (IOException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel_2.add(tglbtnScience);
		tglbtnScience.setAlignmentX(Component.CENTER_ALIGNMENT);
		group2.add(tglbtnScience);
		
		JToggleButton tglbtnSport = new JToggleButton("Sport");
		tglbtnSport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					table.getRowSorter().setSortKeys(null);
					getTweetList(modelTable, 7);
					tglbtnSport.setSelected(true);
					} 
				catch (IOException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel_2.add(tglbtnSport);
		tglbtnSport.setAlignmentX(Component.CENTER_ALIGNMENT);
		group2.add(tglbtnSport);
		
		JToggleButton tglbtnTech = new JToggleButton("Tech");
		tglbtnTech.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					table.getRowSorter().setSortKeys(null);
					getTweetList(modelTable, 8);
					tglbtnTech.setSelected(true);
					} 
				catch (IOException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		panel_2.add(tglbtnTech);
		tglbtnTech.setAlignmentX(Component.CENTER_ALIGNMENT);
		group2.add(tglbtnTech);
		
	}
	
	private static void getTweetList(DefaultTableModel dtm, int prefNum) throws IOException, ParseException{
		//clear table
		int rowCount = dtm.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
		    dtm.removeRow(i);
		}
		
		List<List<String>> records = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader("matrixTweetScoreForTopic.csv"));
		String line;
	    while ((line = br.readLine()) != null) {
	        String[] values = line.split(",");
	        records.add(Arrays.asList(values));
	    }
	    br.close();
	    
	    Double[][] matrixIDScoreForTopic = getMatrixIDScoreForTopic (records, prefNum);
		
		orderMatrix(matrixIDScoreForTopic, true); //true = desc
		displayArray(matrixIDScoreForTopic);
	
		List<Double> lista = new ArrayList<Double>();
		for(int i=0; i<matrixIDScoreForTopic.length; i++) {
			lista.add(matrixIDScoreForTopic[i][0]);
		}
		
		Path idxpathTweet = Paths.get("index");
		Directory dr = FSDirectory.open(idxpathTweet);
		IndexReader reader = DirectoryReader.open(dr);
		
		for(Double element : lista ){
			String tweet = reader.document(element.intValue()).getField("text").stringValue();
			
			String dataTW = reader.document(element.intValue()).getField("createdAt").stringValue();
			DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
			Date data = (Date) formatter.parse(dataTW);
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String format = formatter2.format(data);

			
			String favoriteCount = reader.document(element.intValue()).getField("favoriteCount").stringValue();
			String retweetCount = reader.document(element.intValue()).getField("retweetCount").stringValue();
			String idTweet = reader.document(element.intValue()).getField("idTweetField").stringValue();
			dtm.addRow(new Object[] {tweet, format, new Integer(favoriteCount), new Integer(retweetCount), "https://twitter.com/Atechtrader/status/" + idTweet});
		}
	}
	
	private static Double[][] getMatrixIDScoreForTopic(List<List<String>> lista, int topic) {
		Double[][] matrixIDScore = new Double[lista.size()-1][2];
		for(int i=1; i<lista.size(); i++) {
			List<String> score = lista.get(i);
			matrixIDScore[i-1][0] = Double.parseDouble(score.get(0).toString());
			matrixIDScore[i-1][1] = Double.parseDouble(score.get(topic).toString());
		}
		return matrixIDScore;
	}
	
	private static void orderMatrix(Double[][] itemIdAndQty, boolean desc) {	
		Arrays.sort(itemIdAndQty, new Comparator<Double[]>() {
			@Override
			public int compare(Double[] o1, Double[] o2) {
				Double quantityOne = o1[1];
				Double quantityTwo = o2[1];
				if(desc)
					return quantityTwo.compareTo(quantityOne);
				else
					return quantityOne.compareTo(quantityTwo);
			}
		});
	}
	
	private static void displayArray(Double[][] itemIdAndQty) {
		System.out.println("-------------------------------------");
		System.out.println("Item id\t\tQuantity");
		for (int i = 0; i < itemIdAndQty.length; i++) {
			Double[] itemRecord = itemIdAndQty[i];
			System.out.println(itemRecord[0] + "\t\t" + itemRecord[1]);
		}
		System.out.println("-------------------------------------");
	}
}
