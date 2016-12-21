package jpc;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JavaPieChart extends JPanel implements ComponentListener
{
	private JavaPieChart instance;
	
	private Map<String, Double> normalizedData;
	private Map<String, Double> data;
	private Map<String, ChartLabel> labels;
	private Map<String, Arc2D.Double> arcs;
	private Map<String, Color> colors;
	private List<Color> baseColors;
	private Rectangle2D.Double chartContainer;
	private Point mouseStart;
	
	private int wExt = 600;
	private int hExt = 600;
	private int dChart = 300;
	private int startOffset = 0;
	private int previousStartOffset;
	private double total;
	
	@Override
	public void paintComponent(Graphics g)
	{

		computeArcs();
		computeLabels();
		super.paintComponent(g);
		
		wExt = this.getWidth();
		hExt = this.getHeight();
				
		Graphics2D g2D = (Graphics2D) g;
		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						
		int nb = 0;		
		
		for(Entry<String, Arc2D.Double> e : arcs.entrySet())
		{			
			
			Arc2D.Double arc = e.getValue();
			
			//Drawing connector label line
			g2D.setColor(Color.BLACK);
			
			Point2D centerArc = Utils.getCenterArc(arc);

			Rectangle lBounds = labels.get(e.getKey()).getBounds();
			
			g2D.drawLine((int) centerArc.getX(), (int) centerArc.getY(), (int) lBounds.getCenterX(), (int) lBounds.getCenterY());
			
			//drawing dot
			
			
			
			//Drawing slice
			Color base;
			
			if(colors.containsKey(e.getKey()))
			{
				base = colors.get(e.getKey());
			}
			else
			{
				base = baseColors.get(nb % baseColors.size());
				colors.put(e.getKey(), base);
			}
			
			Color gradient = base.darker().darker();
			
			g2D.setPaint(new GradientPaint((float) arc.getMaxX(), (float) arc.getMaxY(), gradient, (float) arc.getMinX(), (float) arc.getMinY(), base));
			
			g2D.fill(arc);
			nb++;
		}
		
		//drawing separator line
		g2D.setColor(Color.WHITE);
		
		for(Arc2D.Double arc : arcs.values())
		{
			g2D.drawLine((int) Math.round(chartContainer.getCenterX()), (int) Math.round(chartContainer.getCenterY()), (int) Math.round(arc.getEndPoint().getX()), (int) Math.round(arc.getEndPoint().getY()));
		}
		
		 
		
	}

	public JavaPieChart(Map<String, Double> data)
	{
		this.data = data;
		instance = this;
		
		normalizeData();
				
		baseColors = new ArrayList<Color>();
		//default colors
		baseColors.add(new Color(181, 52, 49));//red
		baseColors.add(new Color(0, 175, 80));//green
		baseColors.add(new Color(108, 77, 147));//violet
		baseColors.add(new Color(251, 215, 57));//yellow
		baseColors.add(new Color(47, 156, 185));//cyan
		baseColors.add(new Color(234, 126, 36));//orange
		baseColors.add(new Color(38, 33, 251));//blue
		baseColors.add(new Color(251, 45, 199));//pink
		baseColors.add(new Color(148, 64, 3));//brown
		this.setDoubleBuffered(true);
		
		this.setBackground(Color.WHITE);
		
		labels = new HashMap<String, ChartLabel>();
		colors = new HashMap<String, Color>();
		
		
		for(String s : data.keySet())
		{
			ChartLabel l = new ChartLabel(s+": "+ Math.round(data.get(s)));
			l.addComponentListener(this);
			this.add(l);
			labels.put(s, l);
		}

		this.addMouseWheelListener(new MouseWheelListener()
		{
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				dChart += e.getWheelRotation()*-10;
				//dChart += e.getWheelRotation()*-10;
				
				if(dChart < 10)
				{
					dChart = 10;
				}

				repaint();	
			}
		});
		
		this.addMouseListener(new MouseAdapter()
		{
				
			JDialog colorDialog;
			JColorChooser colorChooser;
			String currentArcName;
			
			ActionListener okColor = new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					colors.put(currentArcName, colorChooser.getColor());
					colorDialog.dispose();
					instance.repaint();
				}
			};
			
			ActionListener cancelColor = new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					colorDialog.dispose();
				}
			};
			
						@Override
			public void mousePressed(MouseEvent e)
			{
				mouseStart = e.getPoint();
				previousStartOffset = startOffset;
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() >= 2)
				{
					for(Entry<String, Arc2D.Double> entry : arcs.entrySet())
					{
						if(entry.getValue().contains(e.getPoint().getX(), e.getPoint().getY()))
						{
							currentArcName = entry.getKey();
							colorChooser = new JColorChooser(colors.get(entry.getKey()));
							//colorChooser.updateUI();
							colorDialog = JColorChooser.createDialog(instance, "Choose color for "+entry.getKey(), true, colorChooser, okColor, cancelColor);
							colorDialog.setVisible(true);							
							break;
						}
					}
				}
			}
			
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter()
		{

			@Override
			public void mouseDragged(MouseEvent e)
			{
				startOffset = previousStartOffset + mouseStart.y - e.getPoint().y;
				repaint();
			}
			
		});
	}
	

	private void normalizeData()
	{
		normalizedData = new HashMap<String, Double>();
		
		double totalAngle = 0;
		double ratio = 1;
		
		int normalizerRatio = 100;
		
		for(Double d : data.values())
		{
			total += d*normalizerRatio;
		}
				
		for(Entry<String, Double> e : data.entrySet())
		{	
			totalAngle += (int) Math.round(e.getValue() * 360 * normalizerRatio / total);
		}

		//System.out.println(totalAngle);
		
		if(totalAngle != 360)
		{
			ratio = 360.0/totalAngle;
			//System.out.println(ratio);
		}
		
		for(Entry<String, Double> e : data.entrySet())
		{	
			normalizedData.put(e.getKey(), e.getValue()*ratio*normalizerRatio);
		}
		
		//System.out.println(normalizedData);
		
	}

	private void computeArcs()
	{
		
		chartContainer = new Rectangle2D.Double((this.wExt-this.dChart)/2, (this.hExt-this.dChart)/2, this.dChart, this.dChart);
		arcs = new HashMap<String, Arc2D.Double>();
		int offset = this.startOffset;
		
		for(Entry<String, Double> e : normalizedData.entrySet())
		{	
			int angle = (int) Math.round(e.getValue() * 360 / total);
					
			//Arc2D.Double arc = new Arc2D.Double(chartContainer, 0, 45, Arc2D.PIE);
			Arc2D.Double arc = new Arc2D.Double(chartContainer, offset, angle, Arc2D.PIE);
			
			//Drawing slice
			arcs.put(e.getKey(), arc);

			offset += angle;	
		}
	}

	/**
	 * Roughly set initial pos of labels
	 */
	private void computeLabels()
	{
		for(Entry<String, ChartLabel> e : labels.entrySet())
		{
			if(!e.getValue().userMoved())
			{
				Arc2D.Double arc = arcs.get(e.getKey());
				Point2D p = Utils.getCenterArc(arc);
				e.getValue().setLocation(new Point((int) p.getX(), (int) p.getY()));
			}
			else
			{
				e.getValue().setLocation(e.getValue().getUserLocation());
			}
		}
		
	}

	public List<Color> getBaseColors()
	{
		return baseColors;
	}

	public void setBaseColors(List<Color> colors)
	{
		this.baseColors = colors;
	}
	
	public void setFrameSize(int w, int h)
	{
		wExt = w;
		hExt = h;
	}
	
	public void setChartSize(int w, int h)
	{
		dChart = w;
		dChart = h;
	}
	
	public void render()
	{
		BufferedImage image = new BufferedImage(this.wExt, this.hExt, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2D = image.createGraphics();
		g2D.setBackground(Color.WHITE);
		g2D.fillRect(0, 0, wExt, hExt);
		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		double total = 0;
		int offset = 0;
		int nb = 0;
		
		for(Double d : normalizedData.values())
		{
			total += d;
		}
		
		Rectangle2D.Double chartContainer = new Rectangle2D.Double((this.wExt-this.dChart)/2, (this.hExt-this.dChart)/2, this.dChart, this.dChart);
		
		ArrayList<Arc2D.Double> arcs = new ArrayList<Arc2D.Double>();
		
		for(Entry<String, Double> e : normalizedData.entrySet())
		{			
			int angle = (int) Math.round(e.getValue() * 360 / total);
		
			//Arc2D.Double arc = new Arc2D.Double(chartContainer, 0, 45, Arc2D.PIE);
			Arc2D.Double arc = new Arc2D.Double(chartContainer, offset, angle, Arc2D.PIE);
			
			//Drawing black line before
			g2D.setColor(Color.BLACK);
			Rectangle2D rec = arc.getBounds2D();
			
			double x;
			double y;
			
			int bisec = (offset + angle) - angle/2;
			
			if(bisec >= 0 && bisec < 90)
			{
				x = rec.getMinX();
				y = rec.getMinY();
			}
			else if(bisec >= 90 && bisec < 180)
			{
				x = rec.getMaxX();
				y = rec.getMinY();
			}
			else if(bisec >= 180 && bisec < 270)
			{
				x = rec.getMaxX();
				y = rec.getMaxY();
			}
			else
			{
				x = rec.getMinX();
				y = rec.getMaxY();
			}
			
			g2D.drawLine((int) chartContainer.getCenterX(), (int)chartContainer.getCenterY(), (int) x, (int) y); 
			
			//Drawing slice
			arcs.add(arc);
			
			Color base = baseColors.get(nb);
			
			Color gradient = base.darker().darker();
			
			g2D.setPaint(new GradientPaint((float) arc.getMaxX(), (float) arc.getMaxY(), gradient, (float) arc.getMinX(), (float) arc.getMinY(), base));
			
			g2D.fill(arc);
			
			nb++;
			offset += angle;
		}
		
		g2D.setColor(Color.WHITE);
		
		for(Arc2D.Double arc : arcs)
		{
			g2D.drawLine((int) Math.round(chartContainer.getCenterX()), (int) Math.round(chartContainer.getCenterY()), (int) Math.round(arc.getEndPoint().getX()), (int) Math.round(arc.getEndPoint().getY()));
		}
		
		
		
		try
		{
			ImageIO.write(image, "png", new File("output.png"));
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	@Override
	public void componentMoved(ComponentEvent e)
	{
		this.repaint();		
	}
	
	@Override
	public void componentResized(ComponentEvent e)
	{
		
		
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		
		
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{}

}





