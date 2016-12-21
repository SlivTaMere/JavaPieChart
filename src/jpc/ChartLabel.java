package jpc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class ChartLabel extends JTextPane
{

	private ChartLabel instance;
	private boolean userMoved = false;
	private Point mousePressLocation;
	private Point userLocation;
	
	public ChartLabel(String s)
	{
		super();
		this.setText(s);
		instance = this;
		
		this.setDisabledTextColor(Color.BLACK);
		this.setEnabled(false);
		
		this.setFont(Font.decode("Calibri-12"));

		StyledDocument doc = this.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		this.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() >= 2 )
				{
					instance.setEnabled(true);
					instance.setForeground(Color.RED);
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				mousePressLocation = e.getPoint();
			}
			
		});
		
		this.addFocusListener(new FocusAdapter()
		{

			@Override
			public void focusLost(FocusEvent e)
			{
				instance.setEnabled(false);
				instance.setForeground(Color.BLACK);
			}
			
		});
		
		this.addKeyListener(new KeyAdapter()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					instance.setEnabled(false);
					instance.setForeground(Color.BLACK);
				}
			}
	
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter()
		{

			@Override
			public void mouseDragged(MouseEvent e)
			{
				if(!instance.isEnabled())
				{
					userMoved = true;
					instance.setLocation(e.getPoint().x + instance.getLocation().x - mousePressLocation.x, e.getPoint().y + instance.getLocation().y - mousePressLocation.y);
					instance.userLocation = instance.getLocation();
				}
			}

			
		});
	}

	public boolean userMoved()
	{
		return userMoved ;
	}

	public Point getUserLocation()
	{
		return userLocation;
	}

}
