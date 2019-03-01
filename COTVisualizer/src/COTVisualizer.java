import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.Cursor;

public class COTVisualizer {
	public static JFrame myframe;
	public static JMenuBar tb;
	public static String[] comboBoxList;
	public static String[] financials, commodities;
	public static JSlider sliderx, slidery;
	public static JPanel panelpaint;
	public static String selected = "";
	public static JPanel oszillator;
	public static String[] dates;
	public static Integer[] commercials;
	public static Integer[] largetraders;
	public static Integer[] smalltraders;
	public static Integer[] oszillator26;
	public static int crosshairx, crosshairy;
	public static boolean drawcrosshair = false;
	public static boolean grid = false;
	public static JCheckBox grid_box, crosshair_box, plus, minus;
	public static int drag_x;
	public static Point mousePT;
	public static int dx = 0;
	public static int dy = 0;
	public static int delta_x = 10;
	public static boolean plusevent = false;
	public static boolean minusevent = false;
	public static JButton update;
	public static COTupdater up;
	
	
	public static void main(String[] args) {
		up = new COTupdater();
		up.init();
		myframe = new JFrame("CoT Report");
		myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			BufferedImage imageholly = null;
			String OS = System.getProperty("os.name");
			if (OS.startsWith("Windows")) imageholly = ImageIO.read(COTVisualizer.class.getResource("/resources/holly.PNG"));
			else imageholly = ImageIO.read(COTVisualizer.class.getResource("/resources/holly.png"));
			myframe.setIconImage(imageholly);
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		// repaint after resize
		myframe.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (!selected.equals("")) {
					MyRectanglePanel.drawgraph = true;
					MyOszillator.drawoszillator = true;
					myframe.repaint();
				}
			}
		});

		// readfiles();

		addComponentsToPane(myframe.getContentPane());

		myframe.setJMenuBar(tb);
		myframe.pack();
		myframe.setVisible(true);
		myframe.repaint();
	}

	/*
	 * public static void readfiles() { String str; List<String> list = new
	 * ArrayList<String>();
	 * 
	 * try { BufferedReader in = new BufferedReader(new FileReader("futures")); File
	 * fut = new File("futures"); if (fut.exists()) while ((str = in.readLine()) !=
	 * null) { list.add(str); }
	 * 
	 * in.close(); }
	 * 
	 * catch (IOException e) { e.printStackTrace(); }
	 * 
	 * comboBoxList = list.toArray(new String[list.size()]); }
	 */

	public static void addComponentsToPane(Container pane) {
		tb = new JMenuBar();
		JLabel label = new JLabel("Select:  ");
		comboBoxList = up.getFuturesList();
		JComboBox<String> mycombobox = new JComboBox<String>(comboBoxList);
		mycombobox.setMaximumSize(new Dimension(300, 30));

		mycombobox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				JComboBox<String> comboboxitem = (JComboBox<String>) event.getSource();
				selected = (String) comboboxitem.getSelectedItem();
				String str;
				List<String> dates_list = new ArrayList<String>();
				List<Integer> commercials_list = new ArrayList<Integer>();
				List<Integer> largetraders_list = new ArrayList<Integer>();
				List<Integer> smalltraders_list = new ArrayList<Integer>();
				BufferedReader in;

				try {
					File tablesFolder = new File("tables"); 
					if (tablesFolder.isDirectory()) {
						String selected_path = "tables/" + selected;
						in = new BufferedReader(new FileReader(selected_path));
						while ((str = in.readLine()) != null) {
							String[] tokens = str.split("\\s+");
							dates_list.add(tokens[0]);
							commercials_list.add(Integer.valueOf(tokens[1]));
							largetraders_list.add(Integer.valueOf(tokens[2]));
							smalltraders_list.add(Integer.valueOf(tokens[3]));
						}

						Collections.reverse(dates_list); //
						dates = dates_list.toArray(new String[dates_list.size()]);
						Collections.reverse(commercials_list); //
						commercials = commercials_list.toArray(new Integer[commercials_list.size()]);
						Collections.reverse(largetraders_list);//
						largetraders = largetraders_list.toArray(new Integer[largetraders_list.size()]);
						Collections.reverse(smalltraders_list); //
						smalltraders = smalltraders_list.toArray(new Integer[smalltraders_list.size()]);
						oszillator26 = new Integer[dates.length - 26]; 

						List<Integer> oszillator26_list = new ArrayList<Integer>();
						int t = 0;
						while (t < oszillator26.length) {
							oszillator26_list = commercials_list.subList(t, 26 + t);
							int min26 = t + oszillator26_list.indexOf(Collections.min(oszillator26_list));
							int max26 = t + oszillator26_list.indexOf(Collections.max(oszillator26_list));

							int d = commercials[t];
							int f = commercials[max26];
							int g = commercials[min26];
							int o = 0;
							if ((f - g) != 0)
								o = 100 * (d - g) / (f - g);

							oszillator26[t] = o;
							t++;
						}

						dx = 0;
						dy = 0;
						MyOszillator.drawoszillator = true;
						MyRectanglePanel.drawgraph = true;
						myframe.repaint();

					}
				}

				catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				catch (IOException e) {
					e.printStackTrace();
				}

				/*
				 * dates = dates_list.toArray(new String[dates_list.size()]); commercials =
				 * commercials_list.toArray(new Integer[commercials_list.size()]); largetraders
				 * = largetraders_list.toArray(new Integer[largetraders_list.size()]);
				 * smalltraders = smalltraders_list.toArray(new
				 * Integer[smalltraders_list.size()]); oszillator26 = new Integer[dates.length -
				 * 26];
				 * 
				 * List<Integer> oszillator26_list = new ArrayList<Integer>(); int t = 0; while
				 * (t < oszillator26.length) { oszillator26_list = commercials_list.subList(t,
				 * 26 + t); int min26 = t +
				 * oszillator26_list.indexOf(Collections.min(oszillator26_list)); int max26 = t
				 * + oszillator26_list.indexOf(Collections.max(oszillator26_list));
				 * 
				 * int d = commercials[t]; int f = commercials[max26]; int g =
				 * commercials[min26]; int o = 0; if ((f - g) != 0) o = 100 * (d - g) / (f - g);
				 * 
				 * oszillator26[t] = o; t++; }
				 * 
				 * dx = 0; dy = 0; MyOszillator.drawoszillator = true;
				 * MyRectanglePanel.drawgraph = true; myframe.repaint();
				 */
			}
		});

		grid_box = new JCheckBox();
		grid_box.setText("grid");
		grid_box.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				grid = !grid;
				if (!selected.equals("")) {
					MyRectanglePanel.drawgraph = true;
					MyOszillator.drawoszillator = true;
				}
				myframe.repaint();
			}
		});

		crosshair_box = new JCheckBox();
		crosshair_box.setText("crosshair");

		crosshair_box.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				drawcrosshair = !drawcrosshair;
				if (!selected.equals("")) {
					MyRectanglePanel.drawgraph = true;
					MyOszillator.drawoszillator = true;
				}
				myframe.repaint();
			}
		});

		minus = new JCheckBox();
		minus.setText("-");

		minus.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				minusevent = !minusevent;
				MyRectanglePanel.drawgraph = true;
				MyOszillator.drawoszillator = true;
				myframe.repaint();
			}
		});

		plus = new JCheckBox();
		plus.setText("+");

		plus.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				plusevent = !plusevent;
				MyRectanglePanel.drawgraph = true;
				MyOszillator.drawoszillator = true;
				myframe.repaint();
			}
		});

		update = new JButton("update COT");
		update.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//JOptionPane.showInternalMessageDialog(panelpaint, "please wait");
				MyRectanglePanel.updating= true;
				myframe.repaint();
				up.update();
				//JOptionPane.showInternalMessageDialog(panelpaint, "data updated");
				//myframe.repaint();
			}
		});
		
		

		tb.add(label);
		tb.add(mycombobox);
		JLabel dummy = new JLabel("                                                                         ");
		tb.add(grid_box);
		tb.add(crosshair_box);
		tb.add(plus);
		tb.add(minus);
		tb.add(update);
		tb.add(dummy);
		oszillator = new MyOszillator();
		oszillator.setPreferredSize(new Dimension(myframe.getWidth(), 150));
		oszillator.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		panelpaint = new MyRectanglePanel();
		panelpaint.setPreferredSize(new Dimension(myframe.getWidth(), 500));
		panelpaint.setBackground(Color.DARK_GRAY);

		panelpaint.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!selected.equals("")) {
					mousePT = e.getPoint();
					MyRectanglePanel.drawgraph = true;
					MyOszillator.drawoszillator = true;
					myframe.repaint();
				}
			}
		});

		panelpaint.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				if ((!selected.equals("")) && (arg0.getX() < MyRectanglePanel.width - MyRectanglePanel.space_right)) {
					dx = arg0.getX() - mousePT.x;
					dy = arg0.getY() - mousePT.y;
					MyRectanglePanel.drawgraph = true;
					MyOszillator.drawoszillator = true;
					myframe.repaint();
				}

				if ((!selected.equals("")) && (arg0.getX() > MyRectanglePanel.width - MyRectanglePanel.space_right)
						&& (arg0.getY() < MyRectanglePanel.height - MyRectanglePanel.space_buttom)) {
					dy = arg0.getY() - mousePT.y;
					MyRectanglePanel.drawgraph = true;
					MyOszillator.drawoszillator = true;
					myframe.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				if ((arg0.getX() > MyRectanglePanel.width - MyRectanglePanel.space_right)
						&& (arg0.getY() < MyRectanglePanel.height - MyRectanglePanel.space_buttom)) {
					Cursor cursor = new Cursor(Cursor.S_RESIZE_CURSOR);
					myframe.setCursor(cursor);
				}

				if ((arg0.getX() <= MyRectanglePanel.width - MyRectanglePanel.space_right)
						|| (arg0.getY() >= MyRectanglePanel.height - MyRectanglePanel.space_buttom)) {
					Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
					myframe.setCursor(cursor);
				}

				if ((arg0.getX() >= MyRectanglePanel.width - MyRectanglePanel.space_right)
						&& (arg0.getY() > MyRectanglePanel.height - MyRectanglePanel.space_buttom)) {
					Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
					myframe.setCursor(cursor);
				}

				crosshairx = arg0.getX();
				crosshairy = arg0.getY();

				File tablesFolder = new File("tables");
			
				if (!selected.equals("") && (tablesFolder.isDirectory())) {
					
					MyRectanglePanel.drawgraph = true;
					MyOszillator.drawoszillator = true;
					myframe.repaint();
				}
			}
		});

		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.add(panelpaint);
		pane.add(oszillator);
	}
}
