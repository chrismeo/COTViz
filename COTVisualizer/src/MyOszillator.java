import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

//import javafx.scene.text.Font;

public class MyOszillator extends JPanel{
	int space_right = 80;
	public static boolean drawoszillator = false;
	@Override
	public void paintComponent(Graphics g)
	{
	      super.paintComponent(g);
	      Font font_small = new Font("Verdana",Font.PLAIN, 10);
	      Point point1 = new Point(getWidth()/2, 0);
	      Point point2 = new Point(getWidth()/2, getHeight());
	      //Point point1 = new Point(10, 10);
	      //Point point2 = new Point(getWidth() - 10, getHeight() - 10);
	      //final GradientPaint gp = new GradientPaint(point1, Color.YELLOW, point2, new Color(255, 225, 100), true);
	      Color color1 = Color.LIGHT_GRAY;
	      Color color2 = Color.BLACK;
	      final GradientPaint gp = new GradientPaint(point1, color1, point2, color2, true);
	      final Graphics2D g2 = (Graphics2D) g;
	      g2.setPaint(gp);
	      g.fillRect(0, 0, getWidth(), getHeight());
	                             
	      g.setColor(Color.LIGHT_GRAY);
		  g.fillRect(getWidth()-space_right+1, 0, getWidth(), getHeight());
	      	      
	      g.setColor(Color.CYAN);
	      Font font = new Font("Verdana", Font.BOLD, 20);
	      g.setFont(font);
	      g.drawString("Oszillator-26", 10, 20);
	      
	      g.setColor(Color.BLACK);
	      g.drawLine(getWidth()-space_right, 0, getWidth()-space_right, getHeight());
	      
	      
	      
	      /*int y= (getHeight()/2-20)/4;
	      g.drawLine(0, getHeight()/2, getWidth()-70, getHeight()/2);
	      g.drawString("0",getWidth()-65 ,getHeight()/2+3);
	      
	      for(int i=1;i<5;i++)
	      {
	    	 g.drawLine(0, getHeight()/2+y*i, getWidth()-space_right-1, getHeight()/2+y*i);
	    	 g.drawLine(0, getHeight()/2-y*i, getWidth()-space_right-1, getHeight()/2-y*i);
	    	 int a = 25*i;
	    	 String aa = String.valueOf(a);
	    	 g.drawString(aa,getWidth()-65 ,getHeight()/2+y*i);
	    	 int b= -25*i;
	    	 String bb = String.valueOf(b);
	    	 g.drawString(bb,getWidth()-65 ,getHeight()/2-y*i);
	      }*/
	      
	      if(drawoszillator){
	    	  g.setColor(Color.LIGHT_GRAY);
		      g.drawLine(0, getHeight()/2, getWidth()-MyRectanglePanel.space_right, getHeight()/2);
		      g.drawLine(0, getHeight()/2-25, getWidth()-MyRectanglePanel.space_right, getHeight()/2-25);
		      g.drawLine(0, getHeight()/2-50, getWidth()-MyRectanglePanel.space_right, getHeight()/2-50);
		      g.drawLine(0, getHeight()/2+25, getWidth()-MyRectanglePanel.space_right, getHeight()/2+25);
		      g.drawLine(0, getHeight()/2+50, getWidth()-MyRectanglePanel.space_right, getHeight()/2+50);
		      
		      g.setColor(Color.GREEN);
		      g.drawLine(getWidth()-MyRectanglePanel.space_right, getHeight()/2, getWidth()-MyRectanglePanel.space_right+5, getHeight()/2);
		      g.drawLine(getWidth()-MyRectanglePanel.space_right, getHeight()/2-25, getWidth()-MyRectanglePanel.space_right+5, getHeight()/2-25);
		      g.drawLine(getWidth()-MyRectanglePanel.space_right, getHeight()/2-50, getWidth()-MyRectanglePanel.space_right+5, getHeight()/2-50);
		      g.drawLine(getWidth()-MyRectanglePanel.space_right, getHeight()/2+25, getWidth()-MyRectanglePanel.space_right+5, getHeight()/2+25);
		      g.drawLine(getWidth()-MyRectanglePanel.space_right, getHeight()/2+50, getWidth()-MyRectanglePanel.space_right+5, getHeight()/2+50);
		      g.setFont(font_small);
		      g.drawString("0",getWidth()-MyRectanglePanel.space_right+10 , getHeight()/2+5+50);
		      g.drawString("25",getWidth()-MyRectanglePanel.space_right+10 , getHeight()/2+5+25);
		      g.drawString("50",getWidth()-MyRectanglePanel.space_right+10 , getHeight()/2+5);
		      g.drawString("75",getWidth()-MyRectanglePanel.space_right+10 , getHeight()/2+5-25);
		      g.drawString("100",getWidth()-MyRectanglePanel.space_right+10 , getHeight()/2+5-50);
	    	  
	    	  g.setColor(Color.GREEN);
	    	  int oszi_x= getWidth()-MyRectanglePanel.space_right+COTVisualizer.dx;
	    	  /*if(oszi_x >MyRectanglePanel.width-MyRectanglePanel.space_right){
	    		  oszi_x=MyRectanglePanel.width-MyRectanglePanel.space_right;
	    	  }*/
	    	  
	          for(int j=0;j<COTVisualizer.oszillator26.length-1/*100*/;j++){
	        	  if(oszi_x-j*5 <=MyRectanglePanel.width-MyRectanglePanel.space_right){
	    	     g.drawLine(oszi_x-j*5/*getWidth()-MyRectanglePanel.space_right-j*5+COTVisualizer.dx*/, 
	    			        getHeight()/2+50-COTVisualizer.oszillator26[j],
	    			        oszi_x-(j+1)*5/*getWidth()-MyRectanglePanel.space_right-(j+1)*5+COTVisualizer.dx*/, 
	    			        getHeight()/2+50-COTVisualizer.oszillator26[j+1]);
	        	  }
	          }
	        drawoszillator = false;
	      }
	   } 
	}
