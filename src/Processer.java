
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Processer {
    int blackRGB = Color.black.getRGB();
    int white = Color.white.getRGB();
    
	public Processer(BufferedImage readyImage) throws FileNotFoundException, UnsupportedEncodingException{
               System.out.println("prasideda Processer");
               
		BufferedImage Image = readyImage;     
                BufferedImage shortLinesImage = copyImage(Image);
                BufferedImage VerticalImage = copyImage(Image);
                BufferedImage HorizontalImage = copyImage(Image);
                BufferedImage leaningRightImage = copyImage(Image);
                BufferedImage leaningLeftImage = copyImage(Image);                
               
                Contour initial = new Contour();
                
                
                findLongLines(VerticalImage, HorizontalImage, leaningRightImage, leaningLeftImage, initial);
                findShortLines(shortLinesImage, initial);
                LinkedList<Contour> contours = partition(initial);                
                orderLines(contours);    
                removeInacurateContours(contours);               
                contours = orderContours(contours);               
                outputContours(contours);
                
                System.out.println("Done");
                 }
        
        
        
        
    public static void invert(BufferedImage image)
    {
        for(int x = 0; x < image.getWidth(); x++)
            for(int y = 0; y < image.getHeight(); y++)
            {
            if(image.getRGB(x,y) == Color.black.getRGB())
            {
            image.setRGB(x,y,Color.white.getRGB());
            }
            else
            {
            image.setRGB(x,y,Color.black.getRGB());
            }
            }
    }
    
    //--------------------------------------------------------------------------------------------------\\
    
    public static BufferedImage copyImage(BufferedImage source)
    {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    //--------------------------------------------------------------------------------------------------\\
    
    public void findLongLines(BufferedImage VerticalImage, BufferedImage HorizontalImage, BufferedImage leaningRightImage, BufferedImage leaningLeftImage, Contour initial)
    {
        for(int y = 0; y<VerticalImage.getHeight(); y++)
        for(int x =0; x<VerticalImage.getWidth(); x++)
        {
            if(VerticalImage.getRGB(x,y) == blackRGB)
            {   if(y+1 < VerticalImage.getHeight())
                if(VerticalImage.getRGB(x,y+1) == blackRGB)
                {Point beginning = new Point(x,y);                
                    Point end = findVerticalLines(VerticalImage, beginning);
                    if(Math.abs(end.y-beginning.y) >= 2)
                    { 
                    Point[] toAdd = new Point[2];
                    toAdd[0] = beginning;
                    toAdd[1] = end;                    
                    Line line = new Line(toAdd);                   
                    initial.add(line);
                    }
                }
            }
        }


   for(int y = 0; y<HorizontalImage.getHeight(); y++)
        for(int x =0; x<HorizontalImage.getWidth(); x++)
    {
        if(HorizontalImage.getRGB(x,y) == blackRGB)
        {   if(x+1 < HorizontalImage.getWidth())
            if(HorizontalImage.getRGB(x+1,y) == blackRGB)
            {Point beginning = new Point(x,y);             
                Point end = findHorizontalLines(HorizontalImage, beginning);             
                if(Math.abs(end.x-beginning.x) >= 2)
                { Point[] toAdd = new Point[2];
                    toAdd[0] = beginning;
                    toAdd[1] = end;                    
                    Line line = new Line(toAdd);
                    initial.add(line);
                }
            }
        }
    }

    for(int y = 0; y<leaningRightImage.getHeight(); y++)
         for(int x =0; x<leaningRightImage.getWidth(); x++)
    {
        if(leaningRightImage.getRGB(x,y) == blackRGB)
        {   if(x-1 > 0 && y+1 < leaningRightImage.getHeight())
            if(leaningRightImage.getRGB(x-1,y+1) == blackRGB)
            {Point beginning = new Point(x,y);
                Point end = findLinesLeaningRight(leaningRightImage, beginning);
                if(Math.abs(end.x - beginning.x) >= 2)
                { 
                Point[] toAdd = new Point[2];
                toAdd[0] = beginning;
                toAdd[1] = end;
                Line line = new Line(toAdd);
                initial.add(line);
                }
            }
        }
    }
    
    for(int y = 0; y<leaningLeftImage.getHeight(); y++)
        for(int x =0; x<leaningLeftImage.getWidth(); x++)
    {
        if(leaningLeftImage.getRGB(x,y) == blackRGB)
        {   if(x+1 < leaningLeftImage.getWidth() && y+1 < leaningLeftImage.getHeight())
            if(leaningLeftImage.getRGB(x+1,y+1) == blackRGB)
            {Point beginning = new Point(x,y);
            
                Point end = findLinesLeaningLeft(leaningLeftImage, beginning);
                if(Math.abs(end.x - beginning.x) >= 2)
                {
                Point[] toAdd = new Point[2];
                toAdd[0] = beginning;
                toAdd[1] = end;                
                Line line = new Line(toAdd);
                initial.add(line);
                }
            }
        }
    }
                          
    }
    
    public static Point findVerticalLines(BufferedImage image, Point point)
    {

        int black = Color.black.getRGB();
        int white = Color.white.getRGB();
        int x = (int)point.getX();
        int y = (int)point.getY();
        try{
        while(image.getRGB(x, y + 1) == black && y + 1 != image.getHeight())
        {
         //   if( y + 1 != image.getHeight())
           // {
                y=y+1;
                image.setRGB(x,y,white);

        }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e)
        {
            image.setRGB(x,y,black);
            return new Point(x,y); 
        }
            image.setRGB(x,y,black);
            return new Point(x,y); 
    }
      public static Point findHorizontalLines(BufferedImage image, Point point)
    {

        int black = Color.black.getRGB();
        int white = Color.white.getRGB();
        int x = (int)point.getX();
        int y = (int)point.getY();
        try{
        while(image.getRGB(x+1, y) == black && x + 1 != image.getWidth())
        {
            x=x+1;
            image.setRGB(x,y,white);

        }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e)
        {
            image.setRGB(x,y,black);
            return new Point(x,y); 
        }
            image.setRGB(x,y,black);
            return new Point(x,y); 
    }
      public static Point findLinesLeaningRight(BufferedImage image, Point point)// pasvire "/"
    {

        int black = Color.black.getRGB();
        int white = Color.white.getRGB();
        int x = (int)point.getX();
        int y = (int)point.getY();
        try{
        while(image.getRGB(x-1, y+1) == black)
        {
           if(x - 1 >= 0 && y + 1 != image.getHeight()) 
           {
            x = x-1;
            y = y+1;
            image.setRGB(x, y, white);

           }
        }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e)
        {
            image.setRGB(x,y,black);
            return new Point(x,y); 
        }
            image.setRGB(x,y,black);
            return new Point(x,y); 
    }
      public static Point findLinesLeaningLeft(BufferedImage image, Point point)// pasvire "\"
    {

        int black = Color.black.getRGB();
        int white = Color.white.getRGB();
        int x = (int)point.getX();
        int y = (int)point.getY();
        try{
        while(image.getRGB(x+1, y+1) == black)
        {
           if(x + 1 != image.getWidth() && y + 1 != image.getHeight())
           {
            x = x+1;
            y = y+1;
            image.setRGB(x, y, white);

           }
        }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e)
        {image.setRGB(x,y,black);
            return new Point(x,y); 
        }
            image.setRGB(x,y,black);
            return new Point(x,y); 
    }
      
      //--------------------------------------------------------------------------------------------------\\
     
      public void findShortLines(BufferedImage shortLinesImage, Contour initial )
      {
           for(int y = 0; y < shortLinesImage.getHeight(); y++)
        for(int x = 0; x < shortLinesImage.getWidth(); x++ )
        {
            if(shortLinesImage.getRGB(x,y) == blackRGB)
                if(y+1 < shortLinesImage.getHeight())
                if(shortLinesImage.getRGB(x,y+1) == blackRGB)
                {
                Point start = new Point(x,y);
                Point end = findVerticalLines(shortLinesImage, start);
                }
        }

     for(int y = 0; y < shortLinesImage.getHeight(); y++)
        for(int x = 0; x < shortLinesImage.getWidth(); x++ )
        {
            if(shortLinesImage.getRGB(x,y) == blackRGB)
                if(x+1 < shortLinesImage.getWidth())
                if(shortLinesImage.getRGB(x+1,y) == blackRGB)
                {
                    Point start = new Point(x,y);
                    Point end = findHorizontalLines(shortLinesImage, start);
                }
        }

     for(int y = 0; y < shortLinesImage.getHeight(); y++)
        for(int x = 0; x < shortLinesImage.getWidth(); x++ )
        {
            if(shortLinesImage.getRGB(x,y) == blackRGB)
                if(x-1 > 0 && y+1 < shortLinesImage.getHeight())
                if(shortLinesImage.getRGB(x-1,y+1) == blackRGB)
                {
                    Point start = new Point(x,y);
                    Point end = findLinesLeaningRight(shortLinesImage, start);
                }
        }

      for(int y = 0; y < shortLinesImage.getHeight(); y++)
        for(int x = 0; x < shortLinesImage.getWidth(); x++ )
        {
            if(shortLinesImage.getRGB(x,y) == blackRGB)
                if(x+1 < shortLinesImage.getWidth() && y+1 < shortLinesImage.getHeight())
                if(shortLinesImage.getRGB(x+1,y+1) == blackRGB)
                {
                    Point start = new Point(x,y);
                    Point end = findLinesLeaningLeft(shortLinesImage, start);
                }

        }
          
          
          
          
          
          
          
             for(int y = 0; y < shortLinesImage.getHeight(); y++)
        for(int x = 0; x < shortLinesImage.getWidth(); x++)
        {
            if(shortLinesImage.getRGB(x,y) == blackRGB)
                if(y+1 < shortLinesImage.getHeight())
                if(shortLinesImage.getRGB(x,y+1) == blackRGB)
                {
                    //shortLinesImage.setRGB(x,y, white);
                    Point start = new Point(x,y);
                    Point end = findVerticalLinesShort(shortLinesImage, start);
                   // shortLinesImage.setRGB(end.x,end.y, white);
                    Point[] toAdd = new Point[2];
                    toAdd[0] = start;
                    toAdd[1] = end;
                    //initialList.add(toAdd);
                    Line line = new Line(toAdd);
                    initial.add(line);
                    //System.out.println("trumpa vetikali prideta");
                }
        }


         for(int y = 0; y < shortLinesImage.getHeight(); y++)
        for(int x = 0; x < shortLinesImage.getWidth(); x++)
        {
            if(shortLinesImage.getRGB(x,y) == blackRGB)
                if(x+1 < shortLinesImage.getWidth())
                if(shortLinesImage.getRGB(x+1,y) == blackRGB)
                {
                    //shortLinesImage.setRGB(x,y, white);
                    Point start = new Point(x,y);
                    Point end = findHorizontalLinesShort(shortLinesImage, start);
                   // shortLinesImage.setRGB(end.x,end.y, white);
                    Point[] toAdd = new Point[2];
                    toAdd[0] = start;
                    toAdd[1] = end;
                    //initialList.add(toAdd);
                    Line line = new Line(toAdd);
                    initial.add(line);
                }
        }

              for(int y = 0; y < shortLinesImage.getHeight(); y++)
        for(int x = 0; x < shortLinesImage.getWidth(); x++)
        {
            if(shortLinesImage.getRGB(x,y) == blackRGB)
                if(x-1 > 0 && y+1 < shortLinesImage.getHeight())
                if(shortLinesImage.getRGB(x-1,y+1) == blackRGB)
                {
                   // shortLinesImage.setRGB(x,y, white);
                    Point start = new Point(x,y);
                    Point end = findLinesLeaningRightShort(shortLinesImage, start);
                   // shortLinesImage.setRGB(end.x,end.y, white);
                    Point[] toAdd = new Point[2];
                    toAdd[0] = start;
                    toAdd[1] = end;
                    //initialList.add(toAdd);
                    Line line = new Line(toAdd);
                    initial.add(line);
                }
        }

                   for(int y = 0; y < shortLinesImage.getHeight(); y++)
        for(int x = 0; x < shortLinesImage.getWidth(); x++)
        {
            if(shortLinesImage.getRGB(x,y) == blackRGB)
                if(x+1 < shortLinesImage.getWidth() && y+1 < shortLinesImage.getHeight())
                if(shortLinesImage.getRGB(x+1,y+1) == blackRGB)
                {
                    //shortLinesImage.setRGB(x,y, white);
                    Point start = new Point(x,y);
                    Point end = findLinesLeaningLeftShort(shortLinesImage, start);
                    //shortLinesImage.setRGB(end.x,end.y, white);
                    Point[] toAdd = new Point[2];
                    toAdd[0] = start;
                    toAdd[1] = end;
                    //initialList.add(toAdd);
                    Line line = new Line(toAdd);
                    initial.add(line);
                }
        }

      }

    public static Point findVerticalLinesShort(BufferedImage image, Point point)
    {

        int black = Color.black.getRGB();
        int white = Color.white.getRGB();
        int x = (int)point.getX();
        int y = (int)point.getY();
        try{
        while(image.getRGB(x, y + 1) == black)
        {
            if( y + 1 != image.getHeight())
            {
            y=y+1;
            }
        }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e)
        {
            return new Point(x,y); 
        }
             return new Point(x,y); 
    }
    public static Point findHorizontalLinesShort(BufferedImage image, Point point)
    {

        int black = Color.black.getRGB();
        int white = Color.white.getRGB();
        int x = (int)point.getX();
        int y = (int)point.getY();
        try{
        while(image.getRGB(x+1, y) == black)
        {
            if( x + 1 != image.getWidth())
            { 
            x=x+1;
            }
        }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e)
        {
            return new Point(x,y); 
        }
            return new Point(x,y); 
    }        

    public static Point findLinesLeaningRightShort(BufferedImage image, Point point)// pasvire "/"
    {

        int black = Color.black.getRGB();
        int white = Color.white.getRGB();
        int x = (int)point.getX();
        int y = (int)point.getY();
        try{
        while(image.getRGB(x-1, y+1) == black)
        {
           if(x - 1 >= 0 && y + 1 != image.getHeight())
           {
            x = x-1;
            y = y+1;
           }
        }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e)
        {
            return new Point(x,y); 
        }
            return new Point(x,y); 
    } 
        
    public static Point findLinesLeaningLeftShort(BufferedImage image, Point point)// pasvire "\"
    {

        int black = Color.black.getRGB();
        int white = Color.white.getRGB();
        int x = (int)point.getX();
        int y = (int)point.getY();
        try{
        while(image.getRGB(x+1, y+1) == black)
        {
           if(x + 1 != image.getWidth() && y + 1 != image.getHeight()) 
           {
            x = x+1;
            y = y+1;
           }
        }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e)
        {
            return new Point(x,y); 
        }
            return new Point(x,y); 
    }     

    //--------------------------------------------------------------------------------------------------\\
        
    public static void colorVerticalLines(BufferedImage image, Point[] vector)
    {
        Point start = vector[0];
        Point end = vector[1];
        int black = Color.black.getRGB();
        if(start.y > end.y)
        {
            Point tmp = start;
            start=end;
            end=tmp;
        }
        for (int i = start.y; i <=end.y; i++)
        {
            image.setRGB(start.x, i, black);
        }

    }

    public static void colorHorizontalLines(BufferedImage image, Point[] vector)
    {
        Point start = vector[0];
        Point end = vector[1];
        int black = Color.black.getRGB();

        for (int i = start.x; i <=end.x; i++)
        {
            image.setRGB(i, start.y, black);
        }

    }

    public static void colorLeaningToRight(BufferedImage image, Point[] vector)
    {
        // / x.start > x.end; y.start < y.end
        Point start = vector[0];
        Point end = vector[1];
        int y = start.y;
        int x = start.x;
          int black = Color.black.getRGB();
      while (x != end.x && y != end.y)
      {
          image.setRGB(x,y, black);
          x--;
          y++;
      }
        image.setRGB(end.x, end.y, black);
    }

    public static void colorLeaningToLeft(BufferedImage image, Point[] vector)
    {
        // \ x.start < x.end; y.start < y.end
        Point start = vector[0];
        Point end = vector[1];
        int y = start.y;
        int x = start.x;
        int black = Color.black.getRGB();
      while (x != end.x && y != end.y)
      {
         image.setRGB(x,y, black);
         x++;
         y++;
      }
        image.setRGB(end.x, end.y, black);  
      }

    //--------------------------------------------------------------------------------------------------\\
    
    void orderLines(LinkedList<Contour> contours)
    {
    int iterationCounter = 0;
    while (true)
    {
        Contour separate = new Contour();
        separate.add(contours.get(0).beginContourFrom());
       
        for(int i = 0; i < separate.size(); i++)
        {
        int counter = 0;
        while(contours.get(0).size() > counter)
        {
        //System.out.println("Ending coordinates as they are: "+separate.get(i).endingX+", "+separate.get(i).endingY+"; compared to");
        
            if(separate.get(i).endingPointConnects(contours.get(0).get(counter).getStartingCoordinates()))
            {   //System.out.println("goes into if, supposed to add lines");
                separate.add(contours.get(0).get(counter));
                contours.get(0).remove(counter);
                break;
            }  
            if(separate.get(i).endingPointConnects(contours.get(0).get(counter).getEndingCoordinates()))
            {   //System.out.println("goes into if, supposed to add lines");
                contours.get(0).get(counter).changeDirection();
                separate.add(contours.get(0).get(counter));
                contours.get(0).remove(counter);
                break;
            }
            counter++;
        }        
        }
        contours.add(separate);
        if(contours.get(0).size() == 0)
        {
            contours.remove(0);
            iterationCounter++;
        }
        if(iterationCounter == contours.size())
        {
            break;
        }
       
    }
    //System.out.println("number of contours "+contours.size());
    }

    void correctPartitioningMistakes(LinkedList<Contour> contours)
    {
       for(int i = 0; i < contours.size(); i++)
       {
           for(int j = 0; j<contours.size(); j++)
           {
               if(i!=j)
               {
               COMPARINGLINES:
              for(int l = 0; l < contours.get(i).size(); l++)
                  for(int k = 0; k < contours.get(j).size(); k++)
                  { 
                   if(contours.get(i).get(l).getStartingCoordinates().equals(contours.get(j).get(k).getStartingCoordinates()) || 
                      contours.get(i).get(l).getStartingCoordinates().equals(contours.get(j).get(k).getEndingCoordinates()) ||
                      contours.get(i).get(l).getEndingCoordinates().equals(contours.get(j).get(k).getStartingCoordinates())||
                      contours.get(i).get(l).getEndingCoordinates().equals(contours.get(j).get(k).getEndingCoordinates()))
                   {
                     
                       contours.get(i).lines.addAll(contours.get(j).lines);
                       contours.remove(contours.get(j));
                       break COMPARINGLINES;
                       
                       
                   }
                  }  
           }
           }
       }
   
    }
    
    
    LinkedList<Contour> partition(Contour initial)
    {
        LinkedList<Contour> contours = new LinkedList();
        Contour separateContour;
        Line line;
        int position = 0;
        //System.out.println("starting size "+initial.size());
        while(!initial.lines.isEmpty())
        {
            int counter = 0;
            
            line = initial.get(position);
            separateContour = new Contour();
            separateContour.add(line);
            initial.remove(line);
          //  System.out.println("Taking new line for new contour, remaining: "+initial.size());
            if(initial.size()>0){
            PartitioningStep2:
            while(initial.size()>counter)               
            {
                if(!line.equals(initial.get(counter)))
                {
                    try{
                        if(line.startingPointConnects(initial.get(counter).getStartingCoordinates()))
                        {
                            separateContour.add(initial.get(counter));
                            initial.remove(counter);
                            if(counter == initial.size())
                                break PartitioningStep2;                                                        
                        }
                        if(line.startingPointConnects(initial.get(counter).getEndingCoordinates()))
                        {
                            separateContour.add(initial.get(counter));
                            initial.remove(counter);
                            if(counter == initial.size())
                                break PartitioningStep2;                                                            
                        }
                        if(line.endingPointConnects(initial.get(counter).getStartingCoordinates()))
                        {
                            separateContour.add(initial.get(counter));
                            initial.remove(counter);
                            if(counter == initial.size())
                                break PartitioningStep2;                                    
                        }
                        if(line.endingPointConnects(initial.get(counter).getEndingCoordinates()))
                        {
                            separateContour.add(initial.get(counter));
                            initial.remove(counter);
                            if(counter == initial.size())
                                break PartitioningStep2;                            
                        }  
                        
                        }catch(java.lang.IndexOutOfBoundsException e){
                    System.err.println("partitioning out of bounds on: "+counter+" lines left: "+initial.size() );
                    } 
                }
                
                counter++;
               
            }
            
            for(int i = 0; i < separateContour.size(); i++)
                for(int j = 0; j < initial.size(); j++)
                {
                    if(!separateContour.get(i).equals(initial.get(j)))
                    {
                        if(separateContour.get(i).startingPointConnects(initial.get(j).getStartingCoordinates()))
                        {
                            if(!separateContour.contains(initial.get(j)))
                            {
                                separateContour.add(initial.get(j));                                
                                initial.remove(j);
                                break;
                            }                            
                        }
                        if(separateContour.get(i).startingPointConnects(initial.get(j).getEndingCoordinates()))
                        {
                            if(!separateContour.contains(initial.get(j)))
                            {
                                separateContour.add(initial.get(j));
                                initial.remove(j);
                                break;
                            }
                        }
                        if(separateContour.get(i).endingPointConnects(initial.get(j).getStartingCoordinates()))
                        {
                            if(!separateContour.contains(initial.get(j)))
                            {
                                separateContour.add(initial.get(j));
                                initial.remove(j);
                                break;
                            }
                        }
                        if(separateContour.get(i).endingPointConnects(initial.get(j).getEndingCoordinates()))
                        {
                            if(!separateContour.contains(initial.get(j)))
                            {
                                separateContour.add(initial.get(j));
                                initial.remove(j);
                                break;
                            }
                        }
                    }
                    initial.remove(separateContour.get(i));
                }
           contours.add(separateContour);
            }
        }
        
        /*System.out.println("adding conours, remaining number of lines: "+initial.size()+" total number of contours: "+contours.size());
        for(Contour cont : contours)
        {
            cont.printLines();
        }
        correctPartitioningMistakes(contours);
    
        System.out.println("Post partition correction, number of contours: "+contours.size());
        */
        return contours;
    }
    

    
    LinkedList<Contour> orderContours(LinkedList<Contour> contour)
    {
        LinkedList<Contour> tmp = new LinkedList();
        Contour first = findNearest(new Point(0,0), contour);
        tmp.add(first);
        while(contour.size() > 0)
        {
            Line lastLine = tmp.getLast().get(tmp.getLast().size()-1);
            tmp.add(findNearest(lastLine.getEndingCoordinates(), contour));
        }
        
        return tmp;
    }
    
    Contour findNearest(Point start, LinkedList<Contour> contours)
    {
        Contour nearest = new Contour();
        nearest = contours.get(0);
       
        
        double minDistance = lineLength(nearest.get(0).getStartingCoordinates(), start);
        
        for(int j = 0; j < contours.size(); j++)
        {
            if(!contours.get(j).checkIfOpenContour()) //can start from any coordinate, gotta check them all
            {
                for(int i = 0; i < contours.get(j).size(); i++)
                {
                   if(minDistance > lineLength(contours.get(j).get(i).getStartingCoordinates(), start))
                {
                    minDistance = lineLength(contours.get(j).get(i).getStartingCoordinates(), start);
                    contours.get(j).startFrom(i);
                    nearest = contours.get(j);                                  
                }  
                }               
            }//if closed contour, can start only from first or last
            else
            {
                if(minDistance > lineLength(contours.get(j).get(0).getStartingCoordinates(), start))
                {
                    minDistance = lineLength(contours.get(j).get(0).getStartingCoordinates(), start);
                    nearest = contours.get(j);                    
                }
                if(minDistance > lineLength(contours.get(j).get(contours.get(j).size()-1).getEndingCoordinates(), start))
                {
                    minDistance = lineLength(contours.get(j).get(contours.get(j).size()-1).getEndingCoordinates(), start);
                    contours.get(j).changeDirection();
                    nearest = contours.get(j);
                }
            }
        }
        contours.remove(nearest);
        return nearest;
    }

    public ArrayList changeDirection(ArrayList<Point[]> contour)
    { 
        ArrayList<Point[]> temp = new ArrayList();
        for(int i = contour.size()-1; i >= 0; i--)
        {
            Point tmp = contour.get(i)[1];
            contour.get(i)[1] = contour.get(i)[0];
            contour.get(i)[0] = tmp;
            temp.add(contour.get(i));        
        }  
        return temp;
    }
    
    //--------------------------------------------------------------------------------------------------\\
    
    public static Double lineLength (Point EndingPoint, Point StartingPoint)
    { double ilgis;
    int x2 = (int) EndingPoint.x;
    int y2 = (int) EndingPoint.y;
    int x1 = (int) StartingPoint.x;
    int y1 = (int) StartingPoint.y;
    ilgis = Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1, 2));

    return ilgis;
    }
    
    //--------------------------------------------------------------------------------------------------\\
    
    void outputContours(LinkedList<Contour> contours)
    {
        PrintWriter writer = null;
        try {
            
            writer = new PrintWriter("output.txt", "UTF-8");
            writer.print(contours.size()+"/ ");//total number of contours
            
            for(Contour cont : contours)
            {
                writer.print(cont.size()+1+"/ ");
                
                for(int i = 0; i < cont.size(); i++)
                {
                    if(i == 0)
                    {
                    writer.print(cont.get(i).startingX+", "+cont.get(i).startingY+"; ");
                    writer.print(cont.get(i).endingX+", "+cont.get(i).endingY+"; ");
                    }
                    else
                    {
                    writer.print(cont.get(i).endingX+", "+cont.get(i).endingY+"; ");
                    }
                }
            }
            
            
        
         } catch (FileNotFoundException ex) {
                Logger.getLogger(Processer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Processer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                writer.close();
            }
    }
    
    //--------------------------------------------------------------------------------------------------\\
    
    void removeInacurateContours(LinkedList<Contour> contours)
    {
        for(int i = 0; i < contours.size(); i++)
            for(int j = 0; j < contours.get(i).size(); j++)
            {
                if(j+1 < contours.get(i).size())
                {
                    if(lineLength(contours.get(i).get(j).getEndingCoordinates(), contours.get(i).get(j+1).getStartingCoordinates()) > 0)
                    {
                        contours.remove(i);
                    }
                }
            }
        
    }
    
    //--------------------------------------------------------------------------------------------------\\
         
        public static void main(String[] args) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Vectorizer s = new Vectorizer();
                    try {
                        s.processer();
                    } catch (Exception ex) {
                        Logger.getLogger(Vectorizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } );
    }
}
