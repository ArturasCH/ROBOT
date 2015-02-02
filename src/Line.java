
import java.awt.Point;


public class Line {
    int startingX;
    int startingY;
    int endingX;
    int endingY;
    
    Line(Point[] coordinates)
    {
        startingX = coordinates[0].x;
        startingY = coordinates[0].y;
        endingX = coordinates[1].x;
        endingY = coordinates[1].y;
    }
    
    void changeDirection()
    {
        int tmpX = startingX;
        int tmpY = startingY;
        
        startingX = endingX;
        startingY = endingY;
        
        endingX = tmpX;
        endingY = tmpY;

    }
    
    void setStartingCoordinates(Point start)
    {
        startingX = start.x;
        startingY = start.y;
    }
    
    void setEndingCoordinates(Point end)
    {
        endingX = end.x;
        endingY = end.y;
    }
    
    Point getStartingCoordinates()
    {
        return new Point(startingX, startingY);
    }
    
    Point getEndingCoordinates()
    {
        return new Point(endingX, endingY);               
    }
    
    boolean startingPointConnects(Point otherLine)
    {
        if(new Point(startingX, startingY).equals(otherLine))
        return true;
        else 
        return false;
    }
     boolean endingPointConnects(Point otherLine)
    {
        if(new Point(endingX, endingY).equals(otherLine))
        return true;
        else 
        return false;
    }
     
     void printCoordinates()
     {
         System.out.println("From "+startingX+", "+startingY+"; to "+endingX+", "+endingY+"; ");
     }
}
