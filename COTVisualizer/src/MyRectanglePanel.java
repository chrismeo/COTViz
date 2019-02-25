
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Console;

import javax.swing.JPanel;

public class MyRectanglePanel extends JPanel{
   public static int space_right = 80;
   public static int space_buttom = 20;
   public static int width, height;
   public static String datum = "";
   static boolean drawgraph = false;
   public static int factor = 1;
   public static int factorm = 1;
  
   
   @Override
   public void paintComponent(Graphics g){
      super.paintComponent(g);
      
	  height= getHeight(); 
	  width = getWidth();
	  g.setColor(Color.RED);
	  
	  g.drawString("Commercials", 10, 10);
	  g.setColor(Color.BLUE);
	  g.drawString("Large Traders", 10, 30);
	  g.setColor(Color.GREEN);
	  g.drawString("Small Traders", 10, 50);
	  g.setColor(Color.GRAY);
      
      Font font_small = new Font("Verdana",Font.PLAIN, 10);
	  Font font = new Font("Verdana", Font.BOLD, 20);
      g.setFont(font);
	  g.drawString(COTVisualizer.selected, 200, 20);
	  
	  g.setColor(Color.BLACK);
	  g.drawLine(width-space_right, 0, width-space_right,height);
	  
	  g.setColor(Color.LIGHT_GRAY);
	  g.fillRect(width-space_right+1, 0, width, height);
	 
	  
	  //Fadenkreuz
      if((COTVisualizer.drawfadenkreuz) && (drawgraph))
      {
    	 
         g.setColor(Color.YELLOW);
         if (COTVisualizer.fadenkreuzx > (width-space_right)){
        	 COTVisualizer.fadenkreuzx = width-space_right;
         }
         g.drawLine(COTVisualizer.fadenkreuzx, 0, COTVisualizer.fadenkreuzx, COTVisualizer.panelpaint.getHeight());
         g.drawLine(0, COTVisualizer.fadenkreuzy, COTVisualizer.panelpaint.getWidth()-space_right, COTVisualizer.fadenkreuzy);
         g.fillRect(COTVisualizer.fadenkreuzx-20, COTVisualizer.panelpaint.getHeight()-20, 40, 20);
         
         
         g.fillRect(COTVisualizer.panelpaint.getWidth()-space_right+1, COTVisualizer.fadenkreuzy-10, 60, 20);
         g.setFont(font_small);
         g.setColor(Color.CYAN);
         int ywert=-(COTVisualizer.fadenkreuzy-height/2)*1000;        
         g.drawString(String.valueOf(ywert),COTVisualizer.panelpaint.getWidth()-space_right+9 ,COTVisualizer.fadenkreuzy+5 );
         
         int index_datum =(width-COTVisualizer.fadenkreuzx-space_right)/10;
        
         /*String*/ datum = COTVisualizer.dates[index_datum];//"11/11";
         
         g.drawString(datum, COTVisualizer.fadenkreuzx-15, height-5);
         
         //g.drawString("Datum: ", COTVisualizer.fadenkreuzx, COTVisualizer.fadenkreuzy+10);
         //g.drawString("Commercials: ", COTVisualizer.fadenkreuzx, COTVisualizer.fadenkreuzy+25);
         //g.drawString("Large Traders: ", COTVisualizer.fadenkreuzx, COTVisualizer.fadenkreuzy+40);
         //g.drawString("Small Traders: ", COTVisualizer.fadenkreuzx, COTVisualizer.fadenkreuzy+55);
         
      }
	  
	  //DRAW GRID
	  if(COTVisualizer.grid)
	  {
	     g.setColor(Color.LIGHT_GRAY);
         g.drawLine(0,height/2,width-space_right-1,height/2);
         g.drawLine(0,height/2+1,width-space_right-1,height/2+1);
         g.drawLine(0,height/2-1,width-space_right-1,height/2-1);
         g.setColor(Color.LIGHT_GRAY);
         int x= width-space_right-1;
         while(x>0)
         {
    	    g.drawLine(x, 0, x, height);
    	    x-=5*COTVisualizer.delta_x;
         }
      
         int y = height/2;
         while(y>0)
         {
    	   g.drawLine(0, y, width-space_right-1, y);
    	   g.drawLine(0, height/2+(height/2-y), width-space_right-1, height/2+(height/2-y));
    	   y-=10*10;
         }
	  }
	  
      if(drawgraph)
      {
    	 g.setFont(font_small);
    	 g.setColor(Color.RED);
    	 g.drawString(String.valueOf(COTVisualizer.commercials[0]), 100, 10);
    	 g.setColor(Color.BLUE);
    	 g.drawString(String.valueOf(COTVisualizer.largetraders[0]), 100, 30);
    	 g.setColor(Color.GREEN);
    	 g.drawString(String.valueOf(COTVisualizer.smalltraders[0]), 100, 50);
    	 if(COTVisualizer.plusevent) factor = 10;
    	 if(COTVisualizer.minusevent) factorm = 5;
         if(!COTVisualizer.plusevent)  factor = 1;
         if(!COTVisualizer.minusevent)  factorm = 1;
         int start_x = width-space_right-1+COTVisualizer.dx;
         int pos=0;
         while((start_x>COTVisualizer.delta_x) &&(pos<COTVisualizer.commercials.length-1))
         {
        	 if(start_x<= width-space_right){
        	//DRAW COMMERCIALS
            g.setColor(Color.RED);
            g.drawLine(start_x-COTVisualizer.delta_x/*start_x-10+COTVisualizer.dx*/, height/2-(factor*COTVisualizer.commercials[pos+1])/factorm/(1000+2*COTVisualizer.dy), 
            		   start_x/*start_x+COTVisualizer.dx*/, height/2-(factor*COTVisualizer.commercials[pos])/factorm/(1000+2*COTVisualizer.dy));
    	    //DRAW LARGETRADERS
            g.setColor(Color.BLUE);
    	    g.drawLine(start_x-COTVisualizer.delta_x/*+COTVisualizer.dx*/, height/2-(factor*COTVisualizer.largetraders[pos+1])/factorm/(1000+2*COTVisualizer.dy), 
    	    		   start_x/*+COTVisualizer.dx*/, height/2-(factor*COTVisualizer.largetraders[pos])/factorm/(1000+2*COTVisualizer.dy));
    	    //DRAW SMALLTRADERS
            g.setColor(Color.GREEN);
    	    g.drawLine(start_x-10/*+COTVisualizer.dx*/, height/2-(factor*COTVisualizer.smalltraders[pos+1])/factorm/(1000+2*COTVisualizer.dy), 
    	    		   start_x/*+COTVisualizer.dx*/, height/2-(factor*COTVisualizer.smalltraders[pos])/factorm/(1000+2*COTVisualizer.dy));

    	    //DRAW X COORDINATES
    	    g.setColor(Color.ORANGE);
    	    g.setFont(font_small);
    	    if(pos%5 ==0 ){
    	    	g.drawString(COTVisualizer.dates[pos], start_x-15/*+COTVisualizer.dx*/, height-10);
    	    	g.drawLine(start_x/*+COTVisualizer.dx*/, height, start_x/*+COTVisualizer.dx*/, height-10);
    	      }
        	 }
    	    start_x-=COTVisualizer.delta_x;
    	    //pos-=1;
    	    pos+=1;
         }
         
         //DRAW Y COORDINATES
         g.setColor(Color.MAGENTA);
         int y1 = (factor * 20* 1000/(1000+2*COTVisualizer.dy))/factorm;
         
         g.drawLine(width+10-space_right, height/2, width-space_right, height/2);
         g.drawString("0", width+10-space_right, height/2+5);
         while (y1<height/2){
        	 g.drawLine(width+5-space_right, height/2+y1, width-space_right, height/2+y1);
        	 String b = String.valueOf((factor * y1*1000)/factorm);
        	 String b2 = "-"+b;
        	 g.drawString(b2, width+10-space_right, height/2+5+y1);
        	 g.drawLine(width+5-space_right, height/2-y1, width-space_right, height/2-y1);
        	 g.drawString(b, width+10-space_right, height/2+5-y1);
        	 y1+=(factor * 20* 1000/(1000+2*COTVisualizer.dy))/factorm;
        	 
         }
         
         drawgraph = false;
      }
   }		
}

