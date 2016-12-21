package jpc.ui;

import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jpc.JavaPieChart;

public class ChartMod
{

	public static void main(String[] args)
	{
		
		final HashMap<String, Double> data = new HashMap<String, Double>();
		
		data.put("Section 1", (double) 1);
		data.put("Section 2", (double) 2);
		data.put("Section 3", (double) 1);
		data.put("Section 4", (double) 3);
		data.put("Section 5", (double) 4);
		
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Example");
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
        		new ChartMod(frame, data);
        		
        		
                frame.setVisible(true);
            }
        });
	}

	public ChartMod(JFrame f, HashMap<String, Double> data)
	{
		Dimension dm = new Dimension(800, 150);
		
        JavaPieChart jpc = new JavaPieChart(data);
        jpc.setMinimumSize(dm);
        jpc.setSize(800, 800);
        
        f.add(jpc);
	}
}
