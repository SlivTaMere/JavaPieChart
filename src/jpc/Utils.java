package jpc;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Utils
{
	
	static public Point2D getCenterArc(Arc2D.Double arc)
	{
		double bisecAngle = (arc.getAngleStart() + arc.getAngleExtent()) - arc.getAngleExtent()/2;
		
		Arc2D.Double bisec = new Arc2D.Double(arc.getFrame(), bisecAngle, 1, Arc2D.OPEN);
			
		return bisec.getStartPoint();
	}
	
	static public Point2D getPointFromCenterArc(Arc2D.Double arc, double ratioExtend)
	{
		
		AffineTransform at = AffineTransform.getScaleInstance(ratioExtend, ratioExtend);
		
		Rectangle2D r = at.createTransformedShape(arc.getFrame()).getBounds2D();
	
		double bisecAngle = (arc.getAngleStart() + arc.getAngleExtent()) - arc.getAngleExtent()/2;
		
		Arc2D.Double bisec = new Arc2D.Double(r, bisecAngle, 1, Arc2D.OPEN);
		
		return bisec.getStartPoint();
	}
}
